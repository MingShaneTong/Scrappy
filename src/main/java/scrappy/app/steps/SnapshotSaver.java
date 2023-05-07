package scrappy.app.steps;

import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;
import scrappy.app.AppLocations;
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
import java.util.Map;

/**
 * Saves snapshots to Jira using the REST API
 */
public class SnapshotSaver {
    private static final DateTimeFormatter TIMEFORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    /**
     * Create tickets for the snapshot that was captured for a run
     * @param api Jira REST Api Properties
     * @param project Project Code
     * @param issue Issue to save
     * @param location location in artifacts
     * @param snapshotTicketsMap Stores UrlIssue to newly created SnapshotIssue
     */
    public void SaveIssues(JiraApiProps api, String project, Issue issue, String location, Map<Issue, String> snapshotTicketsMap) {
        if (issue.getState() != IssueState.InUse) { return; }

        String nextLocation = location + issue.getKey() + "/" ;
        if (issue.hasSubIssues()) {
            for (Issue subIssue : issue) {
                SaveIssues(api, project, subIssue, nextLocation, snapshotTicketsMap);
            }
        } else if(issue instanceof UrlIssue) {
            SaveSnapshot(api, project, (UrlIssue) issue, nextLocation, snapshotTicketsMap);
        }
    }

    /**
     * Creates a Snapshot Issue and add attachment files in folder to it.
     * @param api Jira REST Api Properties
     * @param project Project Code
     * @param issue Issue to save
     * @param folder location in artifacts
     * @param snapshotTicketsMap Stores UrlIssue to newly created SnapshotIssue
     */
    public void SaveSnapshot(JiraApiProps api, String project, UrlIssue issue, String folder, Map<Issue, String> snapshotTicketsMap) {
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

        snapshotTicketsMap.put(issue, issueKey);

        for (File attachment : new File(artifacts).listFiles()) {
            JiraApi.createAttachment(api, issueKey, attachment);
        }
    }
}
