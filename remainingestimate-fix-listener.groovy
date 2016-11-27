//listener for fixing problem with tempo tracker
def original = issue.getOriginalEstimate()
def remaining = issue.getEstimate()
def spent = issue.getTimeSpent()

def needsUpdate = (remaining != 0) && (remaining + spent != original)

if (needsUpdate) {
    Long newRemaining = original - spent

    issue.setEstimate(newRemaining)
    issue.store()
}
