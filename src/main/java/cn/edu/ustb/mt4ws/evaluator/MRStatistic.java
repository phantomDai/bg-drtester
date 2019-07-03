package cn.edu.ustb.mt4ws.evaluator;

import java.util.Iterator;
import java.util.List;

public class MRStatistic {

	/**
	 * projectName+operationName+mrId
	 */
	private String mrName;

	/**
	 * 发现了错误的test case列表
	 */
	private List<String> validTestCaseList;

	public void setMrName(String mrName) {
		this.mrName = mrName;
	}

	public String getMrName() {
		return mrName;
	}

	public void setValidTestCaseList(List<String> validTestCaseList) {
		this.validTestCaseList = validTestCaseList;
	}

	public List<String> getValidTestCaseList() {
		return validTestCaseList;
	}

	public void print() {
		System.out.println("MR Statistic:" + mrName);
		Iterator<String> iter = validTestCaseList.iterator();
		while (iter.hasNext()) {
			String vtc = iter.next();
			System.out.println(vtc+" ");
		}

	}

}
