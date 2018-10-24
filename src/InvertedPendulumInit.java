import MemberShipFunctions.DeployMembershipFunction;
import MemberShipFunctions.TrapezoidalMembership;
import Utility.MembershipUtility;
import Utility.Point;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
public class InvertedPendulumInit {

    /*
        We assume the stick holding the pendulum to be negligible and the torgue resulting only due to the mass
        of the pendulum. All units in standard unit
        Mass - kg
        Length - metre
        Voltage - V
        Current - A
        RPs - rps
    */
    /* Constants for the electric motor and pendulum configs*/
    private static final double G = 9.8;
    private static final double PENDULUM_MASS = 1.0;
    private static final double INITIAL_THETA = 0.0;
    private static final double INITIAL_OMEGA = 0.0;
    private static final double EFFICIENCY = 1.0;
    private static final double VOLTAGE = 240.0;
    private static final double RPS = 20;

    private static final String FILE_NAME = "profile_coords.txt";

    public static final double constantOfProportionality = 1;
    public static double currentTheta = INITIAL_THETA;
    public static double currentAngularVelocity = INITIAL_OMEGA;
    public static double currentTorque = 0;
    private static void drawGraphUsingPython(){
        String[] callAndArgs= {"\"python\"","-u","\"get_profile_plot.py\""};
        try {
            Process p = Runtime.getRuntime().exec(callAndArgs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawProfile(DeployMembershipFunction profile) throws IOException{
        File f = new File(FILE_NAME);
        if(f.exists())
            f.delete();
        f.createNewFile();
        FileWriter fw = new FileWriter(FILE_NAME);
        BufferedWriter bw = new BufferedWriter(fw);
        for(Point point : profile.getTriangularMembership().getProfileCoordinates()){
            bw.write(point.toString() + "\n");
        }
        TrapezoidalMembership []tp = profile.getTrapezoidalMembership();
        for(TrapezoidalMembership tp_member : tp){
            for(Point point : tp_member.getProfileCoordinates()){
                bw.write(point.toString() + "\n");
            }
        }
        bw.close();
        fw.close();
        drawGraphUsingPython();
    }

    private static void takeProfileInput(Scanner sc, Point []triangleProfile, Point [][]trapezoidalProfile){
        String whichProfile = "Negative";
        System.out.println("Enter 3 Points for Triangular(Zero) Profile :: ");
        for(int i = 0 ; i < triangleProfile.length ; i++) {
            System.out.print("Enter Point " + i + " :: ");
            Point p = new Point(sc.nextDouble(), sc.nextDouble());
            triangleProfile[i] = p;
        }
        for(int i = 0 ; i < trapezoidalProfile.length; i ++){
            System.out.println("Enter 4 Points for Trapezoidal Profile( " + whichProfile +" ) :: ");
            for(int j = 0; j < trapezoidalProfile[0].length; j++ ){
                System.out.print("Enter Point " + j + " :: ");
                Point p = new Point(sc.nextDouble(), sc.nextDouble());
                trapezoidalProfile[i][j] = p;
            }
            whichProfile = "Positive";
        }
    }

    public static void main(String args[]){
        // 0.Take User Input
        Scanner sc = new Scanner(System.in);
        Point angleTraingleProfileInput[] = new Point[3];
        Point angleTrapezoidalProfileInput[][] = new Point[2][4];
        Point angularVelocityTraingleProfileInput[] = new Point[3];
        Point angularVelocityTrapezoidalProfileInput[][] = new Point[2][4];
        Point currentTraingleProfileInput[] = new Point[3];
        Point currentTrapezoidalProfileInput[][] = new Point[4][4];

        System.out.println("Enter Angle Profile Inputs :: ");
        takeProfileInput(sc, angleTraingleProfileInput, angleTrapezoidalProfileInput);
        takeProfileInput(sc, angularVelocityTraingleProfileInput, angularVelocityTrapezoidalProfileInput);
        takeProfileInput(sc, currentTraingleProfileInput, currentTrapezoidalProfileInput);
        // 1. Membership Functions . See Normalization if needed.
        DeployMembershipFunction angleProfile
                = new DeployMembershipFunction(MembershipUtility.linguisticIdentifier[0], 2);
        DeployMembershipFunction angularVelocityProfile
                = new DeployMembershipFunction(MembershipUtility.linguisticIdentifier[1], 2);
        DeployMembershipFunction currentProfile
                = new DeployMembershipFunction(MembershipUtility.linguisticIdentifier[2], 4);

        // Initialising Profiles
        angleProfile.initiateTriangularMembersipFunction(angleTraingleProfileInput);
        angleProfile.initiateTrapezoidalMembersipFunction(angleTrapezoidalProfileInput);

        angularVelocityProfile.initiateTriangularMembersipFunction(angularVelocityTraingleProfileInput);
        angularVelocityProfile.initiateTrapezoidalMembersipFunction(angularVelocityTrapezoidalProfileInput);

        currentProfile.initiateTriangularMembersipFunction(currentTraingleProfileInput);
        currentProfile.initiateTrapezoidalMembersipFunction(currentTrapezoidalProfileInput);

        //Draw Profile Graphs
        /*try{
            drawProfile(angleProfile);
            drawProfile(angularVelocityProfile);
            drawProfile(currentProfile);
        }catch (IOException exc){
            System.out.println("Error Drawing Profile........");
            exc.printStackTrace();
        }*/
        // Initialise GUI
        // Run GUI On this Thread and run fuzzification on a separate thread
        FuzzyThread fuzzyThread = new FuzzyThread(angleProfile, angularVelocityProfile, currentProfile);

        // Get Current value an compute new Values for the GUI then refresh GUI.
    }
}
