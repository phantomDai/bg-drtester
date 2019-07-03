package cn.edu.ustb.mt4ws.wsdl.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * WSDL Version
 * @author WangGuan
 *
 */
public class WsdlVersion extends DefaultHandler {

	private int version = 0;

	/**
	 * Get WSDL version by parse the root element of WSDL
	 * @param wsdlUri
	 * @throws SAXException if the version is wsdl1.1 throw a SAXException("WSDL Version:1.1")
	 * @throws Exception
	 */
	public void getVersion(String wsdlUri) throws SAXException,Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		WsdlVersion handler = new WsdlVersion();
		URL url = new URL(wsdlUri);
		URLConnection URLconnection = url.openConnection();
	    HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;
		InputStream xmlStream = httpConnection.getInputStream();
		parser.parse(xmlStream, handler);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		System.out.println("Start Element :" + qName);

		if (qName.contains("definitions")) {
			version = AbstractWsdlReader.WSDL11;
			throw new SAXException("WSDL Version:1.1");
		}

		if (qName.contains("description")) {
			version = AbstractWsdlReader.WSDL20;
			throw new SAXException("WSDL Version:2.0");
		}

		try {
			throw new Exception("can't identify wsdl version!!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		System.out.println("End Element :" + qName);

	}

}
