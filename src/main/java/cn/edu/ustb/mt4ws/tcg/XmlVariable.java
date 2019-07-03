package cn.edu.ustb.mt4ws.tcg;

import org.apache.xmlbeans.SchemaType;

public class XmlVariable extends Variable {

	private SchemaType type;

	public XmlVariable(String name, Object type) {
		super(name,type);
		this.type = (SchemaType) type;
		// TODO Auto-generated constructor stub
	}

	public void setType(SchemaType type) {
		this.type = type;
	}

	public SchemaType getType() {
		return type;
	}

}
