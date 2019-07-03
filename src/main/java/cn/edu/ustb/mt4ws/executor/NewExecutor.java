package cn.edu.ustb.mt4ws.executor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.eviware.soapui.impl.WsdlInterfaceFactory;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.model.iface.Response;

import cn.edu.ustb.mt4ws.configuration.Configuration;
import cn.edu.ustb.mt4ws.configuration.Project;
import cn.edu.ustb.mt4ws.evaluator.MRStatistic;
import cn.edu.ustb.mt4ws.evaluator.OperationStatistic;
import cn.edu.ustb.mt4ws.evaluator.Statistic;
import cn.edu.ustb.mt4ws.evaluator.Verifier;
import cn.edu.ustb.mt4ws.javabean.MetamorphicRelation;
import cn.edu.ustb.mt4ws.soap.SoapInvoker;
import cn.edu.ustb.mt4ws.tcg.Modifier;
import cn.edu.ustb.mt4ws.tcg.TestSuite;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.tcg.XmlTestCase;
import cn.edu.ustb.mt4ws.tcg.XmlTestCaseGenerator;
import cn.edu.ustb.mt4ws.tcg.XmlVariable;

public class NewExecutor implements Callable<Statistic>{

	private ServiceInvoker invoker = new SoapInvoker();
	public List<String> testMessage = new ArrayList<String>();
	private Project project;
	private Map<String, WsdlOperationFormat> opFormats;
	private List<Configuration> configList;
	private List<MetamorphicRelation> mrList;
	
	@Override
	public Statistic call() throws Exception {
		// TODO Auto-generated method stub
		return runTestCase();
	}
	
	/**
	 * 串行执行测试用例
	 * 
	 * @param project
	 * @param opFormats
	 * @param configList
	 * @return
	 * @throws Exception
	 */
	public Statistic runTestCase() throws Exception{
		Statistic statistic = new Statistic();
		statistic.setProject(project);
		List<OperationStatistic> opStatisticList = new ArrayList<OperationStatistic>();
		Modifier mod = new Modifier();
		XmlTestCaseGenerator tcGen = new XmlTestCaseGenerator();
		String projectName = project.getProjectName();
		String wsdlUrl = project.getWsdlUrl();

		addTestingLog("Project Name:" + projectName);
		addTestingLog("Web Services WSDL URL:" + wsdlUrl);

		WsdlInterface iface = WsdlInterfaceFactory.importWsdl(
				new WsdlProject(), wsdlUrl, true)[0];
		Iterator<Configuration> iterConfig = configList.iterator();
		Configuration config = null;

		// 读取config列表
		try{
		while (iterConfig.hasNext()) {
			config = iterConfig.next();
			String operationName = config.getOperationName();
			addTestingLog("Testing Operation:" + wsdlUrl);

			// OperationStatistic

			OperationStatistic opStatistic = new OperationStatistic();
			opStatistic.setOperationName(operationName);
			List<MRStatistic> mrStatisticList = new ArrayList<MRStatistic>();
			// 遍历每个MR
			for (int mrId : config.getMrIds()) {

				addTestingLog("Testing Metamorphic Relation:MR" + mrId);

				// MRStatistic
				MRStatistic mrStatistic = new MRStatistic();
				String mrName = projectName + "_" + operationName + "_"
						+ Integer.toString(mrId);// mrName = projectName +
				// operationName + mrId)
				mrStatistic.setMrName(mrName);
				// 发现了错误的test case列表
				List<String> validTestCaseList = new ArrayList<String>();
				String mrdlLoc = project.getWorkspace().getPath()
						+ project.getProjectName();// MRDL文件存放在服务器中的位置

				addTestingLog("Parsing MRDL file on:" + mrdlLoc);
				
				//TODO 
				//原来为解析XML文件，获取其中的mrList，现在直接从session中取得mrList
				
				// 获得相应mrID的蜕变关系
				MetamorphicRelation mr = mrList.get(mrId - 1);
				Map<String, TestSuite> mrToTestSuite = project
						.getTestSuiteMap();// Map:mrName->TestSuite
				TestSuite suite = mrToTestSuite.get(projectName + "_"
						+ operationName + "_" + "mr" + Integer.toString(mrId));

				// Test Cases执行
				
				// 从数据库中读取testcase
				addTestingLog("Checking Operation Input/Output Format...");
				// 检查输入输出格式
				WsdlOperationFormat opFormat = opFormats.get(operationName);
				// 从config中遍历每个蜕变关系的测试用例ID集合
				for (List<Integer> testcaseCurrentMR : config.getTestCases()) {
					Iterator<Integer> iter = testcaseCurrentMR.iterator();
					// 遍历mr中的测试用例集合
					while (iter.hasNext()) {
						int testcaseId = iter.next();
						addTestingLog("Reading Test Case " + testcaseId);
						// 判断待测试的测试用例是否在该mr的测试用例集中
						if (suite.testcaseList.contains(testcaseId) == false) {
							printTestingException("*ERROR*:  testcase"
									+ testcaseId + "is not in testSuite for MR"
									+ mrId);
							//跳转到while循环中
							continue;
						}

						// 若测试用例在该mr的测试用例集中，则执行以下代码
						// 从数据库中提取测试用例
						addTestingLog("Extract Test Case " + testcaseId
								+ " From Database...");

						XmlTestCase testcaseSource = tcGen.extractFromDB(
								opFormat, testcaseId);

						// 为了测试ATM方便
						//qing.wen改动,以下为ATM实验方便
						
						addTestingLog("Source Test Case Detail: "
								+ testcaseSource.printTC());

						addTestingLog("Generating Follow-Up Test Case...");
						
						Map<XmlVariable, String> inputFollow=new LinkedHashMap<XmlVariable, String>();
						if (operationName.equals("transfer")) {
							tcGen.sqlUtils.getAccountManager().accountTtoDB(
									operationName, testcaseId);
							 inputFollow = mod.modifyTransfer(mr.getRelationOfInput(),testcaseSource.getInput());
						}else if (operationName.equals("rmbToGbbob")){
//							inputFollow = mod.modifyRMB(mr.getRelationOfInput(),
//									testcaseSource.getInput());
						}else if (operationName.equals("matchQuakes")) {
//							inputFollow = mod.modifyQuakes(mr
//									.getRelationOfInput(), testcaseSource
//									.getInput());
						}

						/*addTestingLog("MR Detail: " + mr.toString());
						addTestingLog("Test Case Detail: " + testcaseSource.printTC());*/

						
						
						XmlTestCase testcaseFollow = new XmlTestCase(
								operationName, inputFollow, null);
						
						addTestingLog("MR Detail: " + mr.toString());
						addTestingLog("Test Case Detail: " + testcaseFollow.printTC());
						
						addTestingLog("Creating SOAP Request for SOURCE Test Case...");

						String soapMessageSource = tcGen.fillEmptySoapMessage(
								opFormat, testcaseSource, true);

						addTestingLog("Creating SOAP Request for FOLLOW-UP Test Case...");

						String soapMessgaeFollow = tcGen.fillEmptySoapMessage(
								opFormat, testcaseFollow, false);

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

						addTestingLog("Invoking Web Service with SOURCE Test Case...");

						Response responseSource = (Response) invoker
								.invoke(requestSource);

						addTestingLog("Invoking Web Service with FOLLOW-UP Test Case...");

						Response responseFollow = (Response) invoker
								.invoke(requestFollow);

						addTestingLog("Parsing Response of SOURCE Test Case...");
						
						Map<XmlVariable, String> outputSource = new LinkedHashMap<XmlVariable, String>();
						Map<XmlVariable, String> outputFollowUp = new LinkedHashMap<XmlVariable, String>();
						List<String> outputSourceQuake = new ArrayList<String>();
						List<String> outputFollowUpQuake = new ArrayList<String>();
						

						if (operationName.equals("transfer")
								|| operationName.equals("rmbToGbbob")) {
							outputSource = tcGen.parseSoapResponse(opFormat,
									responseSource);
							addTestingLog("Parsing Response of FOLLOW-UP Test Case...");
							outputFollowUp = tcGen.parseSoapResponse(opFormat,
									responseFollow);
						}else if(operationName.equals("matchQuakes")){
							outputSourceQuake = tcGen.parseSoapResponseQuake(responseSource);
							addTestingLog("Parsing Response of FOLLOW-UP Test Case...");
							outputFollowUpQuake = tcGen.parseSoapResponseQuake(responseFollow);
						}
						
						
						// 验证MR的Rf
						Verifier verifier = new Verifier();

						boolean satisfied = false;//初试化为false
						addTestingLog("Verifying Two Output...");
						if (operationName.equals("transfer")) {
							//TODO
							satisfied = verifier.verify(outputSource,
									outputFollowUp, mr.getRelaitonOfOutput());
						} else if (operationName.equals("rmbToGbbob")) {
//							satisfied = verifier.verifyRMB(outputSource,
//									outputFollowUp, mr.getRelationOfOutput());
						}else if(operationName.equals("matchQuakes")){
//							satisfied = verifier.verify(outputSourceQuake,
//									outputFollowUpQuake, mr.getRelationOfOutput());
						}
						
						
						if (satisfied == false) {
							addTestingLog("Test Case " + testcaseId + "FAILED");
							validTestCaseList
									.add("TC" + testcaseId);
						} else {
							addTestingLog("Test Case " + testcaseId + "PASSED");
						}
						// 为了测试方便
						//qing.wen改动
						if (operation.equals("transfer")) {
							tcGen.sqlUtils.getAccountManager()
									.testRubbishDisposal(operationName,
											testcaseId);
						}
					}
					
					mrStatistic.setValidTestCaseList(validTestCaseList);
					mrStatisticList.add(mrStatistic);
					// 为了测试ATM方便

				}
			}
			opStatistic.setMrStatisticList(mrStatisticList);
			opStatisticList.add(opStatistic);
		}
		}catch(Exception e){
			e.getStackTrace();
		}
		statistic.setOpStatisticList(opStatisticList);
		
		//打印出相关信息
		Iterator<OperationStatistic> iterOpList = opStatisticList.iterator();
		while(iterOpList.hasNext()){
			OperationStatistic opStatic = iterOpList.next();
			System.out.println(opStatic.getOperationName()+"：  " );
			//有效的蜕变关系，即能查出错的蜕变关系
			Iterator<MRStatistic> mrIter = opStatic.getFaultMRList().iterator();
			System.out.println("number of fault MRs: "+ opStatic.getFaultMRList().size());
			while(mrIter.hasNext()){
				MRStatistic mrSta = mrIter.next();
				System.out.println(mrSta.getMrName() + ": " + mrSta.getValidTestCaseList().size());

			}
		}
		
		
		return statistic;
	}

	public void addTestingLog(String testingMessage) {
		this.testMessage.add(testingMessage+"\n");
		System.out.println(testingMessage);
	}

	public void printTestingException(String errotMessage) {
		System.out.println(errotMessage);
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Project getProject() {
		return project;
	}

	public void setOpFormats(Map<String, WsdlOperationFormat> opFormats) {
		this.opFormats = opFormats;
	}

	public Map<String, WsdlOperationFormat> getOpFormats() {
		return opFormats;
	}

	public void setConfigList(List<Configuration> configList) {
		this.configList = configList;
	}

	public List<Configuration> getConfigList() {
		return configList;
	}

	public void setMrList(List<MetamorphicRelation> mrList) {
		this.mrList = mrList;
	}

	public List<MetamorphicRelation> getMrList() {
		return mrList;
	}

}
