package scrappy.jira;

public record JiraApiUrl(String url) {
    public String issueUrl() {
        return url + "/rest/api/3/issue/";
    }

    public String issueUrl(String issueKey) {
        return url + "/rest/api/3/issue/" + issueKey;
    }

    public String browseUrl() {
        return url + "/browse/";
    }

    public String browseUrl(String issueKey) {
        return url + "/browse/" + issueKey;
    }

    public String attachmentUrl(String issueKey) {
        return issueUrl(issueKey) + "/attachments";
    }
}
