package cn.edu.ustb.mt4ws.executor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import cn.edu.ustb.mt4ws.configuration.Configuration;
import cn.edu.ustb.mt4ws.configuration.Project;
import cn.edu.ustb.mt4ws.configuration.Workspace;
import cn.edu.ustb.mt4ws.evaluator.Statistic;
import cn.edu.ustb.mt4ws.tcg.TestSuite;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.wsdl.parser.AbstractWsdlReader;
import cn.edu.ustb.mt4ws.wsdl.parser.WsdlReader11;

public class TestMain {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Project project = new Project();
		Workspace workspace = new Workspace();
		project.setProjectName("ATMService");
		project
				.setWSDLUri("http://localhost:8080/axis2/services/ATMService?wsdl");
		workspace.setUserName("admin");
		project.setWorkspace(workspace);
		TestSuite ts1 = new TestSuite();
		TestSuite ts2 = new TestSuite();
		List<Integer> testcaseList1 = new ArrayList<Integer>();
		testcaseList1.add(1);
		List<Integer> testcaseList2 = new ArrayList<Integer>();
		testcaseList2.add(2);
		ts1.testcaseList = testcaseList1;
		ts2.testcaseList = testcaseList2;
		Map<String, TestSuite> tsMap = new LinkedHashMap<String, TestSuite>();
		tsMap.put("ATMService_transfer_mr1", ts1);
		tsMap.put("ATMService_transfer_mr2", ts2);
		project.setTestSuites(tsMap);// project初始化完毕

		Configuration config = new Configuration();
		config.setOperationName("transfer");
		List<Integer> a = new ArrayList<Integer>();
		a.add(1);
		a.add(2);

		List<Integer> mr1TestSuite = new ArrayList<Integer>();
		List<Integer> mr2TestSuite = new ArrayList<Integer>();
		mr1TestSuite.add(1);
		mr2TestSuite.add(2);
		List<List<Integer>> b = new ArrayList<List<Integer>>();
		b.add(mr1TestSuite);
		b.add(mr2TestSuite);
		config.setMrIds(a);
		config.setTestCases(b);
		List<Configuration> configList = new ArrayList<Configuration>();
		configList.add(config);

		AbstractWsdlReader reader11 = new WsdlReader11();
		String wsdl = "http://localhost:8080/axis2/services/ATMService?wsdl";
		Map<String, WsdlOperationFormat> opFormat = reader11.parseWSDL(wsdl);

		try {
			ExecutorService pool = Executors.newFixedThreadPool(1);
			Callable<Statistic> exec = new Executor();
			((Executor)exec).setProject(project);
			((Executor)exec).setOpFormats(opFormat);
			((Executor)exec).setConfigList(configList);
			Future<Statistic> f = pool.submit(exec);
			Statistic sta = f.get(600, TimeUnit.SECONDS);//10 minutes;
			sta.print();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

}
