package Utility;

public class Utility {

    public static final double membershipFunctionLowerLimit = 0;

    public static final double membershipFunctionUpperLimit = 1;

    public static final String []linguisticIdentifier = new String[]{
            "Angle",
            "Angular_Velocity",
            "Current"
    };

    public static final String []profileIdentifier = new String[]{
            "ZERO",
            "NEGATIVE_MEDIUM",
            "NEGATIVE_SMALL",
            "POSITIVE_SMALL",
            "POSITIVE_MEDIUM"
    };

    public static double computeSlope(Point a, Point b){
        double numerator = (b.getYcoord() - a.getYcoord());
        double denominator = (b.getXcoord() - a.getXcoord());
        return (numerator / denominator);
    }

    public static double getMomentOfInertia(double m, double r){
        return m * r * r;
    }

}
