package cn.edu.ustb.mt4ws.javabean;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.xmlbeans.SchemaType;

import cn.edu.ustb.mt4ws.tcg.Modifier;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.tcg.XmlTestCase;
import cn.edu.ustb.mt4ws.tcg.XmlVariable;

public class CheckQuantityBeanF {

	private int id;		
	private int mrID;
	private int sID;// 表示对应的Source testcase的id
	private String name;
	private String amount;
	
	public CheckQuantityBeanF(){
		
	}

	public CheckQuantityBeanF(int id, int mrID, int sID, String name,
			String amount) {
		super();
		this.id = id;
		this.mrID = mrID;
		this.sID = sID;
		this.name = name;
		this.amount = amount;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	/**
	 * 根据原始测试用例和蜕变关系产生衍生测试用例
	 * 
	 * @param tcTransfer
	 * @param opFormat
	 * @return
	 */
	public static CheckQuantityBeanF generateFollowTC(CheckQuantityBean checkQuantityBean,
			WsdlOperationFormat opFormat, MetamorphicRelation mr) {

		CheckQuantityBeanF tcCheckQuantityF = new CheckQuantityBeanF();

		XmlTestCase xmlTC_S = getXmlTC(checkQuantityBean, opFormat);

		Map<XmlVariable, String> inputFollow = new LinkedHashMap<XmlVariable, String>();
		Modifier mod = new Modifier();

		inputFollow = mod.modifyTransfer(mr.getRelationOfInput(), xmlTC_S
				.getInput());
		tcCheckQuantityF.setsID(checkQuantityBean.getId());// 设置衍生用例中的sID为原始测试用例的ID
		tcCheckQuantityF.setMrID(checkQuantityBean.getMrID());

		Iterator iter = inputFollow.keySet().iterator();
		while (iter.hasNext()) {
			XmlVariable xmlVar = (XmlVariable) iter.next();
			String value = inputFollow.get(xmlVar);
			String name = xmlVar.getName();
			// 根据XmlVariable中name的不同，来的得到相应的值
			if (name.equals("name_")) {
				tcCheckQuantityF.setName(value);
			} else if (name.equals("amount_")) {
				tcCheckQuantityF.setAmount(value);
			} 
		}

		return tcCheckQuantityF;

	}
	
	

	
	/**
	 * 根据TC得到相应的XMLTestCase
	 * 
	 * @param tcCheckQuantity
	 * @param opFormat
	 * @return
	 */
	public static XmlTestCase getXmlTC(CheckQuantityBean checkQuantityBean,
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
			if (name.equals("name")) {
				value = checkQuantityBean.getName();
			} else if (name.equals("amount")) {
				value = checkQuantityBean.getAmount();
			}
			input_S.put(var, value);
		}

		return new XmlTestCase(opFormat.getOperationName(), input_S, null);

	}
	
}
