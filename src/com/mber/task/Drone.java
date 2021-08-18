package com.mber.task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Drone {
    public static void main(String[] args) {
        int[][] photo = {
                {0, 4},
                {1, 3}};

        calculateDronePath(photo);
    }

    public static void calculateDronePath(int[][] map) {
        int numberOfElements = getNumberOfElements(map);
        Point[] points = new Point[numberOfElements];

        int pointCount = 0;
        points[pointCount++] = new Point(0, 0);

        Point markThisPoint = points[0];

        for (int k = 0; k < numberOfElements - 1; k++) {
            Point markedPoint = markThisPoint.marked();

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (isNearby(i, j, markedPoint)) {
                        int cost = getDifference(map[i][j], map[markedPoint.getI()][markedPoint.getJ()]) + 1;
                        int value = cost + markedPoint.getValue();
                        if (isExist(i, j, points)) {
                            Point point = getPoint(i, j, points);
                            if (point != null && point.isNotMarked() && value < point.getValue()) {
                                point.setParent(markedPoint);
                                point.setCost(cost);
                                point.setValue(value);
                            }
                        } else points[pointCount++] = new Point(i, j, markedPoint, cost, value);
                    }
                }
            }
            markThisPoint = getNumberUnmarkedWithMinimalValue(points);
        }
        Point lastPoint = getPoint(getLastIJ(map)[0], getLastIJ(map)[1], points);
        printRouteToFile(getRoute(points, lastPoint));
    }

    private static void printRouteToFile(Point[] route) {
        try {
            Files.write(Paths.get("src/com/mber/task/plan.txt"), getRouteText(route).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isExist(int i, int j, Point[] points) {
        for (Point point : points) if (point != null && point.getI() == i && point.getJ() == j) return true;
        return false;
    }

    private static boolean isNearby(int i, int j, Point point) {
        return     i + 1 == point.getI() && j == point.getJ()
                || i - 1 == point.getI() && j == point.getJ()
                || j + 1 == point.getJ() && i == point.getI()
                || j - 1 == point.getJ() && i == point.getI();
    }

    private static boolean isRoutePoint(Point point, Point parent) {
        return     point.getParent() != null
                && point.getParent().equals(parent)
                && point.getValue() - point.cost == parent.getValue();
    }

    private static int getDifference(int a, int b) {
        return a > b ? a - b : b - a;
    }

    private static int getNumberOfElements(int[][] map) {
        int count = 0;
        for (int[] ints : map) for (int j = 0; j < ints.length; j++) count++;
        return count;
    }

    private static int[] getLastIJ(int[][] map) {
        int[] lastIJ = new int[2];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                lastIJ[0] = i;
                lastIJ[1] = j;
            }
        }
        return lastIJ;
    }

    private static String getRouteText(Point[] route) {
        StringBuilder builder = new StringBuilder();
        int pointCount = 0;
        for (int i = route.length - 1; i >= 0; i--) {
            if (route[i] == null) continue;
            builder.append(route[i]).append("->");
            pointCount++;
        }
        return builder.delete(builder.length() - 2, builder.length())
                .append("\nsteps: ").append(pointCount - 1)
                .append("\nfuel: ").append(route[0] != null ? route[0].getValue() : 0).toString();
    }

    private static Point getNumberUnmarkedWithMinimalValue(Point[] points) {
        int minValue = Integer.MAX_VALUE;
        int minI = points.length;
        int minJ = points.length;

        for (Point point : points) {
            if (point != null && point.isNotMarked()) {
                if (point.getValue() < minValue) {
                    minI = point.getI();
                    minJ = point.getJ();
                    minValue = point.value;
                }
            }
        }
        return getPoint(minI, minJ, points);
    }

    private static Point getPoint(int i, int j, Point[] points) {
        for (Point point : points) if (point != null && point.getI() == i && point.getJ() == j) return point;
        return null;
    }

    private static Point[] getRoute(Point[] points, Point lastPoint) {
        Point[] route = new Point[points.length];
        Point startPoint = points[0];
        Point dynamicPoint = lastPoint;
        int index = 0;
        while (!dynamicPoint.equals(startPoint)) {
            route[index] = dynamicPoint;
            for (Point parentPoint : points)
                if (isRoutePoint(dynamicPoint, parentPoint)) {
                    dynamicPoint = parentPoint;
                    route[++index] = dynamicPoint;
                }
        }
        return route;
    }

    static class Point {
        private final int i;
        private final int j;
        private Point parent;
        private int cost;
        private int value;
        private boolean mark;

        public Point(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public Point(int i, int j, Point pointParent, int cost, int value) {
            this.i = i;
            this.j = j;
            this.cost = cost;
            this.value = value;
            this.parent = pointParent;
        }

        public Point marked() {
            mark = true;
            return this;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }

        public Point getParent() {
            return parent;
        }

        public void setParent(Point parent) {
            this.parent = parent;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public boolean isNotMarked() {
            return !this.mark;
        }

        @Override
        public String toString() {
            return "[" + i + "][" + j + "]";
        }
    }
}

