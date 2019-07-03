package cn.edu.ustb.mt4ws.tcg;

public class Variable implements Comparable<Variable> {

	private String name;

	private Object type;

	public Variable(String name, Object type) {
		this.setName(name);
		this.setType(type);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setType(Object type) {
		this.type = type;
	}

	public Object getType() {
		return type;
	}

	public int compareTo(Variable var) {
		return this.name.compareTo(var.getName());
	}

}
