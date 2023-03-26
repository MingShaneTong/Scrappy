package scrappy.jira;

public class JiraApiProps {
    private final String apiUrl;
    private final String login;
    private final String apiToken;

    public JiraApiProps(String apiUrl, String login, String apiToken) {
        this.apiUrl = apiUrl;
        this.login = login;
        this.apiToken = apiToken;
    }

    public String apiUrl() {
        return apiUrl;
    }

    public String login() {
        return login;
    }

    public String apiToken() {
        return apiToken;
    }
}
