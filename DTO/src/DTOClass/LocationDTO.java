package DTOClass;

import market.Coordinates;

import java.io.Serializable;
import java.util.Comparator;

public class LocationDTO implements Serializable {
    private int x;
    private int y;

    public LocationDTO(int x, int y){
        this.x = x;
        this.y = y;
    }

    public LocationDTO(Coordinates customer_location) {
        this.x = customer_location.getX();
        this.y = customer_location.getY();
    }

    public static boolean isValidCoordinate(int x, int y){
        return x > 0 && x < 51 && y > 0 && y < 51;
    }

    public static boolean isValidCoordinate(LocationDTO check){
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

    public static class CompareByLocation implements Comparator<LocationDTO>{
        @Override
        public int compare(LocationDTO o1, LocationDTO o2) {
            if(o1.x >= o2.x && o1.y >= o2.y){
                return 1;
            }else if(o1.x > o2.x){
                return 1;
            }else{
                return -1;
            }
        }
    }

    public static double getDistance(LocationDTO first, LocationDTO second){
        double powX = Math.pow(first.x - second.x,2);
        double powY = Math.pow(first.y - second.y,2);
        return Math.sqrt(powX + powY);
    }

    public static double getDistance(LocationDTO first, Coordinates second){
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
