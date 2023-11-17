package aisd.proj2.proj.AlgorithmicMechanics;

public class CitizenshipCheck {

    public static boolean isPointInside(Point polygon[], Point p) {
        int n = polygon.length;

        if (n <= 2) {
            return false;
        }

        Point infinityPoint = new Point(Integer.MAX_VALUE, p.getY());

        int cnt = 0, i = 0;
        do {
            int next = (i + 1) % n;

            if (doTheyIntersect(polygon[i], polygon[next], p, infinityPoint)) {
                if (findTripletOrientation(polygon[i], p, polygon[next]) == 0) {
                    return checkIfQIsOnSegment(polygon[i], p, polygon[next]);
                }

                cnt++;
            }

            if (checkIfQIsOnSegment(p, polygon[i], infinityPoint)) {
                cnt++;
            }

            i = next;
        } while (i != 0);

        boolean isPointInside = cnt % 2 == 1;
        return isPointInside;
    }

    private static boolean checkIfQIsOnSegment(Point p, Point q, Point r) {
        if (q.getX() <= Math.max(p.getX(), r.getX()) && q.getX() >= Math.min(p.getX(), r.getX()) &&
                q.getY() <= Math.max(p.getY(), r.getY()) && q.getY() >= Math.min(p.getY(), r.getY())) {
            return true;
        }
        return false;
    }

    private static int findTripletOrientation(Point p, Point q, Point r) {
        double orientation = (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY());
        if (orientation == 0) {
            return 0;
        } else if (orientation > 0) {
            return 1;
        } else {
            return 2;
        }
    }

    private static boolean doTheyIntersect(Point p1, Point q1, Point p2, Point q2) {
        int o1 = findTripletOrientation(p1, q1, p2);
        int o2 = findTripletOrientation(p1, q1, q2);
        int o3 = findTripletOrientation(p2, q2, p1);
        int o4 = findTripletOrientation(p2, q2, q1);

        if (o1 != o2 && o3 != o4) {
            return true;
        }
        if (o1 == 0 && checkIfQIsOnSegment(p1, p2, q1)) {
            return true;
        }
        if (o2 == 0 && checkIfQIsOnSegment(p1, q2, q1)) {
            return true;
        }
        if (o3 == 0 && checkIfQIsOnSegment(p2, p1, q2)) {
            return true;
        }
        if (o4 == 0 && checkIfQIsOnSegment(p2, q1, q2)) {
            return true;
        }
        return false;
    }

}
