package scrappy.core.issuetypes;

import java.util.Iterator;
import java.util.List;

public abstract class Issue implements Iterable<Issue> {
    private final String issueId;
    private final IssueState state;
    private final List<Issue> subIssues;

    public Issue(String issueId, IssueState state, List<Issue> subIssues) {
        this.issueId = issueId;
        this.state = state;
        this.subIssues = subIssues;
    }

    public String getIssueId() {
        return issueId;
    }

    public IssueState getState() {
        return state;
    }
    public boolean hasSubIssues() { return !subIssues.isEmpty(); }

    public Iterator<Issue> iterator() {
        return subIssues.iterator();
    }
}
