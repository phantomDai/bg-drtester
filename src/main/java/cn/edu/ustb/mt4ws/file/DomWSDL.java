package cn.edu.ustb.mt4ws.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomWSDL {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		DomWSDL domWSDL = new DomWSDL();
		String filePath = "D://work//Tools//apache-tomcat-6.0.35//webapps//MT4WSJSP//upload//CaculatorPro//CaculatorPro//CaculatorProcessArtifacts.wsdl";
		domWSDL.getService(filePath);

	}

	/**
	 * 得到serviceName和address
	 * 
	 * @param filePath
	 * @return
	 */
	public Map<String, String> getService(String filePath) {
		File file = new File(filePath);
		Map<String, String> map = new HashMap<String, String>();
		// 判断是否存在
		if (file.exists()) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			try {
				DocumentBuilder builder = builderFactory.newDocumentBuilder();
				/*
				 * builder.parse()方法将给定文件的内容解析为一个 XML 文档， 并且返回一个新的 DOM
				 * Document对象。
				 */
				Document document = builder.parse(file);

				NodeList elements_service = document
						.getElementsByTagName("service");
				// 若为空，则查找wsdl：service
				if (elements_service.getLength() == 0) {
					elements_service = document
							.getElementsByTagName("wsdl:service");
				}
				// 得到service的NODE
				Node node_service = elements_service.item(0);

				String serviceName = node_service.getAttributes().getNamedItem(
						"name").getNodeValue();
				System.out.println("serviceName:" + serviceName);
				NodeList elements_address = node_service.getOwnerDocument()
						.getElementsByTagName("soap:address");
				// 若查找为空
				if (elements_address.getLength() == 0) {
					elements_address = node_service.getOwnerDocument()
							.getElementsByTagName("wsdlsoap:address");
				}
				// 得到address的NODE
				map.put("serviceName", serviceName);
				Node node_address = elements_address.item(0);
				String address = node_address.getAttributes().getNamedItem(
						"location").getNodeValue();
				System.out.println("URL:" + address);
				map.put("address", address);

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return map;
	}

	/**
	 * 在Map中加入address和serviceName
	 * 
	 * @param filePath
	 * @param list
	 * @return
	 */
	public List<Map> addAddress(String filePath, List<Map> list) {
		List<Map> listMap = new ArrayList<Map>();
		filePath = filePath.substring(0, filePath.lastIndexOf("\\") + 1);
		System.out.println("project:" + filePath);
		Iterator<Map> iter = list.iterator();
		while (iter.hasNext()) {
			Map<String, String> map = iter.next();
			String location = filePath + map.get("location");
			System.out.println("WSDL location:" + location);
			map.putAll(getService(location));
			listMap.add(map);
		}
		return listMap;
	}

}
