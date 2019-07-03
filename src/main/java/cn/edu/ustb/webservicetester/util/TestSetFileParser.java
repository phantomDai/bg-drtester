package cn.edu.ustb.webservicetester.util;

import java.io.File;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import cn.edu.ustb.webservicetester.model.*;

public class TestSetFileParser {
	
	private java.util.List<ParameterInfo> paraInfoList =  null;

	public java.util.List<ParameterInfo> getParaInfoList() {
		return paraInfoList;
	}

	public void setParaInfoList(java.util.List<ParameterInfo> paraInfoList) {
		this.paraInfoList = paraInfoList;
	}
	
	public TestSet parseFile(String fileName) {
		TestSet testSet = new TestSet();
		File f = new File(fileName);
		if (f.exists()) {
			SAXReader saxReader = new SAXReader();
			Document document = null;
			Element rootElement = null;
			TestCase tc = null;
			try {
				document = saxReader.read(f);
				rootElement = document.getRootElement();
				System.out.println(((Element)rootElement.elements().get(0)).getName());
				java.util.List<Element> testCaseList = rootElement.selectNodes("testcase");
				System.out.println(testCaseList.size());
				java.util.Iterator<Element> testCaseIter = testCaseList.iterator();
				while (testCaseIter.hasNext()) {
					Element testCaseElement = testCaseIter.next();
					tc = parseTestCaseElement(testCaseElement);
					testSet.add(tc);
				}
			} catch (Exception e) {
				e.printStackTrace();
				testSet = null;
			} finally {
				System.out.println(testSet == null);
			}
		}
		return testSet;
	}

	private TestCase parseTestCaseElement(Element testCaseElement) {
		// TODO Auto-generated method stub
		TestCase tc = new TestCase();
		String id = getAttribute(testCaseElement, "id");
		tc.setId(Integer.parseInt(id));
		java.util.Iterator<ParameterInfo> paraInfoIter = paraInfoList.iterator();
		while (paraInfoIter.hasNext()) {
			ParameterInfo pi = paraInfoIter.next();
			String value = null;
			if (testCaseElement.selectNodes(pi.getName()).size() > 0) {
				value = ((Element)testCaseElement.selectNodes(pi.getName()).iterator().next()).getStringValue();
			} else {
				value = "";
			}
			tc.setValueOfParameter(pi, value);
		}
		if (testCaseElement.selectNodes("expectedresult").size() > 0) {
			tc.setExpectedResult(((Element)testCaseElement.selectNodes("expectedresult").iterator().next()).getStringValue());
		} else {
			tc.setExpectedResult("");
		}
		return tc;
	}

	private String getAttribute(Element e, String attrName) {
		String attrStr = null;
		java.util.List attrList = e.selectNodes("@" + attrName);
		java.util.Iterator attrIter = attrList.iterator();
		if (attrIter.hasNext()) {
			Attribute attr = (Attribute)attrIter.next();
			attrStr = attr.getValue();
		}
		return attrStr;
	}
	
	public static void main(String[] args) {
		WsdlParser wp = new WsdlParser();
		java.util.List<OperationInfo> oiList;
		java.util.List<ParameterInfo> piList;
		oiList = wp.parseWsdl("http://127.0.0.1:8080/axis2/services/AddService?wsdl");
		OperationInfo addInfo = null;
		for (int i = 0; i < oiList.size() && addInfo == null; ++i) {
			if ("add".equals(oiList.get(i).getOpName())) {
				addInfo = oiList.get(i);
			}
		}
		piList = addInfo.getParaList();
		if (piList == null) {
			return;
		}
		String testFileName1 = "testset4addservice$add.xml";
		TestSetFileParser tsfp = new TestSetFileParser();
		tsfp.setParaInfoList(piList);
		TestSet ts = tsfp.parseFile(testFileName1);
		System.out.println(ts.numOfTestCases());
		TestCase tc = ts.get(0);
		System.out.println(tc);
	}

}
