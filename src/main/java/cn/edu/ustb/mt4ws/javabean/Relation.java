package cn.edu.ustb.mt4ws.javabean;

public class Relation {
	
	private Operator notOp;
	
	private Function functionFollowUp;

	private Function functionSource;

	private Operator op;
	
	/**
	 * 构造函数
	 */
	public Relation(){
		
	}
	
	/**
	 * 带参数的构造函数
	 * @param funcFO
	 * @param funcS
	 * @param oper
	 */
	public Relation(Function funcFO,Function funcS,Operator oper,Operator notOp){
		setFunctionFollowUp(funcFO);
		setFunctionSource(funcS);
		setOp(oper);
		setNotOp(notOp);
	}
	

	public void setFunctionFollowUp(Function functionFollowUp) {
		this.functionFollowUp = functionFollowUp;
	}

	public Function getFunctionFollowUp() {
		return functionFollowUp;
	}

	public void setFunctionSource(Function functionSource) {
		this.functionSource = functionSource;
	}

	public Function getFunctionSource() {
		return functionSource;
	}

	public void setOp(Operator op) {
		this.op = op;
	}

	public Operator getOp() {
		return op;
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(this.functionFollowUp.getFunctionDescription()).append(
				this.op.ops[op.getType()]).append(
				this.functionSource.getFunctionDescription());
		return buff.toString();
	}

	public void setNotOp(Operator notOp) {
		this.notOp = notOp;
	}

	public Operator getNotOp() {
		return notOp;
	}
	
}
