package cn.edu.ustb.mt4ws.mr;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.edu.ustb.mt4ws.mr.model.Function;
import cn.edu.ustb.mt4ws.mr.model.MetamorphicRelation;
import cn.edu.ustb.mt4ws.mr.model.Operator;
import cn.edu.ustb.mt4ws.mr.model.Relation;
import cn.edu.ustb.mt4ws.mr.model.RelationOfInput;
import cn.edu.ustb.mt4ws.mr.model.RelationOfOutput;

public class MrdlParser {

	/**
	 * 解析mrdl文件
	 * @param fileLoc mrdl文件的位置
	 * @return 蜕变关系的Java模型
	 */
	public List<MetamorphicRelation> parse(String fileLoc) {
		DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dombuilder = domfac.newDocumentBuilder();
			InputStream is = new FileInputStream(fileLoc);
			Document doc = dombuilder.parse(is);
			Element root = doc.getDocumentElement();// mrSet
			NodeList mrs = root.getChildNodes();
			if (mrs != null) {
				List<MetamorphicRelation> mrList = new Vector<MetamorphicRelation>();
				for (int i = 0; i < mrs.getLength(); i++) {// 处理mrSet
					if (i % 2 == 0)
						continue;
					Node mrNode = mrs.item(i);
					Node relationOfInputNode = mrNode.getChildNodes().item(1);// XML
					// model

					RelationOfInput relationOfInput = new RelationOfInput();// Java
					// model
					List<Relation> relationSetOfInput = new Vector<Relation>();
					NodeList relationsIn = relationOfInputNode.getChildNodes();
					for (int j = 0; j < relationsIn.getLength(); j++) {// 处理relationOfInput
						if (j % 2 == 0)
							continue;
						Node relation = relationsIn.item(j);
						NodeList relationPart = relation.getChildNodes();
						Node functionFollowUpNode = relationPart.item(1);
						Node functionSourceNode = relationPart.item(3);
						Node operatorNode = relationPart.item(5);
						String funcFollowUpDesc = functionFollowUpNode
								.getChildNodes().item(1).getFirstChild()
								.getNodeValue();
						String funcSourceDesc = functionSourceNode
								.getChildNodes().item(1).getFirstChild()
								.getNodeValue();
						String opDesc = operatorNode.getFirstChild().getNodeValue();
						Function followUp = new Function(funcFollowUpDesc);
						Function source = new Function(funcSourceDesc);
						Operator op = new Operator(opDesc);
						if (op.getType() < 0) {// TODO 不能识别op
						}
						Relation r = new Relation(followUp, op, source);
						relationSetOfInput.add(r);
					}
					relationOfInput.setRelationOfInput(relationSetOfInput);

					Node relationOfOutputNode = mrNode.getChildNodes().item(3);// XML
					// model
					RelationOfOutput relationOfOutput = new RelationOfOutput();// Java
					// model
					List<Relation> relationSetOfOutput = new Vector<Relation>();
					NodeList relationsOut = relationOfOutputNode
							.getChildNodes();
					for (int j = 0; j < relationsOut.getLength(); j++) {// 处理relationOfOutput
						if (j % 2 == 0)
							continue;
						Node relation = relationsOut.item(j);
						NodeList relationPart = relation.getChildNodes();
						Node functionFollowUpNode = relationPart.item(1);
						Node functionSourceNode = relationPart.item(3);
						Node operatorNode = relationPart.item(5);
						String funcFollowUpDesc = functionFollowUpNode
								.getChildNodes().item(1).getFirstChild()
								.getNodeValue();
						String funcSourceDesc = functionSourceNode
								.getChildNodes().item(1).getFirstChild()
								.getNodeValue();
						String opDesc = operatorNode.getFirstChild().getNodeValue();
						Function followUp = new Function(funcFollowUpDesc);
						Function source = new Function(funcSourceDesc);
						Operator op = new Operator(opDesc);
						if (op.getType() < 0) {// TODO 不能识别op
						}
						Relation r = new Relation(followUp, op, source);
						relationSetOfOutput.add(r);
					}
					relationOfOutput.setRelationOfOutput(relationSetOfOutput);
					MetamorphicRelation mr = new MetamorphicRelation(
							relationOfInput, relationOfOutput);
					mrList.add(mr);
				}
				return mrList;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
