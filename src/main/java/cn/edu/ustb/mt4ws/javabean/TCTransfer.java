package cn.edu.ustb.mt4ws.javabean;


/**
 * 
 * transfer Input javaBean
 * @author wendy
 *
 */
public class TCTransfer {
	
	private int id;	
	private int mrID;
	private String accountFrom;
	private String accountTo;
	private String mode;
	private String amount;
	
	/**
	 *无参数构造函数 
	 */
	public TCTransfer(){
		
	}
	
	/**
	 * 带参数的构造函数
	 * @param mrID
	 * @param accountFrom
	 * @param accountTo
	 * @param mode
	 * @param amount
	 */
	public TCTransfer(int mrID,String accountFrom,String accountTo,String mode,String amount){
		setMrID(mrID);
		setAccountFrom(accountFrom);
		setAccountTo(accountTo);
		setAmount(amount);
		setMode(mode);	
	}
	/**
	 * 带参数的构造函数
	 * @param id
	 * @param mrID
	 * @param accountFrom
	 * @param accountTo
	 * @param mode
	 * @param amount
	 */
	public TCTransfer(int id,int mrID,String accountFrom,String accountTo,String mode,String amount){
		setId(id);
		setMrID(mrID);
		setAccountFrom(accountFrom);
		setAccountTo(accountTo);
		setAmount(amount);
		setMode(mode);	
	}
	
	/**
	 * set and get
	 */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMrID() {
		return mrID;
	}
	public void setMrID(int mrID) {
		this.mrID = mrID;
	}
	public String getAccountFrom() {
		return accountFrom;
	}
	public void setAccountFrom(String accountFrom) {
		this.accountFrom = accountFrom;
	}
	public String getAccountTo() {
		return accountTo;
	}
	public void setAccountTo(String accountTo) {
		this.accountTo = accountTo;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	/**
	 * 打印原始测试用例
	 * @param tcTransfer
	 */
	public static void printTCF(TCTransfer tcTransfer) {
		System.out.println("**********打印原始测试用例****************");
		System.out.println("Sid: " + tcTransfer.getId());
		System.out.println("mrID: " + tcTransfer.getMrID());
		System.out.println("accountFrom: " + tcTransfer.getAccountFrom());
		System.out.println("accountTo: " + tcTransfer.getAccountTo());
		System.out.println("amount: " + tcTransfer.getAmount());
		System.out.println("mode: " + tcTransfer.getMode());
	}
	
}
