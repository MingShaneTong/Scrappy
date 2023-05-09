package scrappy.jira;

/**
 * Represents Url used on the api
 * @param url Base url of Jira
 */
public record JiraApiUrl(String url) {
    /**
     * REST Api endpoint for jira issues
     * @return issue url
     */
    public String issueUrl() {
        return url + "/rest/api/3/issue/";
    }

    /**
     * REST Api endpoint for a single jira issue
     * @param issueKey issue to reference
     * @return issue url
     */
    public String issueUrl(String issueKey) {
        return issueUrl() + issueKey;
    }

    /**
     * Browse Url to reference other tickets in description
     * @return browse url
     */
    public String browseUrl() {
        return url + "/browse/";
    }

    /**
     * Browse Url to reference other tickets in description
     * @param issueKey issuekey to create link to
     * @return browse url
     */
    public String browseUrl(String issueKey) {
        return browseUrl() + issueKey;
    }

    /**
     * Url to create attachments to an issue
     * @param issueKey issue key to attach files to
     * @return attachment url
     */
    public String attachmentUrl(String issueKey) {
        return issueUrl(issueKey) + "/attachments";
    }

    public String metadataUrl(String project, String issueType)
    {
        return url + "/rest/api/3/issue/createmeta?projectKeys=" + project +"&issuetypeNames=" + issueType.replace(" ", "%20") + "&expand=projects.issuetypes.fields";
    }
}
