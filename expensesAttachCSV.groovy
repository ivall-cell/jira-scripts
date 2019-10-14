import com.atlassian.jira.component.ComponentAccessor
import groovy.json.JsonSlurper
import groovy.json.StreamingJsonBuilder
import com.atlassian.jira.issue.Issue
import org.apache.commons.codec.binary.Base64
import java.text.SimpleDateFormat
import com.atlassian.jira.user.UserPropertyManager
import com.atlassian.jira.user.ApplicationUser
import java.time.Instant
import java.util.Date
import com.atlassian.jira.issue.attachment.CreateAttachmentParamsBean
import com.atlassian.jira.issue.AttachmentError
import com.atlassian.jira.issue.history.ChangeItemBean
import com.atlassian.jira.config.util.JiraHome
import java.sql.Timestamp

// Expense Grid Custom Field Value
def expenseGridID = 12702
// Send the GET request for the media stored on the Expenses custom field
def baseURL = "https://secure.automation-consultants.com/jira/rest/idalko-igrid/1.0/grid/${expenseGridID}/issue/${issue.getId()}"
HttpURLConnection con = sendRequest(baseURL, null, "GET")

// Read the connection input
def br = new BufferedReader(new InputStreamReader(con.getInputStream()))
def line = br.readLine()

// Parse the JSON from the REST Request
def slurper = new groovy.json.JsonSlurper()
Map entries = (Map) slurper.parseText(line)

// Create the CSV file that will be attached to the issue
File file = new File("${ComponentAccessor.getComponentOfType(JiraHome.class).getHome()}/output.csv")
file.write "Category, Description, Date, Cost (inc. VAT), Customer, Location, Receipt\n" //, Ticket ID\n" 

// Loop over the rows of the table
for(def row : entries.values){
    
    String category, description, date, cost, customer, location, receipt = null //, ticketid = null
    
    // Loop through the row
    for(def cell : row){
        
        // Don't include columns that we dont need
        if(!["id", "modified", "ireceipt", "issueId"].contains(cell.getKey())){
            
            
            if(cell.getKey().equals("idate")){
                // Convert Date from Milliseconds to DateTime
                def dateInMilliSeconds = row.get(cell.getKey() as String) as Long
                Long dateInSeconds = (dateInMilliSeconds / 1000)
                Date epochDate = Date.from(Instant.ofEpochSecond(dateInSeconds))
                date = epochDate.toString()
                continue
            }
            
            if(cell.getKey().equals("icategory")){
                def cat = row.get(cell.getKey() as String)
                category = cat.name
                continue
            }
    
            if(cell.getKey().equals("icost")){
                cost = row.get(cell.getKey() as String)
                continue
            }
            
            if(cell.getKey().equals("icustomer")){
                customer = row.get(cell.getKey() as String)
                continue
            }
            
            if(cell.getKey().equals("idescription")){
                description = row.get(cell.getKey() as String)
                continue
            }
            
            if(cell.getKey().equals("ilocation")){
                location = row.get(cell.getKey() as String)
                continue
            }
            
            if(cell.getKey().equals("ireceiptE")){
                receipt = row.get(cell.getKey() as String
                continue
            }
            
            //if(cell.getKey().equals("iticketid")){
            //    ticketid = row.get(cell.getKey() as String)
            //    continue
            //}
        }
    }
    
    // Append the row to the end of the row
    // String rowString = category + ", " + description  + ", " + date + ", " + cost + ", " + customer + ", " + location + ", " + receipt + ", " + ticketid + "\n"
    String rowString = category + ", " + description  + ", " + date + ", " + cost + ", " + customer + ", " + location + ", " + receipt + "\n"
	file << rowString
}

// Create the Attachment Bean to be added to the issue
String ts = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
CreateAttachmentParamsBean bean = new CreateAttachmentParamsBean(file, "output_${issue.getKey()}_${ts}.csv", "text/csv", null, issue, false, null, null, new Date(), false)

// Try to create the attachment 
com.atlassian.fugue.Either<AttachmentError, ChangeItemBean> result = ComponentAccessor.getAttachmentManager().tryCreateAttachment(bean)

// Check if the attachment can be successfully created
if (result.isLeft()) {
    AttachmentError attachmentError = (AttachmentError) result.left().get()
    log.error("AttachmentError '" + attachmentError.getLogMessage())
} else {
	// Create the attachment for the issue
    ChangeItemBean changeItemBean = (ChangeItemBean) result.right().get()
    
    try {
       ComponentAccessor.getAttachmentManager().createAttachment(bean)
    } catch (Exception e){
        log.error(e)
    }
}


// Send a request type to a URL, can include JSON data if you want, also specify the type of request "POST", "GET", etc.
def HttpURLConnection sendRequest(String baseUrl, def data, def type) {
   
    URL url = new URL(baseUrl)
    HttpURLConnection connection = url.openConnection() as HttpURLConnection;

    //Define the username/password of an HR account
    String userPass = "Thomas.Ivall:";
    String basicAuth = "Basic " + new String(new Base64().encode(userPass.getBytes()))
    
    connection.setRequestProperty("Authorization", basicAuth)
    connection.requestMethod = type
    connection.doOutput = true
    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
    
    if(data){
        connection.outputStream.withWriter("UTF-8") { new StreamingJsonBuilder(it, data) }
    }
    
    connection.connect()
    
    log.warn("URL=" + url + " Status="+ connection.getResponseCode() as String)
    log.warn("url: " + url);
    log.warn("ResponseCode:" + connection.getResponseCode())
    log.warn("getResponseMessage:" + connection.getResponseMessage())

    log.warn("Content:" + connection.getContent())
    
    return connection
}