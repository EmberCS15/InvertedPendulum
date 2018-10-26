import MemberShipFunctions.DeployMembershipFunction;
import MemberShipFunctions.TrapezoidalMembership;
import Utility.Point;
import Utility.Utility;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class InvertedPendulumInit extends Application{

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
    private static final String FILE_NAME = "profile_coords";

    public static final double constantOfProportionality = 1;
    public static double currentTheta = 0;
    public static double currentAngularVelocity = 0;
    public static double currentTorque = 0;
    public static final double PENDULUM_MASS = 1.0;
    public static final double PENDULUM_STRING_LENGTH = 3.0;
    public static final double G = 9.8;
    public static FuzzyController fc;

    private static void drawGraphUsingPython(){
        String[] callAndArgs= {"\"python\"","-u","\"get_profile_plot.py\""};
        try {
            Process p = Runtime.getRuntime().exec(callAndArgs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawProfile(DeployMembershipFunction profile, int itr) throws IOException{
        File f = new File(FILE_NAME+ "_" + itr + ".txt");
        if(f.exists())
            f.delete();
        f.createNewFile();
        FileWriter fw = new FileWriter(f.getName());
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

        System.out.println("Do we consider Gravity ? (0 / 1)");
        int considerGravity = sc.nextInt();

        // 1. Membership Functions . See Normalization if needed.
        DeployMembershipFunction angleProfile
                = new DeployMembershipFunction(Utility.linguisticIdentifier[0], 2);
        DeployMembershipFunction angularVelocityProfile
                = new DeployMembershipFunction(Utility.linguisticIdentifier[1], 2);
        DeployMembershipFunction currentProfile
                = new DeployMembershipFunction(Utility.linguisticIdentifier[2], 4);

        // Initialising Profiles
        angleProfile.initiateTriangularMembersipFunction(angleTraingleProfileInput);
        angleProfile.initiateTrapezoidalMembersipFunction(angleTrapezoidalProfileInput);

        angularVelocityProfile.initiateTriangularMembersipFunction(angularVelocityTraingleProfileInput);
        angularVelocityProfile.initiateTrapezoidalMembersipFunction(angularVelocityTrapezoidalProfileInput);

        currentProfile.initiateTriangularMembersipFunction(currentTraingleProfileInput);
        currentProfile.initiateTrapezoidalMembersipFunction(currentTrapezoidalProfileInput);

        //Draw Profile Graphs
        try{
            drawProfile(angleProfile, 1);
            drawProfile(angularVelocityProfile, 2);
            drawProfile(currentProfile, 3);
            drawGraphUsingPython();
        }catch (IOException exc){
            System.out.println("Error Drawing Profile........");
            exc.printStackTrace();
        }
        // Initialise GUI
        fc = new FuzzyController(angleProfile, angularVelocityProfile, currentProfile, considerGravity);
        Application.launch(args);
        // Get Current value an compute new Values for the GUI then refresh GUI.

    }

    @Override
    public void start(Stage stage) throws Exception {
        final Group group = new Group();
        final Scene scene = new Scene(group, 900, 600, Color.WHITE);
        stage.setScene(scene);
        stage.setTitle("Inverted Pendulum Simulation");
        stage.show();
        //Pendulum Line
        final Line pendulumHand = new Line(0, 175, 0, 0);
        pendulumHand.setTranslateX(450);
        pendulumHand.setTranslateY(90);

        //Pendulum Ball
        final Circle circle = new Circle(0, 0, 30);
        circle.setTranslateX(450);
        circle.setTranslateY(90);
        circle.setFill(Color.DARKCYAN);

        final Rectangle rectangle = new Rectangle(350,265,200,30);
        rectangle.setFill(Color.DIMGREY);

        final Label label = new Label("Angular Displacement :");
        label.setLayoutY(5);

        final TextField theta = new TextField();
        theta.setPromptText("Enter Theta Value");
        theta.setTranslateX(165);

        final Label label1 = new Label("Angular Velocity :");
        label1.setTranslateX(400);
        label1.setLayoutY(5);

        final TextField angularVelocity = new TextField();
        angularVelocity.setPromptText("Enter angularVelocity");
        angularVelocity.setTranslateX(520);

        final Button submitInitialConfig = new Button("Submit");
        submitInitialConfig.setTranslateX(700);

        final Button pause = new Button("Pause");
        pause.setTranslateX(600);
        pause.setTranslateY(90);

        final TextArea textArea = new TextArea();
        textArea.setTranslateX(100);
        textArea.setPromptText("FuzzyController Output will be displaced");
        textArea.setTranslateY(350);
        textArea.setPrefWidth(700);
        textArea.setPrefHeight(200);

        final Button play = new Button("Play");
        play.setTranslateX(650);
        play.setTranslateY(90);

        group.getChildren().add(circle);
        group.getChildren().add(pendulumHand);
        group.getChildren().add(rectangle);
        group.getChildren().addAll(theta,angularVelocity,label,label1,submitInitialConfig,textArea,pause,play);
        final Timeline[] fiveSecondsWonder = new Timeline[1];
        submitInitialConfig.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                currentTheta = Double.parseDouble(theta.getText());
                currentAngularVelocity = Double.parseDouble(angularVelocity.getText());

                final Rotate secondRotate = new Rotate(-1*currentTheta,0,175);

                //moves pendulum hand
                fiveSecondsWonder[0] = new Timeline(new KeyFrame(Duration.seconds(.05), new EventHandler<ActionEvent>() {
                    double displacement = Double.parseDouble(theta.getText());
                    double velocity = Double.parseDouble(angularVelocity.getText());
                    @Override
                    public void handle(ActionEvent event) {
                        //Color color[] = new Color[]{Color.RED, Color.ORANGE, Color.BLUE, Color.PURPLE, Color.PINK};
                        //int itr = (int)Math.floor(Math.random() * color.length);
                        double angularAcceleration = fc.calculateAngularAcceleration(velocity, displacement);
                        String x = String.format( "angularVelocity.input = %s and angle.input = %s -> current.output = %s",
                                Double.toString(velocity), Double.toString(displacement),angularAcceleration);
                        textArea.appendText(x+"\n");
                        displacement = displacement+velocity*0.01+0.5*angularAcceleration*0.01*0.01;
                        velocity = velocity+angularAcceleration*.01;
                        //circle.setFill(color[itr]);
                        //pendulumHand.setFill(color[itr]);
                        //rectangle.setFill(color[itr]);
                        secondRotate.setAngle(displacement);
                    }
                }));
                fiveSecondsWonder[0].setCycleCount(Timeline.INDEFINITE);
                pendulumHand.getTransforms().add(secondRotate);
                circle.getTransforms().add(secondRotate);
                fiveSecondsWonder[0].play();
            }
        });
        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                fiveSecondsWonder[0].pause();
            }
        });
        play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                fiveSecondsWonder[0].play();
            }
        });
    }
}
