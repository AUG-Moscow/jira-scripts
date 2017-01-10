import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.changehistory.ChangeHistory
import com.atlassian.jira.issue.history.ChangeItemBean
import com.atlassian.jira.user.ApplicationUsers

def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
def result = null;
String user_to = null;
String user_from = null
int statusFound = 0;
def changedFromInProgrees = [];
def changedFromInProgreesId = [];
def lastChangeId
def changes = changeHistoryManager.getChangeHistories(issue)

changes.eachWithIndex {
    ChangeHistory change, changeId ->
        def properties = change.getChangeItemBeans()
    properties.each {
        ChangeItemBean property ->
            if (property.getField() == "status" && (property.fromString == "In Progress" || property.fromString == "Design")) {
                changedFromInProgrees.push(change);
                changedFromInProgreesId.push(changeId);
                lastChangeId = changeId;
            }
    }
}
if (changedFromInProgreesId != []) {
    changes.eachWithIndex {
        ChangeHistory change, changeId ->
            def properties = change.getChangeItemBeans()
        properties.each {
            ChangeItemBean property ->
                if (statusFound == 0) {
                    if (changeId >= lastChangeId) {
                        if (property.getField() == "assignee") {
                            user_from = property.from;
                            user_to = property.to;
                            def mrBean = property;
                            statusFound = 1;
                        }
                    }
                }
        }
    }
}

result = ApplicationUsers.byKey(issue.getAssigneeId())
if (user_to) {
    result = ApplicationUsers.byKey(user_to)
}
if (user_from) {
    result = ApplicationUsers.byKey(user_from)
}

return result;
