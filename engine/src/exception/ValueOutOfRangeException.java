package exception;

public class ValueOutOfRangeException extends Exception {
    private int small = 0;
    private int big = 0;
    String massage1 = "Exception";

    public ValueOutOfRangeException(String errorParameter, int small, int big){
        super();
        this.small = small;
        this.big = big;
        this.massage1 = errorParameter;
    }

    public ValueOutOfRangeException(String errorParameter){
        super();
        this.massage1 = errorParameter;
    }

    @Override
    public String getMessage() {
        if(this.small != this.big){
            return massage1 + ", Value is out out range : [" + small + "," + big + "]";
        }
        return massage1 + ", Value is out of range";
    }
}
