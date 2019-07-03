package cn.edu.ustb.mt4ws.mr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.xmlbeans.SchemaType;

import cn.edu.ustb.mt4ws.mr.model.Operator;
import cn.edu.ustb.mt4ws.mr.model.Relation;
import cn.edu.ustb.mt4ws.tcg.Type;
import cn.edu.ustb.mt4ws.tcg.Variable;

public class QuakeExpParser {

	String maxd_ = "2003-09-01 00:00:00";
	String mind_ = "2001-01-01 00:00:00";
	float maxlat_ = 90;
	float minlat_ = -90;
	float maxlng_ = 180;
	float minlng_ = -180;
	float maxdepth_ = 10000;
	float mindepth_ = 0;
	float maxmag_ = 9;
	float minmag_ = 4;
	
	/**
	 * 判断地震查询中各个解析式输入是否正确 输入规则较为简单，只能是变量或者变量+_ TODO
	 * 
	 * @param namesAndValues
	 * @param function
	 * @return
	 */

	public boolean checkExp(Map<Variable, String> namesAndValues,
			String function) {
		boolean isSatisfied = false;

		Set<Variable> varSet = new LinkedHashSet<Variable>();
		Set<Map.Entry<Variable, String>> entry = namesAndValues.entrySet();
		Iterator<Map.Entry<Variable, String>> iter = entry.iterator();
		Map.Entry<Variable, String> myentry = null;

		while (iter.hasNext()) {
			myentry = iter.next();
			Variable var = myentry.getKey();
			// 判断function是含有输入变量
			if (function.contains(var.getName())||function.equals("null")) {
				System.out.println("function:" + function);
				System.out.println("var.getName():" + var.getName());
				// 若含有输入变量，则判断是否相等，否则不符合命名规则
				if (function.equals(var.getName())) {
					isSatisfied = true;
					System.out.println("queryQuakes输入的蜕变关系解析式符合规则");
					break;
				}else if(function.equals("null")){
					//输入的变量中含有null
					isSatisfied = true;
					System.out.println("queryQuakes输入的蜕变关系中含有null");
					break;
				}else {
					System.out.println("queryQuakes输入的蜕变关系解析式不符合规则");
					isSatisfied = false;
					continue;
				}
			}
		}

		return isSatisfied;

	}

	/**
	 * quake查询服务中，根据op设置followUp的值
	 * 
	 * @param namesAndValues
	 * @param function
	 * @return
	 */

	public Map<Variable, String> setValue(Map<Variable, String> namesAndValues,
			Relation relation) {
		Map<Variable, String> inputMap = namesAndValues;
		// 得到relation中followUp公式
		String followUp = relation.getFunctionFollowUp()
				.getFunctionDescription();
		String source = relation.getFunctionSource().getFunctionDescription();

		Set<Map.Entry<Variable, String>> entry = namesAndValues.entrySet();
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
				value = setValueByType(var,relation,value);
				inputMap.put(var, value);

			}else if(followUp.contains("null")&&relation.getOp().getType()==Operator.EQ&&source.concat("_").equals(var.getName())){
				//followUp中包含null
				System.out.println("null in followUp");
				value = setFollowByNull(var,relation);
				inputMap.put(var, value);
				//TODO
				
			}else{
				System.out.println("Error in FollowUp");
				//TODO
				//System.exit(0);
			}
		}

		return inputMap;
	}
	
	
	/**
	 * 地震实验中，当followUp输入为null和关系符为=时，根据Source的输入，默认followUp！=null，为区间中的任意值
	 * 
	 * @param var
	 * @param relation
	 * @return followUp的值
	 */
	private String setFollowByNull(Variable var, Relation relation){
		String valueF="";
		SchemaType type = (SchemaType) var.getType();
		Operator op = relation.getOp();
		// 根据type类型以及OP进行赋值
		switch (type.getPrimitiveType().getBuiltinTypeCode()) {
		// float
		case SchemaType.BTC_FLOAT:
			//根据Source中输入的变量，确定followUp的值
			valueF = setValueFByNullAndName(var.getName());
			System.out.println("float 类型：" + valueF);
			break;
		// date_time
		//实验中并不涉及到dateTime
//		case SchemaType.BTC_DATE_TIME:
//			valueF = setDateByOp(op, val);
//			System.out.println("dateTime 类型：" + value);
//			break;
//		// time
//		case SchemaType.BTC_DATE:
//			valueF = setDateByOp(op, val);
//			System.out.println("date类型：" + value);
//			break;
		default:
			break;

		}
		
		
		return valueF;
	}
	
	/**
	 * 在地震实验中，followUp输入为null时，followUp的值默认为区间内随机
	 * @param name
	 * @return
	 */
	private String setValueFByNullAndName(String name){
		String valueF="";
		float returnflo=0;
		if (name.contains("depth")) {
			//returnflo =  maxdepth_;
			returnflo = randomFloat(mindepth_, maxdepth_);
		} else if (name.contains("lat")) {
			//returnflo = maxlat_;
			returnflo = randomFloat(minlat_, maxlat_);
		} else if (name.contains("mag")) {
			//returnflo = maxmag_;
			returnflo = randomFloat(minmag_, maxmag_);
		} else if (name.contains("lng")) {
			//returnflo = maxlng_;
			returnflo = randomFloat(minlng_, maxlng_);
		}
		valueF=String.valueOf(returnflo);
		
		return valueF;
	}
	
	

	/**
	 * 根据var中的schemaType类型（dateTime或者float），relation中的op,初始值val来重新设定value值
	 * 
	 * @param var
	 * @param relation
	 * @param val
	 * @return
	 */
	public String setValueByType(Variable var, Relation relation, String val) {

		String value = "";
		SchemaType type = (SchemaType) var.getType();
		Operator op = relation.getOp();
		// 根据type类型以及OP进行赋值
		switch (type.getPrimitiveType().getBuiltinTypeCode()) {
		// float
		case SchemaType.BTC_FLOAT:
			float flo = Float.parseFloat(val);
			flo = setValueByOpAndName(op, flo, var.getName());
			value = String.valueOf(flo);
			System.out.println("float 类型：" + value);
			break;
		// date_time
		case SchemaType.BTC_DATE_TIME:
			value = setDateByOp(op, val);
			System.out.println("dateTime 类型：" + value);
			break;
		// time
		case SchemaType.BTC_DATE:
			value = setDateByOp(op, val);
			System.out.println("date类型：" + value);
			break;
		default:
			break;

		}

		return value;

	}

	/**
	 * 根据op生成float 随机生成数为0-100的浮点数
	 * 
	 * @param op
	 * @param flo
	 * @return
	 */

	public float setValueByOp(Operator op, Float flo) {
		float flo1 = 0;
		float randomF = (float) Math.random() * 100;
		switch (op.getType()) {
		// =
		case Operator.EQ:
			flo1 = flo;
			break;
		// >
		case Operator.GT:
			while (randomF == 0)
				randomF = (float) Math.random() * 100;
			flo1 = flo + randomF;
			break;
		// <
		case Operator.LT:
			while (randomF == 0)
				randomF = (float) Math.random() * 100;
			flo1 = flo - randomF;
			break;
		// >=
		case Operator.NLT:
			flo1 = flo + randomF;
			break;
		// <=
		case Operator.NGT:
			flo1 = flo - randomF;
			break;
		default:
			flo1 = flo;
		}

		return flo1;
	}
	
	/**
	 * 根据op和var中含有的变量
	 * 
	 * @param op
	 * @param flo
	 * @return
	 */

	public float setValueByOpAndName(Operator op, Float flo,String name) {
		float flo1 = 0;

		switch (op.getType()) {
		// =
		case Operator.EQ:
			flo1 = flo;
			break;
		// >
		case Operator.GT:
			 flo1=setValueByGT(flo,name);
			break;
		// <
		case Operator.LT:		
			flo1 = setValueByLT(flo,name);
			break;
		// >=
		case Operator.NLT:
			flo1=setValueByGT(flo,name);
			break;
		// <=
		case Operator.NGT:
			flo1 = setValueByLT(flo,name);
			break;
		case Operator.NEQ:
			flo1 = setValueByNEQ(name);
			break;
		default:
			flo1 = flo;
		}

		return flo1;
	}

	private float setValueByNEQ(String name){
		float returnflo = 0;
		if (name.contains("depth")) {
			returnflo = randomFloat(mindepth_, maxdepth_);
		} else if (name.contains("lat")) {
			returnflo = randomFloat(minlat_, maxlat_);
		} else if (name.contains("mag")) {
			returnflo = randomFloat(minmag_, maxmag_);
		} else if (name.contains("lng")) {
			returnflo = randomFloat(minlng_, maxlng_);
		}
		return returnflo;
	}
	
	/**
	 * 根据>大于符号，生成衍生测试用例
	 * 
	 * @param flo
	 * @param name
	 * @return
	 */

	private float setValueByGT(Float flo, String name) {
		float returnflo = 0;
		if (name.contains("depth")) {
			//returnflo =  maxdepth_;
			returnflo = randomFloat(flo, maxdepth_);
		} else if (name.contains("lat")) {
			//returnflo = maxlat_;
			returnflo = randomFloat(flo, maxlat_);
		} else if (name.contains("mag")) {
			//returnflo = maxmag_;
			returnflo = randomFloat(flo, maxmag_);
		} else if (name.contains("lng")) {
			//returnflo = maxlng_;
			returnflo = randomFloat(flo, maxlng_);
		}
		return returnflo;
	}
	
	/**
	 * 根据<小于符号，生成衍生测试用例
	 * 
	 * @param flo
	 * @param name
	 * @return
	 */
	private float setValueByLT(Float flo,String name){
		float returnflo=0;
		if(name.contains("depth")){
			//returnflo = mindepth_;
			returnflo = randomFloat(mindepth_,flo);
		}else if(name.contains("lat")){
			//returnflo = minlat_;
			returnflo = randomFloat(minlat_,flo);
		}else if(name.contains("mag")){
			//returnflo = minmag_;
			returnflo = randomFloat(minmag_,flo);
		}else if(name.contains("lng")){
			//returnflo = minlng_;
			returnflo = randomFloat(minlng_,flo);
		}
		return returnflo;
	}
	
	/**
	 * 根据Op随机生成时间 beginTime="2001-01-01" endTime="2003-09-01"
	 * 要求输入的时间在begin和end之间
	 * 
	 * @param op
	 * @param str
	 * @return 随机生成时间
	 */
	public String setDateByOp(Operator op, String str) {
		String dateStr = "";
		SimpleDateFormat dateformat = new SimpleDateFormat(
		"yyyy-MM-dd HH:mm:ss");
		String start = "2001-01-01 00:00:00";
		String end = "2003-09-01 00:00:00";
		switch (op.getType()) {
		// =
		case Operator.EQ:
			dateStr = str;
			break;
		// >
		case Operator.GT:
			//dateStr = end;
			dateStr = dateformat.format(randomDate(str,end));
			break;
		// <
		case Operator.LT:
			//dateStr = start;
			dateStr = dateformat.format(randomDate(start,str));
			break;
		// >=
		case Operator.NLT:
			//dateStr = end;
			dateStr = dateformat.format(randomDate(str,end));
			break;
		// <=
		case Operator.NGT:
			//dateStr = start;
			dateStr = dateformat.format(randomDate(start,str));
			break;
		default:
			dateStr = str;
		}

		return dateStr;
	}

	// 生成随机时间的方法
	public Date randomDate(String beginDate, String endDate) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date start = format.parse(beginDate);// 构造开始日期
			Date end = format.parse(endDate);// 构造结束日期
			// getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
			if (start.getTime() >= end.getTime()) {
				return null;
			}
			long date = random(start.getTime(), end.getTime());

			return new Date(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static long random(long begin, long end) {
		long rtn = begin + (long) (Math.random() * (end - begin));
		// 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
		if (rtn == begin || rtn == end) {
			return random(begin, end);
		}
		return rtn;
	}

	/**
	 * 判断两个时间之间的先后关系，若start比end早，则返回true； else 返回false
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public boolean compareDate(String start, String end) {
		boolean isSatisfied = false;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date startDate = format.parse(start);// 构造开始日期
			Date endDate = format.parse(end);// 构造结束日期
			if (startDate.getTime() > endDate.getTime()) {
				isSatisfied = false;
			} else {
				isSatisfied = true;
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSatisfied;
	}
	
	
	/**
	 * 随机产生一个在min和max之间的float数
	 * @param min
	 * @param max
	 * @return
	 */
	private String randomFloat(String min, String max) {
		float minflo = Float.parseFloat(min);
		float maxflo = Float.parseFloat(max);
		if (minflo > maxflo) {
			return null;
		} else {
			Random rand = new Random();
			float flo = minflo + rand.nextFloat() * (maxflo - minflo);
//			System.out.println(String.valueOf(flo));
			return String.valueOf(flo);
		}

	}
	
	/**
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	private float randomFloat(float min, float max) {
		float flo=0;
		if (min > max) {
			randomFloat(max,min);
		} else {
			Random rand = new Random();
			flo = min + rand.nextFloat() * (max - min);
//			System.out.println(String.valueOf(flo));
			return flo;
		}
		return flo;

	}
	

}
