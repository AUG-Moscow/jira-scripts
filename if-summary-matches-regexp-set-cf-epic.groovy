//use as postfunction
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import org.apache.log4j.Logger
import org.apache.log4j.Level

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.MutableIssue


def log = Logger.getLogger("com.dvhb")
log.setLevel(Level.DEBUG)


IssueManager issueManager = ComponentAccessor.getIssueManager()
UserManager userManager = ComponentAccessor.getUserManager()
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

String userName = "r2d2" //user that performs operation

ApplicationUser user = userManager.getUserByName(userName)
CustomField helper = customFieldManager.getCustomFieldObject('customfield_10601') //your customfield

CustomField epicLinkCustomField = customFieldManager.getCustomFieldObjectByName('Epic Link')

if (issue.getSummary() =~ /(\(|\[| )?(u|U)(x|X)(\)|\]| )?/) {
    issue.setCustomFieldValue(helper, 'UX')
    issue.setCustomFieldValue(epicLinkCustomField, issueManager.getIssueObject("HR-909")) //your epic for UX issues
}

else if (issue.getSummary() =~ /(\(|\[| )?(u|U)(i|I)(\)|\]| )?/) {
    issue.setCustomFieldValue(helper, 'UI')
    issue.setCustomFieldValue(epicLinkCustomField, issueManager.getIssueObject("HR-910")) //your epic for UI issues
}

else {
    issue.setCustomFieldValue(helper, 'Unsorted')
    issue.setCustomFieldValue(epicLinkCustomField, issueManager.getIssueObject("HR-395")) //your epic for unsorted issues
}
issueManager.updateIssue(user, issue, EventDispatchOption.ISSUE_UPDATED, false)
