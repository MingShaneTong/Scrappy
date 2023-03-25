package scrappy.core.issueparser;

import com.google.gson.JsonObject;
import scrappy.core.issuetypes.ExecutionIssue;
import scrappy.core.issuetypes.IssueState;
import scrappy.core.issuetypes.IssueType;

public class IssueParser {
    public static ExecutionIssue ParseJsonToExecutionIssue(JsonObject json) {
        JsonObject fields = json.getAsJsonObject("fields");
        String issueTypeString = fields
            .getAsJsonObject("issuetype")
            .get("name").getAsString();
        IssueType type = IssueTypeParser.tryParse(issueTypeString);
        if (type != IssueType.Execution) {
            throw new RuntimeException("Parsing wrong issue type");
        }

        String summary = fields.get("summary").getAsString();
        String issueKey = json.get("key").getAsString();

        String stateString = fields
            .getAsJsonObject("status")
            .get("name").getAsString();
        IssueState state = IssueStateParser.tryParse(stateString);

        ExecutionIssue issue = new ExecutionIssue(issueKey, summary, state);
        return issue;
    }
}
