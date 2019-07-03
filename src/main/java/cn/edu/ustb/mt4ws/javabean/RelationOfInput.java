package cn.edu.ustb.mt4ws.javabean;

import java.util.List;

public class RelationOfInput {

	//至少含有一个relation
	private Relation relation;
	
	private List<OpAndRelation> listOpAndRe;
	
	/**
	 * 构造函数
	 */
	public RelationOfInput(){
		
	}
	
	/**
	 * 带参数的构造函数
	 * @param relation
	 * @param listOpAndRe
	 */
	public RelationOfInput(Relation relation,List<OpAndRelation> listOpAndRe){
		setRelation(relation);
		setListOpAndRe(listOpAndRe);		
	}



	public void setListOpAndRe(List<OpAndRelation> listOpAndRe) {
		this.listOpAndRe = listOpAndRe;
	}

	public List<OpAndRelation> getListOpAndRe() {
		return listOpAndRe;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public Relation getRelation() {
		return relation;
	}
	
	
}
