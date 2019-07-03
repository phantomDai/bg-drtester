package cn.edu.ustb.mt4ws.servlet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.edu.ustb.mt4ws.configuration.Configuration;
import cn.edu.ustb.mt4ws.configuration.Project;
import cn.edu.ustb.mt4ws.evaluator.Statistic;
import cn.edu.ustb.mt4ws.executor.Executor;
import cn.edu.ustb.mt4ws.mr.jsp.TransferInput;
import cn.edu.ustb.mt4ws.javabean.MetamorphicRelation;
import cn.edu.ustb.mt4ws.tcg.SqlUtils;
import cn.edu.ustb.mt4ws.tcg.TestSuite;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;

public class StartTest extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		session.setAttribute("startTime", dateString);
		Callable<Statistic> exec = null;
		Future<Statistic> f = null;
		Statistic sta = null;
		
		Project project = (Project) session.getAttribute("project");
		String operationName = (String) session.getAttribute("operationName");
		
		// 判断是何种操作
		if (operationName.equals("transfer")) {
			// insertTransferTC(request, response);
		} else if (operationName.equals("rmbToGbbob")) {
			// TODO
		} else if (operationName.equals("matchQuakes")) {
			// TODO
		} else {
			System.out.println("没有此类操作！！！");
		}
		
		
		Map<String, WsdlOperationFormat> opFormat = (Map<String, WsdlOperationFormat>) session
				.getAttribute("operation_map");
		List<Configuration> configList = (List<Configuration>) session
				.getAttribute("config");
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");
		
		
		// default configuration
		if (configList == null) {
			try {
				SqlUtils sqlUtils = SqlUtils.getInstance();
				Map<String, TestSuite> tsMap = new LinkedHashMap<String, TestSuite>();
				List<Integer> mrsToBeTested = new ArrayList<Integer>();
				List<List<Integer>> mrsTs = new ArrayList<List<Integer>>();
				Configuration config = new Configuration();
				config.setOperationName(operationName);
				for (int mrID = 1; mrID <= mrList.size(); mrID++) {
					mrsToBeTested.add(mrID);
					TestSuite ts = new TestSuite();
					List<Integer> testcaseList = sqlUtils.searchTCForMR(
							operationName, mrID);
					ts.testcaseList = testcaseList;
					tsMap.put(project.getProjectName() + "_" + operationName
							+ "_" + "mr" + mrID, ts);
					mrsTs.add(testcaseList);
				}
				project.setTestSuites(tsMap);// project初始化完毕
				config.setMrIds(mrsToBeTested);
				config.setTestCases(mrsTs);
				configList = new ArrayList<Configuration>();
				configList.add(config);
				session.setAttribute("configList", configList);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			ExecutorService pool = Executors.newFixedThreadPool(1);
			exec = new Executor();
			((Executor) exec).setProject(project);
			((Executor) exec).setOpFormats(opFormat);
			((Executor) exec).setConfigList(configList);
			((Executor) exec).setMrList(mrList);
			f = pool.submit(exec);
			sta = f.get(6000, TimeUnit.SECONDS);// 100 minutes;
			while (!f.isDone()) {
				;
			}
			File fileLog = new File(project.getWorkspace().getPath() + "mt.log");
			FileWriter writer = new FileWriter(fileLog, true);
			writer.write(dateString + ":\n");
			List<String> testMsg = ((Executor) exec).testMessage;
			Iterator<String> iter = testMsg.iterator();
			while (iter.hasNext()) {
				writer.write(iter.next());
			}
			writer.write("\r\n\r\n");//在日志文件末尾换行
			writer.close();
			session.setAttribute("statistic", sta);
			// 将TransferInput中的count_testcase初始化为0
			TransferInput.count_testcase = 0;

			RequestDispatcher dispatcher = request
					.getRequestDispatcher("5.report.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

}
