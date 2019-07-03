package cn.edu.ustb.mt4ws.mr.model;

public class Function {
	
	private String functionDescription;
	
	public Function(){
		
	}
	
	public Function(String functionDescription){
		setFunctionDescription(functionDescription);
	}

	public void setFunctionDescription(String functionDescription) {
		this.functionDescription = functionDescription;
	}

	public String getFunctionDescription() {
		return functionDescription;
	}

}
