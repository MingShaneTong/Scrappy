package scrappy.jira;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import scrappy.core.issueparser.IssueStateParser;
import scrappy.core.issueparser.IssueTypeParser;
import scrappy.core.issuetypes.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JiraIssues {
    private static String CONTAINS = "Contains";
    private static String URLFIELD = "customfield_10035";

    public static ExecutionIssue getExecution(JiraApiProps api, String issueKey) throws IOException, InterruptedException {
        JsonObject json = JiraApi.getIssue(api, issueKey);
        JsonObject fields = json.getAsJsonObject("fields");
        String issueTypeString = fields
            .getAsJsonObject("issuetype")
            .get("name").getAsString();
        IssueType type = IssueTypeParser.tryParse(issueTypeString);
        if (type != IssueType.Execution) {
            throw new RuntimeException("Parsing wrong issue type");
        }

        // collect field
        String summary = fields.get("summary").getAsString();
        String stateString = fields
            .getAsJsonObject("status")
            .get("name").getAsString();
        IssueState state = IssueStateParser.tryParse(stateString);

        // get issue links
        JsonArray issuelinks = fields.get("issuelinks").getAsJsonArray();
        List<Issue> subIssues = issuelinks.asList().stream()
            .map(JsonElement::getAsJsonObject)
            .filter(linkObject -> {
                String linkType = linkObject.getAsJsonObject("type")
                    .get("name").getAsString();
                return Objects.equals(linkType, CONTAINS);
            }).map(subIssue -> {
                String subIssueKey = subIssue.getAsJsonObject("outwardIssue")
                    .get("key").getAsString();
                try {
                    return JiraIssues.getSubIssue(api, subIssueKey);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());

        ExecutionIssue issue = new ExecutionIssue(issueKey, summary, state, subIssues);
        return issue;
    }

    public static Issue getSubIssue(JiraApiProps api, String issueKey) throws IOException, InterruptedException {
        JsonObject json = JiraApi.getIssue(api, issueKey);
        JsonObject fields = json.getAsJsonObject("fields");
        String issueTypeString = fields
            .getAsJsonObject("issuetype")
            .get("name").getAsString();
        IssueType type = IssueTypeParser.tryParse(issueTypeString);
        switch (type) {
            case Folder:
                return getFolderIssue(json, api, issueKey);
            case Url:
                return getUrlIssue(json, api, issueKey);
            case Execution:
            default:
                throw new RuntimeException("SubIssue Type not supported");
        }
    }

    private static FolderIssue getFolderIssue(JsonObject json, JiraApiProps api, String issueKey) {
        // collect field
        JsonObject fields = json.getAsJsonObject("fields");
        String summary = fields.get("summary").getAsString();
        String stateString = fields
            .getAsJsonObject("status")
            .get("name").getAsString();
        IssueState state = IssueStateParser.tryParse(stateString);

        // get issue links
        JsonArray issuelinks = fields.get("issuelinks").getAsJsonArray();
        List<Issue> subIssues = issuelinks.asList().stream()
            .map(JsonElement::getAsJsonObject)
            .filter(linkObject -> {
                String linkType = linkObject.getAsJsonObject("type")
                    .get("name").getAsString();
                boolean outwards = linkObject.has("outwardIssue");
                return Objects.equals(linkType, CONTAINS) && outwards;
            }).map(subIssue -> {
                String subIssueKey = subIssue.getAsJsonObject("outwardIssue")
                    .get("key").getAsString();
                try {
                    return JiraIssues.getSubIssue(api, subIssueKey);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());

        FolderIssue issue = new FolderIssue(issueKey, summary, state, subIssues);
        return issue;
    }

    private static UrlIssue getUrlIssue(JsonObject json, JiraApiProps api, String issueKey) {
        // collect field
        JsonObject fields = json.getAsJsonObject("fields");
        String summary = fields.get("summary").getAsString();
        String stateString = fields
            .getAsJsonObject("status")
            .get("name").getAsString();
        IssueState state = IssueStateParser.tryParse(stateString);
        String url = fields.get(URLFIELD).getAsString();

        UrlIssue issue = new UrlIssue(issueKey, summary, state, url);
        return issue;
    }
}
