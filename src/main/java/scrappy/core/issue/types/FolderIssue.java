package scrappy.core.issue.types;

import java.util.List;

/**
 * Contains folders and url issues
 */
public class FolderIssue extends Issue {
    /**
     * Creates a folder issue
     * @param key Issue key of Issue
     * @param summary Title of the Issue
     * @param state Whether the issue is in use
     * @param subIssues Issues contained within issue
     */
    public FolderIssue(String key, String summary, IssueState state, List<Issue> subIssues) {
        super(key, summary, state, subIssues);
    }
}
