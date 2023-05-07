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

/**
 * Parses instructions into an Instruction Node
 */
public class InstructionParser {
    private static void require(Pattern p, String message, Scanner s) {
        if (s.hasNext(p)) {
            s.next();
        } else {
            fail(message, s);
        }
    }

    private static void fail(String message, Scanner s) {
        StringBuilder msg = new StringBuilder(message + "\n   @ ...");
        for (int i = 0; i < 5 && s.hasNext(); i++) {
            msg.append(" ").append(s.next());
        }
        throw new RuntimeException(msg + "...");
    }

    private static final Pattern STMTPAT = Pattern.compile("//|Visit|Click|WaitFor|Screenshot|Capture");
    private static final Pattern COMMENTPAT = Pattern.compile("//");
    private static final Pattern VISITPAT = Pattern.compile("Visit");
    private static final Pattern CLICKPAT = Pattern.compile("Click");
    private static final Pattern WAITFORPAT = Pattern.compile("WaitFor");
    private static final Pattern SCREENSHOTPAT = Pattern.compile("Screenshot");
    private static final Pattern CAPTUREPAT = Pattern.compile("Capture");

    private static final Pattern HTMLPAT = Pattern.compile("HTML");
    private static final Pattern TEXTCONTENTPAT = Pattern.compile("TextContent");

    private static final Pattern OPENPAREN = Pattern.compile("\\(");
    private static final Pattern CLOSEPAREN = Pattern.compile("\\)");

    private static final Pattern WITH = Pattern.compile("with");
    private static final Pattern AS = Pattern.compile("as");
    private static final Pattern FROM = Pattern.compile("from");
    private static final Pattern TO = Pattern.compile("to");
    private static final Pattern FILE = Pattern.compile("file");
    private static final Pattern SELECTOR = Pattern.compile("selector");
    private static final Pattern SEMICOLON = Pattern.compile(";");

    /**
     * Parses the instruction to node
     * @param instructions instruction to parse
     * @return instruction node
     */
    public static IInstructionNode parse(String instructions) {
        Scanner scanner = new Scanner(instructions).useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
        IInstructionNode node = parseProgram(scanner);
        scanner.close();
        return node;
    }

    /**
     * Parses file to instruction node
     * @param file file with instruction to parse
     * @return instruction node
     */
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

    private static IInstructionNode parseProgram(Scanner scanner) {
        List<IInstructionNode> instructions = new ArrayList<>();
        while (scanner.hasNext(STMTPAT)) {
            instructions.add(parseStmt(scanner));
        }
        return new ProgramNode(instructions);
    }

    private static IInstructionNode parseStmt(Scanner scanner) {
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
        require(SEMICOLON, "';' is required", scanner);
        return node;
    }

    private static IInstructionNode parseComment(Scanner scanner) {
        require(COMMENTPAT, "'//' is required", scanner);
        while (!scanner.hasNext(SEMICOLON)) {
            scanner.next();
        }
        return new CommentNode();
    }

    private static IInstructionNode parseVisit(Scanner scanner) {
        require(VISITPAT, "'Visit' is required", scanner);
        String url = parseBracketString(scanner);
        return new VisitNode(url);
    }

    private static IInstructionNode parseClick(Scanner scanner) {
        require(CLICKPAT, "'Click' is required", scanner);
        Selector selector = parseSelector(scanner);
        return new ClickNode(selector);
    }

    private static IInstructionNode parseWaitFor(Scanner scanner) {
        require(WAITFORPAT, "'WaitFor' is required", scanner);
        Selector selector = parseSelector(scanner);
        return new WaitForNode(selector);
    }
    private static IInstructionNode parseScreenshot(Scanner scanner) {
        require(SCREENSHOTPAT, "'Screenshot' is required", scanner);
        Selector selector = parseSelector(scanner);
        require(AS, "'as' is required", scanner);
        require(FILE, "'file' is required", scanner);
        String file = parseBracketString(scanner);
        return new ScreenshotNode(selector, file);
    }

    private static IInstructionNode parseCapture(Scanner scanner) {
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
        return new CaptureNode(type, selector, file);
    }

    private static Selector parseSelector(Scanner scanner) {
        String description = parseBracketString(scanner);
        require(WITH, "'with' is required", scanner);
        require(SELECTOR, "'selector' is required", scanner);
        String selector = parseBracketString(scanner);
        return new Selector(description, selector);
    }

    private static String parseBracketString(Scanner scanner) {
        require(OPENPAREN, "'(' is required", scanner);
        List<String> description = new ArrayList<>();
        while (!scanner.hasNext(CLOSEPAREN)) {
            description.add(scanner.next());
        }
        require(CLOSEPAREN, "')' is required", scanner);
        return String.join(" ", description);
    }
}
