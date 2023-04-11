package scrappy.app;

import scrappy.core.issue.types.Issue;
import scrappy.core.issue.types.IssueState;
import scrappy.core.issue.types.UrlIssue;
import scrappy.web.ScrappyPage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Collects data from the webpages
 */
public class PageCapture {
    /**
     * Captures the webpage data and saves to the given location
     * @param page
     * @param issue
     * @param location
     */
    public void capturePages(ScrappyPage page, Issue issue, String location) {
        if (issue.getState() != IssueState.InUse) { return; }

        String nextLocation = location + issue.getKey() + "/";
        if (issue.hasSubIssues()) {
            for (Issue subIssue : issue) {
                capturePages(page, subIssue, nextLocation);
            }
        } else if(issue instanceof UrlIssue) {
            performCapture(page, issue, nextLocation);
        }
    }

    private void performCapture(ScrappyPage page, Issue issue, String location) {
        Path path = Paths.get(location);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UrlIssue urlIssue = (UrlIssue) issue;
        page.capture(urlIssue.getUrl(), urlIssue.getInstructions(), location);
    }
}
