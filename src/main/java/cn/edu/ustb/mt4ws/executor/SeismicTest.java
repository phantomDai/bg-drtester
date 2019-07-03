package cn.edu.ustb.mt4ws.executor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.xmlbeans.XmlException;

import com.eviware.soapui.impl.WsdlInterfaceFactory;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.support.SoapUIException;

import cn.edu.ustb.mt4ws.configuration.Configuration;
import cn.edu.ustb.mt4ws.configuration.Project;
import cn.edu.ustb.mt4ws.configuration.Workspace;
import cn.edu.ustb.mt4ws.evaluator.MRStatistic;
import cn.edu.ustb.mt4ws.evaluator.OperationStatistic;
import cn.edu.ustb.mt4ws.evaluator.Statistic;
import cn.edu.ustb.mt4ws.evaluator.Verifier;
import cn.edu.ustb.mt4ws.mr.MrdlParser;
import cn.edu.ustb.mt4ws.mr.model.MetamorphicRelation;
import cn.edu.ustb.mt4ws.soap.SoapInvoker;
import cn.edu.ustb.mt4ws.tcg.Modifier;
import cn.edu.ustb.mt4ws.tcg.TestSuite;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.tcg.XmlTestCase;
import cn.edu.ustb.mt4ws.tcg.XmlTestCaseGenerator;
import cn.edu.ustb.mt4ws.tcg.XmlVariable;
import cn.edu.ustb.mt4ws.wsdl.parser.AbstractWsdlReader;
import cn.edu.ustb.mt4ws.wsdl.parser.WsdlReader11;

/**
 * 地震查询服务测试
 * 
 * @author qing.wen
 * 
 */
public class SeismicTest implements Callable<Statistic> {

	private static ServiceInvoker invoker = new SoapInvoker();

	@Override
	public Statistic call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		XmlTestCaseGenerator tcGen = new XmlTestCaseGenerator();
		// String wsdl = "http://localhost:8080/axis2/services/ATMService?wsdl";
		String wsdl = "http://localhost:8080/axis2/services/SeismicAdb?wsdl";
		// String projectName = "ATMService";
		String projectName = "SeismicAdb";
		String userName = "admin";
		String servletPath = "D:/work/Tools/apache-tomcat-6.0.35/webapps/MT4WSJSP/";
		Modifier mod = new Modifier();

		// 设置Project
		Project project = new Project();
		Workspace workspace = new Workspace();
		project.setProjectName(projectName); // 设置工程的名称
		project.setWSDLUri(wsdl);// 设置wsdl路径
		workspace.setUserName(userName);
		workspace.setPath(servletPath);
		project.setWorkspace(workspace);// 设置工程的工作空间

		// 打印Project相关内容
		System.out.println("Project:");
		System.out.println("   ProjectName:" + project.getProjectName());
		System.out.println("   WsdlURL:" + project.getWsdlUrl());
		System.out.println("   Workspace_userName:"
				+ project.getWorkspace().getUserName());
		System.out.println("   Workspace_path:"
				+ project.getWorkspace().getPath());

		TestSuite ts = new TestSuite();// 测试用例集,用以保存两种形式的testcaseID
		List<Integer> testcaseList = new ArrayList<Integer>();// testcaseID,保存在List中
		testcaseList.add(1);
		ts.setTestcaseList(testcaseList);

		// 打印测试用例ID集合
		System.out.println("IDs of testCase:" + ts.getTestcaseList().size());
		for (int i = 0; i < ts.getTestcaseList().size(); i++) {
			System.out.println("testCase" + i + ":"
					+ ts.getTestcaseList().get(i));
		}

		// 设置Map，每个MR对应一个测试用例集
		Map<String, TestSuite> tsMap = new LinkedHashMap<String, TestSuite>();
		// tsMap.put("ATMService_transfer_1", ts);
		tsMap.put("SeismicAdb_matchQuakes_1", ts);
		project.setTestSuites(tsMap);// project 初试化完毕

		Configuration config = new Configuration();
		// config.setOperationName("transfer");
		config.setOperationName("matchQuakes");

		// 蜕变关系MR的ID集合
		List<Integer> mrIDSuite = new ArrayList<Integer>();
		mrIDSuite.add(1);

		// 每条蜕变关系MR的测试用例ID集合
		List<Integer> mrTestSuite = new ArrayList<Integer>();
		mrTestSuite.add(1);

		// 将所有的蜕变关系MR的测试用例ID集合存放在mrTestList中
		List<List<Integer>> mrTestList = new ArrayList<List<Integer>>();
		mrTestList.add(mrTestSuite);

		// 将所有的测试用例存储到配置中
		config.setMrIds(mrIDSuite);
		config.setTestCases(mrTestList);
		// 将每个配置存放到configList集合中
		List<Configuration> configList = new ArrayList<Configuration>();
		configList.add(config);

		// 设置WSDL格式，wsdlOperationFormat
		AbstractWsdlReader reader11 = new WsdlReader11();
		Map<String, WsdlOperationFormat> opFormats = reader11.parseWSDL(wsdl);

		// wsdl相关操作
		WsdlInterface iface = WsdlInterfaceFactory.importWsdl(
				new WsdlProject(), wsdl, true)[0];
		System.out.println("Operations:");
		for (int i = 0; i < iface.getOperationCount(); i++) {
			System.out.println("    Operation" + (i + 1) + ":"
					+ iface.getOperationList().get(i).getName());
		}
		Iterator<Configuration> iterConfig = configList.iterator();

		// 读取configList
		while (iterConfig.hasNext()) {
			Configuration configTemp = iterConfig.next();
			String operationName = configTemp.getOperationName();
			System.out.println("Testing Operation:" + operationName);

			// OperationStatistic
			OperationStatistic opStatistic = new OperationStatistic();
			opStatistic.setOperationName(operationName);
			List<MRStatistic> mrStatisticList = new ArrayList<MRStatistic>();

			for (int mrID : config.getMrIds()) {
				System.out.println("Testing MR:" + "MR" + mrID);

				// MRStatistic
				MRStatistic mrStatistic = new MRStatistic();
				String mrName = projectName + "_" + operationName + "_"
						+ Integer.toString(mrID);// mrName = projectName +
				// operationName + mrId)
				mrStatistic.setMrName(mrName);
				// 发现了错误的test case列表
				List<String> validTestCaseList = new ArrayList<String>();

				String mrdlLoc = project.getWorkspace().getPath()
						+ project.getProjectName();// MRDL文件存放在服务器中的位置
				System.out.println("Parsing MRDL file on:" + mrdlLoc);
				List<MetamorphicRelation> mrList = new MrdlParser()
						.parse(mrdlLoc + "/mrdl_" + operationName + ".xml");
				// 获得相应mrID的蜕变关系
				MetamorphicRelation mr = mrList.get(mrID - 1);
				Map<String, TestSuite> mrToTestSuite = project
						.getTestSuiteMap();// Map:mrName->TestSuite
				System.out.println("mrName:" + mrName);
				TestSuite suite = mrToTestSuite.get(mrName);

				// 从数据库中读取testcase
				System.out
						.println("**********Checking Operation Input/Output Format***********");
				// 检查输入输出格式
				WsdlOperationFormat wsdlOpFormat = opFormats.get(operationName);
				// 从config中遍历每个蜕变关系的测试用例ID集合
				for (List<Integer> testcaseCurrentMR : config.getTestCases()) {

					Iterator<Integer> iterTc = testcaseCurrentMR.iterator();
					// 遍历mr中的测试用例集合
					while (iterTc.hasNext()) {
						int testcaseId = iterTc.next();
						System.out.println("Reading Test Case" + testcaseId);
						// 判断待测试的测试用例是否在该mr的测试用例集中
						if (suite.getTestcaseList().contains(testcaseId) == false) {
							System.out.println("*ERROR*:  testcase"
									+ testcaseId + "is not in testSuite for MR"
									+ mrID);
							// 跳转到while循环中
							continue;
						}
						// 若测试用例在该mr的测试用例集中，则执行以下代码
						// 从数据库中提取测试用例
						System.out.println("Extract Test Case " + testcaseId
								+ " From Database...");
						XmlTestCase testcaseSource = tcGen.extractFromDB(
								wsdlOpFormat, testcaseId);
						System.out.println("Source Test Case Detail: "
								+ testcaseSource.printTC());

						/*
						 * 产生FollowUpSource
						 */

						Map<XmlVariable, String> inputFollow = new LinkedHashMap<XmlVariable, String>();
						System.out.println("Generating Follow-Up Test Case...");
						if (operationName.equals("rmbToGbbob")) {
							inputFollow = mod.modifyStr(
									mr.getRelationOfInput(), testcaseSource
											.getInput());
						} else if (operationName.equals("transfer")) {
							inputFollow = mod.modify(mr.getRelationOfInput(),
									testcaseSource.getInput());
						} else if (operationName.equals("matchQuakes")) {
							inputFollow = mod.modifyQuakes(mr
									.getRelationOfInput(), testcaseSource
									.getInput());
						}
						XmlTestCase testcaseFollow = new XmlTestCase(
								operationName, inputFollow, null);
						
						

						// Soap request For Source
						System.out
								.println("Creating SOAP Request for SOURCE Test Case...");
						String soapMessageSource = tcGen.fillEmptySoapMessage(
								wsdlOpFormat, testcaseSource, true);

						// Soap request For followUp
						System.out
								.println("Creating SOAP Request for FollowUp Test Case...");
						String soapMessgaeFollow = tcGen.fillEmptySoapMessage(
								wsdlOpFormat, testcaseFollow, false);
						

						// 代理模式 TODO
						WsdlOperation operation = (WsdlOperation) iface
								.getOperationByName(operationName);

						WsdlRequest requestSource = operation
								.addNewRequest("requestSource");
						requestSource.setRequestContent(soapMessageSource);

						WsdlRequest requestFollow = operation
								.addNewRequest("requestFollow");
						requestFollow.setRequestContent(soapMessgaeFollow);
						
						// 调用Web Services，获得Response TODO 超时与异常的处理

						System.out
								.println("Invoking Web Service with SOURCE Test Case...");
						Response responseSource = (Response) invoker
								.invoke(requestSource);
						
						System.out
								.println("Invoking Web Service with FOLLOW-UP Test Case...");
						Response responseFollow = (Response) invoker
								.invoke(requestFollow);

						System.out.println("Parsing Response of SOURCE Test Case...");
						List<String> outputSource = tcGen.parseSoapResponseQuake(responseSource);
													
						System.out.println("Parsing Response of FOLLOW-UP Test Case...");
						List<String>  outputFollowUp = tcGen.parseSoapResponseQuake(responseFollow);
						
						// 验证MR的Rf
						Verifier verifier = new Verifier();

						System.out.println("Verifying Two Output...");
						
						boolean satisfied = verifier.verify(outputSource,
								outputFollowUp, mr.getRelationOfOutput());

						if (satisfied == false) {
							System.out.println("Test Case " + testcaseId
									+ "FAILED");
							validTestCaseList.add("TC" + testcaseId);
						} else {
							System.out.println("Test Case " + testcaseId
									+ "PASSED");
						}
												
						System.out.println("测试中…………");

					}

				}

			}
		}

	}

}
