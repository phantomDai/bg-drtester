package cn.edu.ustb.mt4ws.mr.jsp;

/**
 * 集合Set类型的Input输入
 * @author qing.wen
 *
 */
@SuppressWarnings("serial")
public class SetInputBean implements java.io.Serializable{
	private int mrID;
	private String min;
	private String max;
	
	public SetInputBean(int mrID,String min,String max){
		setMrID(mrID);
		setMin(min);
		setMax(max);
	}
	
	
	public void setMrID(int mrID) {
		this.mrID = mrID;
	}
	public int getMrID() {
		return mrID;
	}
	public void setMin(String min) {
		this.min = min;
	}
	public String getMin() {
		return min;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String getMax() {
		return max;
	}
	
}
