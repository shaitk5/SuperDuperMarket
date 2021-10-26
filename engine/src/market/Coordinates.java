package market;

import DTOClass.LocationDTO;
import xml.schema.generated.Location;

public class Coordinates {
    private int x;
    private int y;

    public Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Coordinates(Location location) {
        this.x = location.getX();
        this.y = location.getY();
    }

    public Coordinates(LocationDTO location) {
        this.x = location.getX();
        this.y = location.getY();
    }

    public static boolean isValidCoordinate(int x, int y){
        return x > 0 && x < 51 && y > 0 && y < 51;
    }

    public static boolean isValidCoordinate(Coordinates check){
        return check.getX() > 0 && check.getX() < 51 && check.getY() > 0 && check.getY() < 51;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static double getDistance(Coordinates first, Coordinates second){
        double powX = Math.pow(first.x - second.x,2);
        double powY = Math.pow(first.y - second.y,2);
        return Math.sqrt(powX + powY);
    }

    public static double getDistance(Coordinates first, LocationDTO second){
        double powX = Math.pow(first.x - second.getX(),2);
        double powY = Math.pow(first.y - second.getY(),2);
        return Math.sqrt(powX + powY);
    }

    public static double getDistance(int x1, int y1, int x2, int y2){
        double powX = Math.pow(x1 - x2, 2);
        double powY = Math.pow(y1 - y2,2);
        return Math.sqrt(powX + powY);
    }

    @Override
    public String toString() {
        return "[" + this.x + "," + this.y + "]";
    }
}
