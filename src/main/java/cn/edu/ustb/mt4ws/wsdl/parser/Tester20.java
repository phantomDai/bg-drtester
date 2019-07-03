package cn.edu.ustb.mt4ws.wsdl.parser;

import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.Interface;
/**
 * A simple test script for getting wsdl version 2.0
 * @author WangGuan
 *
 */
public class Tester20 {

	private static WSDLFactory factory = null;
	private static WSDLReader reader = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			factory = WSDLFactory
					.newInstance("org.apache.woden.internal.DOMWSDLFactory");
			reader = factory.newWSDLReader();
			Description desc = null;
			String wsdlURL = "D:/WangGuan/ATMService2.0.wsdl";
			desc = reader.readWSDL(wsdlURL);
			Interface[] interfaces = null;
			interfaces = desc.getInterfaces();
			int numberOfInterfaces = interfaces.length;
			for (int i = 0; i < numberOfInterfaces; i++)
				for (int j = 0; j < interfaces[i].getInterfaceOperations().length; j++) {
					System.out
							.println(interfaces[i].getInterfaceOperations()[j]
									.getName().toString());
				}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
