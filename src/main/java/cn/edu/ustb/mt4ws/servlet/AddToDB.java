package cn.edu.ustb.mt4ws.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.edu.ustb.mt4ws.javabean.MetamorphicRelation;
import cn.edu.ustb.mt4ws.javabean.TCTransfer;
import cn.edu.ustb.mt4ws.javabean.TCTransferF;
import cn.edu.ustb.mt4ws.tcg.SqlUtils;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;

/**
 * 写到数据库中
 * 
 * @author wendy
 * 
 */
public class AddToDB extends HttpServlet {

	/**
	 * 初始化
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	/**
	 * 处理post方法
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(true);
		String operationName = (String) session.getAttribute("operationName");
		WsdlOperationFormat opFormat = ((Map<String, WsdlOperationFormat>) session
				.getAttribute("operation_map")).get(operationName);
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");

		List<TCTransfer> tcsTransferList = (List<TCTransfer>) session
				.getAttribute("tcsTransferList");

		List<TCTransferF> tcsTransferFList = getFollowUpList(tcsTransferList,
				opFormat, mrList,session);

		session.setAttribute("tcsTransferFList", tcsTransferFList);

		System.out.println("********正在输入数据库*********");
		// 判断是何种OperationName
		System.out.println("operationName：" + operationName);
		if (operationName.equals("transfer")) {

			SqlUtils sqlUtils = SqlUtils.getInstance();
			sqlUtils.insertToTransferS(tcsTransferList);
			sqlUtils.insertToTransferF(tcsTransferFList);

		} else if (operationName.equals("rmbToGbbob")) {

			// TODO
		} else if (operationName.equals("matchQuakes")) {

			// TODO
		} else {
			System.out.println("输入数据库出错!!!");
		}

		response.sendRedirect("4.start_test.jsp");

	}

	/**
	 * 处理get方法
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * 写到数据库中
	 * 
	 * @param tcsTransferList
	 */
	public static void insertTransferDB(List<TCTransfer> tcsTransferList) {

		String user = "root";
		String password = "root";
		String url = "jdbc:mysql://localhost:3306/mydb";
		String driver = "org.gjt.mm.mysql.Driver";
		String tablename = "transfer";

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		// 判断测试用例集是否为空
		if (tcsTransferList == null) {
			System.out.println("测试用例集为空!!!");
		} else {

			try {
				Class.forName(driver);
				con = DriverManager.getConnection(url, user, password);
				stmt = con.createStatement();
				String sqlDel = "truncate table mydb.transfer";
				stmt.executeUpdate(sqlDel);
				// 遍历测试用例集，逐个输入
				for (TCTransfer tcTransfer : tcsTransferList) {
					int id = tcTransfer.getId();
					int mrID = tcTransfer.getMrID();
					String accountFrom = tcTransfer.getAccountFrom();
					String accountTo = tcTransfer.getAccountTo();
					String mode = tcTransfer.getMode();
					String amount = tcTransfer.getAmount();

					String sqlstr1 = "INSERT INTO "
							+ tablename
							+ "(id,mrID,accountFrom,accountTo,amount,mode)values('"
							+ id + "','" + mrID + "','" + accountFrom + "','"
							+ accountTo + "','" + amount + "','" + mode + "')";
					// 执行更新
					stmt.executeUpdate(sqlstr1);
				}
				// 打印数据库中的所有数据
				String sqlstr = "select * from " + tablename;
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

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	 * 根据原始测试用例集，得到衍生测试用例集合
	 * 
	 * @param tcTransferList
	 * @return
	 */
	public static List<TCTransferF> getFollowUpList(
			List<TCTransfer> tcsTransferList, WsdlOperationFormat opFormat,
			List<MetamorphicRelation> mrList,HttpSession session) {
		List<TCTransferF> tcTransferFList = new ArrayList<TCTransferF>();
		Iterator iter = tcsTransferList.iterator();
		List<List<String>> tcsListF = new ArrayList<List<String>>();

		while (iter.hasNext()) {
			TCTransfer tcTransfer = (TCTransfer) iter.next();
			MetamorphicRelation mr = mrList.get((tcTransfer.getMrID() - 1));
			TCTransferF tcTransferF = TCTransferF.generateFollowTC(tcTransfer,
					opFormat, mr);
			tcTransferFList.add(tcTransferF);

			// 添加衍生测试用例
			List<String> tcListF = new ArrayList<String>();
			tcListF.add(String.valueOf(tcTransferF.getsID()));// 有待改进
			tcListF.add(String.valueOf(tcTransferF.getMrID()));
			tcListF.add(tcTransferF.getAccountFrom());
			tcListF.add(tcTransferF.getAccountTo());
			tcListF.add(tcTransferF.getMode());
			tcListF.add(tcTransferF.getAmount());
			tcListF.add(String.valueOf(tcTransferF.getsID()));
			tcsListF.add(tcListF);
		}

		session.setAttribute("tcsFList", tcsListF);
		
		return tcTransferFList;
	}

}
