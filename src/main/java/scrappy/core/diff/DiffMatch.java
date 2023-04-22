package scrappy.core.diff;

import java.util.*;

public class DiffMatch {
    /**
     * Represents a type of operation on the text
     */
    public enum Operation {
        DELETE, INSERT, EQUAL
    }

    /**
     *
     */
    public enum DiffDelimiter {
        CHARACTER(""),
        WORD(" "),
        LINE("\n");

        private final String delimiter;

        DiffDelimiter(String delimiter) {
            this.delimiter = delimiter;
        }

        public String delimiter() {
            return this.delimiter;
        }
    }

    public record Diff(Operation operation, String text) { }

    public record Position(int oldTextIndex, int newTextIndex) {
        public Position across() {
            return new Position(oldTextIndex + 1, newTextIndex);
        }

        public Position down() {
            return new Position(oldTextIndex, newTextIndex + 1);
        }

        public Position diagonal() {
            return new Position(oldTextIndex + 1, newTextIndex + 1);
        }
    }

    public record Route(List<Diff> diffs, Position lastPos) {
        public boolean isComplete(Position end) {
            return lastPos.equals(end);
        }
    }

    private final DiffDelimiter delimiter;

    public DiffMatch(DiffDelimiter delimiter) {
        this.delimiter = delimiter;
    }

    public List<Diff> findDiffs(String text1, String text2) {
        String[] split1 = text1.split(delimiter.delimiter());
        String[] split2 = text2.split(delimiter.delimiter());
        return findDiffs(split1, split2);
    }

    public List<Diff> findDiffs(String[] text1, String[] text2) {
        Queue<Route> routeQueue = initialiseRoutes(text1, text2);
        Position endPos = new Position(text1.length, text2.length);

        // check if text are the same
        if (routeQueue.peek() != null && routeQueue.peek().isComplete(endPos)) {
            return routeQueue.peek().diffs();
        }

        // breadth first on each route
        while (!routeQueue.isEmpty()) {
            Route currentRoute = routeQueue.poll();
            Position lastPos = currentRoute.lastPos;

            // find possible positions
            List<Route> newRoutes = Map.of(
                    Operation.DELETE, lastPos.across(),
                    Operation.INSERT, lastPos.down()
                ).entrySet().stream()
                // is valid position
                .filter(entry -> isValidPosition(entry.getValue(), text1, text2))
                .map(entry -> {
                    // create new route and diagonalise
                    String text = "";
                    Operation operation = entry.getKey();
                    Position newPos = entry.getValue();
                    switch (operation) {
                        case DELETE:
                            text = getTextBetween(text1, lastPos.oldTextIndex(), newPos.oldTextIndex());
                            break;
                        case INSERT:
                            text = getTextBetween(text2, lastPos.newTextIndex(), newPos.newTextIndex());
                            break;
                    }
                    Diff diff = new Diff(operation, text);
                    List<Diff> diffs = new ArrayList<>(currentRoute.diffs);
                    diffs.add(diff);
                    Route route = new Route(diffs, newPos);
                    return diagonaliseRoute(route, text1, text2);
                }).toList();

            // check any route reach the end
            for (Route route: newRoutes) {
                routeQueue.add(route);
                if (route.lastPos.equals(endPos)) {
                    return summarise(route.diffs());
                }
            }
        }
        throw new RuntimeException("Diff Error");
    }

    private String getTextBetween(String[] text, int a, int b) {
        String[] textArray = Arrays.copyOfRange(text, a, b);
        return String.join(delimiter.delimiter(), textArray);
    }

    private Queue<Route> initialiseRoutes(String[] text1, String[] text2) {
        Queue<Route> routeQueue = new LinkedList<>();
        Position startPos = new Position(0, 0);
        Route route = new Route(new ArrayList<>(), startPos);
        routeQueue.add(diagonaliseRoute(route, text1, text2));
        return routeQueue;
    }

    private Route diagonaliseRoute(Route route, String[] text1, String[] text2) {
        Position lastPos = route.lastPos();
        Position diagonalised = diagonalisePosition(lastPos, text1, text2);

        if (lastPos.equals(diagonalised)) {
            return route;
        }

        String text = getTextBetween(text1, lastPos.oldTextIndex(), diagonalised.oldTextIndex());
        Diff diff = new Diff(Operation.EQUAL, text);
        List<Diff> diffs = new ArrayList<>(route.diffs);
        diffs.add(diff);
        return new Route(diffs, diagonalised);
    }

    private Position diagonalisePosition(Position pos, String[] text1, String[] text2) {
        String[] sm = stringInPosition(pos, text1, text2);
        if (sm[0] != null && sm[1] != null && Objects.equals(sm[0], sm[1])) {
            return diagonalisePosition(pos.diagonal(), text1, text2);
        }
        return pos;
    }

    private boolean isValidPosition(Position pos, String[] text1, String[] text2) {
        return pos.oldTextIndex <= text1.length && pos.newTextIndex <= text2.length;
    }

    private String[] stringInPosition(Position pos, String[] text1, String[] text2) {
        String s1 = pos.oldTextIndex < text1.length ? text1[pos.oldTextIndex] : null;
        String s2 = pos.newTextIndex < text2.length ? text2[pos.newTextIndex] : null;
        return new String[]{ s1, s2 };
    }

    public List<Diff> summarise(List<Diff> diffs) {
        List<Diff> summary = new ArrayList<>();
        List<Diff> deleteStream = new ArrayList<>();
        List<Diff> insertStream = new ArrayList<>();

        // create delete and insert stream
        for (Diff diff: diffs) {
            if (diff.text.isEmpty()) {
                continue;
            }

            switch (diff.operation()) {
                case DELETE:
                    deleteStream.add(diff);
                    break;
                case INSERT:
                    insertStream.add(diff);
                    break;
                case EQUAL:
                    summariseStreams(summary, deleteStream, insertStream);
                    summary.add(diff);
                    deleteStream.clear();
                    insertStream.clear();
                    break;
            }
        }
        summariseStreams(summary, deleteStream, insertStream);
        return summary;
    }

    private void summariseStreams(List<Diff> summary, List<Diff> deleteStream, List<Diff> insertStream) {
        if(!deleteStream.isEmpty()) {
            StringBuilder deleteText = new StringBuilder();
            for (Diff diff : deleteStream) {
                deleteText.append(diff.text());
            }
            summary.add(new Diff(Operation.DELETE, deleteText.toString()));
        }
        if(!insertStream.isEmpty()) {
            StringBuilder insertText = new StringBuilder();
            for (Diff diff : insertStream) {
                insertText.append(diff.text());
            }
            summary.add(new Diff(Operation.INSERT, insertText.toString()));
        }
    }
}
