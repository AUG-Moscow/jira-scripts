
####List of useful post-functions for `Scriptrunner for JIRA`

Set Original Estimate to 8 hours:

`issue.setOriginalEstimate((long) 28800)`

Set Remaining Estimate to zero:

`issue.setEstimate (0L)`

Set Description with line breaks:

`issue.description = 'First line. \n Second line.'`

`issue.description = 'First line' + '\n' + 'Second line.'`
