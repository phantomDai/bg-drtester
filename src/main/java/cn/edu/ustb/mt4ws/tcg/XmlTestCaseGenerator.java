package cn.edu.ustb.mt4ws.tcg;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.SchemaType;
import cn.edu.ustb.mt4ws.mr.Limitation;
import cn.edu.ustb.mt4ws.mr.MRUtils;
import cn.edu.ustb.mt4ws.javabean.RelationOfInput;

import com.eviware.soapui.model.iface.Response;

public class XmlTestCaseGenerator {

	public SqlUtils sqlUtils = null;
	public XmlTcgUtils tcgUtils = null;

	/**
	 * 所有分量均为随机值<br>
	 * 用于生成XML变量的随机值，放入数据库
	 * 
	 * @param operationName
	 * @return
	 */
	public XmlTestCase genWithRandomValue(String operationName,
			WsdlOperationFormat opFormat) {
		XmlTcgUtils tcgUtils = new XmlTcgUtils();
		Set<Map.Entry<String, SchemaType>> entry = opFormat.getInput().format
				.getSimpleTypes().entrySet();
		Iterator<Map.Entry<String, SchemaType>> iter = entry.iterator();
		Map.Entry<String, SchemaType> myentry = null;
		Map<XmlVariable, String> input = new LinkedHashMap<XmlVariable, String>();
		while (iter.hasNext()) {
			myentry = iter.next();
			String varName = myentry.getKey();

			System.out.print(varName + " ");

			SchemaType type = myentry.getValue();

			System.out.print(type.getPrimitiveType() + " ");

			XmlVariable var = new XmlVariable(varName, type);
			String value = tcgUtils.sampleDataForSimpleType(type, null);// 没有Limitation

			System.out.print(value + " ");

			input.put(var, value);
		}
		return new XmlTestCase(operationName, input, null);
	}

	/**
	 * 根据MR的限制产生TC
	 * 
	 * @param operationName
	 * @param limitation
	 * @return
	 */
	public XmlTestCase genWithLimitation(String operationName,
			XmlMessageFormat inputFormat, RelationOfInput relationOfInput,
			int mrID) {
		Map<String, Limitation> variableLimit = MRUtils.getLimitionOfSourceTCNew(
				inputFormat, relationOfInput);
		tcgUtils = new XmlTcgUtils();
		Set<Map.Entry<String, SchemaType>> entry = inputFormat.getSimpleTypes()
				.entrySet();
		Iterator<Map.Entry<String, SchemaType>> iter = entry.iterator();
		Map.Entry<String, SchemaType> myentry = null;
		Map<XmlVariable, String> input = new LinkedHashMap<XmlVariable, String>();
		while (iter.hasNext()) {
			myentry = iter.next();
			String varName = myentry.getKey();
			SchemaType type = myentry.getValue();
			XmlVariable var = new XmlVariable(varName, type);
			Limitation limit = variableLimit.get(varName);
			String value = tcgUtils.sampleDataForSimpleType(type, limit);
			//限定amount在0-1000中
			if(varName.endsWith("amount")){
				while(Integer.parseInt(value)>1000){
					value = tcgUtils.sampleDataForSimpleType(type, limit);
				}
					
			}
			
			input.put(var, value);
		}

		return new XmlTestCase(operationName, mrID, input, null);
	}

	public void genFromFile() {
		// TODO
	}

	/**
	 * 由XmlTestCase转换成SQL查询语句
	 * 
	 * @param tc
	 * @return
	 */
	public String convertToSQLStatement(XmlTestCase tc) {
		Set<Map.Entry<XmlVariable, String>> entry = tc.getInput().entrySet();
		Iterator<Map.Entry<XmlVariable, String>> iter = entry.iterator();
		Map.Entry<XmlVariable, String> myentry = null;
		StringBuffer sqlInsert = new StringBuffer();
		sqlInsert.append("INSERT INTO " + tc.getOperationName() + " SET ");
		sqlInsert.append("mrID='" + tc.getMrID() + "',");
		while (iter.hasNext()) {
			myentry = iter.next();
			String colName = myentry.getKey().getName();
			String value = myentry.getValue();
			sqlInsert.append(colName + "=" + "'" + value + "'");
			if (iter.hasNext())
				sqlInsert.append(",");
		}
		return sqlInsert.toString();
	}

	/**
	 * 从数据库中取出测试用例数据，生成XmlTestCase<br>
	 * 数据库信息存于config中
	 * 
	 * @param opFormat
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public XmlTestCase extractFromDB(WsdlOperationFormat opFormat,
			int TestCaseId) throws Exception {

		sqlUtils = SqlUtils.getInstance();

		/*
		 * 输入部分，simple type
		 */
		Map<XmlVariable, String> input = new LinkedHashMap<XmlVariable, String>();
		// Iterator<Map.Entry<String, SchemaType>> iter =
		// opFormat.getInput().format
		// .getSimpleTypes().entrySet().iterator();
		// while (iter.hasNext()) {
		// Map.Entry<String, SchemaType> myEntry = iter.next();
		// String name = myEntry.getKey();
		// SchemaType type = myEntry.getValue();
		// XmlVariable var = new XmlVariable(name, type);
		// String value = sqlUtils.getValueFromDB(opFormat.getOperationName(),
		// name, TestCaseId);
		// input.put(var, value);
		//
		// }

		Iterator<Map.Entry<String, SchemaType>> iter2 = opFormat.getInput().format
				.getSimpleTypes().entrySet().iterator();
		while (iter2.hasNext()) {
			Map.Entry<String, SchemaType> myEntry = iter2.next();
			String name = myEntry.getKey();
			SchemaType type = myEntry.getValue();
			XmlVariable var = new XmlVariable(name, type);
			String value = sqlUtils.getValueFromDB(opFormat.getOperationName(),
					name, TestCaseId);
			input.put(var, value);
		}

		/*
		 * 输出部分（预期输出）
		 */
		// Map<XmlVariable, String> exOutput = new LinkedHashMap<XmlVariable,
		// String>();
		// iter = opFormat.getOutput().format.getBuiltInTypes().entrySet()
		// .iterator();
		// while (iter.hasNext()) {
		// Map.Entry<String, SchemaType> myEntry = iter.next();
		// String name = myEntry.getKey();
		// SchemaType type = myEntry.getValue();
		// XmlVariable var = new XmlVariable(name, type);
		// String value = null;
		// exOutput.put(var, value);
		//
		// }
		//
		// iter2 = opFormat.getOutput().format.getSimpleTypes().entrySet()
		// .iterator();
		// while (iter2.hasNext()) {
		// Map.Entry<String, SchemaType> myEntry = iter.next();
		// String name = myEntry.getKey();
		// SchemaType type = myEntry.getValue();
		// XmlVariable var = new XmlVariable(name, type);
		// String value = null;
		// exOutput.put(var, value);
		// }
		return new XmlTestCase(opFormat.getOperationName(), input, null);
	}
	
	/**
	 * 从数据库中取出衍生测试用例数据，生成XmlTestCase<br>
	 * 数据库信息存于config中
	 * 
	 * @param opFormat
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public XmlTestCase extractFromDB_F(WsdlOperationFormat opFormat,
			int mrID,int sID) throws Exception {

		sqlUtils = SqlUtils.getInstance();

		/*
		 * 输入部分，simple type
		 */
		Map<XmlVariable, String> input = new LinkedHashMap<XmlVariable, String>();
	

		Iterator<Map.Entry<String, SchemaType>> iter2 = opFormat.getInput().format
				.getSimpleTypes().entrySet().iterator();
		while (iter2.hasNext()) {
			Map.Entry<String, SchemaType> myEntry = iter2.next();
			String name = myEntry.getKey();
			SchemaType type = myEntry.getValue();
			XmlVariable var = new XmlVariable(name+"_", type);
			String value = sqlUtils.getValueFromDB_F(opFormat.getOperationName(),
					name, mrID,sID);
			input.put(var, value);
		}


		return new XmlTestCase(opFormat.getOperationName(), input, null);
	}
	
	
	

	/**
	 * 将XmlTestCase里面的数据放入SampleSoapMessage
	 * 
	 * @param opFormat
	 * @param testcase
	 * @param isSource
	 *            true:source, false:followUp
	 * @return
	 * @throws ParseException
	 */
	public String fillEmptySoapMessage(WsdlOperationFormat opFormat,
			XmlTestCase testcase, boolean isSource) throws ParseException {
		tcgUtils = new XmlTcgUtils();
		String sampleSoap = opFormat.getInput().sampleSoapMessage;
		String soapWithData = sampleSoap;
		Map<XmlVariable, String> input = testcase.getInput();
		Iterator<Map.Entry<XmlVariable, String>> iter = input.entrySet()
				.iterator();
		Map.Entry<XmlVariable, String> myEntry = null;
		while (iter.hasNext()) {
			myEntry = iter.next();
			String elementName = null;
			if (isSource == true)
				elementName = myEntry.getKey().getName();
			else// followUp 去掉最后的下划线
			{
				elementName = myEntry.getKey().getName();
				elementName = elementName
						.substring(0, elementName.length() - 1);
			}
			Object value = myEntry.getValue();
			value = tcgUtils.convertFormat(myEntry.getKey().getType(), myEntry
					.getValue());
			System.out.println(value
					+ " type:"
					+ myEntry.getKey().getType().getPrimitiveType()
							.getBuiltinTypeCode());
			if (soapWithData.contains(elementName + ">?</")) {
				soapWithData = soapWithData.replace(elementName + ">?</",
						elementName + ">" + value + "</");

			}

			// 修改by qing.wen

			/*
			 * if (soapWithData.contains("<" + elementName + ">?</" +
			 * elementName + ">")) { soapWithData = soapWithData.replace("<" +
			 * elementName + ">?</" + elementName + ">", "<" + elementName + ">"
			 * + value + "</" + elementName + ">"); }
			 */
		}
		
		//20130718 for BPEL process support soap 1.2
		if (opFormat.getOperationName().equals("process")||opFormat.getOperationName().equals("add")||opFormat.getOperationName().equals("sub")) {
			soapWithData = soapWithData.replaceAll("soapenv", "soap");
			soapWithData = soapWithData.replaceAll(
					"http://schemas.xmlsoap.org/soap/envelope/",
					"http://www.w3.org/2003/05/soap-envelope");
		}
		return soapWithData;
	}

	/**
	 * 将Soap response转换成 Map&lt;VariableName,value&gt;
	 * 
	 * @param opFormat
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public Map<XmlVariable, String> parseSoapResponse(
			WsdlOperationFormat opFormat, Response response) throws Exception {
		Map<XmlVariable, String> output = new LinkedHashMap<XmlVariable, String>();
		List<XmlVariable> varList = new ArrayList<XmlVariable>();
		Iterator<Map.Entry<String, SchemaType>> iter = opFormat.getOutput().format
				.getSimpleTypes().entrySet().iterator();
		Map.Entry<String, SchemaType> myEntry = null;
		while (iter.hasNext()) {
			myEntry = iter.next();
			String varName = myEntry.getKey();
			varList.add(new XmlVariable(varName, myEntry.getValue()));
		}
		/*iter = opFormat.getOutput().format.getSimpleTypes().entrySet()
				.iterator();
		while (iter.hasNext()) {
			myEntry = iter.next();
			String varName = myEntry.getKey();
			varList.add(new XmlVariable(varName, myEntry.getValue()));
		}*/
		Iterator<XmlVariable> iterVar = varList.iterator();
		while (iterVar.hasNext()) {
			XmlVariable var = iterVar.next();// TODO
			System.out.println("Debug!!!!!" + var.getName());

			String responseContent = response.getContentAsString();
			// System.out.println("SOAP response:" + responseContent);
			String value = findElementValueInSOAP(responseContent, var
					.getName());
			// System.out.println("Debug!!!!!"+var.getName()+" "+value);
			output.put(var, value);
		}
		return output;
	}

	
	/**
	 * 根据response内容，获取所有的quake相关属性，以String形式存储到list中 。
	 * 只针对quake查询服务 待改进wendy
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	
	public List<String> parseSoapResponseQuake(
			Response response) throws Exception {
		List<String> strList = new ArrayList<String>();
		
		String responseContent = response.getContentAsString();//得到返回的soapMessage
		int begin = 0;// 开始截取String的位置
		int end = 0;// 最后截取String的位置
		begin = responseContent.indexOf("quakes");
		end = responseContent.indexOf("</quakes>");
		//System.out.println("begin:" + begin);
		//System.out.println("end:" + end);
		// 判断responseContent中均含有quakes和</quakes>
		while (begin != -1 && end != -1) {
			// 截取每段包含在quakes中的String
			String temp = responseContent.substring(begin + 7, end);
			//System.out.println("quakes: " + temp);
			/*
			 * 将quakes中的所有quake分别加入到List中
			 */
			int beginQ = temp.indexOf("<quake");
			int endQ = temp.indexOf("/>");
			// 判断temp中不含有quake和/>
			while (beginQ != -1 && endQ != -1) {
				// tempQ为存入list中的String
				String tempQ = temp.substring(beginQ, endQ+2);
				//System.out.println("quake: " + tempQ);
				
				strList.add(tempQ);
				//加入到List中

				temp = temp.substring(endQ + 2);
				//System.out.println("temp:" + temp);
				beginQ = temp.indexOf("<quake");
				endQ = temp.indexOf("/>");

			}

			// 舍弃前一段的quakes内容
			responseContent = responseContent.substring(end + 9);
			//System.out.println("responseContent:" + responseContent);
			begin = responseContent.indexOf("quakes");
			end = responseContent.indexOf("</quakes>");

		}

		return strList;
	}
	
	
	
	
	/**
	 * 检查是否满足Limitation
	 * 
	 * @param namesAndValues
	 * @param limitation
	 * @return true:满足limitation,false:不满足limitation
	 */
	private boolean checkLimitation(Map<XmlVariable, String> namesAndValues,
			String limitation) {
		return true;// TODO
	}

	private String findElementValueInSOAP(String soapContent, String elementName) {
		int begin = soapContent.indexOf(elementName)
				+ elementName.length();
		String soapContentTemp = soapContent.substring(begin);
		begin = soapContentTemp.indexOf(">")+1;
		int end = soapContentTemp.indexOf("</");
		// 修改By qing.wen
		/*
		 * int begin = soapContent.indexOf("<" + elementName + ">") +
		 * elementName.length() + 2; int end = soapContent.indexOf("</" +
		 * elementName + ">");
		 */
		return soapContentTemp.substring(begin, end);

	}

}
