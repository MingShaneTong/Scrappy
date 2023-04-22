package scrappy.core.issue.parser;

import scrappy.core.issue.types.IssueType;

/**
 * Parses the issue type from a string
 */
public class IssueTypeParser {
    /**
     * Safely parses the string to issue type
     * @param type Type string to parse
     * @return Parsed IssueType or null if not a type
     */
    public static IssueType tryParse(String type) {
        try {
            return parse(type);
        } catch (Exception e) {
            return null;
        }
    }

     /**
     * Parses the string to issue type
     * @param type Type string to parse
     * @return Parsed IssueType or null
     */
    public static IssueType parse(String type) {
        switch (type) {
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
