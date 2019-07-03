package cn.edu.ustb.mt4ws.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.edu.ustb.mt4ws.mr.jsp.TransferInputBean;

public class ParserATMTCTest {

	/**
	 * 构造函数
	 */
	public ParserATMTCTest() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "C://Users//lenovo//Desktop//ATMTestCaseSet.xml";

		ParserATMTCTest test = new ParserATMTCTest();
		try {
			test.parseXML(path);
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

	/**
	 * 解析ATMService中的关于testCase的XML文件
	 * 
	 * @param path
	 * @return
	 */
	public List<TransferInputBean> parseXML(String path)
			throws ParserConfigurationException, SAXException, IOException {

		String filePath = path;

		List<TransferInputBean> listTC = new ArrayList<TransferInputBean>();

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();

		/*
		 * builder.parse()方法将给定文件的内容解析为一个 XML 文档， 并且返回一个新的 DOM Document对象。
		 */
		Document document = builder.parse(new File(filePath));
		// 获取文档的根元素，赋值给rootElement变量
		Element rootElement = document.getDocumentElement();

		// 获取rootElement的所有子节点（不包括属性节点），返回一个NodeList对象
		NodeList childNodes_1 = rootElement.getChildNodes();

		for (int i = 0; i < childNodes_1.getLength(); i++) {
			Node childNode_1 = childNodes_1.item(i);

			// 判断子节点为testCase
			if (childNode_1.getNodeName().equals("testCase")) {
				System.out.println("根目录下的第" + i + "个子节点："
						+ childNode_1.getNodeName());
				TransferInputBean testcase = getTestCase(childNode_1);

				listTC.add(testcase);
			}

		}

		return listTC;
	}

	public static TransferInputBean getTestCase(Node node) {
		TransferInputBean testcase = new TransferInputBean();

		// 获取node的所有子节点（不包括属性节点），返回一个NodeList对象
		NodeList childNodes_2 = node.getChildNodes();
		for (int i = 0; i < childNodes_2.getLength(); i++) {
			Node childNode_2 = childNodes_2.item(i);

			if (!childNode_2.getNodeName().equals("#text")) {

				String nodeName = childNode_2.getNodeName();

				System.out.println("  testCase下的第" + i + "个子节点："
						+ childNode_2.getNodeName());
				if (nodeName.equals("accountFrom")) {
					String accountFrom = childNode_2.getChildNodes().item(1)
							.getTextContent();
					System.out.println("    accountFrom:" + accountFrom);
					testcase.setAccoutFrom(accountFrom);

				} else if (nodeName.equals("accountTo")) {
					String accountTo = childNode_2.getChildNodes().item(1)
							.getTextContent();
					System.out.println("    accountTo:" + accountTo);
					testcase.setAccoutTo(accountTo);

				} else if (nodeName.equals("mode")) {
					String mode = childNode_2.getChildNodes().item(1)
							.getTextContent();
					System.out.println("    mode:" + mode);
					testcase.setMode(mode);
				} else if (nodeName.equals("amount")) {
					String amount = childNode_2.getChildNodes().item(1)
							.getTextContent();
					System.out.println("    amount:" + amount);
					testcase.setAmount(amount);

				} else {
					System.out.println("node错误");
				}

			}
		}

		return testcase;
	}

}