package Utility;

public class Point {

    private double xcoord;
    private double ycoord;

    public Point(double xcoord, double ycoord){
        this.xcoord = xcoord;
        this.ycoord = ycoord;
    }

    public double getXcoord() {
        return xcoord;
    }

    public void setXcoord(double xcoord) {
        this.xcoord = xcoord;
    }

    public double getYcoord() {
        return ycoord;
    }

    public void setYcoord(double ycoord) {
        this.ycoord = ycoord;
    }

    public String toString(){
        return this.xcoord + " " + this.ycoord;
    }
}
