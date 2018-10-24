package MemberShipFunctions;

import Utility.Point;

public interface MembershipFunctionSkeleton {

    public Point[] getProfileCoordinates();
    /**
     *
     * @param x
     * @return
     */
    public double getMembershipValue(double x);

    /**
     *
     * @param y
     * @return
     */
    public double[] getQuantityValue(double y);
}
