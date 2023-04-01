package scrappy.jira;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import scrappy.core.issue.parser.IssueStateParser;
import scrappy.core.issue.parser.IssueTypeParser;
import scrappy.core.issue.types.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JiraIssues {
    private static final String CONTAINS = "Contains";
    private static final String URLFIELD = "customfield_10035";

    public static ExecutionIssue getExecution(JiraApiProps api, String issueKey) throws UnirestException {
        JSONObject json = JiraApi.getIssue(api, issueKey);
        JSONObject fields = json.getJSONObject("fields");
        String issueTypeString = fields
            .getJSONObject("issuetype")
            .getString("name");
        IssueType type = IssueTypeParser.tryParse(issueTypeString);
        if (type != IssueType.Execution) {
            throw new RuntimeException("Parsing wrong issue type");
        }

        // collect field
        String summary = fields.getString("summary");
        String stateString = fields
            .getJSONObject("status")
            .getString("name");
        IssueState state = IssueStateParser.tryParse(stateString);

        // get issue links
        JSONArray issuelinks = fields.getJSONArray("issuelinks");
        List<Issue> subIssues = IntStream
            .range(0, issuelinks.length())
            .mapToObj(i -> issuelinks.getJSONObject(i))
            .filter(linkObject -> {
                String linkType = linkObject.getJSONObject("type")
                    .getString("name");
                return Objects.equals(linkType, CONTAINS);
            }).map(subIssue -> {
                String subIssueKey = subIssue.getJSONObject("outwardIssue")
                    .getString("key");
                try {
                    return JiraIssues.getSubIssue(api, subIssueKey);
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());

        return new ExecutionIssue(issueKey, summary, state, subIssues);
    }

    public static Issue getSubIssue(JiraApiProps api, String issueKey) throws UnirestException {
        JSONObject json = JiraApi.getIssue(api, issueKey);
        JSONObject fields = json.getJSONObject("fields");
        String issueTypeString = fields
            .getJSONObject("issuetype")
            .getString("name");
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

    private static FolderIssue getFolderIssue(JSONObject json, JiraApiProps api, String issueKey) {
        // collect field
        JSONObject fields = json.getJSONObject("fields");
        String summary = fields.getString("summary");
        String stateString = fields
            .getJSONObject("status")
            .getString("name");
        IssueState state = IssueStateParser.tryParse(stateString);

        // get issue links
        JSONArray issuelinks = fields.getJSONArray("issuelinks");
        List<Issue> subIssues = IntStream
            .range(0, issuelinks.length())
            .mapToObj(i -> issuelinks.getJSONObject(i))
            .filter(linkObject -> {
                String linkType = linkObject.getJSONObject("type")
                    .getString("name");
                boolean outwards = linkObject.has("outwardIssue");
                return Objects.equals(linkType, CONTAINS) && outwards;
            }).map(subIssue -> {
                String subIssueKey = subIssue.getJSONObject("outwardIssue")
                    .getString("key");
                try {
                    return JiraIssues.getSubIssue(api, subIssueKey);
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());

        return new FolderIssue(issueKey, summary, state, subIssues);
    }

    private static UrlIssue getUrlIssue(JSONObject json, JiraApiProps api, String issueKey) {
        // collect field
        JSONObject fields = json.getJSONObject("fields");
        String summary = fields.getString("summary");
        String stateString = fields
            .getJSONObject("status")
            .getString("name");
        IssueState state = IssueStateParser.tryParse(stateString);
        String url = fields.getString(URLFIELD);

        return new UrlIssue(issueKey, summary, state, url);
    }
}
