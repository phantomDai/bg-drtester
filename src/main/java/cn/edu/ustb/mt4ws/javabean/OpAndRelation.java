package cn.edu.ustb.mt4ws.javabean;

public class OpAndRelation {
	
	private Operator operator;
	private Relation relation;
	
	/**
	 * 构造函数
	 */
	public OpAndRelation(){
		
	}
	
	/**
	 * 带参数的构造函数
	 * @param op
	 * @param relation
	 */
	public OpAndRelation(Operator op,Relation relation){
		setOperator(op);
		setRelation(relation);		
	}
	
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	public Operator getOperator() {
		return operator;
	}
	public void setRelation(Relation relation) {
		this.relation = relation;
	}
	public Relation getRelation() {
		return relation;
	}
	
}
