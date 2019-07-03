package cn.edu.ustb.mt4ws.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.util.Elements;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;

public class DomBPEL {

	public static void main(String[] arges) {
		String filePath="D://work//Tools//apache-tomcat-6.0.35//webapps//ode//WEB-INF//processes//CaculatorPro//CaculatorProcess.bpel";
		DomBPEL dom=new DomBPEL();
		dom.parserBPEL(filePath);
	}

	/**
	 * 解析BPEL文件，返回import标签的所有属性的集合
	 * 
	 * @param filePath
	 * @return
	 */
	public List<Map> parserBPEL(String filePath) {
		File file = new File(filePath);
		// 判断是否存在
		if (file.exists()) {
			// 得到BPEL文件中的import Tab的所有内容
			return getImport(filePath);
		}
		return null;
	}

	/**
	 * 解析得到import的所有属性,若没有则返回null
	 * @param filePath
	 * @return
	 */
	public List<Map> getImport(String filePath) {
		List<Map> list=new ArrayList<Map>();
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder;
		try {
			builder = builderFactory.newDocumentBuilder();
			/*
			 * builder.parse()方法将给定文件的内容解析为一个 XML 文档， 并且返回一个新的 DOM Document对象。
			 */
			Document document = builder.parse(new File(filePath));
			//得到所有tabName为import的NodeList
			NodeList elements_import=document.getElementsByTagName("bpel:import");
			//遍历
			for(int i=0;i<elements_import.getLength();i++){
				Node node_import=elements_import.item(i);
				Map<String,String> map=getImportAttribute(node_import);
				list.add(map);
			}
			return list;
			
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

		return null;
	}
	
	/**
	 * 根据node返回所有属性
	 * @param node_1
	 * @return
	 */
	public Map<String, String> getImportAttribute(Node node_1){
		Map<String,String> map=new HashMap<String,String>();
		Node node=node_1;
		//得到该NODE的所有属性
		NamedNodeMap attributes=node.getAttributes();
		for(int j=0;j<attributes.getLength();j++){
			Node node_att=attributes.item(j);
			map.put(node_att.getNodeName(), node_att.getNodeValue());
			System.out.println(node_att.getNodeName()+":"+node_att.getNodeValue());
		}
		return map;		
	}
	

}
