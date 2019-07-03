package cn.edu.ustb.mt4ws.wsdl.parser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;

/**
 * An abstract WSDL reader
 * @author WangGuan
 *
 */
public abstract class AbstractWsdlReader {

	public static final int WSDL11 = 1;
	public static final int WSDL20 = 2;

	/**
	 * 返回Map，指示BindingOperation与TestCase的对应关系
	 * <p>
	 * 使用Template Pattern(Template模式，用于封装算法)
	 * 
	 * @param WsdlURI
	 * @return Map<BindingOperation(wsdl4j or woden),TestCase>
	 */
	public final Map<String, WsdlOperationFormat> parseWSDL(String WsdlURI) {
		Object doc = getWsdlDoc(WsdlURI);
		Map services = getServices(doc);
		List bindingOps = getBindingOperations(services);
		return putTestCases(WsdlURI, bindingOps);
	}

	/**
	 * @param WsdlURI
	 * @return WSDL1.1: Definition; WSDL2.0: Description
	 */
	public abstract Object getWsdlDoc(String WsdlURI);

	/**
	 * 
	 * @param DefOrDesc
	 *            WSDL1.1->Definition; WSDL2.0->Description
	 * @return
	 */
	public abstract Map getServices(Object DefOrDesc);

	/**
	 * 
	 * @param services
	 * @return bindingOperations
	 */
	public abstract List getBindingOperations(Map services);

	/**
	 * 
	 * @param bop
	 *            BindingOperation(wsdl4j or woden)
	 * @return
	 */
	public abstract WsdlOperationFormat getTestCase(String WsdlURI, Object bop);

	/**
	 * 将每个BindingOperation解析为相应的TestCase并写入Map中返回
	 * <p>
	 * 由WSDL版本自动判断返回值Map里面的Object是哪个版本的BindingOperation
	 * 
	 * @param bindingOps
	 * @return (BindingOperation(wsdl4j or woden),TestCase)
	 */
	private Map<String, WsdlOperationFormat> putTestCases(String WsdlURI,
			List bindingOps) {
		int wsdlVersion = checkWsdlVersion(WsdlURI);
		Map<String, WsdlOperationFormat> map = new LinkedHashMap<String, WsdlOperationFormat>();
		
		if (wsdlVersion == this.WSDL11)
			for (int i = 0; i < bindingOps.size(); i++) {
				Object bop = bindingOps.get(i);			
				WsdlOperationFormat opFormat = getTestCase(WsdlURI, bop);
				
				map
						.put(((javax.wsdl.BindingOperation) bop).getName(),
								opFormat);
				System.out.println("operationName: "
						+ ((javax.wsdl.BindingOperation) bop).getName());
			}
		else
			// wsdl2.0
			for (int i = 0; i < bindingOps.size(); i++) {
				Object bop = bindingOps.get(i);
				WsdlOperationFormat opFormat = getTestCase(WsdlURI, bop);
				map.put(((org.apache.woden.wsdl20.InterfaceOperation) bop)
						.getName().getLocalPart(), opFormat);
			}
		return map;
	}

	private int checkWsdlVersion(String wsdlURI) {
		return this.WSDL11;// TODO
	}

}
