package cn.edu.ustb.webservicetester.model;

public class TestSet {
	
	protected java.util.List<TestCase> testCases;
	
	public TestSet() {
		testCases = new java.util.LinkedList<TestCase>();
	}
	
	public void add(TestCase tc) {
		testCases.add(tc);
	}
	
	public TestCase get(int i) {
		return testCases.get(i);
	}
	
	public boolean contains(TestCase tc) {
		boolean result = false;
		java.util.Iterator<TestCase> tsIter = testCases.iterator();
		while (tsIter.hasNext() && !result) {
			result = tc.equals(tsIter.next());
		}
		return false;
	}
	
	public int numOfTestCases() {
		return testCases.size();
	}

}
