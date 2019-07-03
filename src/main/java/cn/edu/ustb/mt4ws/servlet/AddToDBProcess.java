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
import cn.edu.ustb.mt4ws.javabean.ProcessBean;
import cn.edu.ustb.mt4ws.javabean.ProcessBeanF;
import cn.edu.ustb.mt4ws.javabean.TCTransfer;
import cn.edu.ustb.mt4ws.javabean.TCTransferF;
import cn.edu.ustb.mt4ws.tcg.SqlUtils;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;

public class AddToDBProcess extends HttpServlet {
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

		List<ProcessBean> tcsProcessList = (List<ProcessBean>) session
				.getAttribute("tcsProcessList");

		List<ProcessBeanF> tcsProcessFList = getFollowUpList(tcsProcessList,
				opFormat, mrList,session);

		session.setAttribute("tcsProcessFList", tcsProcessFList);

		System.out.println("********正在输入数据库*********");
		// 判断是何种OperationName
		System.out.println("operationName：" + operationName);
		if (operationName.equals("process")) {

			SqlUtils sqlUtils = SqlUtils.getInstance();
			sqlUtils.insertToProcessS(tcsProcessList);
			sqlUtils.insertToProcessF(tcsProcessFList);

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
	 * 根据原始测试用例集，得到衍生测试用例集合
	 * 
	 * @param tcTransferList
	 * @return
	 */
	public static List<ProcessBeanF> getFollowUpList(
			List<ProcessBean> tcsProcessList, WsdlOperationFormat opFormat,
			List<MetamorphicRelation> mrList,HttpSession session) {
		List<ProcessBeanF> tcProcessFList = new ArrayList<ProcessBeanF>();
		Iterator iter = tcsProcessList.iterator();
		List<List<String>> tcsListF = new ArrayList<List<String>>();

		while (iter.hasNext()) {
			ProcessBean processBean = (ProcessBean) iter.next();
			MetamorphicRelation mr = mrList.get((processBean.getMrID() - 1));
			ProcessBeanF tcProcessF = ProcessBeanF.generateFollowTC(processBean,
					opFormat, mr);
			tcProcessFList.add(tcProcessF);

			// 添加衍生测试用例
			List<String> tcListF = new ArrayList<String>();
			tcListF.add(String.valueOf(tcProcessF.getsID()));// 有待改进
			tcListF.add(String.valueOf(tcProcessF.getMrID()));
			tcListF.add(tcProcessF.getA());
			tcListF.add(tcProcessF.getB());
			tcListF.add(tcProcessF.getType());
			tcsListF.add(tcListF);
		}

		session.setAttribute("tcsFList", tcsListF);
		
		return tcProcessFList;
	}

}
