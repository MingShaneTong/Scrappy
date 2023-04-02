package scrappy.web.instructions;

import scrappy.web.instructions.nodes.CommentNode;
import scrappy.web.instructions.nodes.IInstructionNode;
import scrappy.web.instructions.parameters.Selector;

import java.util.Scanner;
import java.util.regex.Pattern;

public class InstructionParser {
    public static String require(Pattern p, String message, Scanner s) {
        if (s.hasNext(p)) {
            return s.next();
        }
        fail(message, s);
        return null;
    }

    public static void fail(String message, Scanner s) {
        String msg = message + "\n   @ ...";
        for (int i = 0; i < 5 && s.hasNext(); i++) {
            msg += " " + s.next();
        }
        throw new RuntimeException(msg + "...");
    }

    public static final Pattern STMTPAT = Pattern.compile("//|Visit|Click|Wait|Screenshot|Capture|For Each");
    public static final Pattern COMMENTPAT = Pattern.compile("//");
    public static final Pattern VISITPAT = Pattern.compile("Visit");
    public static final Pattern CLICKPAT = Pattern.compile("Click");
    public static final Pattern WAITPAT = Pattern.compile("Wait");
    public static final Pattern SCREENSHOTPAT = Pattern.compile("Screenshot");
    public static final Pattern CAPTUREPAT = Pattern.compile("Capture");
    public static final Pattern FOREACHPAT = Pattern.compile("ForEach");

    public static final Pattern OPENPAREN = Pattern.compile("\\(");
    public static final Pattern CLOSEPAREN = Pattern.compile("\\)");

    public static final Pattern WITH = Pattern.compile("with");
    public static final Pattern SELECTOR = Pattern.compile("selector");
    public static final Pattern SEMICOLON = Pattern.compile(";");

    public static IInstructionNode parse(String instructions) {
        Scanner scanner = new Scanner(instructions).useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
        IInstructionNode node = parseProgram(scanner);
        scanner.close();
        return node;
    }

    public static IInstructionNode parseProgram(Scanner scanner) {
        while (scanner.hasNext(STMTPAT)) {
            parseStmt(scanner);
        }
        return null;
    }

    public static IInstructionNode parseStmt(Scanner scanner) {
        IInstructionNode node = null;
        if (scanner.hasNext(COMMENTPAT)){
            node = parseComment(scanner);
        } else if (scanner.hasNext(VISITPAT)) {
            node = parseVisit(scanner);
        } else if (scanner.hasNext(CLICKPAT)) {
            node = parseClick(scanner);
        } else if (scanner.hasNext(WAITPAT)) {

        } else if (scanner.hasNext(SCREENSHOTPAT)) {

        } else if (scanner.hasNext(CAPTUREPAT)) {

        } else if (scanner.hasNext(FOREACHPAT)) {

        } else {
            fail("STMT not supported", scanner);
        }
        return node;
    }

    public static IInstructionNode parseComment(Scanner scanner) {
        require(COMMENTPAT, "'//' is required", scanner);
        while (scanner.hasNext(SEMICOLON) == false) {
            scanner.next();
        }
        require(SEMICOLON, "';' is required", scanner);
        return new CommentNode();
    }

    public static IInstructionNode parseVisit(Scanner scanner) {
        require(VISITPAT, "'Visit' is required", scanner);
        require(OPENPAREN, "'(' is required", scanner);
        String description = scanner.next();
        require(CLOSEPAREN, "')' is required", scanner);
        require(SEMICOLON, "';' is required", scanner);
        return null;
    }

    public static IInstructionNode parseClick(Scanner scanner) {
        require(CLICKPAT, "'Click' is required", scanner);
        Selector selector = parseSelector(scanner);
        return null;
    }

    public static Selector parseSelector(Scanner scanner) {
        require(OPENPAREN, "'(' is required", scanner);
        String description = "";
        while (scanner.hasNext(CLOSEPAREN) == false) {
            description += scanner.next();
        }
        require(CLOSEPAREN, "')' is required", scanner);
        require(WITH, "'with' is required", scanner);
        require(SELECTOR, "'selector' is required", scanner);
        require(OPENPAREN, "'(' is required", scanner);
        String selector = "";
        while (scanner.hasNext(CLOSEPAREN) == false) {
            selector += scanner.next();
        }
        require(CLOSEPAREN, "')' is required", scanner);
        require(SEMICOLON, "';' is required", scanner);
        return new Selector(description, selector);
    }
}
