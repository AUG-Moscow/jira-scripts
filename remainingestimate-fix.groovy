//fixes problem with tempo timer
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.search.SearchResults
import com.atlassian.jira.jql.builder.JqlQueryBuilder
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.query.Query

import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.dvhb.remainingestimate-fix")
log.setLevel(Level.DEBUG)

SearchService searchService = ComponentAccessor.getComponent(SearchService.class)
ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

Query query = JqlQueryBuilder.newBuilder()
        .where()
        .currentEstimate().gt().number(0L)
        .and()
        .originalEstimate().gt().number(0L)
        .and()
        .timeSpent().gtEq().number(0L)
        .buildQuery()

IssueManager issueManager = ComponentAccessor.getIssueManager()

SearchResults searchResult = searchService.search(user, query, PagerFilter.getUnlimitedFilter())
Collection<Issue> issues = searchResult.issues

def allIssues = issues.collect { issueManager.getIssueObject(it.id) }

allIssues
        .findAll { issue ->
    def original = issue.getOriginalEstimate()
    def remaining = issue.getEstimate()
    def spent = issue.getTimeSpent()

    def needsUpdate = (remaining != 0) && (remaining + spent != original)
    return needsUpdate
}
.each { issue ->
    Long remaining = issue.getOriginalEstimate() - issue.getTimeSpent()

    issue.setEstimate(remaining)
    issue.store()
}


log.debug(allIssues)



