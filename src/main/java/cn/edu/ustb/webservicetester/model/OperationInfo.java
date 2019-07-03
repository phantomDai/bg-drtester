package cn.edu.ustb.webservicetester.model;

import java.util.List;

public class OperationInfo {
	
	private String opName;
	private List<ParameterInfo> paraList;
	
	public OperationInfo() {}

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public List<ParameterInfo> getParaList() {
		return paraList;
	}

	public void setParaList(List<ParameterInfo> paraList) {
		this.paraList = paraList;
	}

}
