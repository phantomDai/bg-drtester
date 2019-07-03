package cn.edu.ustb.mt4ws.mr;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.SchemaType;
import org.nfunk.jep.JEP;

import cn.edu.ustb.mt4ws.tcg.Variable;

/**
 * 表达式解析类，继承自JEP<br>
 * 用于处理数值类表达式的解析
 * 
 * @author WangGuan
 * 
 */

public class MathExpParser extends JEP implements AbstractExpParser {

	
	/**
	 * 初始化
	 * 
	 * @param namesAndValues
	 * @return 所有变量名称
	 */
	public Set<Variable> init(Map<Variable, String> namesAndValues,
			String function) {

		Set<Variable> varSet = new LinkedHashSet<Variable>();
		Set<Map.Entry<Variable, String>> entry = namesAndValues.entrySet();
		Iterator<Map.Entry<Variable, String>> iter = entry.iterator();
		Map.Entry<Variable, String> myentry = null;
		int varType = -1;
		String value = null;
		while (iter.hasNext()) {
			myentry = iter.next();
			Variable var = myentry.getKey();
			value = myentry.getValue();
			if (((SchemaType) var.getType()).getPrimitiveType().getBuiltinTypeCode() == SchemaType.BTC_STRING)
				varType = 1;// var为String型
			else {
				varType = 0;// var为数值型
			}
			if (function.contains(var.getName())) {
				System.out.println("Debug!!!!function.contains(var.getName())="
						+ var.getName());
				if (varType == 1) {
					this.addVariable(var.getName(), value);
				} else if (varType == 0) {
					try {
						double valueDouble = Double.parseDouble(value);
						this.addVariable(var.getName(), valueDouble);
					} catch (NumberFormatException e) {// 当value不是double类型是当做0处理
						System.out
								.println("The value of "
										+ var
										+ " is not double type! We assume the value is 0");
						value = null;
					}
					
				}
				else{
					try {
						throw new Exception("Unknown data type!");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				varSet.add(var);
			}
		}
		return varSet;

	}
	
	
	
	/**
	 * 初始化
	 * @param namesAndValues
	 * @return 所有变量名称
	 */
	/*
	public Set<Variable> init(Map<Variable, String> namesAndValues, String function) {

		Set<Variable> varSet=new LinkedHashSet<Variable>();
		Set<Map.Entry<Variable, String>> entry = namesAndValues.entrySet();
		Iterator<Map.Entry<Variable, String>> iter = entry.iterator();
		Map.Entry<Variable, String> myentry = null;
		double value = 0;
		while (iter.hasNext()) {
			myentry = iter.next();
			Variable var = myentry.getKey();
			try {
				value = Double.parseDouble(myentry.getValue());
			} catch (NumberFormatException e) {// 当value不是double类型是当做0处理
				System.out.println("The value of " + var
						+ " is not double type! We assume the value is String");
				value = 0;
			}
			//判断function中是否包含inputFollowUp变量
			if (function.contains(var.getName())) {
				System.out.println("Debug!!!!function.contains(var.getName())");
				this.addVariable(var.getName(), value);
				varSet.add(var);
			}
		}
		return varSet;
		
	}
	*/


	public void addVariable(String varName, String value) {
		// TODO Auto-generated method stub
		this.addVariable(varName, Long.parseLong(value));
	}

	public void parseExp(String expression) {
		// TODO Auto-generated method stub
		this.parseExpression(expression);
	}

	public String getParsedValue() {
		return this.getValue() + "";
	}

}
