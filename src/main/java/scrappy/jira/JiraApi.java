package scrappy.jira;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;

import java.io.File;

/**
 * Creates Json Requests to perform actions and retrieve data,
 */
public class JiraApi {
    /**
     * Retrieves issue from Jira
     * @param api Jira REST Api properties
     * @param issueKey Issue key to retrieve
     * @return Json body of the Jira Issue
     */
    public static JSONObject getIssue(JiraApiProps api, String issueKey) {
        String url = api.apiUrl().issueUrl(issueKey);
        try {
            HttpResponse<JsonNode> response = Unirest.get(url)
                .basicAuth(api.login(), api.apiToken())
                .asJson();
            return response.getBody().getObject();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates an issue with the provided json data
     * @param api Jira REST Api properties
     * @param issueJson Issue Json Object to post to api
     * @return Json response from REST Api
     */
    public static JSONObject createIssue(JiraApiProps api, String issueJson) {
        try {
            HttpResponse<JsonNode> response = Unirest.post(api.apiUrl().issueUrl())
                .basicAuth(api.login(), api.apiToken())
                .header("Content-type", "application/json")
                .body(issueJson)
                .asJson();
            return response.getBody().getObject();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Attaches file to the Jira issue
     * @param api Jira REST Api properties
     * @param issueKey Issue key to create attachment for
     * @param file File to attach to issue
     * @return Json response from REST Api
     */
    public static JSONObject createAttachment(JiraApiProps api, String issueKey, File file) {
        String url = api.apiUrl().attachmentUrl(issueKey);
        try {
            HttpResponse<JsonNode> response = Unirest.post(url)
                .basicAuth(api.login(), api.apiToken())
                .header("X-Atlassian-Token", "no-check")
                .field("file", file)
                .asJson();
            return response.getBody().getObject();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }
}
