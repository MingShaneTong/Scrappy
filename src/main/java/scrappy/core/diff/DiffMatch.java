package scrappy.core.diff;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiffMatch {
    public enum Operation {
        DELETE, INSERT, EQUAL
    }

    public enum DiffSize {
        CHARACTER, WORD, LINE
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

    private final String delimiter;

    public DiffMatch(DiffSize size) {
        switch (size) {
            case CHARACTER:
                delimiter = "";
                break;
            case WORD:
                delimiter = " ";
                break;
            default:
                delimiter = "\n";
                break;
        }
    }

    public List<Diff> findDiffs(String text1, String text2) {
        String[] split1 = text1.split(delimiter);
        String[] split2 = text2.split(delimiter);
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
                }).collect(Collectors.toList());

            // check any route reach the end
            for (Route route: newRoutes) {
                routeQueue.add(route);
                if (route.lastPos.equals(endPos)) {
                    return route.diffs();
                }
            }
        }
        throw new RuntimeException("Diff Error");
    }

    private String getTextBetween(String[] text, int a, int b) {
        String[] textArray = Arrays.copyOfRange(text, a, b);
        return String.join(delimiter, textArray);
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
}
