package scrappy.app;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.exceptions.UnirestException;
import scrappy.core.issue.builder.SnapshotIssueBuilder;
import scrappy.core.issue.types.Issue;
import scrappy.core.issue.types.IssueState;
import scrappy.core.issue.types.UrlIssue;
import scrappy.jira.JiraApi;
import scrappy.jira.JiraApiProps;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SnapshotSaver {
    private static final DateTimeFormatter TIMEFORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    public void SaveIssues(JiraApiProps api, String project, Issue issue, String location) {
        if (issue.getState() != IssueState.InUse) { return; }

        String nextLocation = location + "/" + issue.getKey();
        if (issue.hasSubIssues()) {
            for (Issue subIssue : issue) {
                SaveIssues(api, project, subIssue, nextLocation);
            }
        } else if(issue instanceof UrlIssue) {
            SaveSnapshot(api, project, (UrlIssue) issue, nextLocation);
        }
    }

    public void SaveSnapshot(JiraApiProps api, String project, UrlIssue issue, String folder) {
        String url = issue.getUrl();
        String time = TIMEFORMATTER.format(LocalDateTime.now());
        String summary = String.format("%s %s", time, url);

        String json = new SnapshotIssueBuilder()
            .setProject(project)
            .setSummary(summary)
            .setIssueLink(issue.getKey())
            .toString();
        try {
            JsonObject newIssue = JiraApi.createIssue(api, json);
            String issueKey = newIssue.get("key").getAsString();

            for (File attachment : new File(folder).listFiles()) {
                JiraApi.createAttachment(api, issueKey, attachment);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }
}
