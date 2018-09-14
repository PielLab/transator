package prediction.json;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 2/7/13
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class Segment {
    private String text;
    private Number xPos;
    private Number yPos;
    private Number yPosCentered;
    private Number yPosNonOverlapping;
    private Number yPosRows;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Number getxPos() {
        return xPos;
    }

    public void setxPos(Number xPos) {
        this.xPos = xPos;
    }

    public Number getyPos() {
        return yPos;
    }

    public void setyPos(Number yPos) {
        this.yPos = yPos;
    }

    public Number getyPosCentered() {
        return yPosCentered;
    }

    public void setyPosCentered(Number yPosCentered) {
        this.yPosCentered = yPosCentered;
    }

    public Number getyPosNonOverlapping() {
        return yPosNonOverlapping;
    }

    public void setyPosNonOverlapping(Number yPosNonOverlapping) {
        this.yPosNonOverlapping = yPosNonOverlapping;
    }

    public Number getyPosRows() {
        return yPosRows;
    }

    public void setyPosRows(Number yPosRows) {
        this.yPosRows = yPosRows;
    }
}
