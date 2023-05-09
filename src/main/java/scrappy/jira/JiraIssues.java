package scrappy.jira;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import scrappy.core.issue.parser.IssueStateParser;
import scrappy.core.issue.parser.IssueTypeParser;
import scrappy.core.issue.types.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Retrieves and parses issues from the api
 */
public class JiraIssues {
    private static final String CONTAINS = "Contains";

    private static Stream<JSONObject> jsonArrayToStream(JSONArray array) {
        return IntStream
            .range(0, array.length())
            .mapToObj(array::getJSONObject);
    }

    private final JiraApiProps apiProps;
    private String urlField;
    private String instructionField;

    public JiraIssues(JiraApiProps apiProps, String project) {
        this.apiProps = apiProps;

        JSONObject json = JiraApi.getIssueMetadata(apiProps, project, "Scrappy Url");
        JSONObject fields = json
            .getJSONArray("projects")
            .getJSONObject(0)
            .getJSONArray("issuetypes")
            .getJSONObject(0)
            .getJSONObject("fields");

        for (String key: fields.keySet()) {
            String name = fields.getJSONObject(key).getString("name");
            if (name.equals("URL")) {
                urlField = key;
            } else if (name.equals("Instructions")) {
                instructionField = key;
            }
        }
    }

    /**
     * Returns Execution issue and its sub-issues.
     * @param issueKey Issue to retrieve
     * @return Execution Issue Object
     */
    public ExecutionIssue getExecution(String issueKey) {
        JSONObject json = JiraApi.getIssue(apiProps, issueKey);
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
            .getJSONObject("statusCategory")
            .getString("key");
        IssueState state = IssueStateParser.tryParse(stateString);
        if (state == IssueState.Done) {
            throw new RuntimeException("Execution jira not in use");
        }

        // get issue links
        JSONArray issuelinks = fields.getJSONArray("issuelinks");
        List<Issue> subIssues = jsonArrayToStream(issuelinks)
            .filter(linkObject -> {
                String linkType = linkObject.getJSONObject("type")
                    .getString("name");
                return Objects.equals(linkType, CONTAINS);
            }).map(subIssue -> {
                String subIssueKey = subIssue.getJSONObject("outwardIssue")
                    .getString("key");
                return getSubIssue(subIssueKey);
            }).filter(Objects::nonNull)
            .collect(Collectors.toList());

        return new ExecutionIssue(issueKey, summary, state, subIssues);
    }

    /**
     * Returns folder or url issue
     * @param issueKey Issue to retrieve
     * @return Execution Issue Object
     */
    public Issue getSubIssue(String issueKey) {
        JSONObject json = JiraApi.getIssue(apiProps, issueKey);
        JSONObject fields = json.getJSONObject("fields");
        String issueTypeString = fields
            .getJSONObject("issuetype")
            .getString("name");
        IssueType type = IssueTypeParser.tryParse(issueTypeString);
        switch (type) {
            case Folder:
                return getFolderIssue(json, issueKey);
            case Url:
                return getUrlIssue(json, issueKey);
            case Execution:
            default:
                throw new RuntimeException("SubIssue Type not supported");
        }
    }

    /**
     * Returns folder issue and its sub-issues.
     * @param json json object to parse
     * @param issueKey Issue to retrieve
     * @return Execution Issue Object
     */
    private FolderIssue getFolderIssue(JSONObject json, String issueKey) {
        // collect field
        JSONObject fields = json.getJSONObject("fields");
        String summary = fields.getString("summary");
        String stateString = fields
            .getJSONObject("status")
            .getString("name");
        IssueState state = IssueStateParser.tryParse(stateString);
        if (state == IssueState.Done) {
            return null;
        }

        // get issue links
        JSONArray issuelinks = fields.getJSONArray("issuelinks");
        List<Issue> subIssues = jsonArrayToStream(issuelinks)
            .filter(linkObject -> {
                String linkType = linkObject.getJSONObject("type")
                    .getString("name");
                boolean outwards = linkObject.has("outwardIssue");
                return Objects.equals(linkType, CONTAINS) && outwards;
            }).map(subIssue -> {
                String subIssueKey = subIssue.getJSONObject("outwardIssue")
                    .getString("key");
                return getSubIssue(subIssueKey);
            }).filter(Objects::nonNull)
            .collect(Collectors.toList());

        return new FolderIssue(issueKey, summary, state, subIssues);
    }

    /**
     * Returns Url issue.
     * @param json json object to parse
     * @param issueKey Issue to retrieve
     * @return Execution Issue Object
     */
    private UrlIssue getUrlIssue(JSONObject json, String issueKey) {
        // collect field
        JSONObject fields = json.getJSONObject("fields");
        String summary = fields.getString("summary");
        String stateString = fields
            .getJSONObject("status")
            .getString("name");
        IssueState state = IssueStateParser.tryParse(stateString);
        String url = fields.getString(urlField);
        if (state == IssueState.Done) {
            return null;
        }

        String instructions = "";
        if (fields.has(instructionField) && !fields.isNull(instructionField)) {
            JSONArray instructionsArray = fields
                .getJSONObject(instructionField)
                .getJSONArray("content");
            instructions = jsonArrayToStream(instructionsArray)
                .filter(instrObj -> instrObj.getString("type").equals("codeBlock"))
                .flatMap(instrObj -> jsonArrayToStream(instrObj.getJSONArray("content")))
                .filter(instrContent -> instrContent.getString("type").equals("text"))
                .map(instrContent -> instrContent.getString("text"))
                .reduce("", (a, b) -> a + b);
        }
        return new UrlIssue(issueKey, summary, state, instructions, url);
    }
}
