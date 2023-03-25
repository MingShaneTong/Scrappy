// src/main/java/scrappy/web/core/App.java
package scrappy.app;

import scrappy.core.issuetypes.ExecutionIssue;
import scrappy.jira.JiraApi;
import scrappy.jira.JiraApiProps;

public class App {
    public static String location = "artifacts/";

    public static void main(String[] args) {
        String apiUrl = args[0];
        String login = args[1];
        String apiToken = args[2];
        String executionJira = args[3];
        JiraApiProps api = new JiraApiProps(apiUrl, login, apiToken);

        JiraApi jiraApi = new JiraApi();
        ExecutionIssue exe = jiraApi.getJiraExecutionTree(api, executionJira);
        System.out.println(exe.getKey());
        System.out.println(exe.getState());
//        ScrappyPage page = new ScrappyPage();
//        PageCollector collector = new PageCollector();
//        collector.CapturePages(page, exe, location);
    }
}