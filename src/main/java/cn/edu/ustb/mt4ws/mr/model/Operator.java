package cn.edu.ustb.mt4ws.mr.model;

import java.util.List;

public class Operator {

	private static final double doubleEq = 1e-6;

	// 操作符代号
	public static final int EQ = 0;// equal
	public static final int GT = 1;// greater than
	public static final int LT = 2;// less than
	public static final int NLT = 3;// not less than
	public static final int NGT = 4;// not greater than
	public static final int NEQ = 5;// not equal
	public static final int RC = 6;//右包含
	public static final int LC = 7;//左包含
	public static final int LA = 8;// String中从左边增加字符
	public static final int RA = 9;// String中从右边增加字符
    public static final int REP = 10;//String中替换中间字符
    public static final int DEL = 11;//String中删除某个字符
	
	public static final String[] ops = { "=", ">", "<", ">=", "<=", "!=", "⊇",
			"⊆", "L+", "R+" ,"REP" ,"DEL" };
	public static final String[] opNames = { "EQ", "GT", "LT", "NLT", "NGT",
			"NEQ" , "RC" , "LC" , "LA" , "RA" ,"REP","DEL"};

	private int type;

	public Operator(){
		
	}
	
	public Operator(int type) {
		this.setType(type);
	}

	public Operator(String opName) {
		this.setType(-1);
		for (int i = 0; i < opNames.length; i++) {
			if (opNames[i].equals(opName))
				this.setType(i);
		}
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	/**
	 * 判断double数之间的关系
	 * @param followUp
	 * @param source
	 * @return
	 */
	public boolean validate(double followUp, double source) {
		boolean isSatisfied = false;
		switch (this.type) {
		case EQ:
			isSatisfied = compareDouble(followUp, source);
			break;
		case GT:
			isSatisfied = (followUp > source);
			break;
		case LT:
			isSatisfied = (followUp < source);
			break;
		case NLT:
			isSatisfied = (followUp >= source);
			break;
		case NGT:
			isSatisfied = (followUp <= source);
			break;
		case NEQ:
			isSatisfied = !compareDouble(followUp, source);
			break;
		default:
		}
		return isSatisfied;
	}
	
	/**
	 * 判断字符串之间的关系 注：其中LA和RA只表示FollowUp和Source之间的关系，但传递过来的为变换之后的值，故判断是否相等
	 * 
	 * @param followUp
	 * @param source
	 * @param opType
	 * @return
	 */
	public boolean validate(String followUp, String source, int opType) {
		boolean isSatisfied = false;
		switch (opType) {
		case EQ:
			isSatisfied = followUp.equals(source);
			break;
		case LA:
			isSatisfied = followUp.equals(source);
			break;
		case RA:
			isSatisfied = followUp.equals(source);
			break;
		case NEQ:
			isSatisfied = !followUp.equals(source);
			break;
		case REP:
			isSatisfied = followUp.equals(source);
		default:
		}
		return isSatisfied;
	}

	/**
	 * 判断集合之间的关系，左包含，右包含，等于，不等于
	 * @param 集合followUp
	 * @param 集合source
	 * @return
	 */
	public boolean validate(List<String> followUp,List<String> source){
		boolean isSatisfied = false;		
		switch (this.type) {
		case EQ:
			isSatisfied = followUp.equals(source);
			break;
		case LC:
			isSatisfied = source.containsAll(followUp);
			break;
		case RC:
			isSatisfied = followUp.containsAll(source);
			break;
		case NEQ:
			isSatisfied = followUp.equals(source);
			break;
		default:	
		}	
		return isSatisfied;
	}
	
	private boolean compareDouble(double x1, double x2) {
		return Math.abs(x1 - x2) < doubleEq;
	}
}
