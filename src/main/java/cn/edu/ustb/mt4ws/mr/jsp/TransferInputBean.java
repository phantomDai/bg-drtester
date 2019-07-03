package cn.edu.ustb.mt4ws.mr.jsp;

/*
 * 测试用例的输入属性
 */
@SuppressWarnings("serial")
public class TransferInputBean implements java.io.Serializable{
	
	private int mrID;
	private String accoutFrom;
	private String accoutTo;
	private String mode;
	private String amount;
	
	
	
	public TransferInputBean(){
		
	}
	
	public TransferInputBean(int mrID,String accoutFrom,String accoutTo,String mode,String amount){
		setMrID(mrID);
		setAccoutFrom(accoutFrom);
		setAccoutTo(accoutTo);
		setMode(mode);	
		setAmount(amount);
	}
	
	public void setAccoutFrom(String accoutFrom) {
		this.accoutFrom = accoutFrom;
	}
	public String getAccoutFrom() {
		return accoutFrom;
	}
	public void setAccoutTo(String accoutTo) {
		this.accoutTo = accoutTo;
	}
	public String getAccoutTo() {
		return accoutTo;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getAmount() {
		return amount;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getMode() {
		return mode;
	}

	public void setMrID(int mrID) {
		this.mrID = mrID;
	}

	public int getMrID() {
		return mrID;
	}
	
	
	

}
