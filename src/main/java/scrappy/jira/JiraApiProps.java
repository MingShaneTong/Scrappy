package scrappy.jira;

/**
 * Jira REST Api Properties
 * @param apiUrl Base Url of the Jira
 * @param login Username to login
 * @param apiToken API Token of user
 */
public record JiraApiProps(JiraApiUrl apiUrl, String login, String apiToken) {
}
