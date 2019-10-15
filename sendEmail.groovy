import com.atlassian.jira.event.issue.AbstractIssueEventListener
import com.atlassian.jira.event.issue.IssueEvent
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.customfields.CustomFieldType
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.mail.Email
import com.atlassian.mail.server.MailServerManager
import com.atlassian.mail.server.SMTPMailServer
import org.apache.log4j.Category
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.component.ComponentAccessor;
import java.text.SimpleDateFormat
import java.sql.Timestamp
import java.text.DateFormat
import java.util.Date
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.project.Project
import com.atlassian.jira.avatar.AvatarService
import com.atlassian.jira.avatar.Avatar.Size
 
String emailAddress = "testing@testing.com"
  
MailServerManager mailServerManager = ComponentAccessor.getMailServerManager()
SMTPMailServer mailServer = mailServerManager.getDefaultSMTPMailServer()

// Get user information 
def user = issue.getReporter()
String username = user.getUsername()
String displayname = user.getDisplayName()
String issuekey = issue.getKey()
String projectname = issue.getProjectObject().getName()
String issuesummary = issue.summary
 
def avatarManager = ComponentAccessor.getComponent(AvatarService)
def avatarurl = avatarManager.getAvatarURL(user, user)

Email email = new Email(emailAddress)
email.setMimeType("text/html")
email.setSubject("Project Start Up Complete");
 
//Custom html template for the email
String template = String.format("<p style=\"margin-left: 18.0pt;\"> </p> <table style=\"width: 100.0PERCENT; background: whitesmoke; border-collapse: collapse;\" width=\"100PERCENT\"> <tbody> <tr> <td style=\"padding: 7.5pt 15.0pt 7.5pt 15.0pt;\"> <table style=\"border-collapse: collapse;\"> <tbody> <tr> <td style=\"width: 24.0pt; padding: 0cm 6.0pt 0cm 0cm;\" width=\"32\"><img src=\"%s\" alt=\"Reporter Image\"></td> <td style=\"padding: 0cm 0cm 0cm 0cm;\"> <p style=\"line-height: 15.0pt;\"><span style=\"font-size: 10.5pt; font-family: 'Arial',sans-serif; position: relative; top: -1.0pt;\"><a href=\"https://jira.forensicrisk.com/secure/ViewProfile.jspa?name=%s\"><span style=\"color: #3b73af;\">%s</span></a> <strong><span style=\"font-family: 'Arial',sans-serif;\">closed</span></strong> an issue as <strong><span style=\"font-family: 'Arial',sans-serif;\">Closed</span></strong> </span> </p> </td> </tr> </tbody> </table> </td> </tr> <tr> <td style=\"padding: 0cm 15.0pt 0cm 15.0pt;\"> <table style=\"width: 100.0PERCENT; border-collapse: collapse; border-spacing: 0;\" width=\"100PERCENT\"> <tbody> <tr style=\"height: 7.5pt;\"> <td style=\"border: solid #CCCCCC 1.0pt; border-bottom: none; background: white; padding: 0cm 11.25pt 0cm 12.0pt; height: 7.5pt; border-top-right-radius: 5px; border-top-left-radius: 5px;\"> <p style=\"line-height: 7.5pt;\"><span style=\"color: white;\"> </span></p> </td> </tr> <tr> <td style=\"border-top: none; border-left: solid #CCCCCC 1.0pt; border-bottom: none; border-right: solid #CCCCCC 1.0pt; background: white; padding: 0cm 11.25pt 0cm 12.0pt;\"> <table style=\"width: 100.0PERCENT; border-collapse: collapse;\" width=\"100PERCENT\"> <tbody> <tr> <td style=\"padding: 7.5pt 0cm 0cm 0cm;\"> <p><span style=\"font-size: 10.5pt; line-height: 107PERCENT; font-family: 'Arial',sans-serif;\"><a href=\"https://jira.forensicrisk.com/browse/%s\"><span style=\"color: #3b73af;\">%s</span></a> / </span> <span style=\"font-size: 10.5pt; line-height: 107PERCENT; font-family: 'Arial',sans-serif;\"><a href=\"https://jira.forensicrisk.com/browse/%s\"><span style=\"color: #3b73af;\">%s</span></a> </span> </p> </td> </tr> <tr> <td style=\"padding: 0cm 3.75pt 0cm 0cm;\"> <p style=\"line-height: 22.5pt;\"><span style=\"font-size: 15.0pt; font-family: 'Arial',sans-serif; position: relative; top: -1.5pt;\"><a href=\"https://jira.forensicrisk.com/browse/%s\"><span style=\"color: #3b73af;\">%s</span></a> </span> </p> </td> </tr> </tbody> </table> </td> </tr> <tr> <td style=\"border-top: none; border-left: solid #CCCCCC 1.0pt; border-bottom: none; border-right: solid #CCCCCC 1.0pt; background: white; padding: 0cm 11.25pt 0cm 12.0pt;\"> <table style=\"border-collapse: collapse;\"> <tbody> <tr> <td style=\"padding: 1.5pt 0cm 1.5pt 0cm;\"> <p style=\"line-height: 15.0pt;\"><strong><span style=\"font-size: 10.5pt; font-family: 'Arial',sans-serif; color: #707070;\">Change By:</span></strong></p> </td> <td style=\"padding: 1.5pt 0cm 1.5pt 3.75pt;\"> <p style=\"line-height: 15.0pt;\"><span style=\"font-size: 10.5pt; font-family: 'Arial',sans-serif;\"><a href=\"https://jira.forensicrisk.com/secure/ViewProfile.jspa?name=%s\"><span style=\"color: #3b73af;\">%s</span></a> </span> </p> </td> </tr> <tr> <td style=\"padding: 1.5pt 0cm 1.5pt 0cm;\"> <p style=\"line-height: 15.0pt;\"><strong><span style=\"font-size: 10.5pt; font-family: 'Arial',sans-serif; color: #707070;\">Resolution:</span></strong></p> </td> <td style=\"padding: 1.5pt 0cm 1.5pt 3.75pt;\"> <p style=\"line-height: 15.0pt;\"><span style=\"font-size: 10.5pt; font-family: 'Arial',sans-serif; background: #DDFADE;\">Closed</span></p> </td> </tr> <tr> <td style=\"padding: 1.5pt 0cm 1.5pt 0cm;\"> <p style=\"line-height: 15.0pt;\"><strong><span style=\"font-size: 10.5pt; font-family: 'Arial',sans-serif; color: #707070;\">Status:</span></strong></p> </td> <td style=\"padding: 1.5pt 0cm 1.5pt 3.75pt;\"> <p style=\"line-height: 15.0pt;\"><span style=\"text-decoration: line-through;\"><span style=\"font-size: 10.5pt; font-family: 'Arial',sans-serif; background: #FFE7E7;\">Waiting for Approval</span></span><span style=\"font-size: 10.5pt; font-family: 'Arial',sans-serif;\"> <span style=\"background: #DDFADE;\">Closed</span> </span> </p> </td> </tr> </tbody> </table> </td> </tr> <tr> <td style=\"border-top: none; border-left: solid #CCCCCC 1.0pt; border-bottom: none; border-right: solid #CCCCCC 1.0pt; background: white; padding: 0cm 11.25pt 0cm 12.0pt;\"> <table style=\"width: 100.0PERCENT; border-collapse: collapse;\" width=\"100PERCENT\"> <tbody> <tr> <td style=\"padding: 7.5pt 0cm 7.5pt 0cm;\"> <table style=\"border-collapse: collapse; margin-left: -2.25pt; margin-right: -2.25pt;\"> <tbody> <tr> <td style=\"padding: 0cm 0cm 0cm 0cm;\"> </td> <td style=\"padding: 0cm 0cm 0cm 3.75pt;\"> <p style=\"line-height: 15.0pt;\"><span style=\"font-size: 10.5pt; font-family: 'Arial',sans-serif; position: relative; top: -3.0pt;\"><a href=\"https://jira.forensicrisk.com/browse/%s#add-comment\"><span style=\"color: #3b73af;\">Add Comment</span></a> </span> </p> </td> </tr> </tbody> </table> </td> </tr> </tbody> </table> </td> </tr> <tr style=\"height: 3.75pt;\"> <td style=\"border: solid #CCCCCC 1.0pt; border-top: none; background: white; padding: 0cm 11.25pt 0cm 12.0pt; height: 3.75pt; border-bottom-right-radius: 5px; border-bottom-left-radius: 5px;\"> <p style=\"line-height: 3.75pt;\"><span style=\"color: white;\"> </span></p> </td> </tr> </tbody> </table> </td> </tr> <tr> <td style=\"padding: 9.0pt 15.0pt 9.0pt 15.0pt;\"> <table style=\"border-collapse: collapse;\"> <tbody> <tr> <td style=\"width: 100.0PERCENT; padding: 0cm 0cm 0cm 0cm;\" width=\"100PERCENT\"> <p style=\"line-height: 13.5pt;\"><span style=\"font-size: 9.0pt; font-family: 'Arial',sans-serif; color: #999999; position: relative; top: -1.5pt;\">This message was sent by Atlassian JIRA (v7.6.7#76009-<span data-commit-id=\"3b7aec647166d4e7df5d26b690432627670d944f}\">sha1:3b7aec6</span>) </span> </p> </td> <td style=\"padding: 0cm 0cm 0cm 15.0pt;\"> <table style=\"border-collapse: collapse;\"> <tbody> <tr> <td style=\"padding: 2.25pt 0cm 0cm 0cm;\">Atlassian</td> </tr> </tbody> </table> </td> </tr> </tbody> </table> </td> </tr> </tbody> </table>", avatarurl, username, displayname, issuekey, projectname, issuekey, issuekey, issuekey, issuesummary, username, displayname, issuekey);
 
String replaced = template.replace("PERCENT", "%");
email.setBody(replaced)
mailServer.send(email)