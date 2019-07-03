package cn.edu.ustb.mt4ws.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.edu.ustb.mt4ws.javabean.*;

public class ParserMrsTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ParserMrsTest test1=new ParserMrsTest();
		String filePath="D://work//Tools//apache-tomcat-6.0.35//webapps//MT4WSJSP//ATMService//mrdl_transfer.xml";
		
		
		try {
			test1.parserMrSet(filePath);
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
	 * 根据路径path解析MrsXML文件
	 * @param path
	 * @return List<MetamorphicRelation> mrList
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private List<MetamorphicRelation> parserMrSet(String path) throws ParserConfigurationException, SAXException, IOException{
		
		String filePath = path;
		List<MetamorphicRelation> mrList=null;
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		
		/*
		 * builder.parse()方法将给定文件的内容解析为一个 XML 文档， 并且返回一个新的 DOM Document对象。
		 */
		Document document = builder.parse(new File(filePath));
		// 获取文档的根元素，赋值给rootElement变量
		// 打印document节点
		//printNode(document, 0);
		
		Element rootElement = document.getDocumentElement();
		NodeList childNodes_MRs = rootElement.getChildNodes();
		
		//遍历MrSet
		for(int i = 0; i < childNodes_MRs.getLength(); i++){
			MetamorphicRelation metamorphicRelation=null;
			Node childNodeMr=childNodes_MRs.item(i);
			//提取其中的mr节点
			if(childNodeMr.getNodeName().equals("mr")){
				System.out.println(childNodeMr.getNodeName()+":"+childNodeMr.getNodeValue());
				
				metamorphicRelation=parserMetaRelation(childNodeMr);
			}
			
		}
		
		
		return mrList;
	}
	
	/**
	 * 解析MetamorphicRelation下的所有节点，并存储在MetamorphicRelation中
	 * @param node
	 * @return
	 */
	private MetamorphicRelation parserMetaRelation(Node node){
		Node nodeMr=node;
		MetamorphicRelation metamorphicRelation=new MetamorphicRelation();
		//获得mr下的所有子节点
		NodeList childNodes_Mr=nodeMr.getChildNodes();
		// 遍历mr
		for (int i = 0; i < childNodes_Mr.getLength(); i++) {
			Node childNode = childNodes_Mr.item(i);
						
			if (childNode.getNodeName().equals("relationOfInput")) {
				//解析节点relationOfInput
				System.out.println("  " + childNode.getNodeName() + ":"
						+ childNode.getNodeValue());
				RelationOfInput relationOfInput=parserRelationOfInput(childNode);
				metamorphicRelation.setRelationOfInput(relationOfInput);
				
			} else if (childNode.getNodeName().equals("relationOfOutput")) {
				//解析节点relationOfOutput
				System.out.println("  " + childNode.getNodeName() + ":"
						+ childNode.getNodeValue());
				RelationOfOutput relationOfOutput=parserRelationOfOutput(childNode);
				metamorphicRelation.setRelaitonOfOutput(relationOfOutput);
				
			}
		}

		return metamorphicRelation;
	}
	
	/**
	 * 解析RelationOfInput下的所有节点，并存储在RelationOfInput中
	 * @param node
	 * @return
	 */
	private RelationOfOutput parserRelationOfOutput(Node node){
		Node nodeRelationOfInput=node;
		RelationOfOutput relationOfOutput=new RelationOfOutput();
		//获得RelationOfInput下的所有子节点
		NodeList childNodes_Input=nodeRelationOfInput.getChildNodes();
		List<OpAndRelation> opAndrelationList = new ArrayList<OpAndRelation>();
		// 遍历RelationOfInput
		for (int i = 0; i < childNodes_Input.getLength(); i++) {
			Node childNode= childNodes_Input.item(i);
			if (childNode.getNodeName().equals("relation")) {
				//解析节点relation
				System.out.println("    " + childNode.getNodeName() + ":"
						+ childNode.getNodeValue());
				Relation relation=parserRelation(childNode);
				relationOfOutput.setRelation(relation);
				
			}else if(childNode.getNodeName().equals("opAndrelation")){
				//解析节点opAndrelation,并存到opAndrelationList中
				System.out.println("    " + childNode.getNodeName() + ":"
						+ childNode.getNodeValue());
				OpAndRelation opAndRelation=parserOpAndRelation(childNode);
				opAndrelationList.add(opAndRelation);
				relationOfOutput.setListOpAndRe(opAndrelationList);				
			}		
		}
		
		return relationOfOutput;
	}
	
	/**
	 * 解析RelationOfInput下的所有节点，并存储在RelationOfInput中
	 * @param node
	 * @return
	 */
	private RelationOfInput parserRelationOfInput(Node node){
		Node nodeRelationOfInput=node;
		RelationOfInput relationOfInput=new RelationOfInput();
		//获得RelationOfInput下的所有子节点
		NodeList childNodes_Input=nodeRelationOfInput.getChildNodes();
		List<OpAndRelation> opAndrelationList = new ArrayList<OpAndRelation>();
		// 遍历RelationOfInput
		for (int i = 0; i < childNodes_Input.getLength(); i++) {
			Node childNode= childNodes_Input.item(i);
			if (childNode.getNodeName().equals("relation")) {
				//解析节点relation
				System.out.println("    " + childNode.getNodeName() + ":"
						+ childNode.getNodeValue());
				Relation relation=parserRelation(childNode);
				relationOfInput.setRelation(relation);
				
			}else if(childNode.getNodeName().equals("opAndrelation")){
				//解析节点opAndrelation,并存到opAndrelationList中
				System.out.println("    " + childNode.getNodeName() + ":"
						+ childNode.getNodeValue());
				OpAndRelation opAndRelation=parserOpAndRelation(childNode);
				opAndrelationList.add(opAndRelation);
				relationOfInput.setListOpAndRe(opAndrelationList);
				
			}
		
		}
		
		return relationOfInput;
	}
	
	/**
	 * 解析Relation下的所有节点，并存储在Relation中
	 * @param node
	 * @return
	 */
	private Relation parserRelation(Node node){
		Node nodeRelation=node;
		Relation relation= new Relation();
		//获得Relation下的所有子节点
		NodeList childNodes_Relation = nodeRelation.getChildNodes();
		//遍历relation
		for (int i = 0; i < childNodes_Relation.getLength(); i++) {
			Node childNode= childNodes_Relation.item(i);
			
			if (childNode.getNodeName().equals("NEGOp")) {
				//NEGOp
				System.out.println("             " + childNode.getNodeName() + ":"
						+ childNode.getTextContent());
				Operator negOp=new Operator(childNode.getTextContent());
				relation.setNotOp(negOp);
				
			}else if (childNode.getNodeName().equals("expressionFollowUp")) {
				//FollowUp
				System.out.println("             " + childNode.getNodeName() + ":"
						+ childNode.getChildNodes().item(1).getTextContent());
				Function functionF=new Function(childNode.getChildNodes().item(1).getTextContent());
				relation.setFunctionFollowUp(functionF);				
			}else if (childNode.getNodeName().equals("operator")) {
				//Operator
				System.out.println("             " + childNode.getNodeName() + ":"
						+ childNode.getTextContent());
				Operator op=new Operator(childNode.getTextContent());
				relation.setOp(op);
				
			}else if (childNode.getNodeName().equals("expressionSource")) {
				//Source
				System.out.println("             " + childNode.getNodeName() + ":"
						+ childNode.getChildNodes().item(1).getTextContent());
				Function functionS=new Function(childNode.getChildNodes().item(1).getTextContent());
				relation.setFunctionSource(functionS);				
			}		
		}

		return relation;
	}
	
	private OpAndRelation parserOpAndRelation(Node node){
		Node nodeOpAndRelation=node;
		OpAndRelation opAndRelation = new OpAndRelation();
		//获得Relation下的所有子节点
		NodeList childNodes_OpAndRelation = nodeOpAndRelation.getChildNodes();
		//遍历relation
		for (int i = 0; i < childNodes_OpAndRelation.getLength(); i++) {
			Node childNode= childNodes_OpAndRelation.item(i);
			
			if (childNode.getNodeName().equals("reOperator")) {
				//reOperator
				System.out.println("        " + childNode.getNodeName() + ":"
						+ childNode.getTextContent());
				Operator reOp=new Operator(childNode.getTextContent());
				opAndRelation.setOperator(reOp);				
			}else if (childNode.getNodeName().equals("relation")) {
				//relation
				System.out.println("        " + childNode.getNodeName() + ":");
				Relation relation=parserRelation(childNode);
				opAndRelation.setRelation(relation);
			}
			
		}
		
		return opAndRelation;
	}
	
	
	
	/*
	 * 打印 DOM 节点 输出格式为： nodeType(nodeName,nodeValue)
	 * "ATTRIBUTE"(attributeName=attributeValue) ...
	 * childNodeType[childNodeName,childNodeValue] ...
	 */
	public static void printNode(Node node, int count) {
		if (node != null) {
			String tmp = "";
			for (int i = 0; i < count; i++)
				tmp += "  ";
			// 获取node节点的节点类型，赋值给nodeType变量
			int nodeType = node.getNodeType();
			switch (nodeType) {
			case Node.ATTRIBUTE_NODE:
				tmp += "ATTRIBUTE";
				break;
			case Node.CDATA_SECTION_NODE:
				tmp += "CDATA_SECTION";
				break;
			case Node.COMMENT_NODE:
				tmp += "COMMENT";
				break;
			case Node.DOCUMENT_FRAGMENT_NODE:
				tmp += "DOCUMENT_FRAGMENT";
				break;
			case Node.DOCUMENT_NODE:
				tmp += "DOCUMENT";
				break;
			case Node.DOCUMENT_TYPE_NODE:
				tmp += "DOCUMENT_TYPE";
				break;
			case Node.ELEMENT_NODE:
				tmp += "ELEMENT";
				break;
			case Node.ENTITY_NODE:
				tmp += "ENTITY";
				break;
			case Node.ENTITY_REFERENCE_NODE:
				tmp += "ENTITY_REFERENCE";
				break;
			case Node.NOTATION_NODE:
				tmp += "NOTATION";
				break;
			case Node.PROCESSING_INSTRUCTION_NODE:
				tmp += "PROCESSING_INSTRUCTION";
				break;
			case Node.TEXT_NODE:
				tmp += "TEXT";
				break;
			default:
				return;// invalid node type.
			}

			System.out.println(tmp + " (" + node.getNodeName() + ","
					+ node.getNodeValue() + ")");
			/*
			 * node.getAttributes()方法返回 包含node节点的属性的 NamedNodeMap（如果它是 Element）
			 */
			NamedNodeMap attrs = node.getAttributes();
			if (attrs != null)
				for (int i = 0; i < attrs.getLength(); i++) {
					printNode(attrs.item(i), count + 1);
				}
			/*
			 * node.getChildNodes()方法返回 包含node节点的所有子节点的 NodeList。
			 */
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				printNode(childNodes.item(i), count + 1);
			}
		}
	}

}
