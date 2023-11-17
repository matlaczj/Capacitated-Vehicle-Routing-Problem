package aisd.proj2.proj.MapElements;

import aisd.proj2.proj.AlgorithmicMechanics.Point;

public class Hospital {
    private int idx;
    private String name;
    private double x;
    private double y;
    private int allBeds;
    private int freeBeds;
    private boolean isReal;

    public Hospital(int idx, String name, double x, double y, int allBeds, int freeBeds) {
        this.idx = idx;
        this.name = name;
        this.x = x;
        this.y = y;
        this.allBeds = allBeds;
        this.freeBeds = freeBeds;
        this.isReal = true;
    }

    public Hospital(int idx, double x, double y, boolean isReal) {
        this.idx = idx;
        this.name = "Fake Hospital" + String.valueOf(idx);
        this.x = x;
        this.y = y;
        this.allBeds = 0;
        this.freeBeds = 0;
        this.isReal = isReal;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getAllBeds() {
        return allBeds;
    }

    public void setAllBeds(int allBeds) {
        this.allBeds = allBeds;
    }

    public int getFreeBeds() {
        return freeBeds;
    }

    public void setFreeBeds(int freeBeds) {
        this.freeBeds = freeBeds;
    }

    @Override
    public String toString() {
        String spacing = " | ";
        return "[" + idx + spacing + name + spacing + x + spacing + y + spacing + allBeds + spacing + freeBeds + "]";
    }

    public boolean getIsReal() {
        return isReal;
    }

    public void setIsTrue() {
        this.isReal = true;
    }

    public void decrementFreeBeds() {
        freeBeds -= 1;
    }

    public Point getPoint() {
        return new Point(x, y);
    }
}
