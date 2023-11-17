package aisd.proj2.proj.AlgorithmicMechanics;

import aisd.proj2.proj.MapElements.Hospital;
import aisd.proj2.proj.MapElements.Thing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class MapContourCreation {
    private ArrayList<Point> points;
    private ArrayList<Point> borders;

    public MapContourCreation(ArrayList<Point> points) {
        this.points = points;
        numeratePoints();
        this.borders = new ArrayList<Point>();
        createBorders();
    }

    public MapContourCreation(ArrayList<Hospital> hospitals, ArrayList<Thing> things) {
        points = createArrayOfPoints(hospitals, things);
        numeratePoints();
        this.borders = new ArrayList<Point>();
        createBorders();
    }

    private void createBorders() {
        Stack<Point> h = new Stack<Point>();
        h.push(points.get(0));
        h.push(points.get(1));
        for (int i = 2; i < points.size(); i++) {
            Point t = h.pop();
            while (Point.ccw(h.peek(), t, points.get(i)) <= 0) {
                if (h.size() != 1) {
                    t = h.pop();
                } else {
                    break;
                }
            }
            h.push(t);
            h.push(points.get(i));
        }
        Point l = h.pop();
        if (Point.ccw(l, h.peek(), points.get(0)) != 0) {
            h.push(l);
        }
        while (h.size() != 0) {
            borders.add(0, h.pop());
        }
    }

    private void numeratePoints() {
        int n = findMinY();
        Point tmp = points.remove(n);
        Collections.sort(points);
        Collections.sort(points, (a, b) -> {
            double cotanA = -(a.getX() - tmp.getX()) / (a.getY() - tmp.getY());
            double cotanB = -(b.getX() - tmp.getX()) / (b.getY() - tmp.getY());
            if (cotanA - cotanB < 0) {
                return -1;
            }
            return 1;
        });
        points.add(0, tmp);
    }

    private int findMinY() {
        int n = 0;
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).getY() < points.get(n).getY()) {
                n = i;
            }
        }
        return n;
    }

    private ArrayList<Point> createArrayOfPoints(ArrayList<Hospital> hospitals, ArrayList<Thing> things) {
        ArrayList<Point> points = new ArrayList<Point>();
        points.addAll(hospitalsToPoints(hospitals));
        points.addAll(thingsToPoints(things));
        return points;
    }

    private ArrayList<Point> hospitalsToPoints(ArrayList<Hospital> hospitals) {
        ArrayList<Point> points = new ArrayList<Point>();
        for (int i = 0; i < hospitals.size(); i++) {
            Hospital h = hospitals.get(i);
            points.add(new Point(h.getX(), h.getY()));
        }
        return points;
    }

    private ArrayList<Point> thingsToPoints(ArrayList<Thing> things) {
        ArrayList<Point> points = new ArrayList<Point>();
        for (int i = 0; i < things.size(); i++) {
            Thing t = things.get(i);
            points.add(new Point(t.getX(), t.getY()));
        }
        return points;
    }

    public void printPoints() {
        System.out.println("Punkty:");
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            System.out.print(p.toString());
        }
    }

    public void printBorders() {
        System.out.println("Punkty tworzące granicę:");
        for (int i = 0; i < borders.size(); i++) {
            Point p = borders.get(i);
            System.out.print(p.toString());
        }
    }

    public Point[] getBorders() {
        return borders.toArray(new Point[borders.size()]);
    }
}
