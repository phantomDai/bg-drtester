package cn.edu.ustb.mt4ws.tcg;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.ustb.mt4ws.wsdl.parser.WsdlReader11;


public class Tester {

	public static void main(String[] args) throws Exception {
		
		WsdlReader11 reader11 = new WsdlReader11();
		String wsdl = "E:/ATMService1.1.wsdl";
		Map<String, WsdlOperationFormat> opFormat = reader11
				.parseWSDL(wsdl);
		Set<Map.Entry<String, WsdlOperationFormat>> entry = opFormat.entrySet();
		Iterator<Map.Entry<String, WsdlOperationFormat>> iter = entry
				.iterator();
		Map.Entry<String, WsdlOperationFormat> myentry = null;
		
		int k = 4;
		for (int i = 0; i < k; i++)
			myentry = iter.next();
		SqlUtils sqlU= SqlUtils.getInstance();
		sqlU.insertRandomTC(myentry.getKey(), myentry.getValue());

	}


}
