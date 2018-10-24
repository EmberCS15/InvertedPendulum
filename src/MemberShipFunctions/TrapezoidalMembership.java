package MemberShipFunctions;

import Utility.MembershipUtility;
import Utility.Point;

import java.util.Arrays;

/*     B   C
        ___
       /   \
    A /_____\ D
*/
public class TrapezoidalMembership extends BaseMembershipFunction implements MembershipFunctionSkeleton {

    private Point pointA;
    private Point pointB;
    private Point pointC;
    private Point pointD;
    private double slopeLineAB;
    private double slopeLineCD;

    public TrapezoidalMembership(String id, Point pointA, Point pointB, Point pointC, Point pointD) {
        super(id);
        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;
        this.pointD = pointD;
        this.slopeLineAB = MembershipUtility.computeSlope(pointA, pointB);
        this.slopeLineCD = MembershipUtility.computeSlope(pointC, pointD);
    }

    @Override
    public double getMembershipValue(double x) {
        double yValue = 0;
        if(x >= this.pointA.getXcoord() && x <= this.pointD.getXcoord()){
            if(x >= this.pointB.getXcoord() && x<= this.pointC.getXcoord()){
                yValue = this.pointB.getYcoord();
            }else if(x >= this.pointA.getXcoord() && x< this.pointB.getXcoord()){
                yValue = this.pointB.getYcoord() + (x - this.pointB.getXcoord()) * this.slopeLineAB;
            }else{
                yValue = this.pointC.getYcoord() + (x - this.pointC.getXcoord()) * this.slopeLineCD;
                System.out.println("Hello : " + yValue+ " " + this.slopeLineCD + " " + this.pointD.toString());
            }
        }
        return yValue;
    }

    @Override
    public double[] getQuantityValue(double y) {
        double xValue[] =new double[2];
        if(y == pointC.getYcoord()){
            Arrays.fill(xValue, pointB.getYcoord());
        }else{
            Arrays.fill(xValue, y - this.pointC.getYcoord());
            xValue[0] = xValue[0] / this.slopeLineAB + this.pointB.getXcoord();
            xValue[1] = xValue[1] / this.slopeLineCD + this.pointC.getXcoord();
        }
        return xValue;
    }

    @Override
    public Point[] getProfileCoordinates() {
        Point coord[]= new Point[4];
        coord[0] = this.pointA;
        coord[1] = this.pointB;
        coord[2] = this.pointC;
        coord[3] = this.pointD;
        return coord;
    }
}
