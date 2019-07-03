package cn.edu.ustb.webservicetester.model;

public class TestCase {

	private int id = -1;
	private java.util.Map<ParameterInfo, String> paraValues;// 各参数的值
	private String expectedResult = null;
	
	public TestCase() {
		paraValues = new java.util.HashMap<ParameterInfo, String>();
	}
	
	public boolean containsParameter(ParameterInfo pi) {
		return paraValues.containsKey(pi);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setValueOfParameter(ParameterInfo pi, String value) {
		paraValues.put(pi, value);
	}
	
	public String getValueOfParameter(ParameterInfo pi) {
		return paraValues.get(pi);
	}
	
	public String getExpectedResult() {
		return expectedResult;
	}

	public void setExpectedResult(String expectedResult) {
		this.expectedResult = expectedResult;
	}

	public java.util.List<ParameterInfo> getParameters() {
		java.util.List<ParameterInfo> paraInfoList = new java.util.LinkedList<ParameterInfo>();
		java.util.Iterator<ParameterInfo> pii = paraValues.keySet().iterator();
		while (pii.hasNext()) {
			paraInfoList.add(pii.next());
		}
		return paraInfoList;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof TestCase)) {
			return false;
		}
		TestCase tc = (TestCase)o;
		if (!((tc.getParameters()).equals(getParameters()))) {
			return false;
		}
		java.util.Iterator<ParameterInfo> paraInfoIter = paraValues.keySet().iterator();
		boolean findDifference = false;
		while (paraInfoIter.hasNext() && !findDifference) {
			ParameterInfo pi = paraInfoIter.next();
			findDifference = !(paraValues.get(pi).equals(tc.getValueOfParameter(pi)));
		}
		return !findDifference;
	}
	
	public int hashCode() {
		int result = 17;
		result = result * 37 + paraValues.hashCode();
		if (expectedResult != null)
			result = result * 37 + expectedResult.hashCode();
		return result;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (ParameterInfo p : getParameters()) {
			sb.append(p.getName());
			sb.append(" : ");
			sb.append(paraValues.get(p));
			sb.append(", ");
		}
		if (expectedResult != null) {
			sb.append("expected result : ");
			sb.append(expectedResult);
		} else {
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.toString();
	}

}
