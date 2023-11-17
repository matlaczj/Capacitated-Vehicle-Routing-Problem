package aisd.proj2.proj.MapElements;

import aisd.proj2.proj.AlgorithmicMechanics.Point;

import java.util.ArrayList;

public class Patient {
    private int idx;
    private double x;
    private double y;

    public Patient(int idx, double x, double y) {
        this.idx = idx;
        this.x = x;
        this.y = y;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        String spacing = " | ";
        return "[" + idx + spacing + x + spacing + y + "]";
    }

    public Hospital findNearestHospital(ArrayList<Hospital> hospitals, int firstIdxOfShadowHosp) {
        double minEuc = Double.MAX_VALUE;
        double currEuc;
        Hospital nearestHosp = null;

        for (int i = 0; i < hospitals.size() && i < firstIdxOfShadowHosp; i++) {
            currEuc = calculateEuclidean(x, y, hospitals.get(i).getX(), hospitals.get(i).getY());
            if (currEuc <= minEuc && hospitals.get(i).getIsReal()) {
                minEuc = currEuc;
                nearestHosp = hospitals.get(i);
            }
        }

        return nearestHosp;
    }

    private double calculateEuclidean(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public Point getPoint() {
        return new Point(this.x, this.y);
    }
}
