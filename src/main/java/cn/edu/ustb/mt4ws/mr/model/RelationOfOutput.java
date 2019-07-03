package cn.edu.ustb.mt4ws.mr.model;

import java.util.List;

public class RelationOfOutput{

	private List<Relation> relationOfOutput;

	public String toString() {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < relationOfOutput.size(); i++)
			buff.append(relationOfOutput.get(i).toString()).append("\n");
		return buff.toString();
	}

	public void setRelationOfOutput(List<Relation> relationOfOutput) {
		this.relationOfOutput = relationOfOutput;
	}

	public List<Relation> getRelationOfOutput() {
		return relationOfOutput;
	}
}
