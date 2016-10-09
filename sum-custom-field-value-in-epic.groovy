import com.atlassian.jira.component.ComponentAccessor;

import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.dvhb.businessvaluesum")
log.setLevel(Level.DEBUG)

enableCache = {-> false}
def issueLinkManager = ComponentAccessor.getIssueLinkManager()
def sum = 0

issueLinkManager.getOutwardLinks(issue.id)?.each { issueLink ->
	//log.debug(issueLink.getDestinationObject().getKey())
    if (issueLink.issueLinkType.name == "Epic-Story Link") {
        customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName("Business Value")
		def value = issueLink.getDestinationObject().getCustomFieldValue(customField)
        def intValue = value?.toString()?.toInteger() ?: 0
        sum += intValue
    }
}
log.debug(sum)
return sum