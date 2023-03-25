package scrappy.jira;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.net.URI;
import java.util.Base64;

public class JiraApi {
    public void getIssue(JiraApiProps api, String issueKey) {
        String url = api.apiUrl() + issueKey;
        Base64.Encoder encoder = Base64.getEncoder();
        String auth  = api.login() + ":" + api.apiToken();
        try {
            String encodedAuth = encoder.encodeToString(auth.getBytes());

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + encodedAuth)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(request.toString());
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
