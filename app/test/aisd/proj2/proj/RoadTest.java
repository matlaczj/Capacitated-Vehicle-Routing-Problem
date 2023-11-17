package aisd.proj2.proj;

import aisd.proj2.proj.AlgorithmicMechanics.Point;
import aisd.proj2.proj.MapElements.Hospital;
import aisd.proj2.proj.MapElements.Road;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class RoadTest {

    @org.junit.Test
    public void doesIntersect() {
        ArrayList<Hospital> hospitals = new ArrayList<Hospital>();
        hospitals.add(new Hospital(0, "1", 0, 0, 0, 0));
        hospitals.add(new Hospital(1, "2", 10, 10, 0, 0));
        hospitals.add(new Hospital(2, "3", 4, 0, 0, 0));
        hospitals.add(new Hospital(3, "4", 5, 10, 0, 0));
        Road road1 = new Road(0, 0, 1, 100);
        Road road2 = new Road(1, 2, 3, 100);
        assertEquals(Point.class, road1.doesIntersect(hospitals, road2).getClass());
    }

    @Test
    public void does_intersect_with_pionowa_line() {
        ArrayList<Hospital> hospitals = new ArrayList<Hospital>();
        hospitals.add(new Hospital(0, "1", 0, 0, 0, 0));
        hospitals.add(new Hospital(1, "2", 0, 2, 0, 0));
        hospitals.add(new Hospital(2, "3", 4, 2, 0, 0));
        hospitals.add(new Hospital(3, "4", -4, 2, 0, 0));
        Road road1 = new Road(0, 0, 1, 100);
        Road road2 = new Road(1, 2, 3, 100);
        Point p = road1.doesIntersect(hospitals, road2);
        Point realPoint = new Point(0, 2);
        assertEquals(realPoint.getX(), p.getX());
        assertEquals(realPoint.getY(), p.getY());
    }

    @Test
    public void find_all_points() {
        ArrayList<Hospital> hospitals = new ArrayList<Hospital>();
        hospitals.add(new Hospital(0, "Szpital Wojewódzki nr 997", 10, 10, 1000, 100));
        hospitals.add(new Hospital(1, "Krakowski Szpital Kliniczny", 100.345, 120, 999, 99));
        hospitals.add(new Hospital(2, "Pierwszy Szpital im. Prezesa RP", 120, 130.2, 99, 0));
        hospitals.add(new Hospital(3, "Drugi Szpital im. Naczelnika RP", 10, 140, 70, 1));
        hospitals.add(new Hospital(4, "Trzeci Szpital im. Króla RP", 140, 10, 996, 0));

        ArrayList<Road> roads = new ArrayList<Road>();
        roads.add(new Road(1, 0, 1, 700));
        roads.add(new Road(2, 0, 3, 550));
        roads.add(new Road(3, 0, 4, 800));
        roads.add(new Road(4, 1, 2, 300));
        roads.add(new Road(5, 1, 3, 550));
        roads.add(new Road(6, 2, 4, 600));
        roads.add(new Road(7, 3, 4, 750));

        ArrayList<Point> points = Road.createAllCrossRoads(hospitals, roads);
        assertEquals(1, points.size());

    }

    @Test
    public void find_all_intersections(){
        ArrayList<Hospital> hospitals = new ArrayList<Hospital>();
        hospitals.add(new Hospital(0, "Szpital Wojewódzki nr 997", 0, 0, 1000, 100));
        hospitals.add(new Hospital(1, "Krakowski Szpital Kliniczny", 3, 3, 999, 99));
        hospitals.add(new Hospital(2, "Pierwszy Szpital im. Prezesa RP", -2, 3, 99, 0));
        hospitals.add(new Hospital(3, "Drugi Szpital im. Naczelnika RP", 1, 6, 70, 1));
        hospitals.add(new Hospital(4, "Trzeci Szpital im. Króla RP", 3, 0, 996, 0));
        hospitals.add(new Hospital(5, "Trzeci Szpital im. Króla RP", -2, 6, 996, 0));
        hospitals.add(new Hospital(6, "Trzeci Szpital im. Króla RP", 0, 2, 996, 0));
        hospitals.add(new Hospital(7, "Trzeci Szpital im. Króla RP", 0, 8, 996, 0));

        ArrayList<Road> roads = new ArrayList<Road>();
        roads.add(new Road(0, 0, 1, 900));
        roads.add(new Road(1, 2, 3, 900));
        roads.add(new Road(2, 4, 5, 900));
        roads.add(new Road(3, 6, 7, 900));

        Road.createAllCrossRoads(hospitals, roads);

        assertEquals(12, hospitals.size());
        assertEquals(12, roads.size());
    }

    @Test
    public void check_if_counts_distance(){
        ArrayList<Hospital> hospitals = new ArrayList<Hospital>();
        hospitals.add(new Hospital(0, "Szpital Wojewódzki nr 997", 0, 0, 1000, 100));
        hospitals.add(new Hospital(1, "Krakowski Szpital Kliniczny", 3, 3, 999, 99));
        hospitals.add(new Hospital(2, "Pierwszy Szpital im. Prezesa RP", 3, 0, 99, 0));
        hospitals.add(new Hospital(3, "Drugi Szpital im. Naczelnika RP", 0, 3, 70, 1));
        ArrayList<Road> roads = new ArrayList<Road>();
        roads.add(new Road(0, 0, 1, 900));
        roads.add(new Road(1, 2, 3, 900));
        Road.createAllCrossRoads(hospitals, roads);
        assertEquals(450.0, roads.get(1).getDist());
    }

    @Test
    public void check_if_T_shape_creates_3_roads_instead_of_4(){
        ArrayList<Hospital> hospitals = new ArrayList<Hospital>();
        hospitals.add(new Hospital(0, "Szpital Wojewódzki nr 997", 0, 0, 1000, 100));
        hospitals.add(new Hospital(1, "Krakowski Szpital Kliniczny", 0, 3, 999, 99));
        hospitals.add(new Hospital(2, "Pierwszy Szpital im. Prezesa RP", -3, 3, 99, 0));
        hospitals.add(new Hospital(3, "Drugi Szpital im. Naczelnika RP", 3, 3, 70, 1));
        ArrayList<Road> roads = new ArrayList<Road>();
        roads.add(new Road(0, 0, 1, 900));
        roads.add(new Road(1, 2, 3, 900));
        Road.createAllCrossRoads(hospitals, roads);

        assertEquals(3, roads.size());

    }
}