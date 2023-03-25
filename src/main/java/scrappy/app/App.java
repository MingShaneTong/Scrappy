// src/main/java/scrappy/web/core/App.java
package scrappy.app;

import scrappy.core.issuetypes.ExecutionIssue;
import scrappy.core.issuetypes.FolderIssue;
import scrappy.core.issuetypes.IssueState;
import scrappy.core.issuetypes.UrlIssue;
import scrappy.web.ScrappyPage;

import java.util.List;

public class App {
    public static String location = "artifacts/";

    public static void main(String[] args) {
        UrlIssue url1 = new UrlIssue("STJ-5", IssueState.InUse, "https://www.trademe.co.nz/a/search?search_string=table");
        UrlIssue url2 = new UrlIssue("STJ-6", IssueState.InUse, "https://www.atlassian.com/company/careers/all-jobs?team=Engineering&location=New%20Zealand%2CAustralia&search=");

        FolderIssue folder1 = new FolderIssue("STJ-4", IssueState.InUse, List.of(url2));
        FolderIssue folder2 = new FolderIssue("STJ-3", IssueState.InUse, List.of(folder1));
        FolderIssue folder3 = new FolderIssue("STJ-2", IssueState.InUse, List.of(url1));

        ExecutionIssue exe = new ExecutionIssue("STJ-1", IssueState.InUse, List.of(folder2, folder3));

        ScrappyPage page = new ScrappyPage();
        PageCollector collector = new PageCollector();
        collector.CapturePages(page, exe, location);
    }
}