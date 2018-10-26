package MemberShipFunctions;

import Utility.Utility;
import Utility.Point;

import java.util.Arrays;
import java.util.Comparator;

public class DeployMembershipFunction {
    private String linguisticIdentifier;
    private TriangularMembership triangularMembership;
    private TrapezoidalMembership trapezoidalMembership[];
    private final String TRAPEZOID_EXCEPTION_MESSAGE = "Trapezoid Dimension Mismatch";

    public DeployMembershipFunction(String linguisticIdentifier, int numberOfTrapezoids){
        this.linguisticIdentifier = linguisticIdentifier;
        this.triangularMembership = null;
        this.trapezoidalMembership = new TrapezoidalMembership[numberOfTrapezoids];
    }

    public String getLinguisticIdentifier() {
        return this.linguisticIdentifier;
    }

    public void setLinguisticIdentifier(String linguisticIdentifier) {
        this.linguisticIdentifier = linguisticIdentifier;
    }

    public TriangularMembership getTriangularMembership() {
        return triangularMembership;
    }

    public void setTriangularMembership(TriangularMembership triangularMembership) {
        this.triangularMembership = triangularMembership;
    }

    public TrapezoidalMembership[] getTrapezoidalMembership() {
        return trapezoidalMembership;
    }

    public void setTrapezoidalMembership(TrapezoidalMembership []trapezoidalMembership) throws Exception {
        if(this.trapezoidalMembership.length != trapezoidalMembership.length)
            throw new Exception(TRAPEZOID_EXCEPTION_MESSAGE);
        this.trapezoidalMembership = trapezoidalMembership;
    }

    public void initiateTriangularMembersipFunction(Point []ptr){
        sortPointsOnX(ptr);
        this.triangularMembership
                = new TriangularMembership(Utility.profileIdentifier[0], ptr[0], ptr[1], ptr[2]);
    }

    public void initiateTrapezoidalMembersipFunction(Point p[][]){
        int pid = (p.length == 4)?1:2;
        for(int i=0;i<p.length;i++){
            sortPointsOnX(p[i]);
            this.trapezoidalMembership[i]
                    = new TrapezoidalMembership(Utility.profileIdentifier[pid++], p[i][0], p[i][1], p[i][2], p[i][3]);
        }
    }

    private void sortPointsOnX(Point p[]){
        Arrays.sort(p, new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return (o1.getXcoord()<= o2.getXcoord())?-1:1;
            }
        });
    }
}
