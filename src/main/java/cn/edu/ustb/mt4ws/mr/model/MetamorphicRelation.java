package cn.edu.ustb.mt4ws.mr.model;

public class MetamorphicRelation {

	private RelationOfInput relationOfInput;

	private RelationOfOutput relationOfOutput;

	public MetamorphicRelation(RelationOfInput relationOfInput,
			RelationOfOutput relationOfOutput) {
		setRelationOfInput(relationOfInput);
		setRelationOfOutput(relationOfOutput);
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("Relation Of Input: " + relationOfInput.toString()).append(
				"Relation Of Output: " + relationOfOutput.toString());
		return buff.toString();
	}

	public void setRelationOfInput(RelationOfInput relationOfInput) {
		this.relationOfInput = relationOfInput;
	}

	public RelationOfInput getRelationOfInput() {
		return relationOfInput;
	}

	public void setRelationOfOutput(RelationOfOutput relationOfOutput) {
		this.relationOfOutput = relationOfOutput;
	}

	public RelationOfOutput getRelationOfOutput() {
		return relationOfOutput;
	}

}
