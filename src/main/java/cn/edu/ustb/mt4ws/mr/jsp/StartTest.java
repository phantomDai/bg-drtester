package cn.edu.ustb.mt4ws.mr.jsp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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
import cn.edu.ustb.mt4ws.mr.model.MetamorphicRelation;
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
		if (session == null) {
			// TODO
		}

		// insert test cases into database

		Project project = (Project) session.getAttribute("project");
		String operationName = (String) session.getAttribute("operationName");

		// 判断是何种操作
		if (operationName.equals("transfer")) {
			//insertTransferTC(request, response);
		} else if (operationName.equals("rmbToGbbob")) {
			insertRMBTC(request, response);
		} else if (operationName.equals("queryQuakes")) {
			insertQuakesTC(request, response);
		} else if (operationName.equals("matchQuakes")) {
			insertMQuakesTC(request, response);
		}else if (operationName.equals("getList")) {
			insertSetTC(request, response);
		} else {
			System.out.println("没有此类操作！！！");
		}

		Map<String, WsdlOperationFormat> opFormat = (Map<String, WsdlOperationFormat>) session
				.getAttribute("operation_map");
		List<Configuration> configList = (List<Configuration>) session
				.getAttribute("config");
		List<MetamorphicRelation> mrs = (List<MetamorphicRelation>) session
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
				for (int mrID = 1; mrID <= mrs.size(); mrID++) {
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
			writer.close();
			session.setAttribute("statistic", sta);
			//将TransferInput中的count_testcase初始化为0
			TransferInput.count_testcase=0;
						
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

	/**
	 * rmb转换操作录入数据库
	 * 
	 * @param request
	 * @param response
	 */
	private void insertRMBTC(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			// TODO
		}
		if (session.getAttribute("rmbInputSet") == null) {
			System.out.println("请输入测试用例……");
		} else {
			String user = "root";
			String password = "root";
			String url = "jdbc:mysql://localhost:3306/mydb";
			String driver = "org.gjt.mm.mysql.Driver";
			String tablename = "rmbtogbbob";
			String sqlstr;
			String sqlstr1;
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				RMBInputBeanSet rmbInputSet = (RMBInputBeanSet) session
						.getAttribute("rmbInputSet");
				int num_testcase = rmbInputSet.getRmbinputbeanset().size();

				Class.forName(driver);
				con = DriverManager.getConnection(url, user, password);
				stmt = con.createStatement();
				for (int i = 0; i < num_testcase; i++) {
					int mrID = rmbInputSet.getRmbinputbeanset().get(i)
							.getMrID();
					String rmb = rmbInputSet.getRmbinputbeanset().get(i)
							.getRmb();

					sqlstr1 = "INSERT INTO " + tablename + "(mrID,RMB)values('"
							+ mrID + "','" + rmb + "')";
					stmt.executeUpdate(sqlstr1);
				}
				sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}
			} catch (ClassNotFoundException e1) {
				System.out.println("数据库驱动不存在");
				e1.printStackTrace();
			} catch (SQLException e2) {
				System.out.println("数据库异常");
				e2.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					if (con != null)
						con.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}
			}
		}
	}

	/**
	 * 地震服务将数据输入数据库
	 * 
	 * @param request
	 * @param response
	 */
	private void insertQuakesTC(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			// TODO
		}
		if (session.getAttribute("quakeInputSet") == null) {
			System.out.println("请输入测试用例……");
		} else {
			String user = "root";
			String password = "root";
			String url = "jdbc:mysql://localhost:3306/mydb";
			String driver = "org.gjt.mm.mysql.Driver";
			String tablename = "queryquakes";
			String sqlstr;
			String sqlstr1;
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				QuakeInputBeanSet quakeInputSet = (QuakeInputBeanSet) session
						.getAttribute("quakeInputSet");
				int num_testcase = quakeInputSet.getQuakeinputbeanset().size();

				Class.forName(driver);
				con = DriverManager.getConnection(url, user, password);
				stmt = con.createStatement();
				for (int i = 0; i < num_testcase; i++) {
					int mrID = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMrID();
					String maxd = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMaxd();
					String mind = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMind();
					String maxlng = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMaxlng();
					String minlng = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMinlng();
					String maxlat = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMaxlat();
					String minlat = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMinlat();

					sqlstr1 = "INSERT INTO "
							+ tablename
							+ "(mrID,MAXD,MIND,MAXLNG,MINLNG,MAXLAT,MINLAT)values('"
							+ mrID + "','" + maxd + "','" + mind + "','"
							+ maxlng + "','" + minlng + "','" + maxlat + "','"
							+ minlat + "')";
					System.out.println("sql:  " + sqlstr1);
					stmt.executeUpdate(sqlstr1);
				}
				sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}
			} catch (ClassNotFoundException e1) {
				System.out.println("数据库驱动不存在");
				e1.printStackTrace();
			} catch (SQLException e2) {
				System.out.println("数据库异常");
				e2.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					if (con != null)
						con.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}
			}
		}
	}
	
	
	/**matchQuakes
	 * 地震服务将数据输入数据库
	 * 
	 * @param request
	 * @param response
	 */
	private void insertMQuakesTC(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			// TODO
		}
		if (session.getAttribute("quakeInputSet") == null) {
			System.out.println("请输入测试用例……");
		} else {
			String user = "root";
			String password = "root";
			String url = "jdbc:mysql://localhost:3306/mydb";
			String driver = "org.gjt.mm.mysql.Driver";
			String tablename = "matchquakes";
			String sqlstr;
			String sqlstr1;
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				QuakeInputBeanSet quakeInputSet = (QuakeInputBeanSet) session
						.getAttribute("quakeInputSet");
				int num_testcase = quakeInputSet.getQuakeinputbeanset().size();

				Class.forName(driver);
				con = DriverManager.getConnection(url, user, password);
				stmt = con.createStatement();
				for (int i = 0; i < num_testcase; i++) {
					int mrID = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMrID();
					String maxd = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMaxd();
					String mind = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMind();
					String maxlng = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMaxlng();
					String minlng = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMinlng();
					String maxlat = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMaxlat();
					String minlat = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMinlat();
					String maxmag = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMaxmag();
					String minmag = quakeInputSet.getQuakeinputbeanset().get(i)
							.getMinmag();
					String maxdepth = quakeInputSet.getQuakeinputbeanset().get(
							i).getMaxdepth();
					String mindepth = quakeInputSet.getQuakeinputbeanset().get(
							i).getMindepth();
					

					sqlstr1 = "INSERT INTO "
							+ tablename
							+ "(mrID,MAXD,MIND,MAXLNG,MINLNG,MAXLAT,MINLAT,MAXMAG,MINMAG,MAXDEPTH,MINDEPTH)values('"
							+ mrID + "','" + maxd + "','" + mind + "','"
							+ maxlng + "','" + minlng + "','" + maxlat + "','"
							+ minlat + "','" + maxmag + "','" + minmag + "','"
							+ maxdepth + "','" + mindepth + "')";
					System.out.println("sql:  " + sqlstr1);
					stmt.executeUpdate(sqlstr1);
				}
				sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}
			} catch (ClassNotFoundException e1) {
				System.out.println("数据库驱动不存在");
				e1.printStackTrace();
			} catch (SQLException e2) {
				System.out.println("数据库异常");
				e2.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					if (con != null)
						con.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}
			}
		}
	}


	/**
	 * 集合getList操作录入数据库
	 * 
	 * @param request
	 * @param response
	 */
	private void insertSetTC(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			// TODO
		}
		if (session.getAttribute("setinputset") == null) {
			System.out.println("请输入测试用例……");
		} else {
			String user = "root";
			String password = "root";
			String url = "jdbc:mysql://localhost:3306/mydb";
			String driver = "org.gjt.mm.mysql.Driver";
			String tablename = "getregion";
			String sqlstr;
			String sqlstr1;
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				SetInputBeanSet setinputset = (SetInputBeanSet) session
						.getAttribute("setinputset");
				int num_testcase = setinputset.getSetinputbeanset().size();

				Class.forName(driver);
				con = DriverManager.getConnection(url, user, password);
				stmt = con.createStatement();
				for (int i = 0; i < num_testcase; i++) {
					int mrID = setinputset.getSetinputbeanset().get(i)
							.getMrID();
					String min = setinputset.getSetinputbeanset().get(i)
							.getMin();
					String max = setinputset.getSetinputbeanset().get(i)
							.getMax();
					sqlstr1 = "INSERT INTO " + tablename
							+ "(mrID,min,max)values('" + mrID + "','" + min
							+ "','" + max + "')";
					stmt.executeUpdate(sqlstr1);
				}
				sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}
			} catch (ClassNotFoundException e1) {
				System.out.println("数据库驱动不存在");
				e1.printStackTrace();
			} catch (SQLException e2) {
				System.out.println("数据库异常");
				e2.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					if (con != null)
						con.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}
			}
		}
	}

	/**
	 * transfer操作录入数据库
	 * 
	 * @param request
	 * @param response
	 */

	private void insertTransferTC(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			// TODO
		}
		if (session.getAttribute("transferinputset") == null) {
			System.out.println("请输入测试用例……");
		} else {
			String user = "root";
			String password = "root";
			String url = "jdbc:mysql://localhost:3306/mydb";
			String driver = "org.gjt.mm.mysql.Driver";
			String tablename = "transfer";
			String sqlstr;
			String sqlstr1;
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				TransferInputBeanSet transferinputset = (TransferInputBeanSet) session
						.getAttribute("transferinputset");
				int num_testcase = transferinputset.getTransferset().size();

				Class.forName(driver);
				con = DriverManager.getConnection(url, user, password);
				stmt = con.createStatement();
				for (int i = 0; i < num_testcase; i++) {
					int mrID = transferinputset.getTransferset().get(i)
							.getMrID();
					String accountFrom = transferinputset.getTransferset().get(
							i).getAccoutFrom();
					String accountTo = transferinputset.getTransferset().get(i)
							.getAccoutTo();
					String amount = transferinputset.getTransferset().get(i)
							.getAmount();
					String mode = transferinputset.getTransferset().get(i)
							.getMode();
					sqlstr1 = "INSERT INTO "
							+ tablename
							+ "(mrID,accountFrom,accountTo,amount,mode)values('"
							+ mrID + "','" + accountFrom + "','" + accountTo
							+ "','" + amount + "','" + mode + "')";
					stmt.executeUpdate(sqlstr1);
				}
				sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}
			} catch (ClassNotFoundException e1) {
				System.out.println("数据库驱动不存在");
				e1.printStackTrace();
			} catch (SQLException e2) {
				System.out.println("数据库异常");
				e2.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					if (con != null)
						con.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}
			}
		}
	}

}
