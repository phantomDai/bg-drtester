package cn.edu.ustb.mt4ws.configuration;

import java.util.Map;

import cn.edu.ustb.mt4ws.tcg.TestSuite;

public class Project {

	
	private Workspace workspace;
	private String projectName;
	private String wsdlUrl;
	
	/**
	 * Map&lt;MR_Name,TestSuite&gt; <br>
	 * MR_Name=projectName + operationName+ mrId
	 */
	protected Map<String, TestSuite> testSuites;
	
	public String getProjectName() {
		return projectName;
	}

	public String getWsdlUrl() {
		return wsdlUrl;
	}

	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public Map<String, TestSuite> getTestSuiteMap() {
		return testSuites;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setWSDLUri(String wsdlUri) {
		this.wsdlUrl = wsdlUri;
	}

	public void setTestSuites(Map<String, TestSuite> testsuites) {
		this.testSuites = testsuites;
	}

	public void print() {
		System.out.println("Project:" + projectName);
		System.out.println("WSDL URL=" + wsdlUrl);
		System.out.println("workspace=" + workspace.getPath());
	}

}
