package scrappy.core.issue.parser;

import scrappy.core.issue.types.IssueState;

/**
 * Parses the issue state from a string
 */
public class IssueStateParser {
    public static IssueState tryParse(String state) {
        try {
            return parse(state);
        } catch (Exception e) {
            return null;
        }
    }

    public static IssueState parse(String state) throws Exception {
        switch (state) {
            case "In Use":
                return IssueState.InUse;
            case "To Be Used":
                return IssueState.ToBeUsed;
            case "Not In Use":
                return IssueState.NotInUse;
            default:
                throw new Exception("Issue State Not Supported.");
        }
    }
}
