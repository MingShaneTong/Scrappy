// src/main/java/scrappy/web/core/App.java
package scrappy.app;

import scrappy.core.issue.types.ExecutionIssue;
import scrappy.core.issue.types.IssueState;
import scrappy.core.issue.types.UrlIssue;
import scrappy.jira.JiraApiProps;
import scrappy.jira.JiraIssues;

import java.io.File;

public class TestApp {
    private final String apiUrl;
    private final String project;
    private final String login;
    private final String apiToken;
    private final String executionJira;

    public TestApp(String apiUrl, String project, String login, String apiToken, String executionJira) {
        this.apiUrl = apiUrl;
        this.project = project;
        this.login = login;
        this.apiToken = apiToken;
        this.executionJira = executionJira;
    }

    public static void deleteDirectory(File file)
    {
        if (!file.exists()) { return; }

        for (File subfile : file.listFiles()) {
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }
            subfile.delete();
        }
        file.delete();
    }

    public void start() {
        JiraApiProps api = new JiraApiProps(apiUrl, login, apiToken);

        UrlIssue exe = new UrlIssue("test", "s", IssueState.InUse, "", "");

        System.out.println("Checking for differences...");
        DiffDetector dd = new DiffDetector();
        dd.detectDifferences(exe);
    }

    public static void main(String[] args) {
        String apiUrl = args[0];
        String project = args[1];
        String login = args[2];
        String apiToken = args[3];
        String executionJira = args[4];

        TestApp app = new TestApp(apiUrl, project, login, apiToken, executionJira);
        app.start();
    }
}