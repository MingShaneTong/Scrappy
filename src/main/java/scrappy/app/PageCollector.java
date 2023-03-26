package scrappy.app;

import scrappy.core.issuetypes.Issue;
import scrappy.core.issuetypes.IssueState;
import scrappy.core.issuetypes.UrlIssue;
import scrappy.web.ScrappyPage;

public class PageCollector {
    public void CapturePages(ScrappyPage page, Issue issue, String location) {
        if (issue.getState() != IssueState.InUse) { return; }

        String nextLocation = location + "/" + issue.getKey();
        if (issue.hasSubIssues()) {
            for (Issue subIssue : issue) {
                CapturePages(page, subIssue, nextLocation);
            }
        } else if(issue instanceof UrlIssue) {
            page.VisitAndCaptureData(((UrlIssue) issue).getUrl(), nextLocation);
        }
    }
}
