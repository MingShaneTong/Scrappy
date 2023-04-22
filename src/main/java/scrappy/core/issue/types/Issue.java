package scrappy.core.issue.types;

import java.util.Iterator;
import java.util.List;

/**
 * Generic Issue that could contain sub issues
 */
public abstract class Issue implements Iterable<Issue> {
    private final String key;
    private final String summary;
    private final IssueState state;
    private final List<Issue> subIssues;

    /**
     * Creates an issue
     * @param key jira key of issue
     * @param summary Title of the issue
     * @param state Whether the issue is in use or not
     * @param subIssues The issues contained in the issue
     */
    public Issue(String key, String summary, IssueState state, List<Issue> subIssues) {
        this.key = key;
        this.summary = summary;
        this.state = state;
        this.subIssues = subIssues;
    }

    public String getKey() {
        return key;
    }

    public String getSummary() {
        return summary;
    }

    public IssueState getState() {
        return state;
    }

    /**
     * Checks if the issue has sub issues
     * @return True if issue has sub issues
     */
    public boolean hasSubIssues() {
        return !subIssues.isEmpty();
    }

    /**
     * Iterates through each of the sub issues.
     * @return Iterator for each sub issue
     */
    public Iterator<Issue> iterator() {
        return subIssues.iterator();
    }

    /**
     * Return issue tree string starting as the root.
     * @return issue string represented as a tree
     */
    public String printTree() {
        return printTree(0, "");
    }

    /**
     * Returns issue tree continuing from depth 'tabs'.
     * @param tabs Number of indentations into the tree
     * @param current Current string to append to
     * @return issue string represented as a tree
     */
    public String printTree(int tabs, String current) {
        String newStr = current;
        newStr += "\t".repeat(tabs);
        newStr += getKey();
        newStr += " ";
        newStr += getSummary();
        newStr += "\n";

        for (Issue sub : subIssues) {
            newStr = sub.printTree(tabs + 1, newStr);
        }

        return newStr;
    }
}
