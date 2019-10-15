import com.atlassian.jira.component.ComponentAccessor
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.issue.history.ChangeItemBean
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.issue.IssueInputParameters
import java.util.LinkedHashMap
import java.util.ArrayList
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.workflow.TransitionOptions
import com.atlassian.jira.issue.IssueInputParametersImpl
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.ConstantsManager
import com.atlassian.jira.issue.status.Status
 
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
 
// Define the mappings from the status and the transition id that takes you to that status.
LinkedHashMap<String, Integer> statusMappings = new LinkedHashMap<String, Integer>()
statusMappings.put("Accepted", 21)
statusMappings.put("Assigned", 31)
statusMappings.put("In Development", 41)
statusMappings.put("Development Complete", 111)
 
ArrayList<String> indexes = new ArrayList<String>(statusMappings.keySet())
 
def issueManager = ComponentAccessor.getIssueManager()
 
// Get the last status the issue was at
def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
def changeHistories = changeHistoryManager.getChangeHistories(issue)
 
def workflowManager = ComponentAccessor.getWorkflowManager()
def workflow = workflowManager.getWorkflow(issue)
 
def changeItem = ComponentAccessor.getChangeHistoryManager().getChangeItemsForField(issue, 'status')?.last()
def lastStatus = changeItem.getFromString();
 
// No need to transition any further
if(lastStatus.equals("Submitted")){
    log.debug("Status was in submitted, no need to loop through transitions")
    return
}
 
// Get the index of the last status the issue was on in the hashmap
def ind = indexes.indexOf(lastStatus)
log.debug("Index of previous status in the key set was $ind")
 
// Get the last status
ConstantsManager constantsManager = ComponentAccessor.getConstantsManager();
Status status = constantsManager.getStatusByName(lastStatus);
 
def statusString = status.getName()
log.debug("Last status was $statusString")
 
// Transition options ot skip all validators, permissions, conditions
final TransitionOptions transitionOptions = new TransitionOptions.Builder().skipPermissions().skipValidators().setAutomaticTransition().skipConditions().build()
log.debug("Built transition options settings")
 
IssueService issueService = ComponentAccessor.getIssueService()
 
for(int i = 0; i <= ind; i++){
    def statusAtIndex = indexes.get(i)
    log.debug(statusAtIndex)
     
    def transitionId = statusMappings.get(indexes.get(i))
     
    // transition an issue
    def transitionValidationResult = issueService.validateTransition(currentUser, issue.id, transitionId, new IssueInputParametersImpl(),transitionOptions)
 
    if(transitionValidationResult.isValid())
    {
        def transitionResult = issueService.transition(currentUser, transitionValidationResult)
     
        if (transitionResult.isValid())
        {
            log.debug("Transitioned issue $issue through action $transitionId")
        }
        else
        {
            log.debug("Transition result is not valid")
        }
    }
}
 
issue.setStatus(status)