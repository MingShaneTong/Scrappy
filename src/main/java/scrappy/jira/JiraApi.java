package scrappy.jira;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.io.File;

public class JiraApi {
    public static JSONObject getIssue(JiraApiProps api, String issueKey) throws UnirestException {
        String url = api.apiUrl() + issueKey;
        HttpResponse<JsonNode> response = Unirest.get(url)
            .basicAuth(api.login(), api.apiToken())
            .asJson();
        return response.getBody().getObject();
    }

    public static JSONObject createIssue(JiraApiProps api, String issueJson) throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.post(api.apiUrl())
            .basicAuth(api.login(), api.apiToken())
            .header("Content-type", "application/json")
            .body(issueJson)
            .asJson();
        return response.getBody().getObject();
    }

    public static JSONObject createAttachment(JiraApiProps api, String issueKey, File file) throws UnirestException {
        String url = api.apiUrl() + issueKey + "/attachments";
        HttpResponse<JsonNode> response = Unirest.post(url)
            .basicAuth(api.login(), api.apiToken())
            .header("X-Atlassian-Token", "no-check")
            .field("file", file)
            .asJson();
        return response.getBody().getObject();
    }
}
