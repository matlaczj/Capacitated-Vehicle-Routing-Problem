package aisd.proj2.proj;

import aisd.proj2.proj.AlgorithmicMechanics.MapContourCreation;
import aisd.proj2.proj.AlgorithmicMechanics.Point;
import aisd.proj2.proj.MapElements.Hospital;
import aisd.proj2.proj.MapElements.Thing;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class MapContourCreationTest {

    @Test
    public void check_if_creates_points_properly() {
        ArrayList<Hospital> hospitals = new ArrayList<Hospital>();
        hospitals.add(new Hospital(0,"1", 0, 0, 0,0));
        hospitals.add(new Hospital(1,"2", 10, 10, 0,0));
        hospitals.add(new Hospital(2,"3", 4, 0, 0,0));
        hospitals.add(new Hospital(3,"4", 5, 10, 0,0));
        ArrayList<Thing> things = new ArrayList<Thing>();
        things.add(new Thing(0, "Plac Politechniki", 20, 30));
        things.add(new Thing(0, "PJATK", -20, 40));
        MapContourCreation map = new MapContourCreation(hospitals, things);
        assertEquals(5, map.getBorders().length);
    }

    @Test
    public void check_if_creates_points_properly_2(){
        ArrayList<Point> points = new ArrayList<Point>();
        points.add(new Point(0, 0));
        points.add(new Point(10, 0));
        points.add(new Point(0, 10));
        points.add(new Point(10, 10));
        points.add(new Point(0, 2));
        points.add(new Point(0, 3));
        points.add(new Point(0, 4));
        points.add(new Point(5, 10));
        points.add(new Point(5, 5));
        points.add(new Point(2, 0));
        MapContourCreation map2 = new MapContourCreation(points);
        assertEquals(4, map2.getBorders().length);
    }
}