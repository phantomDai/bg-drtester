package cn.edu.ustb.mt4ws.mr;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cn.edu.ustb.mt4ws.mr.model.Operator;
import cn.edu.ustb.mt4ws.tcg.Variable;

public class StrExpParser {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String expS = "123+1234.56";
		String expRF = "1234.56_";
		StrExpParser myParser = new StrExpParser();
		// 判断表达式是否符合要求
		boolean bool = myParser.checkStrExp(expRF, expS, 8);
		System.out.println("Exp是否满足要求：" + bool);
		// 获取增加的Str
		if (bool) {
			String addStr = myParser.getAddStr(expRF, expS, 8);
			System.out.println("增加字段为：" + addStr);
			// 得到FollowUp的值
			String followUpValue = myParser.getFollowUpValue(expRF, addStr, 8);
			System.out.println("FollowUp的值为：" + followUpValue);
		}

	}

	/**
	 * 得到FollowUp的值， 若相等，则值与Source一样 若替换，采取随机生成的办法
	 * 
	 * @param expRF
	 * @param addStr
	 * @param operationType
	 * @return
	 */

	public String getFollowUpValue(String expRF, String addStr,
			int operationType) {
		String followUpValue = "";
		String strTemp = expRF.substring(0, expRF.length() - 1);
		switch (operationType) {
		case Operator.EQ:
			// 相等
			followUpValue = strTemp;
			break;
		case Operator.LA:
			// 左边添加字符串
			followUpValue = addStr.concat(strTemp);
			break;
		case Operator.RA:
			// 右边添加字符串
			followUpValue = strTemp.concat(addStr);
			break;
		case Operator.REP:
			// 中间替换,随机替换中间一个字符，需要改进，目前只支持钱币转换服务
			StrExpParser myParser = new StrExpParser();
			followUpValue = myParser.randomRepStr(strTemp);
			break;
		case Operator.NEQ:
			// 不相等
			followUpValue = strTemp;
			break;
		default:
		}
		return followUpValue;
	}

	/**
	 * 目前只支持钱币转换服务的随机替换 根据是否有小数点做两种判断
	 * 
	 * @param str
	 * @return
	 */

	public String randomRepStr(String str) {
		String strRep = "";
		Random r = new Random();
		int num = r.nextInt(str.length());
		// 是否有小数点
		if (str.contains(".")) {
			int numPoint = str.indexOf(".");
			while (num == numPoint) {
				num = r.nextInt(str.length());
			}
			strRep = str.replace(str.charAt(num), Integer.toString(
					r.nextInt(10)).charAt(0));
		} else {
			strRep = str.replace(str.charAt(num), Integer.toString(
					r.nextInt(10)).charAt(0));
		}
		return strRep;
	}

	/**
	 * 判断RF和S表达式之间是否符合要求
	 * 
	 * @param expRF
	 * @param expS
	 * @param operationType
	 * @return
	 */

	public boolean checkStrExp(String expRF, String expS, int operationType) {
		boolean isSatisfied = false;
		switch (operationType) {
		case Operator.EQ:
			// 相等
			if (expRF.endsWith("_")) {
				String strTemp = expRF.substring(0, expRF.length() - 1);
				System.out.println("RF结尾是_,则前面字符串为：" + strTemp);
				// 判断expRF和expS相等
				if (expS.equals(strTemp)) {

					isSatisfied = true;
				}
			}
			break;
		case Operator.LA:
			// 左边添加字符串
			if (expRF.endsWith("_")) {
				String strTemp = expRF.substring(0, expRF.length() - 1);
				System.out.println("RF结尾是_,则前面字符串为：" + strTemp);
				// expS结尾必须为expRF
				if (expS.endsWith("+" + strTemp)) {
					isSatisfied = true;
				}
			}
			break;
		case Operator.RA:
			// 右边添加字符串
			if (expRF.endsWith("_")) {
				String strTemp = expRF.substring(0, expRF.length() - 1);
				System.out.println("RF结尾是_,则前面字符串为：" + strTemp);
				// expS开头必须为expRF
				if (expS.startsWith(strTemp + "+")) {
					isSatisfied = true;
				}
			}
			break;
		case Operator.REP:
			// 中间替换
			if (expRF.endsWith("_")) {
				String strTemp = expRF.substring(0, expRF.length() - 1);
				System.out.println("RF结尾是_,则前面字符串为：" + strTemp);
				// 判断expRF和expS相等
				if (expS.equals(strTemp)) {
					isSatisfied = true;
				}
			}
			break;
		case Operator.NEQ:
			// 不相等
			if (expRF.endsWith("_")) {
				String strTemp = expRF.substring(0, expRF.length() - 1);
				System.out.println("RF结尾是_,则前面字符串为：" + strTemp);
				// 判断expRF和expS相等
				if (expS.equals(strTemp)) {

					isSatisfied = true;
				}
			}
			break;
		default:
		}
		return isSatisfied;
	}

	/**
	 * 获取增加的字段 若为相等或者替换，则返回为null
	 * 
	 * @param expRF
	 * @param expS
	 * @param operationType
	 * @return
	 */
	public String getAddStr(String expRF, String expS, int operationType) {
		String addStr = "";
		switch (operationType) {
		case Operator.EQ:
			// 相等
			if (expRF.endsWith("_")) {
				String strTemp = expRF.substring(0, expRF.length() - 1);
				System.out.println("RF结尾是_,则前面字符串为：" + strTemp);
				// 判断expRF和expS相等
				if (expS.equals(strTemp)) {
					addStr = null;
				} else
					System.out.println("EQ获取增加字段处错误！！！");
			}
			break;
		case Operator.LA:
			// 左边添加字符串
			if (expRF.endsWith("_")) {
				String strTemp = expRF.substring(0, expRF.length() - 1);
				System.out.println("RF结尾是_,则前面字符串为：" + strTemp);
				// expS结尾必须为expRF
				if (expS.endsWith("+" + strTemp)) {
					addStr = expS.substring(0, expS.length() - 1
							- strTemp.length());
				} else
					System.out.println("LA获取增加字段处错误！！！");

			}
			break;
		case Operator.RA:
			// 右边添加字符串
			if (expRF.endsWith("_")) {
				String strTemp = expRF.substring(0, expRF.length() - 1);
				System.out.println("RF结尾是_,则前面字符串为：" + strTemp);
				// expS开头必须为expRF
				if (expS.startsWith(strTemp + "+")) {
					addStr = expS.substring(1 + strTemp.length());
				} else
					System.out.println("RA获取增加字段处错误！！！");
			}
			break;
		case Operator.REP:
			// 中间替换
			if (expRF.endsWith("_")) {
				String strTemp = expRF.substring(0, expRF.length() - 1);
				System.out.println("RF结尾是_,则前面字符串为：" + strTemp);
				// 判断expRF和expS相等
				if (expS.equals(strTemp)) {
					addStr = null;
				} else
					System.out.println("LA获取增加字段处错误！！！");
			}
			break;
		case Operator.NEQ:
			// 不相等
			if (expRF.endsWith("_")) {
				String strTemp = expRF.substring(0, expRF.length() - 1);
				System.out.println("RF结尾是_,则前面字符串为：" + strTemp);
				// 判断expRF和expS相等
				if (expS.equals(strTemp)) {
					addStr = null;
				} else
					System.out.println("EQ获取增加字段处错误！！！");
			}
			break;
		default:
		}
		return addStr;
	}

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
		String value = "";
		while (iter.hasNext()) {
			myentry = iter.next();
			Variable var = myentry.getKey();
			value = myentry.getValue();
			if (function.contains(var.getName())) {
				System.out.println("Debug!!!!function.contains(var.getName())");
				// this.addVariable(var.getName(), value);
				varSet.add(var);
			}
		}
		return varSet;

	}

}
