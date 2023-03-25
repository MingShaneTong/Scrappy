package scrappy.core.issuetypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Issue implements Iterable<Issue> {
    private final String key;
    private final String summary;
    private final IssueState state;
    private final List<Issue> subIssues;

    public Issue(String key, String summary, IssueState state) {
        this.key = key;
        this.summary = summary;
        this.state = state;
        this.subIssues = new ArrayList<>();
    }

    public Issue(String key, String summary, IssueState state, List<Issue> subIssues) {
        this.key = key;
        this.summary = summary;
        this.state = state;
        this.subIssues = subIssues;
    }

    public String getKey() {
        return key;
    }

    public IssueState getState() {
        return state;
    }

    public void addIssue(Issue issue) {
        subIssues.add(issue);
    }

    public boolean hasSubIssues() {
        return !subIssues.isEmpty();
    }

    public Iterator<Issue> iterator() {
        return subIssues.iterator();
    }
}
