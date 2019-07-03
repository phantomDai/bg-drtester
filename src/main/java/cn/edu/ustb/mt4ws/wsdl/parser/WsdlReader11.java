package cn.edu.ustb.mt4ws.wsdl.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.wsdl.Part;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.tcg.XmlInputFormat;
import cn.edu.ustb.mt4ws.tcg.XmlOutputFormat;

import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import com.ibm.wsdl.OperationImpl;

/**
 * A real WSDL Reader for WSDL1.1
 * @author WangGuan
 *
 */
public class WsdlReader11 extends AbstractWsdlReader {

	private String WsdlURI;
	
	@Override
	public List getBindingOperations(Map services) {
		List bopL = new ArrayList();
		Iterator servicesIter = services.values().iterator();
		while (servicesIter.hasNext()) {
			Service service = (Service) servicesIter.next();
			Iterator portsIter = service.getPorts().values().iterator();
			while (portsIter.hasNext()) {
				Port port = (Port) portsIter.next();
				Binding binding = port.getBinding();
				List bindingOperations = binding.getBindingOperations();
				Iterator bOperIter = bindingOperations.iterator();
				while (bOperIter.hasNext()) {
					BindingOperation bindingOper = (BindingOperation) bOperIter
							.next();
					bopL.add(bindingOper);
				}
			}
		}
		return bopL;
	}

	@Override
	public Map getServices(Object DefOrDesc) {
		Definition def = (Definition) DefOrDesc;
		Map services = def.getServices();
		return services;
	}

	@Override
	public Definition getWsdlDoc(String WsdlURI) {
		WSDLFactory factory;
		this.WsdlURI = WsdlURI;
		try {
			factory = WSDLFactory.newInstance();
			WSDLReader reader = factory.newWSDLReader();
			reader.setFeature("javax.wsdl.verbose", true);
			reader.setFeature("javax.wsdl.importDocuments", true);
			Definition def = reader.readWSDL(WsdlURI);
			return def;
		} catch (WSDLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param WsdlURI
	 * @param bop
	 * @param direct
	 *            true:input; false:output
	 * @return
	 */
	private XmlInputFormat getInputFormat(String WsdlURI, BindingOperation bop) {
		SampleSoapBuilder builder = new SampleSoapBuilder(new WsdlContext(
				WsdlURI));
		String soapRequest = null;
		try {
			soapRequest = builder.buildSoapMessageFromInput(bop, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XmlInputFormat xmlInputFormat = new XmlInputFormat(soapRequest,builder.getFormat());
		return xmlInputFormat;
	}

	private XmlOutputFormat getOutputFormat(String WsdlURI, BindingOperation bop) {
		SampleSoapBuilder builder = new SampleSoapBuilder(new WsdlContext(
				WsdlURI));
		String soapResponse = null;
		try {
			soapResponse = builder.buildSoapMessageFromOutput(bop, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XmlOutputFormat xmlOutputFormat = new XmlOutputFormat(soapResponse,builder.getFormat());
		return xmlOutputFormat;
	}

	@Override
	public WsdlOperationFormat getTestCase(String WsdlURI, Object bop) {
		// TODO
		BindingOperation bindingop = (BindingOperation) bop;

		WsdlOperationFormat opFormat = new WsdlOperationFormat();
		opFormat.build(bindingop.getName());
		opFormat.build(getInputFormat(WsdlURI, bindingop), getOutputFormat(WsdlURI,
				bindingop));
		return opFormat;
	}
	
	public void zzqTest(String WsdlURI){
		Object doc = getWsdlDoc(WsdlURI);
		Map services = getServices(doc);
		List bindingOps = getBindingOperations(services);
		List<String> tl = new ArrayList();
		tl.add("byte");
		tl.add("short");
		tl.add("int");
		tl.add("long");
		tl.add("double");
		tl.add("float");
		tl.add("boolean");
		tl.add("char");
		tl.add("String");
		for(int i = 0; i < bindingOps.size(); i++){
			
			System.out.println(bindingOps.size());
			System.out.println(i);
			
			Object bop = bindingOps.get(i);			
			WsdlOperationFormat opFormat = getTestCase(WsdlURI, bop);
//			System.out.println("zzq开始尝试");
//			System.out.println("opFormat.getInput().toString():");
//			System.out.println(opFormat.getInput().toString());//输出各个操作输入（格式 参数）的信息
//			System.out.println("opFormat.getInput().format.printComplexTypes():");
//			System.out.println(opFormat.getInput().format.printComplexTypes());
//			System.out.println("opFormat.getInput().format.printSimpleTypes():");
			System.out.println(((javax.wsdl.BindingOperation) bop).getName());
//			System.out.println(opFormat.getInput().format.printSimpleTypes());
//			System.out.println(opFormat.getOutput().format.printSimpleTypes());
//			System.out.println("zzq尝试结束");
			
//			if (((javax.wsdl.BindingOperation) bop).getName().equals("transfer")) {
				System.out.println("?");
				Object[] opPremAry = opFormat.getInput().format.getSimpleTypes().keySet().toArray();
				String[] opPremTypeAry = new String[opPremAry.length];
				for (int j = 0; j < opPremAry.length; ++j) {
					opPremTypeAry[j] = opFormat.getInput().format.getSimpleTypes().get(opPremAry[j]).toString().split("@")[0].split("=")[1];
					if (!tl.contains(opPremTypeAry[j])) {
						File f = new File("temp.xml");
						if (!f.exists()) {
							try {
								WSDLFactory factory = WSDLFactory.newInstance();
								WSDLReader reader = factory.newWSDLReader();
								reader.setFeature("javax.wsdl.verbose", true);
								reader.setFeature("javax.wsdl.importDocuments", true);
								Definition def = reader.readWSDL(WsdlURI);
								Writer xmlWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("temp.xml")));
								WSDLWriter wsdlWriter = factory.newWSDLWriter();
								wsdlWriter.writeWSDL(def, xmlWriter);
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
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
							if (e.attribute("name").getValue().equals(opPremTypeAry[j])) {
								org.dom4j.Element rElement = e.element("restriction");
								opPremTypeAry[j] = rElement.attribute("base").getValue().split(":")[1];
								break;
							}
						}
					} catch (DocumentException e) {}
					System.out.print((String)(opPremAry[j]));
					System.out.print(" : ");
					System.out.println(opPremTypeAry[j]);
				}
//			}
		}
		
//		List bdops = getBindingOperations(getServices(doc));
//		BindingOperation bdop = (BindingOperation)(bdops.get(0));
//		Operation op = bdop.getOperation();
//		List parts = op.getInput().getMessage().getOrderedParts(null);
//		int count = parts.size();
//		String[] inNames = new String[count];
//		Class[] inTypes = new Class[count];
//		for (int i = 0; i < count; ++i) {
//			inNames[i] = ((Part)parts.get(i)).getName();
//			String t = ((Part)parts.get(i)).getTypeName().getLocalPart();
//			System.out.println(t);
//		}
	}
	
	public static void main(String[] args){
		WsdlReader11 wr11test = new WsdlReader11();
		wr11test.zzqTest("http://localhost:8080/axis2/services/ATMService?wsdl");
	}
}
