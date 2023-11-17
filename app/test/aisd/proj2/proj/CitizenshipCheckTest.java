package aisd.proj2.proj;

import aisd.proj2.proj.AlgorithmicMechanics.CitizenshipCheck;
import aisd.proj2.proj.AlgorithmicMechanics.Point;
import org.junit.Assert;
import org.junit.Test;

public class CitizenshipCheckTest {

    @Test
    public void allPositiveXYPolygon(){
        Point[] polygon = {
                new Point(3, 7),
                new Point(6, 6),
                new Point(6, 3),
                new Point(2, 2)
        };

        Point p = new Point(4, 6);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(4, 3);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(5, 4);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(6, 5);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(100, 100);
        Assert.assertFalse(CitizenshipCheck.isPointInside(polygon, p));
    }

    @Test
    public void allNegativeXYPolygon(){
        Point[] polygon = {
                new Point(-3, -7),
                new Point(-6, -6),
                new Point(-6, -3),
                new Point(-2, -2)
        };

        Point p = new Point(-4, -6);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(-4, -3);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(-5, -4);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(-6, -5);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(-100, -100);
        Assert.assertFalse(CitizenshipCheck.isPointInside(polygon, p));
    }

    @Test
    public void mixedXYPolygon(){
        Point[] polygon = {
                new Point(-3, 1),
                new Point(-2, 4),
                new Point(0, 6),
                new Point(3, 4),
                new Point(1, 1)
        };

        Point p = new Point(-1, 1);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(-1, 4);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(0, 6);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(3, 4);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(-100, 100);
        Assert.assertFalse(CitizenshipCheck.isPointInside(polygon, p));
    }

    @Test
    public void doubleXYPolygon(){
        Point[] polygon = {
                new Point(3.1, 7.1),
                new Point(6.2, 6.3),
                new Point(6.1, 3.2),
                new Point(2.2, 2.5)
        };

        Point p = new Point(4.3, 6.2);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(4, 3);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(5, 4);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(6.1, 5);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(100.111, 100);
        Assert.assertFalse(CitizenshipCheck.isPointInside(polygon, p));
    }

    @Test
    public void doublePoints(){
        Point[] polygon = {
                new Point(1, 1),
                new Point(4, 1),
                new Point(4, 4)
        };

        Point p = new Point(2.5, 2.5);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(2.512123, 2.512123);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));

        p = new Point(2.512123, 1);
        Assert.assertTrue(CitizenshipCheck.isPointInside(polygon, p));
    }
}