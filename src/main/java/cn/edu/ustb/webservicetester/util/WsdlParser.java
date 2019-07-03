package cn.edu.ustb.webservicetester.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.wsdl.parser.WsdlReader11;
import cn.edu.ustb.webservicetester.model.OperationInfo;
import cn.edu.ustb.webservicetester.model.ParameterInfo;

public class WsdlParser {
	
	public WsdlParser() {}
	
	public List<OperationInfo> parseWsdl(String wsdlUrl) {
		java.util.List<OperationInfo> opList = new java.util.LinkedList<OperationInfo>();
		OperationInfo tOpInfo;
		java.util.List<ParameterInfo> pmList;
		ParameterInfo tPmInfo;
		WsdlReader11 reader11 = new WsdlReader11();
		Object doc = reader11.getWsdlDoc(wsdlUrl);
		Map services = reader11.getServices(doc);
		List bindingOps = reader11.getBindingOperations(services);
		List<String> tl = new ArrayList(10);
		tl.add("byte");
		tl.add("short");
		tl.add("int");
		tl.add("long");
		tl.add("double");
		tl.add("float");
		tl.add("boolean");
		tl.add("char");
		tl.add("String");
		tl.add("string");
		for(int i = 0; i < bindingOps.size(); i++){
			tOpInfo = new OperationInfo();
			pmList = new java.util.LinkedList<ParameterInfo>();
			Object bop = bindingOps.get(i);			
			WsdlOperationFormat opFormat = reader11.getTestCase(wsdlUrl, bop);
//			System.out.println("JJT_Test begin");
//			System.out.println(opFormat.getOperationName());
//			System.out.println(opFormat.getInput());
//			System.out.println(opFormat.getOutput());
//			System.out.println("JJT_Test end");
			tOpInfo.setOpName(((javax.wsdl.BindingOperation) bop).getName());
			Object[] opPremAry = opFormat.getInput().format.getSimpleTypes().keySet().toArray();
//			System.out.println(opFormat.getOutput().format.getSimpleTypes().keySet());
			String[] opPremClassAry = new String[opPremAry.length];
			for (int j = 0; j < opPremAry.length; ++j) {
				String restriction = "";
				opPremClassAry[j] = opFormat.getInput().format.getSimpleTypes().get(opPremAry[j]).toString().split("@")[0].split("=")[1];
				if (!tl.contains(opPremClassAry[j])) {
					File f = new File("temp.xml");
//					if (!f.exists()) {
						try {
							WSDLFactory factory = WSDLFactory.newInstance();
							WSDLReader reader = factory.newWSDLReader();
							reader.setFeature("javax.wsdl.verbose", true);
							reader.setFeature("javax.wsdl.importDocuments", true);
							Definition def = reader.readWSDL(wsdlUrl);
							Writer xmlWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("temp.xml")));
							WSDLWriter wsdlWriter = factory.newWSDLWriter();
							wsdlWriter.writeWSDL(def, xmlWriter);
						} catch(Exception e) {
							e.printStackTrace();
						}
//					}
					SAXReader dReader = new SAXReader();
					org.dom4j.Document document; 
					try {
						document = dReader.read(new File("temp.xml"));
						org.dom4j.Element root = document.getRootElement();
						org.dom4j.Element typesElement = root.element("types");
						org.dom4j.Element schemaElement = typesElement.element("schema");
						@SuppressWarnings("unchecked")
						List<org.dom4j.Element> elist = (List<org.dom4j.Element>)schemaElement.elements();
						for (org.dom4j.Element e : elist) {
							if (e.attribute("name").getValue().equals(opPremClassAry[j])) {
								org.dom4j.Element rElement = e.element("restriction");
								opPremClassAry[j] = rElement.attribute("base").getValue().split(":")[1];
								restriction = e.asXML();
								break;
							}
						}
					} catch (DocumentException e) {}
				}
				tPmInfo = new ParameterInfo();
				tPmInfo.setName((String)opPremAry[j]);
				tPmInfo.setClassInfo(opPremClassAry[j]);
				tPmInfo.setRestriction(restriction);
				pmList.add(tPmInfo);
			}
			tOpInfo.setParaList(pmList);
			opList.add(tOpInfo);
		}
		return opList;
	} 
	
	public static void main(String[] args) {
		WsdlParser wp = new WsdlParser();
		List<OperationInfo> oplist = wp.parseWsdl("http://localhost:8084/services/acms?wsdl");
		System.out.println(oplist);
		for (OperationInfo operationInfo : oplist){
			System.out.println("操作名为：" + operationInfo.getOpName());
			List<ParameterInfo> palist = operationInfo.getParaList();
			for (ParameterInfo parameterInfo : palist){
				System.out.println("参数名为：" + parameterInfo);
			}
		}
	}

}
