package cn.edu.ustb.mt4ws.mr;

public interface AbstractExpParser {
	
	public void addVariable(String varName,String value);
	
	public String getParsedValue();
	
	public void parseExp(String expression);
	

}
