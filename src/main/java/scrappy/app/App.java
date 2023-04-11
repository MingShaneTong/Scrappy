// src/main/java/scrappy/web/core/App.java
package scrappy.app;

import scrappy.core.issue.types.ExecutionIssue;
import scrappy.jira.JiraApiProps;
import scrappy.jira.JiraIssues;
import scrappy.web.ScrappyPage;

import java.io.File;

public class App {
    private final String apiUrl;
    private final String project;
    private final String login;
    private final String apiToken;
    private final String executionJira;

    public App(String apiUrl, String project, String login, String apiToken, String executionJira) {
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

        // get jira data
        System.out.println("Collecting Jira Data...");
        ExecutionIssue exe = JiraIssues.getExecution(api, executionJira);

        // use previous current as new archive
        System.out.println("Resetting artifacts...");
        File archiveFolder = new File(AppLocations.ARCHIVE);
        File artifactsFolder = new File(AppLocations.ARTIFACTS);
        deleteDirectory(archiveFolder);
        artifactsFolder.renameTo(archiveFolder);

        // capture data
        System.out.println("Capturing artifacts...");
        ScrappyPage page = new ScrappyPage();
        PageCapture collector = new PageCapture();
        collector.capturePages(page, exe, AppLocations.ARTIFACTS);

        // collecting diff
        System.out.println("Checking for differences...");
        DiffDetector dd = new DiffDetector();
        dd.detectDifferences(exe);

        // create jira with data attached
        System.out.println("Documenting artifacts on Jira...");
        SnapshotSaver saver = new SnapshotSaver();
        saver.SaveIssues(api, project, exe, AppLocations.ARTIFACTS);
    }

    public static void main(String[] args) {
        String apiUrl = args[0];
        String project = args[1];
        String login = args[2];
        String apiToken = args[3];
        String executionJira = args[4];

        App app = new App(apiUrl, project, login, apiToken, executionJira);
        app.start();
    }
}