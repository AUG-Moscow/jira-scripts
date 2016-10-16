//use as workflow post-function
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField

CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

Long originalEstimateValue = issue.getOriginalEstimate()
Long timeSpentValue = issue.getTimeSpent()

def expectedEstimateID = 'customfield_14103'
CustomField expectedEstimateField = customFieldManager.getCustomFieldObject(expectedEstimateID)

issue.setOriginalEstimate(timeSpentValue < originalEstimateValue ? timeSpentValue : originalEstimateValue)
issue.setCustomFieldValue(expectedEstimateField, originalEstimateValue as Double)
issue.setEstimate(0L)
