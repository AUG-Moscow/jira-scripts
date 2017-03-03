//use with free text searcher
import groovy.time.TimeDuration
import groovy.time.TimeCategory    

if (issue.resolution == null) {
    return null
}
 
TimeDuration duration = TimeCategory.minus(issue.resolutionDate,issue.created)

duration.days > 1 ? (duration.days + " days") : "1 day"
