package cn.edu.ustb.mt4ws.wsdl.parser;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.edu.ustb.mt4ws.tcg.SqlUtils;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;

/**
 * A Tester for WsdlReader11
 * @author WangGuan
 *
 */
public class WsdlReader11Test {

	public static void main(String[] args) {
		WsdlReader11 reader11 = new WsdlReader11();
		String wsdl = "http://localhost:8080/axis2/services/ATMService?wsdl";
		Map<String, WsdlOperationFormat> opFormat = reader11
				.parseWSDL(wsdl);
		Set<Map.Entry<String, WsdlOperationFormat>> entry = opFormat.entrySet();
		Iterator<Map.Entry<String, WsdlOperationFormat>> iter = entry
				.iterator();
		Map.Entry<String, WsdlOperationFormat> myentry = null;

		/*
		 * 打印所有operation的名字
		 */
		// while (iter.hasNext()) {
		// myentry = iter.next();
		// System.out.println(((BindingOperation) myentry.getKey()).getName());
		// }

		/*
		 * 打印第k个operation的build-in types
		 */
//		iter = entry.iterator();// 回到初始位置
//		int k = 4;
//		for (int i = 0; i < k; i++)
//			myentry = iter.next();
		//
		// System.out.println(((BindingOperation) myentry.getKey()).getName()
		// + ":\n"
		// + myentry.getValue().getInput().format.printBuiltInTypes());

		/*
		 * 生成一个随机TC存入数据库
		 */
		 SqlUtils sqlUtils = SqlUtils.getInstance();
		 sqlUtils.createTableByOperation(opFormat);
		// sqlUtils.insertTestCaseToDB(
		// ((BindingOperation) myentry.getKey()).getName(), myentry
		// .getValue().getInput().format);

		// 打印所有operation的名字及TestCase格式

//		System.out.println(myentry.getKey() + "&&&&&"
//				+ myentry.getValue().getOutput().toString());

		/*
		 * MR包的测试
		 */
		// Modifier mod = new Modifier();
		// String expR = "amount_=2*amount";
		// String expRs[] = { expR };
		// XmlTestCaseGenerator tcGen = new
		// XmlTestCaseGenerator(myentry.getValue()
		// .getInput().format);
		// Map<XmlVariable, String> inputSource = tcGen.genWithRandomValue(
		// ((BindingOperation) myentry.getKey()).getName()).getInput();
		//
		// Map<XmlVariable, String> inputFollow = mod.modify(inputSource,
		// expRs);
		// System.out.println("amount="+inputSource.get("amount"));
		// System.out.println("amount_="+inputFollow.get("amount_"));
		//
		// Verifier ver = new Verifier();
		// String expRF = "amount_=2*amount";
		// String expRFs[] = { expRF };
		// System.out.println(ver.verify(inputSource, inputFollow, expRFs));

		System.exit(1);

	}
}