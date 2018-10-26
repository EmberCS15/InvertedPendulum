import MemberShipFunctions.DeployMembershipFunction;
import MemberShipFunctions.TrapezoidalMembership;
import Utility.Utility;
import Utility.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FuzzyController  {

    private DeployMembershipFunction angleProfile, angularVelocityProfile, currentProfile;
    private HashMap<String, String> controlChart;
    private int considerGravity;
    public static double currentActualValue = 0;

    public FuzzyController(DeployMembershipFunction angleProfile, DeployMembershipFunction angularVelocityProfile,
                           DeployMembershipFunction currentProfile, int considerGravity){
            this.angleProfile = angleProfile;
            this.angularVelocityProfile = angularVelocityProfile;
            this.currentProfile = currentProfile;
            this.considerGravity = considerGravity;
            initialiseControlChart();
    }

    public double calculateAngularAcceleration(double currTheta, double currVelocity) {
        HashMap<String, Double> thetaFuzzyOutput = getThetaFuzzyOutput(currTheta);
        HashMap<String, Double> angularVelocityFuzzyOutput = getAngularVelocityFuzzyOutput(currVelocity);
        ArrayList<ArrayList<Double>> currentActualOutput = new ArrayList<>();
        for(Map.Entry<String, Double> theta:thetaFuzzyOutput.entrySet()){
            for(Map.Entry<String, Double> omega : angularVelocityFuzzyOutput.entrySet()){
                currentActualOutput.add(defuzzifyCurrentValue(theta, omega));
            }
        }

        currentActualValue = centroidCurrentActualValue(currentActualOutput);
        //System.out.println("Current output :: " + currentActualValue);
        InvertedPendulumInit.currentTorque = InvertedPendulumInit.constantOfProportionality * currentActualValue;

        double momentOfInertia = Utility.getMomentOfInertia(InvertedPendulumInit.PENDULUM_MASS,
                InvertedPendulumInit.PENDULUM_STRING_LENGTH);

        double gravityTorque
                = (this.considerGravity * InvertedPendulumInit.PENDULUM_MASS * InvertedPendulumInit.G
                * Math.sin(Math.toRadians(currTheta)) * InvertedPendulumInit.PENDULUM_STRING_LENGTH);

        return ( InvertedPendulumInit.currentTorque + gravityTorque ) / momentOfInertia;
        //return InvertedPendulumInit.currentTorque / momentOfInertia;
    }

    private HashMap<String, Double> getThetaFuzzyOutput(double currTheta){
        HashMap<String, Double> thetaFuzzyValues = new HashMap<>();
        thetaFuzzyValues.put(angleProfile.getTriangularMembership().getMembershipId(),
                angleProfile.getTriangularMembership().getMembershipValue(currTheta));

        for(TrapezoidalMembership trapezoid : angleProfile.getTrapezoidalMembership()){
            thetaFuzzyValues.put(trapezoid.getMembershipId(), trapezoid.getMembershipValue(currTheta));
        }

        //System.out.println("Theta Values :: " + thetaFuzzyValues);
        return thetaFuzzyValues;
    }

    private HashMap<String, Double> getAngularVelocityFuzzyOutput(double currVelocity){
        HashMap<String, Double> angularVelocityFuzzyValues = new HashMap<>();
        angularVelocityFuzzyValues.put(angularVelocityProfile.getTriangularMembership().getMembershipId(),
                angularVelocityProfile.getTriangularMembership().getMembershipValue(currVelocity));

        for(TrapezoidalMembership trapezoid : angularVelocityProfile.getTrapezoidalMembership()){
            angularVelocityFuzzyValues.put(trapezoid.getMembershipId(), trapezoid.getMembershipValue(currVelocity));
        }

        //System.out.println("Omega Values :: " + angularVelocityFuzzyValues);
        return angularVelocityFuzzyValues;
    }

    private double computeCentoid(double []xValues){
        double numerator = (xValues[3]*xValues[3] + xValues[3]*xValues[2] + xValues[2]*xValues[2]) -
                (xValues[0]*xValues[0] + xValues[0] * xValues[1] + xValues[1] * xValues[1]);
        double denominator = 3*((xValues[3] + xValues[2]) - (xValues[0] + xValues[1]));
        return numerator / denominator;
    }

    private double computeArea(double []xValues, double yValue){
        return (yValue * ((xValues[3] - xValues[0]) + (xValues[2] - xValues[1]))) / 2;
    }

    private ArrayList<Double> defuzzifyCurrentValue(Map.Entry<String, Double> theta, Map.Entry<String, Double> omega){
        //System.out.println("Profiles :: " + theta.getKey() + " " +  omega.getKey());
        //System.out.println("Values :: " + theta.getValue() + " " +  omega.getValue());
        double currentFuzzyValue = Math.min(theta.getValue(), omega.getValue());
        String controlChartKey = theta.getKey() + "_" + omega.getKey();
        String selectCurrentProfile = controlChart.get(controlChartKey);
        //System.out.println("Current Fuzzy Value :: " + selectCurrentProfile + " " + currentFuzzyValue);
        double xValues[] = new double[4];
        ArrayList<Double> centroidAndArea = new ArrayList<>();
        if(selectCurrentProfile.equals(currentProfile.getTriangularMembership().getMembershipId())){
            Point []coords = currentProfile.getTriangularMembership().getProfileCoordinates();
            double intersection[] = currentProfile.getTriangularMembership().getQuantityValue(currentFuzzyValue);
            // See the return value is A, B, C
            // TO-DO :: See case of non-intersection
            xValues[0] = coords[0].getXcoord();
            xValues[3] = coords[1].getXcoord();
            xValues[1] = intersection[0];
            xValues[2] = intersection[1];
            Arrays.sort(xValues);
        }else{
            for(TrapezoidalMembership trapezoid : currentProfile.getTrapezoidalMembership()){
                if(selectCurrentProfile.equals(trapezoid.getMembershipId())){
                    Point []coords = trapezoid.getProfileCoordinates();
                    double intersection[] = trapezoid.getQuantityValue(currentFuzzyValue);
                    //See the return value is A, B, C, D
                    // TO-DO :: See case of non-intersection
                    xValues[0] = coords[0].getXcoord();
                    xValues[3] = coords[3].getXcoord();
                    xValues[1] = intersection[0];
                    xValues[2] = intersection[1];
                    Arrays.sort(xValues);
                    break;
                }
            }
        }
        centroidAndArea.add(computeCentoid(xValues));
        centroidAndArea.add(computeArea(xValues, currentFuzzyValue));
        return centroidAndArea;
    }

    private double centroidCurrentActualValue(ArrayList<ArrayList<Double>> currentActualOutput){
        double numerator = 0, denominator = 0;
        for(ArrayList<Double> centroidAndArea : currentActualOutput){
            numerator += (centroidAndArea.get(0) * centroidAndArea.get(1));
            denominator += centroidAndArea.get(1);
        }
        return numerator / denominator;
    }

    private void initialiseControlChart(){
        controlChart = new HashMap<String, String>();
        controlChart.put(Utility.profileIdentifier[2]+"_"+ Utility.profileIdentifier[2],
                Utility.profileIdentifier[4]);
        controlChart.put(Utility.profileIdentifier[2]+"_"+ Utility.profileIdentifier[0],
                Utility.profileIdentifier[3]);
        controlChart.put(Utility.profileIdentifier[2]+"_"+ Utility.profileIdentifier[3],
                Utility.profileIdentifier[0]);
        controlChart.put(Utility.profileIdentifier[0]+"_"+ Utility.profileIdentifier[2],
                Utility.profileIdentifier[3]);
        controlChart.put(Utility.profileIdentifier[0]+"_"+ Utility.profileIdentifier[0],
                Utility.profileIdentifier[0]);
        controlChart.put(Utility.profileIdentifier[0]+"_"+ Utility.profileIdentifier[3],
                Utility.profileIdentifier[2]);
        controlChart.put(Utility.profileIdentifier[3]+"_"+ Utility.profileIdentifier[2],
                Utility.profileIdentifier[0]);
        controlChart.put(Utility.profileIdentifier[3]+"_"+ Utility.profileIdentifier[0],
                Utility.profileIdentifier[2]);
        controlChart.put(Utility.profileIdentifier[3]+"_"+ Utility.profileIdentifier[3],
                Utility.profileIdentifier[1]);
    }
}
