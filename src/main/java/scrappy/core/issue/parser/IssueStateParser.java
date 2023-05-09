package scrappy.core.issue.parser;

import scrappy.core.issue.types.IssueState;

/**
 * Parses the issue state from a string
 */
public class IssueStateParser {
    /**
     * Safely parses the string to issue state
     * @param state State string to parse
     * @return Parsed IssueState or null if not a type
     */
    public static IssueState tryParse(String state) {
        try {
            return parse(state);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parses the string to issue state
     * @param key State key to parse
     * @return Parsed IssueState or null if not a type
     */
    public static IssueState parse(String key) {
        switch (key) {
            case "new":
                return IssueState.New;
            case "indeterminate":
                return IssueState.Indeterminate;
            case "done":
                return IssueState.Done;
            default:
                throw new RuntimeException("Issue State Not Supported.");
        }
    }
}
