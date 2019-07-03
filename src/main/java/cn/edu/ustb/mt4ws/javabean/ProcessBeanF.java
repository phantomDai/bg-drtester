package cn.edu.ustb.mt4ws.javabean;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.xmlbeans.SchemaType;

import cn.edu.ustb.mt4ws.tcg.Modifier;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.tcg.XmlTestCase;
import cn.edu.ustb.mt4ws.tcg.XmlVariable;

public class ProcessBeanF {
	
	private int id;
	private int mrID;
	private int sID;// 表示对应的Source testcase的id
	private String a;
	private String b;
	private String type;

	public ProcessBeanF(int id,int mrId,String a,String b,String type){
		this.id=id;
		this.mrID=mrId;
		this.a=a;
		this.b=b;
		this.type=type;
	}
	
	public ProcessBeanF(){
		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMrID() {
		return mrID;
	}
	public void setMrID(int mrID) {
		this.mrID = mrID;
	}
	public int getsID() {
		return sID;
	}
	public void setsID(int sID) {
		this.sID = sID;
	}
	public String getA() {
		return a;
	}
	public void setA(String a) {
		this.a = a;
	}
	public String getB() {
		return b;
	}
	public void setB(String b) {
		this.b = b;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	/**
	 * 根据原始测试用例和蜕变关系产生衍生测试用例
	 * 
	 * @param tcTransfer
	 * @param opFormat
	 * @return
	 */
	public static ProcessBeanF generateFollowTC(ProcessBean processBean,
			WsdlOperationFormat opFormat, MetamorphicRelation mr) {

		ProcessBeanF tcProcessF = new ProcessBeanF();

		XmlTestCase xmlTC_S = getXmlTC(processBean, opFormat);

		Map<XmlVariable, String> inputFollow = new LinkedHashMap<XmlVariable, String>();
		Modifier mod = new Modifier();

		inputFollow = mod.modifyTransfer(mr.getRelationOfInput(), xmlTC_S
				.getInput());
		tcProcessF.setsID(processBean.getId());// 设置衍生用例中的sID为原始测试用例的ID
		tcProcessF.setMrID(processBean.getMrID());

		Iterator iter = inputFollow.keySet().iterator();
		while (iter.hasNext()) {
			XmlVariable xmlVar = (XmlVariable) iter.next();
			String value = inputFollow.get(xmlVar);
			String name = xmlVar.getName();
			// 根据XmlVariable中name的不同，来的得到相应的值
			if (name.equals("a_")) {
				tcProcessF.setA(value);
			} else if (name.equals("b_")) {
				tcProcessF.setB(value);
			} else if (name.equals("type_")) {
				tcProcessF.setType(value);
			} 
		}

		return tcProcessF;

	}
	
	
	/**
	 * 根据TC得到相应的XMLTestCase
	 * 
	 * @param tcTransfer
	 * @param opFormat
	 * @return
	 */
	public static XmlTestCase getXmlTC(ProcessBean processBean,
			WsdlOperationFormat opFormat) {
		Map<XmlVariable, String> input_S = new LinkedHashMap<XmlVariable, String>();

		Iterator<Map.Entry<String, SchemaType>> iter = opFormat.getInput().format
				.getSimpleTypes().entrySet().iterator();
		// 遍历所有的SimpleType
		while (iter.hasNext()) {
			Map.Entry<String, SchemaType> myEntry = iter.next();
			String name = myEntry.getKey();
			SchemaType type = myEntry.getValue();
			XmlVariable var = new XmlVariable(name, type);
			String value = "";
			// 根据name来从Transfer中得到相应的值
			if (name.equals("a")) {
				value = processBean.getA();
			} else if (name.equals("b")) {
				value = processBean.getB();
			} else if (name.equals("type")) {
				value = processBean.getType();
			}
			input_S.put(var, value);
		}

		return new XmlTestCase(opFormat.getOperationName(), input_S, null);

	}

}
