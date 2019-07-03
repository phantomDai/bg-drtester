package cn.edu.ustb.mt4ws.javabean;

public class SubBean {


	private int id;		
	private int mrID;
	private String a;
	private String b;
	
	public SubBean(){
		
	}
	
	public SubBean(int id, int mrID, String a, String b) {
		this.id = id;
		this.mrID = mrID;
		this.a = a;
		this.b = b;
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
	public String getA() {
		return a;
	}
	public void setA(String a) {
		this.a = a;
	}
	public String getB() {
		return b;
	}
	public void setB(String b) {
		this.b = b;
	}
	
}
