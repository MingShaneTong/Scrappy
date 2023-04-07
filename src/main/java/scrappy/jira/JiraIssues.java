package scrappy.jira;

import org.json.JSONArray;
import org.json.JSONObject;
import scrappy.core.issue.parser.IssueStateParser;
import scrappy.core.issue.parser.IssueTypeParser;
import scrappy.core.issue.types.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JiraIssues {
    private static final String CONTAINS = "Contains";
    private static final String URLFIELD = "customfield_10035";
    private static final String INSTRUCTIONFIELD = "customfield_10036";

    private static Stream<JSONObject> jsonArrayToStream(JSONArray array) {
        return IntStream
            .range(0, array.length())
            .mapToObj(i -> array.getJSONObject(i));
    }

    /**
     * Returns Execution issue and its sub-issues.
     * @param api
     * @param issueKey
     * @return
     */
    public static ExecutionIssue getExecution(JiraApiProps api, String issueKey) {
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
        List<Issue> subIssues = jsonArrayToStream(issuelinks)
            .filter(linkObject -> {
                String linkType = linkObject.getJSONObject("type")
                    .getString("name");
                return Objects.equals(linkType, CONTAINS);
            }).map(subIssue -> {
                String subIssueKey = subIssue.getJSONObject("outwardIssue")
                    .getString("key");
                return JiraIssues.getSubIssue(api, subIssueKey);
            })
            .collect(Collectors.toList());

        return new ExecutionIssue(issueKey, summary, state, subIssues);
    }

    /**
     * Returns folder or url issue
     * @param api
     * @param issueKey
     * @return
     */
    public static Issue getSubIssue(JiraApiProps api, String issueKey) {
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

    /**
     * Returns folder issue and its sub-issues.
     * @param json
     * @param api
     * @param issueKey
     * @return
     */
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
        List<Issue> subIssues = jsonArrayToStream(issuelinks)
            .filter(linkObject -> {
                String linkType = linkObject.getJSONObject("type")
                    .getString("name");
                boolean outwards = linkObject.has("outwardIssue");
                return Objects.equals(linkType, CONTAINS) && outwards;
            }).map(subIssue -> {
                String subIssueKey = subIssue.getJSONObject("outwardIssue")
                    .getString("key");
                return JiraIssues.getSubIssue(api, subIssueKey);
            })
            .collect(Collectors.toList());

        return new FolderIssue(issueKey, summary, state, subIssues);
    }

    /**
     * Returns Url issue.
     * @param json
     * @param api
     * @param issueKey
     * @return
     */
    private static UrlIssue getUrlIssue(JSONObject json, JiraApiProps api, String issueKey) {
        // collect field
        JSONObject fields = json.getJSONObject("fields");
        String summary = fields.getString("summary");
        String stateString = fields
            .getJSONObject("status")
            .getString("name");
        IssueState state = IssueStateParser.tryParse(stateString);
        String url = fields.getString(URLFIELD);

        String instructions = "";
        if (fields.has(INSTRUCTIONFIELD) && fields.isNull(INSTRUCTIONFIELD) == false) {
            JSONArray instructionsArray = fields
                .getJSONObject(INSTRUCTIONFIELD)
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
