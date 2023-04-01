package scrappy.core.issue.parser;

import scrappy.core.issue.types.IssueType;

/**
 * Parses the issue type from a string
 */
public class IssueTypeParser {
    public static IssueType tryParse(String state) {
        try {
            return parse(state);
        } catch (Exception e) {
            return null;
        }
    }

    public static IssueType parse(String state) {
        switch (state) {
            case "Scrappy Execution":
                return IssueType.Execution;
            case "Scrappy Folder":
                return IssueType.Folder;
            case "Scrappy Url":
                return IssueType.Url;
            default:
                throw new RuntimeException("Issue Type Not Supported.");
        }
    }
}
