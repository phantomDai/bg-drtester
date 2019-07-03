package cn.edu.ustb.mt4ws.configuration;

public class Workspace {

	public static final String TOMCAT_HOME = "E:/apache-tomcat-6.0.26/";
	public static final String WORKSPACE = TOMCAT_HOME
			+ "webapps/mt4ws/workspace/";
	private String userName;
	private String path;

	/**
	 * 设置工程的地址
	 * @param path
	 */
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getPath(){
		return path;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserName(){
		return userName; 
	}
	

	

}
