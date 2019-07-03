package cn.edu.ustb.mt4ws.javabean;

public class MetamorphicRelation {
	
	private RelationOfInput relationOfInput;
	
	private RelationOfOutput relaitonOfOutput;
	
	/**
	 * 构造函数
	 */
	public MetamorphicRelation(){
		
	}
	
	/**
	 * 带参数的构造函数
	 * @param relationOfInput
	 * @param relationOfOutput
	 */
	public MetamorphicRelation(RelationOfInput relationOfInput,RelationOfOutput relationOfOutput){
		setRelaitonOfOutput(relationOfOutput);
		setRelationOfInput(relationOfInput);
	}

	public void setRelationOfInput(RelationOfInput relationOfInput) {
		this.relationOfInput = relationOfInput;
	}

	public RelationOfInput getRelationOfInput() {
		return relationOfInput;
	}

	public void setRelaitonOfOutput(RelationOfOutput relaitonOfOutput) {
		this.relaitonOfOutput = relaitonOfOutput;
	}

	public RelationOfOutput getRelaitonOfOutput() {
		return relaitonOfOutput;
	}

	
	
	
	
	
}
