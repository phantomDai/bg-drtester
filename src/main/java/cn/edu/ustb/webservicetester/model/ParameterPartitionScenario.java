package cn.edu.ustb.webservicetester.model;

/**
 * 系数分区方案
 * @author zzq
 *
 */
public class ParameterPartitionScenario {
	
	private ParameterInfo paraInfo;// 参数信息
	private java.util.List<ParameterIntervalScenario> partitionScenario;// 分区方案
	
	public ParameterPartitionScenario() {
		partitionScenario = new java.util.LinkedList<ParameterIntervalScenario>();
	}
	public ParameterInfo getParaInfo() {
		return paraInfo;
	}
	public void setParaInfo(ParameterInfo paraInfo) {
		this.paraInfo = paraInfo;
	}
	public ParameterIntervalScenario get(int index) {
		return partitionScenario.get(index);
	}
	public void add(ParameterIntervalScenario paraInter) {
		partitionScenario.add(paraInter);
	}
	public int numOfIntervals() {
		return partitionScenario.size();
	}
}
