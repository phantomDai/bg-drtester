package cn.edu.ustb.mt4ws.javabean;

public class ProcessBean {
	
	private int id;	
	private int mrID;
	private String a;
	private String b;
	private String type;
	
	public ProcessBean(int id,int mrId,String a,String b,String type){
		this.id=id;
		this.mrID=mrId;
		this.a=a;
		this.b=b;
		this.type=type;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	

}
