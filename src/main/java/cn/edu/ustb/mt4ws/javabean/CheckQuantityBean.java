package cn.edu.ustb.mt4ws.javabean;

public class CheckQuantityBean {

	private int id;		
	private int mrID;
	private String name;
	private String amount;
	
	public CheckQuantityBean(){
		
	}
	
	public CheckQuantityBean(int id, int mrID, String name, String amount) {
		super();
		this.id = id;
		this.mrID = mrID;
		this.name = name;
		this.amount = amount;
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}


	
}
