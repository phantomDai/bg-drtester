package cn.edu.ustb.mt4ws.javabean;

public class Function {
  
	private String functionDescription;
	
	/**
	 * 构造函数
	 */
	public Function(){
		
	}

	public Function(String funcDes){
		setFunctionDescription(funcDes);
	}
	
	public void setFunctionDescription(String functionDescription) {
		this.functionDescription = functionDescription;
	}

	public String getFunctionDescription() {
		return functionDescription;
	}
	
	
}
