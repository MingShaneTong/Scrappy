package scrappy.jira;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.io.File;

/**
 * Creates Json Requests to perform actions and retrieve data,
 */
public class JiraApi {
    /**
     * Retrieves issue from Jira
     * @param api
     * @param issueKey
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
     * @param api
     * @param issueJson
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
     * @param api
     * @param issueKey
     * @param file
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
