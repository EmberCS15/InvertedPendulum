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

    public static double getUpdatedAngle(double time, double torque, double theta, double omega, double moi){
        double alpha = torque / moi;
        theta = theta + omega * time + 0.5 * alpha * time * time;
        return theta;
    }

    public static double getUpdatedAngularVelocity(double time, double torque, double theta, double omega, double moi){
        double alpha = torque / moi;
        omega = omega + alpha * time;
        return omega;
    }
}
