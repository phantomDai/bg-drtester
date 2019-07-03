package cn.edu.ustb.mt4ws.mr.model;

import java.util.List;

public class RelationOfInput{
	
	private List<Relation> relationOfInput;
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		for(int i=0;i<relationOfInput.size();i++)
			buff.append(relationOfInput.get(i).toString()).append("\n");
		return buff.toString();
	}

	public void setRelationOfInput(List<Relation> relationOfInput) {
		this.relationOfInput = relationOfInput;
	}

	public List<Relation> getRelationOfInput() {
		return relationOfInput;
	}

}
