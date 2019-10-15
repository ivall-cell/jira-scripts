import com.atlassian.jira.permission.PermissionSchemeService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.project.Project

// Method to change the permission scheme of a project 
// Designed to be used when a final issue is closed and the project needed to be locked down

def changePermissionScheme(schemeName, issue) {

	def projectKey = issue.projectObject.key
	 
	Project proj = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey)
	 
	ComponentAccessor.getPermissionSchemeManager().removeSchemesFromProject(proj)
	ComponentAccessor.getPermissionSchemeManager().addSchemeToProject(issue.projectObject, ComponentAccessor.getPermissionSchemeManager().getSchemeObject(schemeName))

} 

changePermissionScheme("Permission Scheme A", issue)
