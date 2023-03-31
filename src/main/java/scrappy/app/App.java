// src/main/java/scrappy/web/core/App.java
package scrappy.app;

import scrappy.core.issue.types.ExecutionIssue;
import scrappy.jira.JiraApi;
import scrappy.jira.JiraApiProps;
import scrappy.jira.JiraIssues;
import scrappy.web.ScrappyPage;

import java.io.IOException;

public class App {
    public static String location = "artifacts/";

    public static void main(String[] args) throws IOException, InterruptedException {
        String apiUrl = args[0];
        String project = args[1];
        String login = args[2];
        String apiToken = args[3];
        String executionJira = args[4];
        JiraApiProps api = new JiraApiProps(apiUrl, login, apiToken);

        ExecutionIssue exe = null;
        try {
            exe = JiraIssues.getExecution(api, executionJira);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        SnapshotSaver saver = new SnapshotSaver();
        saver.SaveIssues(api, project, exe, location);
        /*

        ScrappyPage page = new ScrappyPage();
        PageCollector collector = new PageCollector();
        collector.CapturePages(page, exe, location);*/
    }
}