package cn.edu.ustb.mt4ws.javabean;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.xmlbeans.SchemaType;

import cn.edu.ustb.mt4ws.tcg.Modifier;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.tcg.XmlTestCase;
import cn.edu.ustb.mt4ws.tcg.XmlVariable;

public class TCTransferF {

	private int id;
	private int mrID;
	private int sID;// 表示对应的Source testcase的id
	private String accountFrom;
	private String accountTo;
	private String mode;
	private String amount;

	/**
	 *无参数构造函数
	 */
	public TCTransferF() {

	}

	/**
	 * 带参数的构造函数
	 * 
	 * @param mrID
	 * @param accountFrom
	 * @param accountTo
	 * @param mode
	 * @param amount
	 */
	public TCTransferF(int mrID, String accountFrom, String accountTo,
			String mode, String amount) {
		setMrID(mrID);
		setAccountFrom(accountFrom);
		setAccountTo(accountTo);
		setAmount(amount);
		setMode(mode);
	}

	/**
	 * 带参数的构造函数
	 * 
	 * @param id
	 * @param mrID
	 * @param accountFrom
	 * @param accountTo
	 * @param mode
	 * @param amount
	 */
	public TCTransferF(int id, int mrID, String accountFrom, String accountTo,
			String mode, String amount) {
		setId(id);
		setMrID(mrID);
		setAccountFrom(accountFrom);
		setAccountTo(accountTo);
		setAmount(amount);
		setMode(mode);
	}

	/**
	 * 带参数的构造函数
	 * 
	 * @param id
	 * @param sID
	 * @param mrID
	 * @param accountFrom
	 * @param accountTo
	 * @param mode
	 * @param amount
	 */
	public TCTransferF(int id, int sID, int mrID, String accountFrom,
			String accountTo, String mode, String amount) {
		setId(id);
		setsID(sID);
		setMrID(mrID);
		setAccountFrom(accountFrom);
		setAccountTo(accountTo);
		setAmount(amount);
		setMode(mode);
	}

	/**
	 * 
	 * @param sID
	 * @param mrID
	 * @param accountFrom
	 * @param accountTo
	 * @param mode
	 * @param amount
	 */
	public TCTransferF(int mrID, String accountFrom, String accountTo,
			String mode, String amount, int sID) {
		setsID(sID);
		setMrID(mrID);
		setAccountFrom(accountFrom);
		setAccountTo(accountTo);
		setAmount(amount);
		setMode(mode);
	}

	/**
	 * set和get方法
	 */

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getAmount() {
		return amount;
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

	public String getAccountFrom() {
		return accountFrom;
	}

	public void setAccountFrom(String accountFrom) {
		this.accountFrom = accountFrom;
	}

	public String getAccountTo() {
		return accountTo;
	}

	public void setAccountTo(String accountTo) {
		this.accountTo = accountTo;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * 将衍生测试用例变成原始测试用例
	 * 
	 * @param tcTransferF
	 * @return
	 */
	public TCTransfer tranferToS() {
		TCTransfer tcTransfer = new TCTransfer();
		//tcTransfer.setId(id);
		//tcTransfer.setMrID(mrID);
		tcTransfer.setAccountFrom(accountFrom);
		tcTransfer.setAccountTo(accountTo);
		tcTransfer.setAmount(amount);
		tcTransfer.setMode(mode);
		return tcTransfer;

	}

	/**
	 * 根据原始测试用例和蜕变关系产生衍生测试用例
	 * 
	 * @param tcTransfer
	 * @param opFormat
	 * @return
	 */
	public static TCTransferF generateFollowTC(TCTransfer tcTransfer,
			WsdlOperationFormat opFormat, MetamorphicRelation mr) {

		TCTransferF tcTransferF = new TCTransferF();

		XmlTestCase xmlTC_S = getXmlTC(tcTransfer, opFormat);

		Map<XmlVariable, String> inputFollow = new LinkedHashMap<XmlVariable, String>();
		Modifier mod = new Modifier();

		inputFollow = mod.modifyTransfer(mr.getRelationOfInput(), xmlTC_S
				.getInput());
		tcTransferF.setsID(tcTransfer.getId());// 设置衍生用例中的sID为原始测试用例的ID
		tcTransferF.setMrID(tcTransfer.getMrID());

		Iterator iter = inputFollow.keySet().iterator();
		while (iter.hasNext()) {
			XmlVariable xmlVar = (XmlVariable) iter.next();
			String value = inputFollow.get(xmlVar);
			String name = xmlVar.getName();
			// 根据XmlVariable中name的不同，来的得到相应的值
			if (name.equals("accountFrom_")) {
				tcTransferF.setAccountFrom(value);
			} else if (name.equals("accountTo_")) {
				tcTransferF.setAccountTo(value);
			} else if (name.equals("mode_")) {
				tcTransferF.setMode(value);
			} else if (name.equals("amount_")) {
				tcTransferF.setAmount(value);
			}
		}

		return tcTransferF;

	}

	/**
	 * 根据TC得到相应的XMLTestCase
	 * 
	 * @param tcTransfer
	 * @param opFormat
	 * @return
	 */
	public static XmlTestCase getXmlTC(TCTransfer tcTransfer,
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
			if (name.equals("accountFrom")) {
				value = tcTransfer.getAccountFrom();
			} else if (name.equals("accountTo")) {
				value = tcTransfer.getAccountTo();
			} else if (name.equals("mode")) {
				value = tcTransfer.getMode();
			} else if (name.equals("amount")) {
				value = tcTransfer.getAmount();
			}
			input_S.put(var, value);
		}

		return new XmlTestCase(opFormat.getOperationName(), input_S, null);

	}

	/**
	 * 依次打印衍生测试用例
	 * 
	 * @param tcTransferF
	 */
	public static void printTCF(TCTransferF tcTransferF) {
		System.out.println("**********打印衍生测试用例****************");
		System.out.println("Sid: " + tcTransferF.getsID());
		System.out.println("mrID: " + tcTransferF.getMrID());
		System.out.println("accountFrom: " + tcTransferF.getAccountFrom());
		System.out.println("accountTo: " + tcTransferF.getAccountTo());
		System.out.println("amount: " + tcTransferF.getAmount());
		System.out.println("mode: " + tcTransferF.getMode());
	}
}
