package cn.edu.ustb.mt4ws.mr;

import org.nfunk.jep.*;
/**
 * 数学表达式解析
 * @author qing.wen
 *
 */

public class JEPTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		JEP jep = new JEP(); //一个数学表达式
		String exp = "((a+b)*(c+b))/(c+a)/b"; //给变量赋值
		jep.addVariable("a", 10);
	    jep.addVariable("b", 10);
		jep.addVariable("c", 10);
		//执行
			//若想要计算表达式中涉及到三角函数或者pi，e；
			jep.addStandardConstants();
			jep.addStandardFunctions();
		Node node=jep.parseExpression(exp);
		Object result;
		try {
			result = jep.evaluate(node);
			System.out.println("计算结果： " + result);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
         



	}

}
