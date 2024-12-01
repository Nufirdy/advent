import org.locationtech.jts.algorithm.LineIntersector;
import org.locationtech.jts.algorithm.RobustLineIntersector;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.PrecisionModel;

import java.util.*;

public class Day24 {

    public static void main(String[] args) {
        List<String> lines = InputUtils.getFromResource("Day24-test1.txt");

//        part1(lines, 200000000000000L, 400000000000000L);
        part2(lines);
    }

    private static void part2(List<String> input) {
        List<Hailstone> hailstones = new ArrayList<>();
        for (String string : input) {
            String[] pointAndVelocity = string.split(" @ ");
            String[] pointSplit = pointAndVelocity[0].split(", ");
            String[] velSplit = pointAndVelocity[1].split(", ");

            long x = Long.parseLong(pointSplit[0].trim());
            long y = Long.parseLong(pointSplit[1].trim());
            long z = Long.parseLong(pointSplit[2].trim());

            long velX = Long.parseLong(velSplit[0].trim());
            long velY = Long.parseLong(velSplit[1].trim());
            long velZ = Long.parseLong(velSplit[2].trim());

            hailstones.add(new Hailstone(x, y, z, velX, velY, velZ));
        }

        int targetXVel = 1;
        while (true) {
            List<LineSegment> segments = new ArrayList<>();
            for (Hailstone hailstone : hailstones) {
                Coordinate start = new Coordinate(hailstone.x - (hailstone.xVel - targetXVel), -1);
                Coordinate end = new Coordinate(hailstone.x + ((hailstone.xVel - targetXVel) * 10), 10);
                LineSegment segment = new LineSegment(start, end);
                segments.add(segment);
            }

            Set<Long> timeValues = new HashSet<>();

            LineSegment segment1 = segments.get(0);
            LineSegment segment2 = segments.get(1);

            LineIntersector intersector = new RobustLineIntersector();
            intersector.setPrecisionModel(new PrecisionModel(PrecisionModel.FLOATING));
            intersector.computeIntersection(segment1.p0, segment1.p1, segment2.p0, segment2.p1);

            Coordinate intersection = intersector.getIntersection(0);
            long possibleIntersection = Math.round(intersection.getX());
            System.out.println("intersection : " + possibleIntersection);

            bruteforce:
            for (int i = 0; i < segments.size(); i++) {
                segment1 = segments.get(i);
                for (int j = i + 1; j < segments.size(); j++) {
                    segment2 = segments.get(j);

                    intersector = new RobustLineIntersector();
                    intersector.setPrecisionModel(new PrecisionModel(PrecisionModel.FLOATING));
                    intersector.computeIntersection(segment1.p0, segment1.p1, segment2.p0, segment2.p1);

                    if (intersector.getIntersectionNum() != 1) {
                        break bruteforce;
                    }
                    intersection = intersector.getIntersection(0);

                    long y = Math.round(intersection.getX());
                    if (possibleIntersection != y) {
                        break bruteforce;
                    }

                    long time = Math.round(intersection.getY());
                    if (timeValues.contains(time)) {
                        break bruteforce;
                    } else {
                        timeValues.add(time);
                    }
                }
            }

            if (timeValues.size() == hailstones.size()) {
                System.out.println("Target x vel: " + targetXVel);
                break;
            }

            if (targetXVel < 0) {
                targetXVel *= -1;
                targetXVel++;
            } else {
                targetXVel *= -1;
            }
        }
    }

    private static void part1(List<String> input, long lower, long upper) {

        List<LineSegment> segments = new ArrayList<>();
        for (String string : input) {
            String[] pointAndVelocity = string.split(" @ ");
            String[] pointSplit = pointAndVelocity[0].split(", ");
            String[] velSplit = pointAndVelocity[1].split(", ");

            long x = Long.parseLong(pointSplit[0].trim());
            long y = Long.parseLong(pointSplit[1].trim());

            long velX = Long.parseLong(velSplit[0].trim());
            long velY = Long.parseLong(velSplit[1].trim());

            Coordinate start = new Coordinate(x, y);
            Coordinate end = new Coordinate(x + (velX * 80000000000000L), y + (velY * 80000000000000L));

            LineSegment segment = new LineSegment(start, end);
            segments.add(segment);
        }

        List<Coordinate> intersections = new ArrayList<>();
        for (int i = 0; i < segments.size(); i++) {
            LineSegment segment1 = segments.get(i);
            for (int j = i + 1; j < segments.size(); j++) {
                LineSegment segment2 = segments.get(j);

                RobustLineIntersector intersector = new RobustLineIntersector();
                intersector.setPrecisionModel(new PrecisionModel());

                intersector.computeIntersection(segment1.p0, segment1.p1, segment2.p0, segment2.p1);

                if (intersector.getIntersectionNum() == 1) {
                    Coordinate intersection = intersector.getIntersection(0);
                    intersections.add(intersection);
                }
            }

        }

        int count = 0;
        for (Coordinate intersection : intersections) {
            if (intersection.getX() > lower && intersection.getX() < upper
                    && intersection.getY() > lower && intersection.getY() < upper) {
                count++;
            }
        }
        System.out.println(count);
    }

    static class Hailstone {
        long x;
        long y;
        long z;

        long xVel;
        long yVel;
        long zVel;

        public Hailstone(long x, long y, long z, long xVel, long yVel, long zVel) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.xVel = xVel;
            this.yVel = yVel;
            this.zVel = zVel;
        }

        public long xAt(int time) {
            return x + (xVel * time);
        }
    }
}
