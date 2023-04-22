package scrappy.web.instructions;

import scrappy.web.instructions.nodes.*;
import scrappy.web.instructions.parameters.CaptureType;
import scrappy.web.instructions.parameters.Selector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class InstructionParser {
    public static void require(Pattern p, String message, Scanner s) {
        if (s.hasNext(p)) {
            s.next();
        }
        fail(message, s);
    }

    public static void fail(String message, Scanner s) {
        StringBuilder msg = new StringBuilder(message + "\n   @ ...");
        for (int i = 0; i < 5 && s.hasNext(); i++) {
            msg.append(" ").append(s.next());
        }
        throw new RuntimeException(msg + "...");
    }

    public static final Pattern STMTPAT = Pattern.compile("//|Visit|Click|WaitFor|Screenshot|Capture");
    public static final Pattern COMMENTPAT = Pattern.compile("//");
    public static final Pattern VISITPAT = Pattern.compile("Visit");
    public static final Pattern CLICKPAT = Pattern.compile("Click");
    public static final Pattern WAITFORPAT = Pattern.compile("WaitFor");
    public static final Pattern SCREENSHOTPAT = Pattern.compile("Screenshot");
    public static final Pattern CAPTUREPAT = Pattern.compile("Capture");

    public static final Pattern HTMLPAT = Pattern.compile("HTML");
    public static final Pattern TEXTCONTENTPAT = Pattern.compile("TextContent");

    public static final Pattern OPENPAREN = Pattern.compile("\\(");
    public static final Pattern CLOSEPAREN = Pattern.compile("\\)");

    public static final Pattern WITH = Pattern.compile("with");
    public static final Pattern AS = Pattern.compile("as");
    public static final Pattern FROM = Pattern.compile("from");
    public static final Pattern TO = Pattern.compile("to");
    public static final Pattern FILE = Pattern.compile("file");
    public static final Pattern SELECTOR = Pattern.compile("selector");    public static final Pattern SEMICOLON = Pattern.compile(";");

    public static IInstructionNode parse(String instructions) {
        Scanner scanner = new Scanner(instructions).useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
        IInstructionNode node = parseProgram(scanner);
        scanner.close();
        return node;
    }

    public static IInstructionNode parseFile(String file) {
        Scanner scanner;
        try {
            scanner = new Scanner(new File(file)).useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        IInstructionNode node = parseProgram(scanner);
        scanner.close();
        return node;
    }

    public static IInstructionNode parseProgram(Scanner scanner) {
        List<IInstructionNode> instructions = new ArrayList<>();
        while (scanner.hasNext(STMTPAT)) {
            instructions.add(parseStmt(scanner));
        }
        return new ProgramNode(instructions);
    }

    public static IInstructionNode parseStmt(Scanner scanner) {
        IInstructionNode node = null;
        if (scanner.hasNext(COMMENTPAT)){
            node = parseComment(scanner);
        } else if (scanner.hasNext(VISITPAT)) {
            node = parseVisit(scanner);
        } else if (scanner.hasNext(CLICKPAT)) {
            node = parseClick(scanner);
        } else if (scanner.hasNext(WAITFORPAT)) {
            node = parseWaitFor(scanner);
        } else if (scanner.hasNext(SCREENSHOTPAT)) {
            node = parseScreenshot(scanner);
        } else if (scanner.hasNext(CAPTUREPAT)) {
            node = parseCapture(scanner);
        } else {
            fail("STMT not supported", scanner);
        }
        return node;
    }

    public static IInstructionNode parseComment(Scanner scanner) {
        require(COMMENTPAT, "'//' is required", scanner);
        while (!scanner.hasNext(SEMICOLON)) {
            scanner.next();
        }
        require(SEMICOLON, "';' is required", scanner);
        return new CommentNode();
    }

    public static IInstructionNode parseVisit(Scanner scanner) {
        require(VISITPAT, "'Visit' is required", scanner);
        String url = parseBracketString(scanner);
        require(SEMICOLON, "';' is required", scanner);
        return new VisitNode(url);
    }

    public static IInstructionNode parseClick(Scanner scanner) {
        require(CLICKPAT, "'Click' is required", scanner);
        Selector selector = parseSelector(scanner);
        require(SEMICOLON, "';' is required", scanner);
        return new ClickNode(selector);
    }

    public static IInstructionNode parseWaitFor(Scanner scanner) {
        require(WAITFORPAT, "'WaitFor' is required", scanner);
        Selector selector = parseSelector(scanner);
        require(SEMICOLON, "';' is required", scanner);
        return new WaitForNode(selector);
    }
    public static IInstructionNode parseScreenshot(Scanner scanner) {
        require(SCREENSHOTPAT, "'Screenshot' is required", scanner);
        Selector selector = parseSelector(scanner);
        require(AS, "'as' is required", scanner);
        require(FILE, "'file' is required", scanner);
        String file = parseBracketString(scanner);
        require(SEMICOLON, "';' is required", scanner);
        return new ScreenshotNode(selector, file);
    }

    public static IInstructionNode parseCapture(Scanner scanner) {
        require(CAPTUREPAT, "'Capture' is required", scanner);
        CaptureType type;
        if (scanner.hasNext(HTMLPAT)) {
            require(HTMLPAT, "'HTML' is required", scanner);
            type = CaptureType.HTML;
        } else if (scanner.hasNext(TEXTCONTENTPAT)) {
            require(TEXTCONTENTPAT, "'TextContent' is required", scanner);
            type = CaptureType.TEXTCONTENT;
        } else {
            require(HTMLPAT, "Capture type is not valid", scanner);
            return null;
        }
        require(FROM, "'from' is required", scanner);
        Selector selector = parseSelector(scanner);
        require(TO, "'to' is required", scanner);
        require(FILE, "'file' is required", scanner);
        String file = parseBracketString(scanner);
        require(SEMICOLON, "';' is required", scanner);
        return new CaptureNode(type, selector, file);
    }

    public static Selector parseSelector(Scanner scanner) {
        String description = parseBracketString(scanner);
        require(WITH, "'with' is required", scanner);
        require(SELECTOR, "'selector' is required", scanner);
        String selector = parseBracketString(scanner);
        return new Selector(description, selector);
    }

    public static String parseBracketString(Scanner scanner) {
        require(OPENPAREN, "'(' is required", scanner);
        List<String> description = new ArrayList<>();
        while (!scanner.hasNext(CLOSEPAREN)) {
            description.add(scanner.next());
        }
        require(CLOSEPAREN, "')' is required", scanner);
        return String.join(" ", description);
    }
}
