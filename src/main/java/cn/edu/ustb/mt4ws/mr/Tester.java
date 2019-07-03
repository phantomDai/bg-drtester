package cn.edu.ustb.mt4ws.mr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import cn.edu.ustb.mt4ws.evaluator.Verifier;
import cn.edu.ustb.mt4ws.mr.model.Function;
import cn.edu.ustb.mt4ws.mr.model.MetamorphicRelation;
import cn.edu.ustb.mt4ws.mr.model.Operator;
import cn.edu.ustb.mt4ws.mr.model.Relation;
import cn.edu.ustb.mt4ws.mr.model.RelationOfInput;
import cn.edu.ustb.mt4ws.mr.model.RelationOfOutput;
import cn.edu.ustb.mt4ws.soap.SoapUtils;
import cn.edu.ustb.mt4ws.tcg.Modifier;
import cn.edu.ustb.mt4ws.tcg.Type;
import cn.edu.ustb.mt4ws.tcg.Variable;

public class Tester {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

		testMRDLParser();

	}

	public static void testModifierAndVerifier() throws Exception {
		Modifier mod = new Modifier();
		RelationOfInput relationInput = new RelationOfInput();
		List<Relation> relationRSet = new Vector<Relation>();
		Function fF = new Function("a_");
		Operator op = new Operator(Operator.GT);
		Function fS = new Function("a");
		Relation r = new Relation(fF, op, fS);
		relationRSet.add(r);
		relationInput.setRelationOfInput(relationRSet);
		Map<Variable, String> inputSource = new LinkedHashMap<Variable, String>();
		Variable a = new Variable("a", new Type(Type.INT));
		Variable b = new Variable("b", new Type(Type.INT));
		inputSource.put(a, "1");
		inputSource.put(b, "2");
		Map<Variable, String> inputFollow = mod.modify(relationInput,
				inputSource);
		Set<Map.Entry<Variable, String>> entrySet = inputFollow.entrySet();
		Iterator<Map.Entry<Variable, String>> iter = entrySet.iterator();
		Map.Entry<Variable, String> myEntry = null;
		while (iter.hasNext()) {
			myEntry = iter.next();
			System.out.println(myEntry.getKey().getName() + "="
					+ myEntry.getValue());
		}

		Verifier ver = new Verifier();
		RelationOfOutput relationOutput = new RelationOfOutput();
		List<Relation> relationRFSet = new Vector<Relation>();
		Function fF2 = new Function("a_");
		Operator op2 = new Operator(Operator.GT);
		Function fS2 = new Function("a");
		Relation r2 = new Relation(fF2, op2, fS2);
		relationRFSet.add(r2);
		relationOutput.setRelationOfOutput(relationRFSet);
		System.out
				.println(ver.verify(inputSource, inputFollow, relationOutput));
	}

	public static void testMRDLParser() throws ParserConfigurationException,
			SAXException, IOException {
		String mrdlLoc = "D:/work/Tools/apache-tomcat-6.0.35/webapps/MT4WSJSP/RmbToGbbobService/mrdl_rmbToGbbob.xml";
		List<MetamorphicRelation> mrList = new ArrayList<MetamorphicRelation>();
		MrdlParser parser = new MrdlParser();
		mrList = parser.parse(mrdlLoc);
		Iterator<MetamorphicRelation> iter = mrList.iterator();
		while (iter.hasNext()) {
			MetamorphicRelation mr = iter.next();
			System.out.println(mr.toString());
		}
		// List<Relation> rSet = mr.getRelationOfInput().getRelationOfInput();
		// Iterator<Relation> iter2 = rSet.iterator();
		// Relation r = iter2.next();
		// String out = r.getOp().getType()+"";
		// System.out.println(out);
		// DocumentBuilderFactory domFactory =
		// DocumentBuilderFactory.newInstance();
		// domFactory.setNamespaceAware(true); // never forget this!
		// DocumentBuilder builder = domFactory.newDocumentBuilder();
		// Document doc = builder.parse("C:/temp.xml");
		// Node node = SoapUtils.selectSingleNode("//balanceDeltaFrom",
		// doc.getDocumentElement());
		// System.out.println(node.getFirstChild().getNodeValue());
	}
}
