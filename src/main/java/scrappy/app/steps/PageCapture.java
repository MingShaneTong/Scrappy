package scrappy.app.steps;

import scrappy.app.AppLocations;
import scrappy.core.issue.types.Issue;
import scrappy.core.issue.types.IssueState;
import scrappy.core.issue.types.UrlIssue;
import scrappy.web.ScrappyPage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Collects data from the webpages
 */
public class PageCapture {
    /**
     * Captures the webpage data and saves to the given location
     * @param page page object performing scraping
     * @param issue issue to capture from
     * @param location location in file directory
     */
    public void capturePages(ScrappyPage page, Issue issue, String location) {
        if (issue.getState() == IssueState.Done) { return; }

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
            UrlIssue urlIssue = (UrlIssue) issue;
            page.capture(urlIssue.getUrl(), urlIssue.getInstructions(), location);
        } catch (Exception exception) {
            System.out.println("Problem Capturing " + issue.getKey());
            System.out.println("Error: " + exception);
            String stackTraceFile = location + AppLocations.STACK_TRACE_FILE;
            try (PrintWriter writer = new PrintWriter(stackTraceFile)) {
                exception.printStackTrace(writer);
            } catch (IOException e) {
                System.out.println("Saving Stack Trace Failure");
            }
        }
    }
}
