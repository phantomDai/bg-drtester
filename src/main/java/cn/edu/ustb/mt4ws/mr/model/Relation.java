package cn.edu.ustb.mt4ws.mr.model;

public class Relation {

	private Function functionFollowUp;

	private Function functionSource;

	private Operator op;

	/**
	 * 构造函数
	 */
	public Relation(){
		
	}
	
	public Relation(Function functionFollowUp, Operator op,
			Function functionSource) {
		setFunctionFollowUp(functionFollowUp);
		setOp(op);
		setFunctionSource(functionSource);
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(this.functionFollowUp.getFunctionDescription()).append(
				this.op.ops[op.getType()]).append(
				this.functionSource.getFunctionDescription());
		return buff.toString();
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

}
