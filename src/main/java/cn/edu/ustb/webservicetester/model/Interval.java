package cn.edu.ustb.webservicetester.model;

/**
 * 测试域的区间，由各个参数的区间构成
 * 
 * @author zzq
 *
 */
public class Interval {
	
	private java.util.Map<ParameterInfo, ParameterIntervalScenario> range;// 分区范围，即各个参数的区间范围
	private int weight;// 权重
	private java.util.List<TestCase> testCases;
	
	public Interval(java.util.Map<ParameterInfo, ParameterIntervalScenario> r, int w) {
		this.range = r;
		this.weight = w;
		testCases = new java.util.LinkedList<TestCase>();
	}
//	public ParameterIntervalScenario get(ParameterInfo paraInfo) {
//		return range.get(paraInfo);
//	}
//	public void put(ParameterInfo paraInfo, ParameterIntervalScenario paraInter) {
//		range.put(paraInfo, paraInter);
//	}
	public int getWeight() {
		return weight;
	}
//	public void setWeight(int weight) {
//		this.weight = weight;
//	}
	public int numOfTestCases() {
		return testCases.size();
	}
	
	public void addTestCase(TestCase tc) {
		testCases.add(tc);
	}
	
	public TestCase getTestCase(int i) {
		return testCases.get(i);
	}
	
	/**
	 * 判断一个测试用例是否属于此区间
	 * @param tc 指定测试用例
	 * @return 指定测试用例的值属于此区间，则返回 true；否则返回 false
	 */
	public boolean includes(TestCase tc) {
		for (ParameterInfo pi : tc.getParameters()) {
			if (pi.getType() == ParameterInfo.SCATTERED) {
				if (excludeScatteredValue(tc, pi))
					return false;
			} else if (pi.getType() == ParameterInfo.CONTINUOUS){
				if (excludesContinuousValue(tc, pi))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断测试用例 tc 中参数 pi 的值是否在区间之外（参数 pi 取值类型为连续型）
	 * @param tc 测试用例
	 * @param pi 参数信息
	 * @return 参数 pi 的值区间之外返回 true，否则返回 false
	 */
	private boolean excludesContinuousValue(TestCase tc, ParameterInfo pi) {
		String rangeOfParaInterval = range.get(pi).getRange();
		String valueOfTestCase = tc.getValueOfParameter(pi);
		double upperBound;
		double lowerBound;
		String[] ss = rangeOfParaInterval.split(", ");
		lowerBound = Double.valueOf(ss[0].substring(1));
		upperBound = Double.valueOf(ss[1].substring(0, ss[1].length() - 1));
		double votc = Double.valueOf(valueOfTestCase);
		if (rangeOfParaInterval.startsWith("[") && votc < lowerBound)
			return true;
		if (rangeOfParaInterval.startsWith("(") && votc <= lowerBound)
			return true;
		if (rangeOfParaInterval.endsWith("]") && votc > upperBound)
			return true;
		if (rangeOfParaInterval.endsWith(")") && votc >= upperBound)
			return true;
		return false;
	}
	
	/**
	 * 判断测试用例 tc 中参数 pi 的值是否在区间之外（参数 pi 取值类型为枚举型）
	 * @param tc 测试用例
	 * @param pi 参数信息
	 * @return 参数 pi 的值区间之外返回 true，否则返回 false
	 */
	private boolean excludeScatteredValue(TestCase tc, ParameterInfo pi) {
		return !tc.getValueOfParameter(pi).equals(range.get(pi.getName()).getRange());
	}
	

}
