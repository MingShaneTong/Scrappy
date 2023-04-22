package scrappy.core.issue.builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Creates a description for a summary issue
 */
public class SummaryDescriptionBuilder {
    private static final String diffFoundAdf;
    private static final String inlineCardAdf;
    private static final String listItemAdf;
    private static final String bulletListAdf;
    private static final String paragraphAdf;

    static {
        try {
            Path diffFoundPath = Paths.get("template/document/text/diffFound.json");
            Path inlineCardPath = Paths.get("template/document/text/inlineCard.json");
            Path listItemPath = Paths.get("template/document/list/listItem.json");
            Path bulletListPath = Paths.get("template/document/list/bulletList.json");
            Path paragraphPath = Paths.get("template/document/text/paragraph.json");

            diffFoundAdf = new String(Files.readAllBytes(diffFoundPath));
            inlineCardAdf = new String(Files.readAllBytes(inlineCardPath));
            listItemAdf = new String(Files.readAllBytes(listItemPath));
            bulletListAdf = new String(Files.readAllBytes(bulletListPath));
            paragraphAdf = new String(Files.readAllBytes(paragraphPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String createParagraphAdf(String content) {
        return String.format(paragraphAdf, content);
    }

    public static String createDiffFoundAdf() {
        return diffFoundAdf;
    }

    public static String createInlineCardAdf(String content) {
        return String.format(inlineCardAdf, content.replace("\"", "\\\""));
    }

    public static String createListItemAdf(String content) {
        return String.format(listItemAdf, content);
    }

    public static String createBulletListAdf(String content) {
        return String.format(bulletListAdf, content);
    }
}
