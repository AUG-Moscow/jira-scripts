//use with Events: Issue Updated, Work Logged On Issue, Issue Worklog Updated
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.event.issue.IssueEvent
import com.atlassian.jira.issue.MutableIssue

import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.dvhb.epicscomponentsversionssum")
log.setLevel(Level.DEBUG)

log.debug("Issue Event happened:")
log.debug(event)

IssueEvent event = event as IssueEvent
String issueKey = event.getIssue().getKey()
MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject(issueKey)

Long original = issue.getOriginalEstimate()
Long remaining = issue.getEstimate()
Long spent = issue.getTimeSpent()

Boolean needsUpdate = (remaining != 0) && (remaining + spent != original)

if (needsUpdate) {
    Long newRemaining = original - spent

    issue.setEstimate(newRemaining)
    issue.store()
}
