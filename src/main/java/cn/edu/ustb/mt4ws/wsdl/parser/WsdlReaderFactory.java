package cn.edu.ustb.mt4ws.wsdl.parser;

/**
 * Wsdl Reader factory 
 * @author WangGuan
 *
 */
public class WsdlReaderFactory {


	public WsdlReaderFactory() {
	}

	/**
	 * get WSDL Reader according to WSDL version
	 * @param wsdlVersion
	 * @return
	 * @throws Exception
	 */
	public AbstractWsdlReader getReader(int wsdlVersion) throws Exception {
		AbstractWsdlReader reader = null;
		if (wsdlVersion == AbstractWsdlReader.WSDL11) {
			reader = new WsdlReader11();
		} else if (wsdlVersion == AbstractWsdlReader.WSDL20) {
			reader = new WsdlReader20();
		} else {
			throw new Exception("can't identify WSDL Version!!!");
		}
		return reader;
	}

}
