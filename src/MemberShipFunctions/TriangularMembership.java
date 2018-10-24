package MemberShipFunctions;

import Utility.MembershipUtility;
import Utility.Point;

import java.util.Arrays;


/*   C
     .
    / \
   /   \
A /_____\ B

*/
public class TriangularMembership extends BaseMembershipFunction implements MembershipFunctionSkeleton{

    private Point pointA;
    private Point pointB;
    private Point pointC;
    private double slopeLineAC;
    private double slopeLineBC;

    public TriangularMembership(String id, Point pointA, Point pointB, Point pointC){
        super(id);
        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;
        configurePoints();
        this.slopeLineAC = MembershipUtility.computeSlope(pointA, pointC);
        this.slopeLineBC = MembershipUtility.computeSlope(pointB, pointC);
    }

    private void swap(Point a, Point b){
        double t_x = a.getXcoord(), t_y = a.getYcoord();
        a.setXcoord(b.getXcoord()); a.setYcoord(b.getYcoord());
        b.setXcoord(t_x); b.setYcoord(t_y);
    }

    private void configurePoints(){
        if(this.pointA.getYcoord() == this.pointC.getYcoord()){
            swap(this.pointC, this.pointB);
            //System.out.println(this.pointC.toString()+"_"+this.pointB.toString());
        }else if(this.pointB.getYcoord() == this.pointC.getYcoord()){
            swap(this.pointC, this.pointA);
        }
    }

    @Override
    public double getMembershipValue(double x){
        double yValue = 0;
        if(x < pointA.getXcoord() || x > pointB.getXcoord())
            return 0;
        if(x <= pointC.getXcoord()){
            yValue = this.pointC.getYcoord() + this.slopeLineAC * (x - this.pointC.getXcoord());
        }else{
            yValue = this.pointC.getYcoord() + this.slopeLineBC * (x - this.pointC.getXcoord());
        }
        return yValue;
    }

    @Override
    public double[] getQuantityValue(double y){
        double []xValue = new double[2];
        if(y >= MembershipUtility.membershipFunctionLowerLimit && y <= MembershipUtility.membershipFunctionUpperLimit){
            Arrays.fill(xValue, y - this.pointC.getYcoord());
            xValue[0] = xValue[0] / this.slopeLineAC + this.pointC.getXcoord();
            xValue[1] = xValue[1] / this.slopeLineBC + this.pointC.getXcoord();
        }
        return xValue;
    }

    @Override
    public Point[] getProfileCoordinates() {
        Point coord[]= new Point[3];
        coord[0] = this.pointA;
        coord[1] = this.pointB;
        coord[2] = this.pointC;
        return coord;
    }
}
