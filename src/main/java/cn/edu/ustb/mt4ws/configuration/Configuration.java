package cn.edu.ustb.mt4ws.configuration;

import java.util.List;

/**
 * 测试用例的配置，包含OperationName，
 * 待测试的蜕变关系Mr的ID集合，
 * 将所有待测试的蜕变关系Mr的ID集合最后存放在testCases集合中
 * @author qing.wen
 * 
 */

public class Configuration {
	// TODO
	private String operationName;
	private List<Integer> mrIds;
	private List<List<Integer>> testCases;

	public Configuration(){
	}
	public Configuration(String operationName, List<Integer> mrIds,
			List<List<Integer>> testCases) {
		this.operationName = operationName;
		this.mrIds = mrIds;
		this.testCases = testCases;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setMrIds(List<Integer> mrIds) {
		this.mrIds = mrIds;
	}

	public List<Integer> getMrIds() {
		return mrIds;
	}

	public void setTestCases(List<List<Integer>> testCases) {
		this.testCases = testCases;
	}

	public List<List<Integer>> getTestCases() {
		return testCases;
	}
}
