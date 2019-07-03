package cn.edu.ustb.mt4ws.mr.jsp;

/**
 * 人民币转换服务输入的合理性
 * @author qing.wen
 * 
 *
 */

@SuppressWarnings("serial")
public class RMBInputBean implements java.io.Serializable{
	//蜕变关系ID
	private int mrID;
    //输入rmb
	private String rmb;

	public RMBInputBean(int id,String RMB){
		setMrID(id);
		setRmb(RMB);		
	}
	
	public void setMrID(int mrID) {
		this.mrID = mrID;
	}

	public int getMrID() {
		return mrID;
	}

	public void setRmb(String rmb) {
		this.rmb = rmb;
	}

	public String getRmb() {
		return rmb;
	}
	
	
	
}
