package cn.edu.ustb.mt4ws.wsdl.parser;

import javax.wsdl.*;
import javax.wsdl.extensions.*;
import javax.wsdl.factory.*;
import javax.wsdl.xml.*;
import javax.xml.namespace.QName;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * A simple test script for getting wsdl version 1.1
 * @author WangGuan
 *
 */
public class Tester11 {

	public static void main(String[] args) {
		try {
			String wsdlUri = "http://localhost:8080/axis2/services/ATMService?wsdl";
			WsdlVersion version = new WsdlVersion();
			WSDLFactory factory = WSDLFactory.newInstance();
			WSDLReader reader = factory.newWSDLReader();
			reader.setFeature("javax.wsdl.verbose", true);
			reader.setFeature("javax.wsdl.importDocuments", true);
			//Get version of the WSDL
			try {
				version.getVersion(wsdlUri);
			} catch (SAXException se) {
				if ("WSDL Version:1.1".equals(se.getMessage()))
					System.out.println("@#$%^&*#$%^&*(#$%^&*(111111111");
			}
			Definition def = reader.readWSDL(wsdlUri);
			// get a service in the WSDL
			System.out.println("----------");
			System.out.println("/nService Name:");
			String tns = "http://localhost:8080/axis2/ATMService";
			Service service = def.getService(new QName(tns, "ATMService"));
			System.out.println(service.getQName().getLocalPart());
			// get a portType in the WSDL
			System.out.println("/nOperation Name:");
			Port port = service.getPort("ATMService");
			Binding binding = port.getBinding();
			PortType portType = binding.getPortType();

			List operations = portType.getOperations();
			Iterator operIter = operations.iterator();
			while (operIter.hasNext()) {
				Operation operation = (Operation) operIter.next();
				if (!operation.isUndefined()) {
					System.out.println(operation.getName());
				}
			}
			// get wsdl messages
			System.out.println("/nMessages:");
			Map messages = def.getMessages();
			Iterator msgIter = messages.values().iterator();
			while (msgIter.hasNext()) {
				Message msg = (Message) msgIter.next();
				if (!msg.isUndefined()) {
					System.out.println(msg.getQName().getLocalPart());
					Iterator partIter = msg.getParts().values().iterator();
					while (partIter.hasNext()) {
						Part part = (Part) partIter.next();
						System.out.print("parameter name:" + part.getName()
								+ "/t");
						System.out.println("parameter type:"
								+ part.getTypeName().getLocalPart());
					}
				}

			}
			System.out.println("/nService location:");
			List l = port.getExtensibilityElements();
			ExtensibilityElement element = (ExtensibilityElement) l.get(0);
			String s = element.toString();
			System.out.println(s.substring(s.indexOf("location")));
			System.out.println("---------");

			WSDLWriter writer = factory.newWSDLWriter();
			Document doc = writer.getDocument(def);
			NodeList list = doc.getElementsByTagName("simpleType");

		} catch (WSDLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
