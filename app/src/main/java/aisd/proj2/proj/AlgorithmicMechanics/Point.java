package aisd.proj2.proj.AlgorithmicMechanics;

public class Point implements Comparable<Point> {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "x= " + getX() + " y= " + getY() + "\n";
    }

    @Override
    public int compareTo(Point p2) {
        if (this.y - p2.getY() > 0) {
            return 1;
        } else if (this.y - p2.getY() < 0) {
            return -1;
        } else if (this.x - p2.getX() > 0) {
            return -1;
        } else {
            return 1;
        }
    }

    public static int ccw(Point a, Point b, Point c) {
        double area = (b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY()) * (c.getX() - a.getX());
        if (area < 0) {
            return -1;
        } else if (area > 0) {
            return 1;
        } else {
            return 0;
        }
    }

}
