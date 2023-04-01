// src/main/java/scrappy/web/core/App.java
package scrappy.app;

import com.mashape.unirest.http.exceptions.UnirestException;
import scrappy.core.issue.types.ExecutionIssue;
import scrappy.jira.JiraApiProps;
import scrappy.jira.JiraIssues;
import scrappy.web.ScrappyPage;

public class App {
    public static String location = "artifacts/";

    public static void main(String[] args) {
        String apiUrl = args[0];
        String project = args[1];
        String login = args[2];
        String apiToken = args[3];
        String executionJira = args[4];
        JiraApiProps api = new JiraApiProps(apiUrl, login, apiToken);

        ExecutionIssue exe;
        try {
            exe = JiraIssues.getExecution(api, executionJira);
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }

        ScrappyPage page = new ScrappyPage();
        PageCollector collector = new PageCollector();
        collector.CapturePages(page, exe, location);

        SnapshotSaver saver = new SnapshotSaver();
        saver.SaveIssues(api, project, exe, location);
    }
}