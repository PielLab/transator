
package prediction.json;

import java.util.List;

public class Label{
   	private String text;
   	private String total;
   	private Number xPos;
   	private Number yPos;
   	private Number yPosCentered;
   	private Number yPosNonOverlapping;
   	private Number yPosRows;

 	public String getText(){
		return this.text;
	}
	public void setText(String text){
		this.text = text;
	}
 	public String getTotal(){
		return this.total;
	}
	public void setTotal(String total){
		this.total = total;
	}
 	public Number getXPos(){
		return this.xPos;
	}
	public void setXPos(Number xPos){
		this.xPos = xPos;
	}
 	public Number getYPos(){
		return this.yPos;
	}
	public void setYPos(Number yPos){
		this.yPos = yPos;
	}
 	public Number getYPosCentered(){
		return this.yPosCentered;
	}
	public void setYPosCentered(Number yPosCentered){
		this.yPosCentered = yPosCentered;
	}
 	public Number getYPosNonOverlapping(){
		return this.yPosNonOverlapping;
	}
	public void setYPosNonOverlapping(Number yPosNonOverlapping){
		this.yPosNonOverlapping = yPosNonOverlapping;
	}
 	public Number getYPosRows(){
		return this.yPosRows;
	}
	public void setYPosRows(Number yPosRows){
		this.yPosRows = yPosRows;
	}
}
