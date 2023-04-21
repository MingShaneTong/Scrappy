package scrappy.app.steps;

import org.json.JSONException;
import org.json.JSONObject;
import scrappy.core.issue.builder.SummaryDescriptionBuilder;
import scrappy.core.issue.builder.SummaryIssueBuilder;
import scrappy.core.issue.types.Issue;
import scrappy.core.issue.types.IssueState;
import scrappy.core.issue.types.UrlIssue;
import scrappy.jira.JiraApi;
import scrappy.jira.JiraApiProps;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SummarySaver {
    private static final DateTimeFormatter TIMEFORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    public String createSummary(JiraApiProps apiProps, String project, Issue exe, Map<Issue, Boolean> diffMap, Map<Issue, String> snapshotTicketsMap) {
        String time = TIMEFORMATTER.format(LocalDateTime.now());
        String summary = String.format("%s %s", time, exe.getSummary());
        String description = SummaryDescriptionBuilder.createBulletListAdf(
            createSummaryDescription(apiProps.apiUrl().browseUrl(), exe, diffMap, snapshotTicketsMap)
        );
        String summaryJson = new SummaryIssueBuilder()
            .setProject(project)
            .setSummary(summary)
            .setIssueLink(exe.getKey())
            .setDescription(description)
            .toString();

        try {
            JSONObject newIssue = JiraApi.createIssue(apiProps, summaryJson);
            return newIssue.getString("key");
        } catch (JSONException e) {
            System.out.println("Summary Creation failed for " + exe.getKey());
            System.out.println(summaryJson);
            return "";
        }
    }

    public String createSummaryDescription(String browseUrl, Issue issue, Map<Issue, Boolean> diffMap, Map<Issue, String> snapshotTicketsMap) {
        if (issue.getState() != IssueState.InUse) { return ""; }

        if (issue.hasSubIssues()) {
            List<String> items = new ArrayList<>();
            for (Issue subIssue: issue) {
                items.add(createSummaryDescription(browseUrl, subIssue, diffMap, snapshotTicketsMap));
            }
            String sublist = SummaryDescriptionBuilder.createBulletListAdf(String.join(",", items));
            String label = createLinkItemParagraph(browseUrl + issue.getKey(), false);
            return SummaryDescriptionBuilder.createListItemAdf(label + "," + sublist);
        } else if (issue instanceof UrlIssue) {
            String snapshotIssue = snapshotTicketsMap.get(issue);
            if(snapshotIssue == null) {
                return "";
            }
            boolean diffIncluded = diffMap.get(issue);
            String link = createLinkItemParagraph(browseUrl + snapshotIssue, diffIncluded);
            return SummaryDescriptionBuilder.createListItemAdf(link);
        }
        return "";
    }

    private String createLinkItemParagraph(String url, boolean includeDiff) {
        String diff = includeDiff ? SummaryDescriptionBuilder.createDiffFoundAdf() + "," : "";
        String inlineCard = SummaryDescriptionBuilder.createInlineCardAdf(url);
        return SummaryDescriptionBuilder.createParagraphAdf(diff + inlineCard);
    }
}
