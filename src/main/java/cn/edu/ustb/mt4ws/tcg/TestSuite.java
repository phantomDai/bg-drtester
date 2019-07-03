package cn.edu.ustb.mt4ws.tcg;

import java.util.List;

public class TestSuite {
	/**
	 * 可保存两种形式的testcaseID，一种是单一数字，如12，另一种是范围，如1-50
	 */
	public List<Integer> testcaseList;

	public List<Integer> getTestcaseList() {
		return testcaseList;
	}

	public void setTestcaseList(List<Integer> testcaseList) {
		this.testcaseList = testcaseList;
	}
}
