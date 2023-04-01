package scrappy.jira;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.net.URI;
import java.nio.file.Path;
import java.util.Base64;

public class JiraApi {
    public static HttpRequest.Builder createAuthenticatedRequestBuilder(JiraApiProps api) {
        Base64.Encoder encoder = Base64.getEncoder();
        String auth  = api.login() + ":" + api.apiToken();
        String encodedAuth = encoder.encodeToString(auth.getBytes());
        return HttpRequest.newBuilder()
            .header("Authorization", "Basic " + encodedAuth);
    }

    public static JsonObject getIssue(JiraApiProps api, String issueKey) throws IOException, InterruptedException {
        String url = api.apiUrl() + issueKey;

        HttpRequest request = createAuthenticatedRequestBuilder(api)
            .uri(URI.create(url))
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    public static JsonObject createIssue(JiraApiProps api, String issueJson) throws IOException, InterruptedException {
        HttpRequest request = createAuthenticatedRequestBuilder(api)
            .uri(URI.create(api.apiUrl()))
            .header("Content-type", "application/json")
            .method("POST", HttpRequest.BodyPublishers.ofString(issueJson))
            .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    public static void createAttachment(JiraApiProps api, String issueKey, File file) throws IOException, InterruptedException, UnirestException {
        com.mashape.unirest.http.HttpResponse<String> response = Unirest.post(api.apiUrl() + issueKey + "/attachments")
            .basicAuth(api.login(), api.apiToken())
            .header("Accept", "application/json")
            .header("X-Atlassian-Token", "no-check")
            .field("file", file)
            .asString();
    }
}
