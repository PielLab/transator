
package prediction.json;

import java.util.List;

public class Legend{
   	private List<Key> key;
   	private Segment segment;

 	public List<Key> getKey(){
		return this.key;
	}
	public void setKey(List<Key> key){
		this.key = key;
	}
 	public Segment getSegment(){
		return this.segment;
	}
	public void setSegment(Segment segment){
		this.segment = segment;
	}
}
