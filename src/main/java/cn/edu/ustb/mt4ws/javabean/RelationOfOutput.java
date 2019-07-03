package cn.edu.ustb.mt4ws.javabean;

import java.util.List;

public class RelationOfOutput {
	
	private Relation relation;
	
	private List<OpAndRelation> listOpAndRe;
	
	/**
	 * 构造函数
	 */
	public RelationOfOutput(){
		
	}
	
	/**
	 * 带参数的构造函数
	 * @param relation
	 * @param listOpAndRe
	 */
	public RelationOfOutput(Relation relation,List<OpAndRelation> listOpAndRe){
		setRelation(relation);
		setListOpAndRe(listOpAndRe);
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setListOpAndRe(List<OpAndRelation> listOpAndRe) {
		this.listOpAndRe = listOpAndRe;
	}

	public List<OpAndRelation> getListOpAndRe() {
		return listOpAndRe;
	}
	
	
	
	
	
}
