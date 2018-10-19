
logger.info("issue.fields.summary = " + issue.fields.summary)
 
if (issue.fields.summary =~ /(\(|\[| )?(d|D)(o|O)(g|G)(\)|\]| )?/) { //check if summary contains a non case-sensitive word 'dog'
    logger.info("issue.fields.summary contains 'dog'")
    issueInput.fields.customfield_10013 = 'SKP-10' //set epic
    logger.info("issueInput.fields.customfield_10013 = " + issueInput.fields.customfield_10013)
    issueInput.fields.components = [[name: 'Python']] //set component
    logger.info("issueInput.fields.components = " + issueInput.fields.components)
}
else if (issue.fields.summary =~ /(\(|\[| )?(c|C)(a|A)(t|T)(\)|\]| )?/) {
    logger.info("issue.fields.summary contains 'cat'")
    issueInput.fields.customfield_10013 = 'SKP-11'
    logger.info("issueInput.fields.customfield_10013 = " + issueInput.fields.customfield_10013)
    issueInput.fields.components = [[name: 'Cat']]
    logger.info("issueInput.fields.components = " + issueInput.fields.components)
}
else {
    logger.info("issue.fields.summary doesn't match")
    issueInput.fields.customfield_10013 = 'SKP-12'
    logger.info("issueInput.fields.customfield_10013 = " + issueInput.fields.customfield_10013)
    issueInput.fields.components = [[name: 'Unsorted']]
    logger.info("issueInput.fields.components = " + issueInput.fields.components)
}
