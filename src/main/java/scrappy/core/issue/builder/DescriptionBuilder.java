package scrappy.core.issue.builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Constructs Description to Atlassian Document Format
 */
public class DescriptionBuilder {

    private static final String deleteAdf;
    private static final String insertAdf;
    private static final String plaintextAdf;
    private static final String hardbreakAdf;
    private static final String paragraphAdf;
    private static final String tableCellAdf;
    private static final String tableRowAdf;
    private static final String tableAdf;

    static {
        try {
            Path deletePath = Paths.get("template/document/text/deletetext.json");
            Path insertPath = Paths.get("template/document/text/inserttext.json");
            Path plaintextPath = Paths.get("template/document/text/plaintext.json");
            Path hardbreakPath = Paths.get("template/document/text/hardbreak.json");
            Path paragraphPath = Paths.get("template/document/text/paragraph.json");
            Path tableCellPath = Paths.get("template/document/table/tableCell.json");
            Path tableRowPath = Paths.get("template/document/table/tableRow.json");
            Path tablePath = Paths.get("template/document/table/table.json");

            deleteAdf = new String(Files.readAllBytes(deletePath));
            insertAdf = new String(Files.readAllBytes(insertPath));
            plaintextAdf = new String(Files.readAllBytes(plaintextPath));
            hardbreakAdf = new String(Files.readAllBytes(hardbreakPath));
            paragraphAdf = new String(Files.readAllBytes(paragraphPath));
            tableCellAdf = new String(Files.readAllBytes(tableCellPath));
            tableRowAdf = new String(Files.readAllBytes(tableRowPath));
            tableAdf = new String(Files.readAllBytes(tablePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String createParagraphAdf(String content) {
        return String.format(paragraphAdf, content);
    }

    public static String createTableCellAdf(String content) {
        return String.format(tableCellAdf, content);
    }

    public static String createTableRowAdf(String content) {
        return String.format(tableRowAdf, content);
    }

    public static String createTableAdf(String content) {
        return String.format(tableAdf, content);
    }

    public static String createHardBreakAdf() {
        return hardbreakAdf;
    }

    public static String createDeleteTextAdf(String content) {
        if (content.isEmpty()) {
            return createHardBreakAdf();
        }
        return String.format(deleteAdf, content.replace("\"", "\\\""));
    }

    public static String createInsertTextAdf(String content) {
        if (content.isEmpty()) {
            return createHardBreakAdf();
        }
        return String.format(insertAdf, content.replace("\"", "\\\""));
    }

    public static String createPlainTextAdf(String content) {
        if (content.isEmpty()) {
            return createHardBreakAdf();
        }
        return String.format(plaintextAdf, content.replace("\"", "\\\""));
    }
}
