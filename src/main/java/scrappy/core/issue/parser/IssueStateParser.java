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
     * @param state State string to parse
     * @return Parsed IssueState or null if not a type
     */
    public static IssueState parse(String state) {
        switch (state) {
            case "In Use":
                return IssueState.InUse;
            case "To Be Used":
                return IssueState.ToBeUsed;
            case "Not In Use":
                return IssueState.NotInUse;
            default:
                throw new RuntimeException("Issue State Not Supported.");
        }
    }
}
