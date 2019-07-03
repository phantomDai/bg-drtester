package cn.edu.ustb.mt4ws.mr;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cn.edu.ustb.mt4ws.mr.model.Relation;

import cn.edu.ustb.mt4ws.mr.model.Operator;
import cn.edu.ustb.mt4ws.tcg.Variable;

public class RMBExpTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RMBExpTest rmbtest = new RMBExpTest();
		System.out.println(rmbtest.randomInteger());
		
	}
	
	//人民币大小写map
	public static Map<String,String> numMap = new LinkedHashMap<String,String>();
	
	//人民币数量级map
	public static Map<String,String> magMap = new LinkedHashMap<String,String>();
	
	//RMB实验中，MR的类型
	//相等 MRType为0，其余根据实验依次为1-6
	//7为蜕变关系表示错误
	public static String MRType="";
	
	public static String d;//随机非零数字
	public static String Cd;//d对应的人民币大写
	public static String Cp;//d所对应的最高数量级
	public static String di;//随机非零数字
	public static String dj;//随机非零数字
	public static String Cdi;//di对应的人民币大写
	public static String Cdj;//df对应的人民币大写
	
	/**
	 * 构造函数，初试化
	 */
	
	public RMBExpTest(){
		
		numMap.put("1", "壹");
		numMap.put("2", "贰");
		numMap.put("3", "叁");
		numMap.put("4", "肆");
		numMap.put("5", "伍");
		numMap.put("6", "陆");
		numMap.put("7", "柒");
		numMap.put("8", "捌");
		numMap.put("9", "玖");
		numMap.put("0", "零");
		
		magMap.put("-3", "Error");
		magMap.put("-2", "分");
		magMap.put("-1", "角");
		magMap.put("0", "元");
		magMap.put("1", "拾");
		magMap.put("2", "佰");
		magMap.put("3", "仟");
		magMap.put("4", "万");
		magMap.put("5", "拾万");
		magMap.put("6", "佰万");
	}
	
	/**
	 * 设置inputFollowUp的值
	 * @param namesAndValues
	 * @param relation
	 * @return
	 */
	public Map<Variable, String> setInputFollowUp(
			Map<Variable, String> namesAndValues, Relation relation) {
		Map<Variable, String> inputMap = namesAndValues;
		// 得到relation中followUp公式
		String followUp = relation.getFunctionFollowUp()
				.getFunctionDescription();

		Set<Map.Entry<Variable, String>> entry = inputMap.entrySet();
		Iterator<Map.Entry<Variable, String>> iter = entry.iterator();
		Map.Entry<Variable, String> myentry = null;
		// 遍历Map（inputFollowUp）
		while (iter.hasNext()) {
			myentry = iter.next();
			Variable var = myentry.getKey();
			String value = myentry.getValue();

			// 判断function中是否包含inputFollowUp变量
			if (followUp.contains(var.getName())) {
				System.out.println("Debug!!!!function.contains("
						+ var.getName() + ")");
				value = setValueByType(var, MRType, value);
				inputMap.put(var, value);
			}
		}		
		return inputMap;
		
	}
	
	/**
	 * 根据原始用例返回的结果，将result根据蜕变关系和MRType生成所需的结果
	 * @param val
	 * @return
	 */
	public String getOutputS_(String val) {
		String mrValue = "";
		String type = MRType;
		if (type.equals("0")) {
			// 相等
			mrValue = val;

		} else if (type.equals("1")) {
			// followUp：RMB_ Source: d+RMB
			mrValue = Cd.concat(Cp).concat(val);
		} else if (type.equals("2")) {
			// Source1: N +.+di+dj MRType:2
			mrValue = val.concat(Cdi + "角" + Cdj + "分");
		} else if (type.equals("3")) {
			// Source2：N +.+0+dj MRType:3
			mrValue = val.concat(Cdj + "分");
		} else if (type.equals("4")) {
			// Source3: N +.+di MRType:4
			mrValue = val.concat(Cdi + "角");
		} else if (type.equals("5")) {
			// Source1: RMB(di,dj); MRType:5
			if (val.contains(Cdi)) {
				mrValue = val.replaceAll(Cdi, Cdj);
			} else
				mrValue = "";

		} else if (type.equals("6")) {
			System.out.println("****ToDo******");
			// Source2: RMB(di,0); MRType:6
		} else if (type.equals("7")) {
			System.out.println("****Error******");
		}
		System.out.println(MRType + ":  " + mrValue);

		return mrValue;
	}
	
/**
 * 根据MRType，设定value的值	
 * @param variable
 * @param type
 * @param val
 * @return
 */
	private String setValueByType(Variable variable, String type, String val) {
		String value = "";

		if (type.equals("0")) {
			// 相等
			value = val;
			
		} else if (type.equals("1")) {
			// followUp：RMB_ Source: d+RMB
			d = randomInt();
			Cd = numMap.get(d);			
			value = d.concat(val);
			Cp = magMap.get(getCp(value));
			
		} else if (type.equals("2")) {
			// Source1: N +.+di+dj MRType:2
			di = randomInt();
			Cdi = numMap.get(di);
			dj = randomInt();
			Cdj = numMap.get(dj);
			value = val.concat("."+di+dj);
			
		} else if (type.equals("3")) {
			// Source2：N +.+0+dj MRType:3
			dj = randomInt();
			Cdj = numMap.get(dj);
			value = val.concat(".0"+dj);
		} else if (type.equals("4")) {
			// Source3: N +.+di MRType:4
			di = randomInt();
			Cdi = numMap.get(di);
			value = val.concat("."+di);
			
		} else if (type.equals("5")) {
			// Source1: RMB(di,dj); MRType:5
			di = getRandomDi(val);
			Cdi = numMap.get(di);
			dj = randomInt();
			Cdj = numMap.get(dj);
			//di和dj不能相等，且di为非零
			while(di.equals(dj)){
				dj = randomInt();
				Cdj = numMap.get(dj);
			}
			value = val.replaceAll(di, dj);
			
		} else if (type.equals("6")) {
			di = getRandomDi(val);
			Cdi = numMap.get(di);
			dj = "0";
			Cdj = numMap.get(dj);
			
			value = val.replaceAll(di, dj);
			// Source2: RMB(di,0); MRType:6
		} else if (type.equals("7")) {
			System.out.println("****Error******");
		}
         System.out.println("followUp Value： " + value);
		return value;
	}


	/**
	 * 验证input蜕变关系表达式是否正确
	 * @param relation
	 * @return
	 */
	public boolean checkInputExp(Relation relation){
		String functionSource = relation.getFunctionSource()
				.getFunctionDescription();//
		String functionFollowUp = relation.getFunctionFollowUp()
				.getFunctionDescription();
		Operator op = relation.getOp();
		int type = op.getType();
		
		boolean isSatisfied = checkExpInput(functionSource,functionFollowUp,type);
	
		return isSatisfied;
	}
	
	/**
	 * 验证output蜕变关系表达式是否正确
	 * @param relation
	 * @return
	 */
	public boolean checkOutputExp(Relation relation) {
		String functionSource = relation.getFunctionSource()
				.getFunctionDescription();//
		String functionFollowUp = relation.getFunctionFollowUp()
				.getFunctionDescription();
		Operator op = relation.getOp();
		int type = op.getType();

		boolean isSatisfied = checkExpOutput(functionSource, functionFollowUp,
				type);

		return isSatisfied;
	}
	
	
	
	
	/**
	 * 只针对RMB服务，验证input表达式是否正确
	 * 
	 * @param expS
	 * @param expF
	 * @param type
	 * @return
	 */
	public boolean checkExpInput(String expS, String expF, int type) {
		boolean isSatisfied = false;
		switch (type) {
		case Operator.EQ:
			// 相等
			if (expF.endsWith("_")) {
				String strTemp = expF.substring(0, expF.length() - 1);
				System.out.println("RF结尾是_,则前面字符串为：" + strTemp);
				// 判断expRF和expS相等
				if (expS.equals(strTemp)) {
					MRType = "0";
					isSatisfied = true;
				}
			}
			break;
		case Operator.LA:
			// 左加 followUp：RMB_ Source: d+RMB
			// MRType:1
			// 其他表达式均为错误
			if (expF.equals("RMB_")) {
				if (expS.equals("d+RMB")) {
					isSatisfied = true;
					MRType = "1";
				}
			}
			break;
		case Operator.RA:
			// 右加 followUp：RMB_
			// Source1: N +.+di+dj MRType:2
			// Source2：N +.+0+dj MRType:3
			// Source3: N +.+di MRType:4
			if (expF.equals("RMB_")) {
				if (expS.equals("RMB+.+di+dj")) {
					MRType = "2";
					isSatisfied = true;
				} else if (expS.equals("RMB+.+0+dj")) {
					MRType = "3";
					isSatisfied = true;
				} else if (expS.equals("RMB+.+di")) {
					MRType = "4";
					isSatisfied = true;
				} else {
					isSatisfied = false;
				}
			}
			break;
		case Operator.REP:
			// 中间随机替换 followUp：RMB_
			// Source1: RMB(di,dj); MRType:5
			// Source2: RMB(di,0); MRType:6
			if (expF.equals("RMB_")) {
				if (expS.equals("RMB(di,dj)")) {
					MRType = "5";
					isSatisfied = true;
				} else if (expS.equals("RMB(di,0)")) {
					MRType = "6";
					isSatisfied = true;
				} else {
					isSatisfied = false;
				}
			}
			break;
		default:
			MRType = "7";
			break;
		}

		return isSatisfied;
	}

	/**
	 * 只针对RMB服务，验证output表达式是否正确
	 * 
	 * @param expS
	 * @param expF
	 * @param type
	 * @return
	 */
	public boolean checkExpOutput(String expS, String expF, int type) {
		boolean isSatisfied = false;
		//expF在实验中均为return_,根据type来判断expS是否满足
		if (expF.equals("return_")) {
			switch (type) {
			case Operator.EQ:
				// 相等
				if (MRType.equals("0") && expS.equals("return"))
					isSatisfied = true;
					break;
			case Operator.LA:
				// 左加 followUp：RMB_ Source: Cdi+RMB
				// MRType:1
				// 其他表达式均为错误
				if(MRType.equals("1")&&expS.equals("Cd+Cp+return"))
					isSatisfied = true;
				break;
			case Operator.RA:
				// 右加 followUp：RMB_
				// Source1: N +.+di+dj MRType:2
				// Source2：N +.+0+dj MRType:3
				// Source3: N +.+di MRType:4
				if(MRType.equals("2")&&expS.equals("return+Cdi+角+Cdj+分"))
					isSatisfied = true;
				else if(MRType.equals("3")&&expS.equals("return+Cdj+分"))
					isSatisfied = true;
				else if(MRType.equals("4")&&expS.equals("return+Cdi+角"))
					isSatisfied = true;
				else isSatisfied = false;
		
				break;
			case Operator.REP:
				// 中间随机替换 followUp：return_
				// Source1: RMB(di,dj); MRType:5
				if(MRType.equals("5")&&expS.equals("return(Cdi,Cdj)"))
					isSatisfied = true;
				break;
			case Operator.DEL:
				if(MRType.equals("6")&&expS.equals("return(Cdi,Cp)"))
					isSatisfied = true;
				break;
			default:
				MRType = "7";
				isSatisfied = false;
				break;
			}
		} else
			isSatisfied = false;
		System.out.println("checkExpOutput：" + isSatisfied);
		
		return isSatisfied;
	}
	/**
	 * 随机产生一个1-9之间的整数
	 * @return
	 */
	private String randomInt() {
		String intStr = "";
		int num = (int) (Math.random() * 9 + 1);
		intStr = String.valueOf(num);
		return intStr;
	}
	
	/**
	 * 若100，则数量级为仟，即比最高位还高1位
	 * @param Num
	 * @return
	 */
	private String getCp(String Num){
		String Cp="";
		float flo = Float.parseFloat(Num);
		if (flo >= 1000000 && flo < 10000000) {
			Cp = "6";//百万
		} else if (flo >= 100000 && flo < 1000000) {
			Cp = "5";// 十万
		} else if (flo >= 10000 && flo < 100000) {
			Cp = "4";// 万
		} else if (flo >= 1000 && flo < 10000) {
			Cp = "3";// 仟
		} else if (flo >= 100 && flo < 1000) {
			Cp = "2";// 佰
		} else if (flo >= 10 && flo < 100) {
			Cp = "1";// 十
		} else if (flo >= 1 && flo < 10) {
			Cp = "0";//元
		} else if (flo >= 0.1 && flo < 1) {
			Cp = "-1";// 角
		} else if (flo >= 0.01 && flo < 0.1) {
			Cp = "-2";// 分
		} else {
			Cp = "-3";
		}
			
		return Cp;
	}
	
	/**
	 * 随机得到numR中的一个非零数字
	 * @param numR
	 * @return
	 */
	private String getRandomDi(String numR){
		int index =(int) Math.random() * numR.length() ;
		while(numR.charAt(index)=='.'||numR.charAt(index)=='0'){
			index=(int) Math.random() * numR.length();
		}
		return String.valueOf(numR.charAt(index));
		
	}
	
	/**
	 * 随机产生一个小于1000000.00的小数，
	 * @return
	 */
	public String randomFloat(){
		//最大数值
		double maxnum = 10000.00;
		String floStr="";
		//产生随机的double类型
		DecimalFormat dcmFmt = new DecimalFormat("0.00");
		Random rand = new Random();
		Double f = rand.nextDouble() * maxnum;
		//转换成String类型
		floStr=dcmFmt.format(f).toString();
		return floStr;	
	}
	
	/**
	 * 随机产生一个小于1000000的整数，
	 * 
	 * @return
	 */
	public String randomInteger() {
		// 最大数值
		int maxnum = 10000;
		String floStr = "";
		// 产生随机的double类型

		//Random rand = new Random();
		int f = (int)(Math.random()*maxnum);
		// 转换成String类型
		floStr = String.valueOf(f);
		return floStr;
	}
	
	
	
}
