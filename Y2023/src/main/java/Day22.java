import java.util.*;
import java.util.stream.Collectors;

public class Day22 {
    static int totalToFall = 0;

    public static void main(String[] args) {
        List<String> lines = InputUtils.getFromResource("Day22.txt");

        part1(lines);
        part2(lines);
    }

    private static void part2(List<String> lines) {
        Tower tower = new Tower(lines);
        totalToFall = 0;
        tower.countRemovable();
        System.out.println(totalToFall);
    }

    private static void part1(List<String> lines) {
        Tower tower = new Tower(lines);
        System.out.println(tower.countRemovable());
    }

    static class Tower {
        TowerState towerState;

        public Tower(List<String> lines) {
            towerState = new TowerState();
            for (String line : lines) {
                Brick brick = new Brick();
                String[] coords = line.split("~");
                int x1, x2;
                int y1, y2;
                int z1, z2;

                String[] xyz1 = coords[0].split(",");
                String[] xyz2 = coords[1].split(",");

                x1 = Integer.parseInt(xyz1[0]);
                y1 = Integer.parseInt(xyz1[1]);
                z1 = Integer.parseInt(xyz1[2]);

                x2 = Integer.parseInt(xyz2[0]);
                y2 = Integer.parseInt(xyz2[1]);
                z2 = Integer.parseInt(xyz2[2]);

                int xLength = Math.abs(x1 - x2);
                int yLength = Math.abs(y1 - y2);
                int zLength = Math.abs(z1 - z2);

                Set<Position> brickPositions = new HashSet<>();
                if (xLength > 0) {
                    for (int i = 0; i < xLength + 1; i++) {
                        Position position = new Position(x1 + i, y1, z1);
                        brickPositions.add(position);
                    }
                } else if (yLength > 0) {
                    for (int i = 0; i < yLength + 1; i++) {
                        Position position = new Position(x1, y1 + i, z1);
                        brickPositions.add(position);
                    }
                } else if (zLength > 0) {
                    for (int i = 0; i < zLength + 1; i++) {
                        Position position = new Position(x1, y1, z1 + i);
                        brickPositions.add(position);
                    }
                } else {
                    brickPositions.add(new Position(x1, y1, z1));
                }
                towerState.addBrick(brick, brickPositions);
            }

            stabilizeTower();
        }

        public void stabilizeTower() {
            while (!towerState.allBricksAreStable()) {
                towerState =  towerState.dropBricksByOne();
            }
        }

        public int countRemovable() {
            int count = 0;
            for (Brick brick : towerState.bricksPositions.keySet()) {
                if (towerState.isRemovable(brick)) {
                    count++;
                }
            }

            return count;
        }

        public int countSumTotalToFall() {
            return towerState.countSumTotalToFall();
        }
    }

    static class TowerState {
        Map<Position, Brick> bricks;
        Map<Brick, Set<Position>> bricksPositions;

        public TowerState() {
            bricks = new HashMap<>();
            bricksPositions = new HashMap<>();
        }

        public TowerState(Map<Brick, Set<Position>> bricksPositions) {
            this.bricksPositions = bricksPositions;
            bricks = new HashMap<>();
            for (Map.Entry<Brick, Set<Position>> entry : bricksPositions.entrySet()) {
                for (Position position : entry.getValue()) {
                    bricks.put(position, entry.getKey());
                }
            }
        }

        public boolean allBricksAreStable() {
            return bricksPositions.keySet()
                    .stream()
                    .allMatch(brick -> brick.isStable);
        }

        public TowerState dropBricksByOne() {
            TowerState droppedTower = new TowerState();
            for (Map.Entry<Brick, Set<Position>> entry : bricksPositions.entrySet()) {
                updateBrickPosition(droppedTower, entry.getKey(), entry.getValue());
            }

            return droppedTower;
        }

        public void updateBrickPosition(TowerState towerState, Brick brick, Set<Position> brickPositions) {
            if (towerState.bricksPositions.containsKey(brick)) {
                return;
            }

            boolean shouldBeStable = true;
            boolean hasStableUnderneath = false;
            boolean onGround = false;
            if (!brick.isStable) {
                for (Position pos : brickPositions) {
                    if (pos.z == 1) {
                        onGround = true;
                        break;
                    }

                    Position lowerByOne = pos.oneLower();
                    Brick lowerBrick = bricks.get(lowerByOne);
                    if (lowerBrick != null
                            && !lowerBrick.equals(brick)
                            && !towerState.bricksPositions.containsKey(lowerBrick)) {
                        updateBrickPosition(towerState, lowerBrick, bricksPositions.get(lowerBrick));
                    }
                    lowerBrick = towerState.bricks.get(lowerByOne);
                    if (lowerBrick != null
                            && !lowerBrick.equals(brick)
                            && lowerBrick.isStable) {
                        hasStableUnderneath = true;
                    }
                }

                shouldBeStable = onGround || hasStableUnderneath;
                if (shouldBeStable) {
                    brick = new Brick(brick);
                    brick.isStable = true;
                }
            }

            Set<Position> newPositions = new HashSet<>();
            for (Position position : brickPositions) {
                if (!brick.isStable) {
                    position = position.oneLower();
                }
                newPositions.add(position);
            }
            towerState.addBrick(brick, newPositions);
        }

        public void addBrick(Brick brick, Set<Position> positions) {
            bricksPositions.put(brick, positions);
            for (Position position : positions) {
                bricks.put(position, brick);
            }
        }

        public boolean isRemovable(Brick brick) {
            Map<Brick, Set<Position>> removedBrick = bricksPositions.entrySet()
                    .stream()
                    .filter(brickListEntry -> !brickListEntry.getKey().equals(brick))
                    .collect(Collectors.toMap(brickListEntry -> {
                                Brick brick1 = new Brick(brickListEntry.getKey());
                                brick1.isStable = false;
                                return brick1;
                            },
                            Map.Entry::getValue));

            TowerState stateAfterRemoval = new TowerState(removedBrick);
            stateAfterRemoval = stateAfterRemoval.dropBricksByOne();

            return haveSamePositions(this, stateAfterRemoval);
        }

        private static boolean haveSamePositions(TowerState towerState, TowerState withRemovedBrick) {
            boolean haveSamePositions = true;
            for (Map.Entry<Brick, Set<Position>> bricks :
                    towerState.bricksPositions.entrySet()) {
                Set<Position> fromReducedTower = withRemovedBrick.bricksPositions.get(bricks.getKey());
                if (fromReducedTower != null && !bricks.getValue().containsAll(fromReducedTower)) {
                    haveSamePositions = false;
                    totalToFall++;
                }
            }

            return haveSamePositions;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TowerState that)) return false;
            return Objects.equals(bricks, that.bricks) && Objects.equals(bricksPositions,
                    that.bricksPositions);
        }

        @Override
        public int hashCode() {
            return Objects.hash(bricks, bricksPositions);
        }

        public int countSumTotalToFall() {
            return 0;
        }
    }

    static class Brick {
        private static int idCounter = 0;

        String id;
        boolean isStable;
        Set<Brick> laysOn;

        public Brick() {
            id = String.valueOf(idCounter);
            idCounter++;
        }

        public Brick(String id) {
            this.id = id;
        }

        public Brick(Brick brick) {
            id = brick.id;
            isStable = brick.isStable;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Brick brick)) return false;
            return id.equals(brick.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "Brick{" +
                    "id='" + id + '\'' +
                    ", isStable=" + isStable +
                    '}';
        }
    }

    static class Position {
        int x;
        int y;
        int z;

        public Position(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Position oneLower() {
            return new Position(x, y, z - 1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Position position)) return false;
            return x == position.x && y == position.y && z == position.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        @Override
        public String toString() {
            return "Position{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }
}
