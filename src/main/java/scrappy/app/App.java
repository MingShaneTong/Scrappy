// src/main/java/scrappy/web/core/App.java
package scrappy.app;

import scrappy.app.steps.DiffDetector;
import scrappy.app.steps.PageCapture;
import scrappy.app.steps.SnapshotSaver;
import scrappy.app.steps.SummarySaver;
import scrappy.core.issue.types.ExecutionIssue;
import scrappy.core.issue.types.Issue;
import scrappy.jira.JiraApiProps;
import scrappy.jira.JiraApiUrl;
import scrappy.jira.JiraIssues;
import scrappy.web.ScrappyPage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Main application
 */
public class App {
    private final String url;
    private final String project;
    private final String login;
    private final String apiToken;
    private final String executionJira;

    /**
     * Creates an app
     * @param url Base url of Jira
     * @param project Project Code
     * @param login Username to login
     * @param apiToken Api Token for username
     * @param executionJira Execution Jira issue key
     */
    public App(String url, String project, String login, String apiToken, String executionJira) {
        this.url = url;
        this.project = project;
        this.login = login;
        this.apiToken = apiToken;
        this.executionJira = executionJira;
    }

    private static void deleteDirectory(File file) {
        if (!file.exists()) { return; }

        for (File subfile : file.listFiles()) {
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }
            subfile.delete();
        }
        file.delete();
    }

    /**
     * Performs the steps
     * - Data collection
     * - Reset Artifacts
     * - Capturing Artifacts
     * - Check for differences
     * - Documenting artifacts
     * - Document summary
     */
    public void start() {
        JiraApiProps api = new JiraApiProps(new JiraApiUrl(url), login, apiToken);

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
        Map<Issue, Boolean> diffMap = new HashMap<>();
        dd.detectDifferences(exe, diffMap);

        // create jira with data attached
        System.out.println("Documenting artifacts on Jira...");
        SnapshotSaver saver = new SnapshotSaver();
        Map<Issue, String> snapshotTicketsMap = new HashMap<>();
        saver.SaveIssues(api, project, exe, "", snapshotTicketsMap);

        // create summary
        System.out.println("Documenting summary on Jira...");
        SummarySaver sumSaver = new SummarySaver();
        String sumkey = sumSaver.createSummary(api, project, exe, diffMap, snapshotTicketsMap);
        System.out.println("Summary created on " + sumkey);
    }

    /**
     * Starts application
     * @param args
     */
    public static void main(String[] args) throws IOException {
        String[] settings = Files.readAllLines(Path.of(".settings")).toArray(new String[0]);

        String url = settings[0];
        String project = settings[1];
        String login = settings[2];
        String apiToken = settings[3];
        String executionJira = settings[4];

        App app = new App(url, project, login, apiToken, executionJira);
        app.start();
    }
}