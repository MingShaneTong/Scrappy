package scrappy.jira;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import scrappy.core.issuetypes.ExecutionIssue;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.net.URI;
import java.util.Base64;

public class JiraApi {
    public static JsonObject getIssue(JiraApiProps api, String issueKey) throws IOException, InterruptedException {
        String url = api.apiUrl() + issueKey;
        Base64.Encoder encoder = Base64.getEncoder();
        String auth  = api.login() + ":" + api.apiToken();

        String encodedAuth = encoder.encodeToString(auth.getBytes());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Basic " + encodedAuth)
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }
}
