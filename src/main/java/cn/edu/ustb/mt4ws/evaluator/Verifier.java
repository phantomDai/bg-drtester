package cn.edu.ustb.mt4ws.evaluator;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.ustb.mt4ws.mr.MathExpParser;
import cn.edu.ustb.mt4ws.mr.RMBExpTest;
import cn.edu.ustb.mt4ws.mr.StrExpParser;
import cn.edu.ustb.mt4ws.mr.model.Operator;
import cn.edu.ustb.mt4ws.mr.model.Relation;
import cn.edu.ustb.mt4ws.mr.model.RelationOfOutput;
import cn.edu.ustb.mt4ws.tcg.Variable;
import cn.edu.ustb.mt4ws.tcg.XmlVariable;

public class Verifier {
	
	/**
	 * 验证地震查询测试中outputS和outputF之间的关系
	 * TODO
	 * @param outputSource
	 * @param outputFollowUp
	 * @param rm
	 * @return
	 */
	public boolean verify(List<String> outputSource,
			List<String> outputFollowUp, RelationOfOutput rm) {
		boolean isPassed = true;
		//因为输入的蜕变关系中output只能为sum(quake),其余表达式均为错误，因此关系只有一种，故只有一次循环
		Iterator<Relation> iterRelation = rm.getRelationOfOutput().iterator();
		while (iterRelation.hasNext() && isPassed == true) {
			Relation relation = iterRelation.next();
			String functionSource = relation.getFunctionSource()
					.getFunctionDescription();
			String functionFollowUp = relation.getFunctionFollowUp()
					.getFunctionDescription();
			Operator op = relation.getOp();
			//判断functionSource和functionFollowUp表达式是否正确
			if(functionSource.equals("sum(quake)")&&functionFollowUp.equals("sum(quake)_")){
				System.out.println("*****表达式正确****");
				System.out.println("MR：" + functionFollowUp+op.opNames[op.getType()]+functionSource);
				
				
				
				isPassed &= op.validate(outputFollowUp, outputSource);
				
				
			}else{
				System.out.println("*****表达式错误****");
				System.exit(0);
			}
			
		}

		return isPassed;
	}
	
	/**
	 * 验证transfer，实现and和or；
	 * @param outputSource
	 * @param outputFollowUp
	 * @param ro
	 * @return
	 */
	public boolean verify(Map outputSource, Map outputFollowUp,
			cn.edu.ustb.mt4ws.javabean.RelationOfOutput ro) {
		outputFollowUp = copyOutput(outputFollowUp);

		StringBuffer outputVectorS = new StringBuffer();
		Set<Map.Entry<Variable, String>> entry = outputSource.entrySet();
		Iterator<Map.Entry<Variable, String>> iter11 = entry.iterator();
		while (iter11.hasNext()) {
			Map.Entry<Variable, String> myEntry = iter11.next();
			outputVectorS.append(myEntry.getKey().getName()).append('=');
			outputVectorS.append(myEntry.getValue()).append(' ');
		}

		StringBuffer outputVectorF = new StringBuffer();
		Set<Map.Entry<Variable, String>> entry2 = outputFollowUp.entrySet();
		Iterator<Map.Entry<Variable, String>> iter22 = entry2.iterator();
		while (iter22.hasNext()) {
			Map.Entry<Variable, String> myEntry = iter22.next();
			outputVectorF.append(myEntry.getKey().getName()).append('=');
			outputVectorF.append(myEntry.getValue()).append(' ');
		}
		
		//isPassed判断测试是否通过
		boolean isPassed=false;
		cn.edu.ustb.mt4ws.javabean.Relation relation1=ro.getRelation();
		List<cn.edu.ustb.mt4ws.javabean.OpAndRelation> opAndRelationList = null;
		
		//判断第一个relation
		isPassed=checkOutput(outputSource,outputFollowUp,relation1);
		//遍历后面的relations
		if (ro.getListOpAndRe() != null) {
			opAndRelationList = ro.getListOpAndRe();
			Iterator<cn.edu.ustb.mt4ws.javabean.OpAndRelation> opAndRelationIter = opAndRelationList
					.iterator();
			// 遍历
			while (opAndRelationIter.hasNext()&&isPassed) {
				cn.edu.ustb.mt4ws.javabean.OpAndRelation opAndRelation = opAndRelationIter
						.next();
				cn.edu.ustb.mt4ws.javabean.Relation relation = opAndRelation
						.getRelation();
				if (opAndRelation.getOperator().getType() == cn.edu.ustb.mt4ws.javabean.Operator.AND) {
					// 当relation之间的operator是and
					System.out.println("'And' Operator");
					isPassed=isPassed&&checkOutput(outputSource,outputFollowUp,relation);
										
				} else if (opAndRelation.getOperator().getType() == cn.edu.ustb.mt4ws.javabean.Operator.OR) {
					// 当relation之间的operator是OR
					System.out.println("'OR' Operator");
					isPassed=isPassed||checkOutput(outputSource,outputFollowUp,relation);

				}

			}
			
		}else{
			System.out.println("output中只有一个relation");
		}
		
		
		return isPassed;
	}
	
	/**
	 * 判断Output中每个relation是否符合要求
	 * @param outputSource
	 * @param outputFollowUp
	 * @return
	 */
	private boolean checkOutput(Map outputSource,Map outputFollowUp,cn.edu.ustb.mt4ws.javabean.Relation relation){
		boolean isPassed=false;
		MathExpParser parserSource = new MathExpParser();
		MathExpParser parserFollowUp = new MathExpParser();
		String functionSource=relation.getFunctionSource().getFunctionDescription();
		String functionFollowUp =relation.getFunctionFollowUp().getFunctionDescription();
		cn.edu.ustb.mt4ws.javabean.Operator op = relation.getOp();
		cn.edu.ustb.mt4ws.javabean.Operator negOp=relation.getNotOp();
		
		if (negOp.getType() == negOp.NEG) {
			op = cn.edu.ustb.mt4ws.javabean.Operator.mapNEGOp(op);
		}
		
		parserSource.init(outputSource,functionSource);
		parserFollowUp.init(outputFollowUp,functionFollowUp);
		parserSource.parseExpression(functionSource);
		parserFollowUp.parseExpression(functionFollowUp);
		double resultSource = parserSource.getValue();
		double resultFollowUp = parserFollowUp.getValue();

		System.out.println(resultFollowUp+Operator.ops[op.getType()]+resultSource+"???");
		isPassed=op.validate(resultFollowUp, resultSource);
		return isPassed;
	}
	
	

	/**
	 * Verify the outputs of source test case and follow-up test case
	 * @param outputSource
	 * @param outputFollowUp
	 * @param rm
	 * @return true if test case passed, false if test case failed
	 */
	public boolean verify(Map outputSource,
			Map outputFollowUp, RelationOfOutput rm) {
		
		outputFollowUp = copyOutput(outputFollowUp);
		
		StringBuffer outputVectorS = new StringBuffer();
		Set<Map.Entry<Variable, String>> entry = outputSource.entrySet();
		Iterator<Map.Entry<Variable, String>> iter11 = entry.iterator();
		while (iter11.hasNext()) {
			Map.Entry<Variable, String> myEntry = iter11.next();
			outputVectorS.append(myEntry.getKey().getName()).append('=');
			outputVectorS.append(myEntry.getValue()).append(' ');
		}
		
		StringBuffer outputVectorF = new StringBuffer();
		Set<Map.Entry<Variable, String>> entry2 = outputFollowUp.entrySet();
		Iterator<Map.Entry<Variable, String>> iter22 = entry2.iterator();
		while (iter22.hasNext()) {
			Map.Entry<Variable, String> myEntry = iter22.next();
			outputVectorF.append(myEntry.getKey().getName()).append('=');
			outputVectorF.append(myEntry.getValue()).append(' ');
		}

		MathExpParser parserSource = new MathExpParser();
		MathExpParser parserFollowUp = new MathExpParser();

		Iterator<Relation> iterRelation = rm.getRelationOfOutput().iterator();
		boolean isPassed = true;
		while (iterRelation.hasNext() && isPassed == true) {
			Relation relation = iterRelation.next();
			String functionSource = relation.getFunctionSource()
					.getFunctionDescription();
			String functionFollowUp = relation.getFunctionFollowUp()
					.getFunctionDescription();
			Operator op = relation.getOp();
			
			parserSource.init(outputSource,functionSource);
			parserFollowUp.init(outputFollowUp,functionFollowUp);
			parserSource.parseExpression(functionSource);
			parserFollowUp.parseExpression(functionFollowUp);
			double resultSource = parserSource.getValue();
			double resultFollowUp = parserFollowUp.getValue();
			System.out.println(outputVectorS.toString());
			System.out.println(outputVectorF.toString());
			System.out.println(resultFollowUp+Operator.ops[op.getType()]+resultSource+"???");
			isPassed &= op.validate(resultFollowUp, resultSource);
		}
		return isPassed;
	}
	
	/**
	 * 验证钱币转换服务的结果
	 * if testcase passed ,return true
	 * else return false
	 * @param outputSource
	 * @param outputFollowUp
	 * @param rm
	 * @return
	 */
	
	public boolean verifyStr(Map outputSource,
			Map outputFollowUp, RelationOfOutput rm) {
		boolean isPassed = true;
		//把衍生用例所有变量加下划线
		outputFollowUp = copyOutput(outputFollowUp);

		StringBuffer outputVectorS = new StringBuffer();
		Set<Map.Entry<Variable, String>> entry = outputSource.entrySet();
		Iterator<Map.Entry<Variable, String>> iter11 = entry.iterator();
		while (iter11.hasNext()) {
			Map.Entry<Variable, String> myEntry = iter11.next();
			outputVectorS.append(myEntry.getKey().getName()).append('=');
			outputVectorS.append(myEntry.getValue()).append(' ');
		}

		StringBuffer outputVectorF = new StringBuffer();
		Set<Map.Entry<Variable, String>> entry2 = outputFollowUp.entrySet();
		Iterator<Map.Entry<Variable, String>> iter22 = entry2.iterator();
		while (iter11.hasNext()) {
			Map.Entry<Variable, String> myEntry = iter22.next();
			outputVectorF.append(myEntry.getKey().getName()).append('=');
			outputVectorF.append(myEntry.getValue()).append(' ');
		}

		StrExpParser parserSource = new StrExpParser();
		StrExpParser parserFollowUp = new StrExpParser();
		//relation迭代器
		Iterator<Relation> iterRelation = rm.getRelationOfOutput().iterator();
		//有蜕变关系，且ispassed 是true
		while (iterRelation.hasNext() && isPassed == true) {
			//获得蜕变关系
			Relation relation = iterRelation.next();
			String functionSource = relation.getFunctionSource()
					.getFunctionDescription();
			String functionFollowUp = relation.getFunctionFollowUp()
					.getFunctionDescription();
			Operator op = relation.getOp();
			int opType=op.getType();
			
            String valueS="";
            String valueF="";
            //遍历outputSource,获得相应的值
            iter11 = entry.iterator();
            iter22 = entry2.iterator();
			while (iter11.hasNext()) {
				Map.Entry<Variable, String> myEntryS = iter11.next();
				String name=myEntryS.getKey().getName();
				if(functionSource.contains(name)){
					System.out.println("Debug!!!!function.contains("+name+")");
					valueS=myEntryS.getValue();
					//遍历outputFollowUp
					while (iter22.hasNext()) {
						Map.Entry<Variable, String> myEntryF = iter22.next();
						if(functionFollowUp.contains(name+"_")){
							System.out.println("Debug!!!!function.contains("+name+"_)");
							valueF=myEntryF.getValue();
							break;
						}
					}
					break;
				}
			}
			
			boolean bool = parserFollowUp.checkStrExp(functionFollowUp,
					functionSource, opType);
			System.out.println("Exp是否满足要求：" + bool);
			// 获取增加的Str
			if (bool) {
				String addStr = parserFollowUp.getAddStr(functionFollowUp,
						functionSource, opType);
				String resultName = functionFollowUp.substring(0,functionFollowUp.length()-1);
				System.out.println("增加字段为：" + addStr);
				// 得到FollowUp的值
				String valueS_ = parserFollowUp.getFollowUpValue(
						functionFollowUp, addStr, opType);
				valueS = valueS_.replaceAll(resultName, valueS);
				System.out.println("测试的Source结果值为：" + valueS);
								
			} else
				System.out.println("输入蜕变关系表达式不满足要求！！！");
			
			System.out.println(outputVectorS.toString());
			System.out.println(outputVectorF.toString());
			System.out.println(valueF + Operator.ops[op.getType()]
					+ valueS + "???");
			isPassed &= op.validate(valueF, valueS,opType);
		}

		return isPassed;
	}
/**
 * 对于人民币转换实验专门写的method
 * @param outputSource
 * @param outputFollowUp
 * @param rm
 * @return
 */
	public boolean verifyRMB(Map outputSource,
			Map outputFollowUp, RelationOfOutput rm) {
		boolean isPassed = true;
		//蜕变关系根据MRType output的蜕变关系只符合实验中设定的几种情况
		Iterator<Relation> iterRelation = rm.getRelationOfOutput().iterator();
		while (iterRelation.hasNext() && isPassed == true) {
			Relation relation = iterRelation.next();
			String functionSource = relation.getFunctionSource()
					.getFunctionDescription();
			String functionFollowUp = relation.getFunctionFollowUp()
					.getFunctionDescription();
			Operator op = relation.getOp();
			RMBExpTest rmbParser = new RMBExpTest();
			//判断functionSource和functionFollowUp表达式是否正确
			
			
			Set<Map.Entry<Variable, String>> entry = outputSource.entrySet();
			Iterator<Map.Entry<Variable, String>> iter11 = entry.iterator();
			Set<Map.Entry<Variable, String>> entry2 = outputFollowUp.entrySet();
			Iterator<Map.Entry<Variable, String>> iter22 = entry2.iterator();

			String valueS = "";
			String valueF = "";
			// 遍历outputSource,获得相应的值
			iter11 = entry.iterator();
			iter22 = entry2.iterator();
			while (iter11.hasNext()) {
				Map.Entry<Variable, String> myEntryS = iter11.next();
				String name = myEntryS.getKey().getName();				
				System.out.println("Debug!!!!function.contains(" + name + ")");
				valueS = myEntryS.getValue();
				// 遍历outputFollowUp
				while (iter22.hasNext()) {
					Map.Entry<Variable, String> myEntryF = iter22.next();
					System.out.println("Debug!!!!function.contains(" + name
							+ "_)");
					valueF = myEntryF.getValue();
					break;
	
				}
				break;

			}
			
			
			
			String mrSource="";
			
			boolean isSatisfied = rmbParser.checkOutputExp(relation);
			if(isSatisfied){
				System.out.println("*****表达式正确****");
				System.out.println("MR：" + functionFollowUp+op.opNames[op.getType()]+functionSource);
				mrSource = rmbParser.getOutputS_(valueS);
				System.out.println("followUp output: " + valueF);
				isPassed &= op.validate(valueF, mrSource,op.getType());
				
			}else {
				System.out.println("*****表达式错误****");
				System.exit(0);
			}

			
		}
		return isPassed;
	}
	
	
	
	
	
	/**
	 * 把衍生用例所有变量加下划线
	 * 
	 * @param inputSource
	 * @return
	 */
	private Map<Variable, String> copyOutput(Map<Variable, String> outputSource) {
		Map<Variable, String> inputFollowUp = new LinkedHashMap<Variable, String>();
		Set<Map.Entry<Variable, String>> entry = outputSource.entrySet();
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
