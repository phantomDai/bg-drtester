package cn.edu.ustb.mt4ws.tcg;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.xmlbeans.SchemaType;

import cn.edu.ustb.mt4ws.mr.MRUtils;
import cn.edu.ustb.mt4ws.mr.MathExpParser;
import cn.edu.ustb.mt4ws.mr.QuakeExpParser;
import cn.edu.ustb.mt4ws.mr.RMBExpTest;
import cn.edu.ustb.mt4ws.mr.StrExpParser;
import cn.edu.ustb.mt4ws.mr.model.Operator;
import cn.edu.ustb.mt4ws.mr.model.Relation;
import cn.edu.ustb.mt4ws.mr.model.RelationOfInput;

public class Modifier {

	// private Map<String, Operator> opList;

	/**
	 * 只针对地震服务测试
	 * 
	 * @param relationOfInput
	 *            又MRDL解析来的relationOfMessage元素
	 * @param inputSource
	 *            原始用例输入向量
	 * @return 衍生测试用例
	 * @throws Exception
	 */
	public Map modifyQuakes(RelationOfInput relationOfInput,
			Map<Variable, String> inputSource) throws Exception {
		// 复制inputSource，在Variable.getName中添加下划线
		Map<Variable, String> inputFollowUp = copyInput(inputSource);
		// 解析式：quakes中较为简单，只是变量，并没有复杂的解析方程，TODO

		QuakeExpParser myParserS = new QuakeExpParser();// parser for source
		QuakeExpParser myParserF = new QuakeExpParser();// parser for follow-up

		Iterator<Relation> iterRelation = relationOfInput.getRelationOfInput()
				.iterator();
		// 遍历蜕变关系中inputRelation
		while (iterRelation.hasNext()) {
			Relation relation = iterRelation.next();
			String functionSource = relation.getFunctionSource()
					.getFunctionDescription();
			String functionFollowUp = relation.getFunctionFollowUp()
					.getFunctionDescription();
			Operator op = relation.getOp();

			// 判断输入的解析式是否正确，quakes中较为简单，只能为变量或者变量+_,TODO
			boolean isSatisfiedS = myParserS.checkExp(inputSource,
					functionSource);
			boolean isSatisfiedF = myParserF.checkExp(inputFollowUp,
					functionFollowUp);
			if (!isSatisfiedS || !isSatisfiedF) {
				System.out.println("quakes服务中输入解析式出错……");
				System.exit(0);
			} else {
				System.out.println("quakes服务中输入解析式正常……");
				inputFollowUp = myParserF.setValue(inputFollowUp, relation);
			}

		}

		return inputFollowUp;

	}

	/**
	 * 由原始用例产生衍生用例 time:20130421
	 * 
	 * @param relationOfInput
	 * @param inputSource
	 * @return
	 */
	public Map modifyTransfer(
			cn.edu.ustb.mt4ws.javabean.RelationOfInput relationOfInput,
			Map<Variable, String> inputSource) {

		Map<Variable, String> inputFollowUp = copyInput(inputSource);

		// Debug
		StringBuffer inputVector = new StringBuffer();
		Set<Map.Entry<Variable, String>> entry = inputFollowUp.entrySet();
		Iterator<Map.Entry<Variable, String>> iter11 = entry.iterator();
		while (iter11.hasNext()) {
			Map.Entry<Variable, String> myEntry = iter11.next();
			inputVector.append(myEntry.getKey().getName());
			inputVector.append(myEntry.getValue());
		}

		cn.edu.ustb.mt4ws.javabean.Relation relation1 = relationOfInput
				.getRelation();
		List<cn.edu.ustb.mt4ws.javabean.OpAndRelation> opAndRelationList = null;

		try {
			// 根据表达式设置第一个relation产生的followUp
			inputFollowUp = setValueByExp(inputFollowUp, inputSource, relation1);
			// 判断是否有opAndRelationList，有则循环
			if (relationOfInput.getListOpAndRe() != null) {
				opAndRelationList = relationOfInput.getListOpAndRe();
				Iterator<cn.edu.ustb.mt4ws.javabean.OpAndRelation> opAndRelationIter = opAndRelationList
						.iterator();
				// 遍历
				while (opAndRelationIter.hasNext()) {
					cn.edu.ustb.mt4ws.javabean.OpAndRelation opAndRelation = opAndRelationIter
							.next();
					if (opAndRelation.getOperator().getType() == cn.edu.ustb.mt4ws.javabean.Operator.AND) {
						// 当relation之间的operator是and
						System.out.println("'And' Operator");
						cn.edu.ustb.mt4ws.javabean.Relation relation = opAndRelation
								.getRelation();

						inputFollowUp = setValueByExp(inputFollowUp,
								inputSource, relation);
					} else if (opAndRelation.getOperator().getType() == cn.edu.ustb.mt4ws.javabean.Operator.OR) {
						// 当relation之间的operator是OR
						System.out.println("'OR' Operator");

					}

				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return inputFollowUp;
	}


	/**
	 * 由原始用例产生衍生用例 time:20130715
	 * BPEL 例子测试
	 * @param relationOfInput
	 * @param inputSource
	 * @return
	 */
	public Map modifyProcess(
			cn.edu.ustb.mt4ws.javabean.RelationOfInput relationOfInput,
			Map<Variable, String> inputSource) {

		Map<Variable, String> inputFollowUp = copyInput(inputSource);

		// Debug
		StringBuffer inputVector = new StringBuffer();
		Set<Map.Entry<Variable, String>> entry = inputFollowUp.entrySet();
		Iterator<Map.Entry<Variable, String>> iter11 = entry.iterator();
		while (iter11.hasNext()) {
			Map.Entry<Variable, String> myEntry = iter11.next();
			inputVector.append(myEntry.getKey().getName());
			inputVector.append(myEntry.getValue());
		}

		cn.edu.ustb.mt4ws.javabean.Relation relation1 = relationOfInput
				.getRelation();
		List<cn.edu.ustb.mt4ws.javabean.OpAndRelation> opAndRelationList = null;

		try {
			// 根据表达式设置第一个relation产生的followUp
			inputFollowUp = setValueByExp(inputFollowUp, inputSource, relation1);
			// 判断是否有opAndRelationList，有则循环
			if (relationOfInput.getListOpAndRe() != null) {
				opAndRelationList = relationOfInput.getListOpAndRe();
				Iterator<cn.edu.ustb.mt4ws.javabean.OpAndRelation> opAndRelationIter = opAndRelationList
						.iterator();
				// 遍历
				while (opAndRelationIter.hasNext()) {
					cn.edu.ustb.mt4ws.javabean.OpAndRelation opAndRelation = opAndRelationIter
							.next();
					if (opAndRelation.getOperator().getType() == cn.edu.ustb.mt4ws.javabean.Operator.AND) {
						// 当relation之间的operator是and
						System.out.println("'And' Operator");
						cn.edu.ustb.mt4ws.javabean.Relation relation = opAndRelation
								.getRelation();

						inputFollowUp = setValueByExp(inputFollowUp,
								inputSource, relation);
					} else if (opAndRelation.getOperator().getType() == cn.edu.ustb.mt4ws.javabean.Operator.OR) {
						// 当relation之间的operator是OR
						System.out.println("'OR' Operator");

					}

				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return inputFollowUp;
	}

	
	/**
	 * 根据数学公式，设置followUp的值
	 * 
	 * @param inputFollowUp
	 * @param relation
	 * @return
	 * @throws Exception
	 */
	private Map<Variable, String> setValueByExp(
			Map<Variable, String> inputFollowUp,
			Map<Variable, String> inputSource,
			cn.edu.ustb.mt4ws.javabean.Relation relation) throws Exception {
		XmlTcgUtils tcgUtils = new XmlTcgUtils();

		MathExpParser myParserS = new MathExpParser();// parser for source
		// testcase
		myParserS.addStandardConstants();
		myParserS.addStandardFunctions();
		MathExpParser myParserF = new MathExpParser();// parser for follow-up
		// test case
		myParserF.addStandardConstants();
		myParserF.addStandardFunctions();

		String functionSource = relation.getFunctionSource()
				.getFunctionDescription();
		String functionFollowUp = relation.getFunctionFollowUp()
				.getFunctionDescription();
		cn.edu.ustb.mt4ws.javabean.Operator negOp = relation.getNotOp();
		cn.edu.ustb.mt4ws.javabean.Operator op = relation.getOp();
		if (negOp.getType() == negOp.NEG) {
			op = cn.edu.ustb.mt4ws.javabean.Operator.mapNEGOp(op);
		}
		myParserS.init(inputSource, functionSource);
		myParserS.parseExpression(functionSource);

		Set<Variable> varSetFollowUp = myParserF.init(inputFollowUp,
				functionFollowUp);

		if (varSetFollowUp.size() == 0) {// TODO
			System.out.println("Testing Info:Limitation Scource Test Case");
		} else {
			System.out.println("Testing Info:Random Scource Test Case");
		}

		myParserF.parseExpression(functionFollowUp);
		long valueOfFunctionSource = (long) myParserS.getValue();// functionSource的值
		long valueOfFunctionFollowUp = (long) myParserF.getValue();// functionFollow的值
		if (Double.isNaN(valueOfFunctionSource)
				|| Double.isNaN(valueOfFunctionFollowUp)) {
			throw new Exception(
					"*Error*: variables in relation and input can't match!!!: ");
		}
		Iterator<Variable> iter = varSetFollowUp.iterator();
		while (op.validate(valueOfFunctionFollowUp, valueOfFunctionSource) == false) {
			while (iter.hasNext()) {
				Variable var = iter.next();
				if (var instanceof XmlVariable)// TODO
				// 非XML格式的TestCase后续将支持，生成随机值算法需改进
				{
					if (varSetFollowUp.size() > 1) {
						myParserF.setVarValue(var.getName(), tcgUtils
								.sampleDataForSimpleType((SchemaType) var
										.getType(), null));

					} else {
						myParserF.setVarValue(var.getName(), MRUtils
								.getFollowUpValue(valueOfFunctionSource, op
										.getType()));
						break;
					}

				} else {
					String temp = TcgUtils.createRandomValue(((Type) var
							.getType()).getType());
					myParserF.setVarValue(var.getName(), Double
							.parseDouble(temp));

				}
			}
			valueOfFunctionFollowUp = (long) myParserF.getValue();
			System.out.println(valueOfFunctionFollowUp + "@"
					+ valueOfFunctionSource);
		}
		iter = varSetFollowUp.iterator();
		while (iter.hasNext()) {
			Variable var = iter.next();
			inputFollowUp.put(var, ""
					+ (long) Double.parseDouble(myParserF.getVarValue(
							var.getName()).toString()));
		}

		return inputFollowUp;
	}

	/**
	 * 由原始用例产生衍生用例
	 * 
	 * @param relationOfInput
	 *            又MRDL解析来的relationOfMessage元素
	 * @param inputSource
	 *            原始用例输入向量
	 * @return 衍生测试用例
	 * @throws Exception
	 */
	public Map modify(RelationOfInput relationOfInput,
			Map<Variable, String> inputSource) throws Exception {

		XmlTcgUtils tcgUtils = new XmlTcgUtils();
		Map<Variable, String> inputFollowUp = copyInput(inputSource);

		// Debug
		StringBuffer inputVector = new StringBuffer();
		Set<Map.Entry<Variable, String>> entry = inputFollowUp.entrySet();
		Iterator<Map.Entry<Variable, String>> iter11 = entry.iterator();
		while (iter11.hasNext()) {
			Map.Entry<Variable, String> myEntry = iter11.next();
			inputVector.append(myEntry.getKey().getName());
			inputVector.append(myEntry.getValue());
		}
		MathExpParser myParserS = new MathExpParser();// parser for source
		// testcase
		myParserS.addStandardConstants();
		myParserS.addStandardFunctions();
		MathExpParser myParserF = new MathExpParser();// parser for follow-up
		// test case
		myParserF.addStandardConstants();
		myParserF.addStandardFunctions();

		Iterator<Relation> iterRelation = relationOfInput.getRelationOfInput()
				.iterator();
		while (iterRelation.hasNext()) {
			Relation relation = iterRelation.next();
			String functionSource = relation.getFunctionSource()
					.getFunctionDescription();
			String functionFollowUp = relation.getFunctionFollowUp()
					.getFunctionDescription();
			Operator op = relation.getOp();

			myParserS.init(inputSource, functionSource);
			myParserS.parseExpression(functionSource);

			Set<Variable> varSetFollowUp = myParserF.init(inputFollowUp,
					functionFollowUp);

			if (varSetFollowUp.size() == 0) {// TODO
				System.out.println("Testing Info:Limitation Scource Test Case");
			} else {
				System.out.println("Testing Info:Random Scource Test Case");
			}

			myParserF.parseExpression(functionFollowUp);
			long valueOfFunctionSource = (long) myParserS.getValue();// functionSource的值
			long valueOfFunctionFollowUp = (long) myParserF.getValue();// functionFollow的值
			if (Double.isNaN(valueOfFunctionSource)
					|| Double.isNaN(valueOfFunctionFollowUp)) {
				throw new Exception(
						"*Error*: variables in relation and input can't match!!!: "
								+ relationOfInput + " "
								+ inputVector.toString());
			}
			Iterator<Variable> iter = varSetFollowUp.iterator();
			while (op.validate(valueOfFunctionFollowUp, valueOfFunctionSource) == false) {
				while (iter.hasNext()) {
					Variable var = iter.next();
					if (var instanceof XmlVariable)// TODO
					// 非XML格式的TestCase后续将支持，生成随机值算法需改进
					{
						if (varSetFollowUp.size() > 1) {
							myParserF.setVarValue(var.getName(), tcgUtils
									.sampleDataForSimpleType((SchemaType) var
											.getType(), null));

						} else {
							myParserF.setVarValue(var.getName(), MRUtils
									.getFollowUpValue(valueOfFunctionSource, op
											.getType()));
							break;
						}

					} else {
						String temp = TcgUtils.createRandomValue(((Type) var
								.getType()).getType());
						myParserF.setVarValue(var.getName(), Double
								.parseDouble(temp));

					}
				}
				valueOfFunctionFollowUp = (long) myParserF.getValue();
				System.out.println(valueOfFunctionFollowUp + "@"
						+ valueOfFunctionSource);
			}
			iter = varSetFollowUp.iterator();
			while (iter.hasNext()) {
				Variable var = iter.next();
				inputFollowUp.put(var, ""
						+ (long) Double.parseDouble(myParserF.getVarValue(
								var.getName()).toString()));
			}

		}
		return inputFollowUp;
	}

	// /**
	// * 由原始用例产生衍生用例
	// *
	// * @param relationOfInput
	// * 又MRDL解析来的relationOfMessage元素
	// * @param inputSource
	// * 原始用例输入向量
	// * @return 衍生测试用例
	// * @throws Exception
	// */
	// public Map modify(RelationOfInput relationOfInput,
	// Map<Variable, String> inputSource) throws Exception {
	//
	// XmlTcgUtils tcgUtils = new XmlTcgUtils();
	//
	// Map<Variable, String> inputFollowUp = copyInput(inputSource);
	//
	// // Debug
	// StringBuffer inputVector = new StringBuffer();
	// Set<Map.Entry<Variable, String>> entry = inputFollowUp.entrySet();
	// Iterator<Map.Entry<Variable, String>> iter11 = entry.iterator();
	// while (iter11.hasNext()) {
	// Map.Entry<Variable, String> myEntry = iter11.next();
	// inputVector.append(myEntry.getKey().getName());
	// inputVector.append(myEntry.getValue());
	// }
	//
	// MathExpParser myParserS = new MathExpParser();// parser for source
	// // testcase
	// myParserS.addStandardConstants();
	// myParserS.addStandardFunctions();
	// MathExpParser myParserF = new MathExpParser();// parser for follow-up
	// // test case
	// myParserF.addStandardConstants();
	// myParserF.addStandardFunctions();
	//
	// Iterator<Relation> iterRelation = relationOfInput.getRelationOfInput()
	// .iterator();
	// while (iterRelation.hasNext()) {
	// Relation relation = iterRelation.next();
	// String functionSource = relation.getFunctionSource()
	// .getFunctionDescription();
	// String functionFollowUp = relation.getFunctionFollowUp()
	// .getFunctionDescription();
	// Operator op = relation.getOp();
	//
	// myParserS.init(inputSource, functionSource);
	// myParserS.parseExpression(functionSource);
	//
	// // 初试化：将inputFollowUp和function进行联系，返回Variable的集合
	// Set<Variable> varSetFollowUp = myParserF.init(inputFollowUp,
	// functionFollowUp);
	//
	// if (varSetFollowUp.size() == 0) {// TODO
	// System.out.println("Testing Info:Limitation Scource Test Case");
	// } else {
	// System.out.println("Testing Info:Random Scource Test Case");
	// }
	// // 解析数学表达式
	// myParserF.parseExpression(functionFollowUp);
	// double valueOfFunctionSource = myParserS.getValue();// functionSource的值
	// double valueOfFunctionFollowUp = myParserF.getValue();// functionFollow的值
	// if (Double.isNaN(valueOfFunctionSource)
	// || Double.isNaN(valueOfFunctionFollowUp)) {
	// throw new Exception(
	// "*Error*: variables in relation and input can't match!!!: "
	// + relationOfInput + " "
	// + inputVector.toString());
	// }
	// Iterator<Variable> iter = varSetFollowUp.iterator();
	// while (op.validate(valueOfFunctionFollowUp, valueOfFunctionSource) ==
	// false) {
	// while (iter.hasNext()) {
	// Variable var = iter.next();
	// if (var instanceof XmlVariable)// TODO
	// // 非XML格式的TestCase后续将支持，生成随机值算法需改进
	// {
	// if (varSetFollowUp.size() > 1) {
	// myParserF.setVarValue(var.getName(), tcgUtils
	// .sampleDataForSimpleType((SchemaType) var
	// .getType(), null));
	//
	// } else {
	// myParserF.setVarValue(var.getName(), MRUtils
	// .getFollowUpValue(valueOfFunctionSource, op
	// .getType()));
	// break;
	// }
	//
	// } else {
	// String temp = TcgUtils.createRandomValue(((Type) var
	// .getType()).getType());
	// myParserF.setVarValue(var.getName(), Double
	// .parseDouble(temp));
	//
	// }
	// }
	// valueOfFunctionFollowUp = myParserF.getValue();
	// System.out.println("valueOfFunctionFollowUp:"
	// + valueOfFunctionFollowUp + "\n"
	// + "valueOfFunctionSource:" + valueOfFunctionSource);
	// }
	// iter = varSetFollowUp.iterator();
	// while (iter.hasNext()) {
	// Variable var = iter.next();
	// inputFollowUp.put(var, myParserF.getVarValue(var.getName())
	// .toString());
	// }
	//
	// }
	//
	// return inputFollowUp;
	//
	// }
	//	
	/**
	 * 只针对RMB转换服务测试
	 * 
	 * @param relationOfInput
	 *            又MRDL解析来的relationOfMessage元素
	 * @param inputSource
	 *            原始用例输入向量
	 * @return 衍生测试用例
	 * @throws Exception
	 */

	public Map modifyStr(RelationOfInput relationOfInput,
			Map<Variable, String> inputSource) throws Exception {

		// 将原始用例输入复制到衍生用例输入，并把所有变量加下划线
		Map<Variable, String> inputFollowUp = copyInput(inputSource);

		// Debug
		StringBuffer inputVector = new StringBuffer();
		Set<Map.Entry<Variable, String>> entry = inputFollowUp.entrySet();
		Iterator<Map.Entry<Variable, String>> iter11 = entry.iterator();
		while (iter11.hasNext()) {
			Map.Entry<Variable, String> myEntry = iter11.next();
			inputVector.append(myEntry.getKey().getName());
			inputVector.append(myEntry.getValue());
		}

		// parser for source test case
		StrExpParser myParserS = new StrExpParser();
		// parser for followUp test case
		StrExpParser myParserF = new StrExpParser();
		// 得到Relation集合，开始逐个遍历
		Iterator<Relation> iterRelation = relationOfInput.getRelationOfInput()
				.iterator();
		while (iterRelation.hasNext()) {
			Relation relation = iterRelation.next();
			String functionSource = relation.getFunctionSource()
					.getFunctionDescription();
			String functionFollowUp = relation.getFunctionFollowUp()
					.getFunctionDescription();
			Operator op = relation.getOp();
			int opType = op.getType();

			// followUp初试化
			Set<Variable> varSetFollowUp = myParserF.init(inputFollowUp,
					functionFollowUp);

			if (varSetFollowUp.size() == 0) {// TODO
				System.out.println("Testing Info:Limitation Scource Test Case");
			} else {
				System.out.println("Testing Info:Random Scource Test Case");
			}

			Iterator<Variable> iter = varSetFollowUp.iterator();
			// 判断表达式是否符合要求
			boolean bool = myParserF.checkStrExp(functionFollowUp,
					functionSource, opType);
			System.out.println("Exp是否满足要求：" + bool);
			// 获取增加的Str
			if (bool) {
				String addStr = myParserF.getAddStr(functionFollowUp,
						functionSource, opType);
				System.out.println("增加字段为：" + addStr);
				// 得到FollowUp的值
				String followUpValue = myParserF.getFollowUpValue(
						functionFollowUp, addStr, opType);
				System.out.println("FollowUp为：" + followUpValue);
				while (iter.hasNext()) {
					Variable var = iter.next();
					String followUpName = var.getName().substring(0,
							var.getName().length() - 1);
					followUpValue = followUpValue.replaceAll(followUpName,
							inputFollowUp.get(var));
					System.out.println("followUp的值为" + followUpValue);
					inputFollowUp.put(var, followUpValue);
				}
			} else
				System.out.println("输入蜕变关系表达式不满足要求！！！");
		}

		return inputFollowUp;

	}

	/**
	 * 只针对RMB转换服务测试 为做实验专门写的method
	 * 
	 * @param relationOfInput
	 *            又MRDL解析来的relationOfMessage元素
	 * @param inputSource
	 *            原始用例输入向量
	 * @return 衍生测试用例
	 * @throws Exception
	 */

	public Map modifyRMB(RelationOfInput relationOfInput,
			Map<Variable, String> inputSource) throws Exception {

		// 将原始用例输入复制到衍生用例输入，并把所有变量加下划线
		Map<Variable, String> inputFollowUp = copyInput(inputSource);

		// parser for source and FollowUp test case
		RMBExpTest rmbParser = new RMBExpTest();

		// 得到Relation集合，开始逐个遍历
		Iterator<Relation> iterRelation = relationOfInput.getRelationOfInput()
				.iterator();
		while (iterRelation.hasNext()) {
			Relation relation = iterRelation.next();
			String functionSource = relation.getFunctionSource()
					.getFunctionDescription();
			String functionFollowUp = relation.getFunctionFollowUp()
					.getFunctionDescription();
			Operator op = relation.getOp();
			int opType = op.getType();

			// 检查inputRelation的表达式是否正确
			boolean isSatisfied = rmbParser.checkInputExp(relation);
			System.out.println("Exp是否满足要求：" + isSatisfied);
			// 判断表达式是否符合要求
			if (isSatisfied) {
				inputFollowUp = rmbParser.setInputFollowUp(inputFollowUp,
						relation);

			} else {
				System.out.println("输入蜕变关系表达式不满足要求！！！");
				System.exit(0);
			}
		}

		return inputFollowUp;

	}

	/**
	 * 将原始用例输入复制到衍生用例输入，并把所有变量加下划线
	 * 
	 * @param inputSource
	 * @return
	 */
	private Map<Variable, String> copyInput(Map<Variable, String> inputSource) {
		Map<Variable, String> inputFollowUp = new LinkedHashMap<Variable, String>();
		Set<Map.Entry<Variable, String>> entry = inputSource.entrySet();
		Iterator<Map.Entry<Variable, String>> iter = entry.iterator();
		Map.Entry<Variable, String> myEntry = null;
		Variable var;
		String value;
		while (iter.hasNext()) {
			myEntry = iter.next();
			var = myEntry.getKey();
			value = myEntry.getValue();
			if (var instanceof XmlVariable) {
				inputFollowUp.put(new XmlVariable(var.getName() + "_", var
						.getType()), value);
			} else {
				inputFollowUp.put(new Variable(var.getName() + "_", var
						.getType()), value);
			}
		}
		return inputFollowUp;
	}

}
