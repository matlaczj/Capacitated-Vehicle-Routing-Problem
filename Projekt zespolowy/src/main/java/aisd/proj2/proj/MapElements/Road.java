package aisd.proj2.proj.MapElements;

import aisd.proj2.proj.AlgorithmicMechanics.Point;

import java.util.ArrayList;
import java.util.Arrays;

public class Road {
    private int idx;
    private int firstHospIdx;
    private int secondHospIdx;
    private double dist;

    public Road(int idx, int firstHospIdx, int secondHospIdx, double dist) {
        this.idx = idx;
        this.firstHospIdx = firstHospIdx;
        this.secondHospIdx = secondHospIdx;
        this.dist = dist;
    }

    public static ArrayList<Point> createAllCrossRoads(ArrayList<Hospital> hospitals, ArrayList<Road> roads) {
        ArrayList<Point> points = new ArrayList<Point>();
        int maxid = hospitals.get(0).getIdx();
        for (int i = 1; i < hospitals.size(); i++) {
            if (hospitals.get(i).getIdx() > maxid) {
                maxid = hospitals.get(i).getIdx();
            }
        }
        int flag = 0;
        int n = roads.size();
        for (int i = 0; i < n - 1; i++) {
            flag = 0;
            for (int j = i + 1; j < n; j++) {
                if (roads.get(i).doesIntersect(hospitals, roads.get(j)) != null) {
                    Point p = roads.get(i).doesIntersect(hospitals, roads.get(j));
                    points.add(p);
                    maxid++;
                    Hospital fakeH = new Hospital(maxid, p.getX(), p.getY(), false);
                    roads.get(i).createRoadsFromIntersection(hospitals, roads, fakeH);
                    roads.get(j).createRoadsFromIntersection(hospitals, roads, fakeH);
                    hospitals.remove(hospitals.size() - 1);
                    Road r1 = roads.get(i);
                    Road r2 = roads.get(j);
                    roads.remove(r1);
                    roads.remove(r2);
                    i--;
                    j--;
                    flag = 1;
                    break;
                }
            }
            n = roads.size();
        }
        return points;
    }

    private void createRoadsFromIntersection(ArrayList<Hospital> hospitals, ArrayList<Road> roads, Hospital fakeH) {
        int maxId = roads.get(0).getIdx();
        for (int i = 1; i < roads.size(); i++) {
            if (roads.get(i).getIdx() > maxId) {
                maxId = roads.get(i).getIdx();
            }
        }
        maxId++;
        hospitals.add(fakeH);
        Hospital pA1 = hospitals.get(getFirstHospIdx());
        Hospital pA2 = hospitals.get(getSecondHospIdx());


        Road r1 = new Road(maxId, pA1.getIdx(), fakeH.getIdx(), findDistance(this, hospitals, fakeH, pA1));
        maxId++;
        Road r2 = new Road(maxId, fakeH.getIdx(), pA2.getIdx(), findDistance(this, hospitals, fakeH, pA2));
        if (findDistance(this, hospitals, fakeH, pA1) > 0) {
            roads.add(r1);
        }
        if (findDistance(this, hospitals, fakeH, pA2) > 0) {
            roads.add(r2);
        }
    }

    private static double findDistance(Road road, ArrayList<Hospital> hospitals, Hospital crossroad, Hospital b) {
        Hospital roadA = hospitals.get(road.getFirstHospIdx());
        Hospital roadB = hospitals.get(road.getSecondHospIdx());
        double roadEuc = calcEuclidean(roadA, roadB);
        double newRoadEuc = calcEuclidean(crossroad, b);
        return (newRoadEuc / roadEuc) * road.getDist();
    }

    private static double calcEuclidean(Hospital a, Hospital b) {
        return Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2));
    }


    public Point doesIntersect(ArrayList<Hospital> hospitals, Road road2) {
        Point p;
        Hospital pA1 = hospitals.get(firstHospIdx);
        Hospital pB1 = hospitals.get(secondHospIdx);
        Hospital pA2 = hospitals.get(road2.getFirstHospIdx());
        Hospital pB2 = hospitals.get(road2.getSecondHospIdx());
        double a1 = (pB1.getY() - pA1.getY()) / (pB1.getX() - pA1.getX());
        double a2 = (pB2.getY() - pA2.getY()) / (pB2.getX() - pA2.getX());
        double b1 = (pA1.getY() - a1 * pA1.getX());
        double b2 = (pA2.getY() - a2 * pA2.getX());

        double x1[] = new double[2];
        x1[0] = pB1.getX();
        x1[1] = pA1.getX();
        Arrays.sort(x1);

        double x2[] = new double[2];
        x2[0] = pB2.getX();
        x2[1] = pA2.getX();
        Arrays.sort(x2);

        double y1[] = new double[2];
        y1[0] = pB1.getY();
        y1[1] = pA1.getY();
        Arrays.sort(y1);

        double y2[] = new double[2];
        y2[0] = pB2.getY();
        y2[1] = pA2.getY();
        Arrays.sort(y2);

        if (a1 - a2 == 0) {
            return null;
        } else if (checkIfHasSameHospital(road2)) {
            return null;
        } else {
            if (pB1.getX() - pA1.getX() == 0) {
                double x = pB1.getX();
                if (isInRange(x, x2) && isInRange(x * a2 + b2, y1)) {
                    return new Point(x, x * a2 + b2);
                } else {
                    return null;
                }
            } else if (pB2.getX() - pA2.getX() == 0) {
                double x = pB2.getX();
                if (isInRange(x, x1) && isInRange(x * a1 + b1, y2)) {
                    return new Point(x, x * a1 + b1);
                } else {
                    return null;
                }
            } else {
                double x = (b2 - b1) / (a1 - a2);
                if (isInRange(x, x1) && isInRange(x, x2)) {
                    double y = x * a1 + b1;
                    return new Point(x, y);
                } else {
                    return null;
                }
            }
        }
    }

    private boolean checkIfHasSameHospital(Road road2) {
        double[] array = new double[]{getFirstHospIdx(), getSecondHospIdx(), road2.getFirstHospIdx(), road2.getSecondHospIdx()};
        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 4; j++) {
                if (array[i] == array[j]) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInRange(double x, double range[]) {
        if (x >= range[0] && x <= range[1]) {
            return true;
        }
        else {
            return false;
        }
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getFirstHospIdx() {
        return firstHospIdx;
    }

    public void setFirstHospIdx(int firstHospIdx) {
        this.firstHospIdx = firstHospIdx;
    }

    public int getSecondHospIdx() {
        return secondHospIdx;
    }

    public void setSecondHospIdx(int secondHospIdx) {
        this.secondHospIdx = secondHospIdx;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    @Override
    public String toString() {
        String spacing = " | ";
        return "[" + idx + spacing + firstHospIdx + spacing + secondHospIdx + spacing + dist + "]";
    }

}
