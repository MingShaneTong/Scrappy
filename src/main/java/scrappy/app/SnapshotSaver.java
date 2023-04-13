package scrappy.app;

import org.json.JSONException;
import org.json.JSONObject;
import scrappy.core.issue.builder.SnapshotIssueBuilder;
import scrappy.core.issue.types.Issue;
import scrappy.core.issue.types.IssueState;
import scrappy.core.issue.types.UrlIssue;
import scrappy.jira.JiraApi;
import scrappy.jira.JiraApiProps;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Saves snapshots to Jira using the REST API
 */
public class SnapshotSaver {
    private static final DateTimeFormatter TIMEFORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    /**
     * Create tickets for the snapshot that was captured for a run
     * @param api
     * @param project
     * @param issue
     * @param location
     */
    public void SaveIssues(JiraApiProps api, String project, Issue issue, String location) {
        if (issue.getState() != IssueState.InUse) { return; }

        String nextLocation = location + issue.getKey() + "/" ;
        if (issue.hasSubIssues()) {
            for (Issue subIssue : issue) {
                SaveIssues(api, project, subIssue, nextLocation);
            }
        } else if(issue instanceof UrlIssue) {
            SaveSnapshot(api, project, (UrlIssue) issue, nextLocation);
        }
    }

    /**
     * Creates a Snapshot Issue and add attachment files in folder to it.
     * @param api
     * @param project
     * @param issue
     * @param folder
     */
    public void SaveSnapshot(JiraApiProps api, String project, UrlIssue issue, String folder) {
        String artifacts = AppLocations.ARTIFACTS + folder;
        String diff = AppLocations.DIFF + folder + AppLocations.DIFF_FILE;

        Path diffPath = Paths.get(diff);
        String diffJson;
        try {
            diffJson = Files.readString(diffPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String time = TIMEFORMATTER.format(LocalDateTime.now());
        String summary = String.format("%s %s", time, issue.getSummary());

        String json = new SnapshotIssueBuilder()
            .setProject(project)
            .setSummary(summary)
            .setIssueLink(issue.getKey())
            .setDescription(diffJson)
            .toString();

        String issueKey;
        try {
            JSONObject newIssue = JiraApi.createIssue(api, json);
            issueKey = newIssue.getString("key");
        } catch (JSONException e) {
            System.out.println("Snapshot Creation failed for " + issue.getKey());
            System.out.println(json);
            return;
        }

        for (File attachment : new File(artifacts).listFiles()) {
            JiraApi.createAttachment(api, issueKey, attachment);
        }
    }
}
