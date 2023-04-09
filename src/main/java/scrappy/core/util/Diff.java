package scrappy.core.util;

import java.util.*;

public class Diff {
    public enum ChangeType {
        NONE,
        INSERT,
        DELETE
    }

    public record Change(ChangeType type, Position oldPos, Position newPos) {
        public record Position(int x, int y) {
            public Position diagonal() {
                return new Position(x + 1, y + 1);
            }

            public Position across() {
                return new Position(x + 1, y);
            }

            public Position down() {
                return new Position(x, y + 1);
            }

            private boolean inDiffTable(List<String> oldText, List<String> newText) {
                return x() < oldText.size() && y() < newText.size();
            }
        }
    }

    public static List<Change> getDiff(List<String> oldText, List<String> newText) {
        Change.Position start = new Change.Position(0,0);
        Change.Position end = new Change.Position(oldText.size() - 1, newText.size() - 1);
        Set<Change.Position> visited = new HashSet<>();

        // init routes
        Queue<Deque<Change>> routes = new ArrayDeque<>();
        visited.add(start);
        for (Change change: getChanges(start, oldText, newText)) {
            Deque<Change> newRoute = new ArrayDeque<>();
            newRoute.add(change);
            visited.add(change.newPos());
            routes.add(newRoute);

            // found end
            if (change.newPos().equals(end)) {
                return newRoute.stream().toList();
            }
        }

        while (routes.isEmpty() == false) {
            Deque<Change> route = routes.poll();
            Change.Position current = route.peekLast().newPos();
            for (Change change: getChanges(current, oldText, newText)) {
                // check if visited before
                if (visited.contains(change.newPos())) {
                    continue;
                }

                Deque<Change> newRoute = new ArrayDeque<>(route);
                visited.add(change.newPos());
                newRoute.add(change);
                routes.add(newRoute);

                // found end
                if (change.newPos().equals(end)) {
                    return newRoute.stream().toList();
                }
            }
        }
        throw new RuntimeException("Diff Error");
    }

    private static List<Change> getChanges(Change.Position pos, List<String> oldText, List<String> newText) {
        // move diagonally
        Change.Position diagonal = pos;
        boolean searchDiagonal = true;
        while (searchDiagonal && oldText.get(diagonal.x()).equals(newText.get(diagonal.y()))) {
            Change.Position newDiagonal = diagonal.diagonal();
            if (newDiagonal.inDiffTable(oldText, newText)) {
                diagonal = newDiagonal;
            } else {
                searchDiagonal = false;
            }
        }

        // diagonal move has been done
        if (diagonal.equals(pos) == false) {
            return List.of(new Change(ChangeType.NONE, pos, diagonal));
        }

        // check across and down
        List<Change> changes = new ArrayList<>();
        Change.Position across = pos.across();
        Change.Position down = pos.down();

        if (across.inDiffTable(oldText, newText)) {
            changes.add(new Change(ChangeType.DELETE, pos, across));
        }
        if (down.inDiffTable(oldText, newText)) {
            changes.add(new Change(ChangeType.INSERT, pos, down));
        }
        return changes;
    }
}
