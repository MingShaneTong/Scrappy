package scrappy.app;

import scrappy.core.issue.types.Issue;
import scrappy.core.issue.types.IssueState;
import scrappy.core.issue.types.UrlIssue;
import scrappy.web.ScrappyPage;

/**
 * Collects data from the webpages
 */
public class PageCollector {
    /**
     * Captures the webpage data and saves to the given location
     * @param page
     * @param issue
     * @param location
     */
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
