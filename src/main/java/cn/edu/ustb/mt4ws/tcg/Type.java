package cn.edu.ustb.mt4ws.tcg;

public class Type {

	private int type;

	public static final int INT = 1;
	public static final int DOUBLE = 2;
    public static final int String = 3;
	
	
	public Type(int type) {
		this.setType(type);
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

}
