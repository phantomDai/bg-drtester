package cn.edu.ustb.mt4ws.javabean;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.xmlbeans.SchemaType;

import cn.edu.ustb.mt4ws.tcg.Modifier;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.tcg.XmlTestCase;
import cn.edu.ustb.mt4ws.tcg.XmlVariable;

public class AddBeanF {

	private int id;
	private int mrID;
	private int sID;// 表示对应的Source testcase的id
	private String a;
	private String b;
	
	public AddBeanF(int id, int mrID, int sID, String a, String b) {
		super();
		this.id = id;
		this.mrID = mrID;
		this.sID = sID;
		this.a = a;
		this.b = b;
	}



	public AddBeanF(){
		
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
	
	/**
	 * 根据原始测试用例和蜕变关系产生衍生测试用例
	 * 
	 * @param tcTransfer
	 * @param opFormat
	 * @return
	 */
	public static AddBeanF generateFollowTC(AddBean addBean,
			WsdlOperationFormat opFormat, MetamorphicRelation mr) {

		AddBeanF tcAddF = new AddBeanF();

		XmlTestCase xmlTC_S = getXmlTC(addBean, opFormat);

		Map<XmlVariable, String> inputFollow = new LinkedHashMap<XmlVariable, String>();
		Modifier mod = new Modifier();

		inputFollow = mod.modifyTransfer(mr.getRelationOfInput(), xmlTC_S
				.getInput());
		tcAddF.setsID(addBean.getId());// 设置衍生用例中的sID为原始测试用例的ID
		tcAddF.setMrID(addBean.getMrID());

		Iterator iter = inputFollow.keySet().iterator();
		while (iter.hasNext()) {
			XmlVariable xmlVar = (XmlVariable) iter.next();
			String value = inputFollow.get(xmlVar);
			String name = xmlVar.getName();
			// 根据XmlVariable中name的不同，来的得到相应的值
			if (name.equals("a_")) {
				tcAddF.setA(value);
			} else if (name.equals("b_")) {
				tcAddF.setB(value);
			} 
		}

		return tcAddF;

	}
	
	
	/**
	 * 根据TC得到相应的XMLTestCase
	 * 
	 * @param tcTransfer
	 * @param opFormat
	 * @return
	 */
	public static XmlTestCase getXmlTC(AddBean addBean,
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
				value = addBean.getA();
			} else if (name.equals("b")) {
				value = addBean.getB();
			}
			input_S.put(var, value);
		}

		return new XmlTestCase(opFormat.getOperationName(), input_S, null);

	}
}
