//Use it in Scripted Post Functions with labels custom field
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.issue.label.LabelManager


def customFieldId = 13501L //your labels custom field id
def groupName = "Group Name"
def customFieldBusinessValue = ["Business"] as Set<String>
def customFieldDvhbValue = ["Internal"] as Set<String>

def groupManager = ComponentManager.getComponentInstanceOfType(GroupManager.class)
def labelManager = ComponentManager.getComponentInstanceOfType(LabelManager.class)

def user = issue.getAssignee()
def issueId = issue.getId()

if (groupManager.isUserInGroup(user, groupName)) {
	labelManager.setLabels(user, issueId, customFieldId, customFieldBusinessValue, false, true)
}
else {
	labelManager.setLabels(user, issueId, customFieldId, customFieldDvhbValue, false, true)
}
