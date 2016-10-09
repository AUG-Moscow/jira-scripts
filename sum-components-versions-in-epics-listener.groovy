import com.atlassian.jira.bc.project.component.ProjectComponent
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.link.IssueLinkManager
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.project.version.Version
import com.atlassian.jira.user.util.UserManager

import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.dvhb.epicscomponentsversionssum")
log.setLevel(Level.DEBUG)

// Utility functions
def epicForIssue = { Issue issue ->
    def epicLinkID = 'customfield_10007'
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

    issueManager.updateIssue(userManager.getUserByName("amu"), epic, EventDispatchOption.ISSUE_UPDATED, false)
    issueIndexingService.reIndex(epic)
}

def setComponentsAndVersionsOfIssuesToEpic = { MutableIssue epic ->
    if (epic == null) {
        log.debug("Epic not found. Terminating.")
        return
    }

    Collection<Issue> issues = issuesForEpic(epic)
    Collection<ProjectComponent> components = componentsForIssues(issues)
    Collection<Version> versions = versionsForIssues(issues)

    Boolean needsUpdate = (epic.components.sort() != components.sort()) || (epic.fixVersions.sort() != versions.sort())

    if (needsUpdate) {
        log.debug("Updating")
        updateEpicAndReindex(epic, components, versions)
    }
}

// Fetch epic
MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject("SKP-273")
MutableIssue epic = epicForIssue(issue)

// --- Logic ----
setComponentsAndVersionsOfIssuesToEpic(epic)
/// ---- ----




//customFieldManager.getCustomFieldObjects(epic).collect { log.debug("ID: " + it.id + " Name: " + it.name ) }
//


//log.debug("Epic ID: " + epic.id + " Summary: " + epic.summary)
//components
//        .collect { "Calculated Component ID: " + it.id + " Name: " + it.name }
//        .collect { log.debug(it) }
//
//epic.components
//        .collect { "Component ID: " + it.id + " Name: " + it.name }
//        .collect { log.debug(it) }
//
//versions
//        .collect { "Calculated Version ID: " + it.id + " Name: " + it.name }
//        .collect { log.debug(it) }
//
//epic.fixVersions
//        .collect { "Epic Version ID: " + it.id + " Name: " + it.name }
//        .collect { log.debug(it) }

