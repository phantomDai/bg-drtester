package cn.edu.ustb.webservicetester.model;

public class ParameterInfo {
	
	public static final int WHATEVER = 0;// 参数取值类型只涉及分区，若参数与分区无关，则其取值类型为“无所谓”
	public static final int CONTINUOUS = 1;// 参数取值类型——“连续型”
	public static final int SCATTERED = 2;// 参数取值类型——“枚举型”
	
	private String name;// 参数名称
	private String classInfo;// 参数的数据类型
	private int type;// 参数的取值类型，连续型或枚举（离散）型
	private String restriction;// WSDL文档中关于此参数的限制
	
	public ParameterInfo() {
		type = WHATEVER;
	}

	public String getName() {
		return name;
	}

	public void setName(String id) {
		this.name = id;
	}

	public String getClassInfo() {
		return classInfo;
	}

	public void setClassInfo(String classInfo) {
		this.classInfo = classInfo;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getRestriction() {
		return restriction;
	}

	public void setRestriction(String restriction) {
		this.restriction = restriction;
	}
	
	public String toString() {
		return name + " : " + classInfo;
	}
	
	private boolean equals(ParameterInfo pi) {
		if (pi.getName().equals(this.getName()) &&
				pi.getRestriction().equals(this.getRestriction()) &&
				pi.getClassInfo().equals(this.getClassInfo())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof ParameterInfo)) {
			return false;
		}
		ParameterInfo pi = (ParameterInfo)o;
		return equals(pi);
	}
	
	public int hashCode() {
		int result = 17;
		if (name != null) {
			result = result * 37 + name.hashCode();
		}
		if (classInfo != null) {
			result = result * 37 + classInfo.hashCode();
		}
		if (restriction != null) {
			result = result * 37 + restriction.hashCode();
		}
		return result;
	}

}
