//use in script field with Searcher: Free Text Searcher and Template: Text Field (multi-line)
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.core.util.DateUtils


def log = Logger.getLogger("com.dvhb.epicscomponentsversionssum")
log.setLevel(Level.DEBUG)

def expectedEstimateID = 'customfield_14103'

CustomField expectedEstimateField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(expectedEstimateID)

List<Long> durations = ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.id)?.collect { issueLink ->
    if (!issueLink.issueLinkType.isSubTaskLinkType()) { return 0L }
    Issue issue = issueLink.getDestinationObject()
    return issue.getCustomFieldValue(expectedEstimateField) as Long ?: issue.getOriginalEstimate() //sum cf values of Original Estimate values if cf value is empty
} ?: ([] as List<Long>)

Long totalDuration = durations.inject(0L) { a, b -> a + b } as Long

log.debug(totalDuration)
return DateUtils.getDurationString(totalDuration)
