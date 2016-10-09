import com.atlassian.jira.bc.project.component.ProjectComponent
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.issue.search.SearchResults
import com.atlassian.jira.jql.builder.JqlQueryBuilder
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.project.version.Version
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import com.atlassian.query.Query
import com.atlassian.jira.web.bean.PagerFilter;

import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.dvhb.epicscomponentsversionssum")
log.setLevel(Level.DEBUG)


// Utility functions
def epicForIssue = { Issue issue ->
    def epicLinkID = 'customfield_10007' //Component/s field id
    CustomField epicLinkField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(epicLinkID)
    MutableIssue epic = issue.getCustomFieldValue(epicLinkField) as MutableIssue
    return epic
}

def issuesForEpic = { Issue epic ->
    ComponentAccessor.getIssueLinkManager().getOutwardLinks(epic.id)
            .findAll { it.issueLinkType.name == "Epic-Story Link" } // filter
            .collect { it.getDestinationObject() } // map
}

def componentsForIssues = { Collection<Issue> issues ->
    issues
            .collect { it.components }
            .flatten()
            .unique(false) as Collection<ProjectComponent>
}

def versionsForIssues = { Collection<Issue> issues ->
    issues
            .collect { it.fixVersions }
            .flatten()
            .unique(false) as Collection<Version>
}

def updateEpicAndReindex = { MutableIssue epic, Collection<ProjectComponent> components, Collection<Version> versions ->
    UserManager userManager = ComponentAccessor.getUserManager()
    IssueManager issueManager = ComponentAccessor.getIssueManager()
    IssueIndexingService issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService.class)

    epic.setComponent(components)
    epic.setFixVersions(versions)

    issueManager.updateIssue(userManager.getUserByName("username"), epic, EventDispatchOption.ISSUE_UPDATED, false) //JIRA username that should run this script
    issueIndexingService.reIndex(epic)
}

def setComponentsAndVersionsOfIssuesToEpic = { MutableIssue epic ->
    if (epic == null) {
        log.debug("Epic not found. Terminating.")
        return
    }

    log.debug("Epic " + epic.getKey() + " found. Analyzing.")
    Collection<Issue> issues = issuesForEpic(epic)
    Collection<ProjectComponent> components = componentsForIssues(issues)
    Collection<Version> versions = versionsForIssues(issues)

    Boolean needsUpdate = (epic.components.sort() != components.sort()) || (epic.fixVersions.sort() != versions.sort())

    if (needsUpdate) {
        log.debug("Updating Epic " + epic.getKey())
        updateEpicAndReindex(epic, components, versions)
    }
    else {
        log.debug("Epic " + epic.getKey() + ". No update necessary")
    }
}

def projectForKey = { String key ->
    ProjectManager projectManager = ComponentAccessor.getProjectManager()
    Project project = projectManager.getProjectObjByKey(key)
    return project
}

def epicsForProject = { Project project ->
    SearchService searchService = ComponentAccessor.getComponent(SearchService.class)
    ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
    Query query = JqlQueryBuilder.newBuilder().where().project(project.key).buildQuery()
    IssueManager issueManager = ComponentAccessor.getIssueManager()

    SearchResults searchResult = searchService.search(user, query, PagerFilter.getUnlimitedFilter())
    Collection<Issue> issues = searchResult.issues

    return issues
            .findAll { it.getIssueType().getName() == "Epic" }
            .collect { issueManager.getIssueObject(it.id) }
}

// Logic
Project project = projectForKey("PROJECTKEY") // Project key
Collection<MutableIssue> epics = epicsForProject(project)
epics.collect(setComponentsAndVersionsOfIssuesToEpic)
// ---


// Debug
epics.collect { log.debug(it) }
