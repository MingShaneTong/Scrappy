package scrappy.core.issue.types;

import java.util.Iterator;
import java.util.List;

public abstract class Issue implements Iterable<Issue> {
    private final String key;
    private final String summary;
    private final IssueState state;
    private final List<Issue> subIssues;

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

    public boolean hasSubIssues() {
        return !subIssues.isEmpty();
    }

    public Iterator<Issue> iterator() {
        return subIssues.iterator();
    }

    public String printTree() {
        return printTree(0, "");
    }
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
