//Use this in Scripted Validators
import com.opensymphony.workflow.InvalidInputException
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.customfields.option.LazyLoadedOption

def customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName("Review")
def valueList = (issue.getCustomFieldValue(customField) ?: []) as List
def requiredOption = valueList.find { item -> (item as LazyLoadedOption).getValue() == "Required" }
if (requiredOption == null) return // No Required option selected. Allow transaction.
if (issue.getStatus().getSimpleStatus().getName().equals("Client")) return // Issue is in Client status. Allow transaction.
def previousClientStatus = ComponentAccessor.getChangeHistoryManager().getChangeItemsForField(issue, "status").find { item -> item.getFromString().equals("Client") }
if (previousClientStatus) return // Issue has been in Client status. Allow transaction.

invalidInputException = new InvalidInputException("Required option is checked but issue has never been in 'Client' status")
