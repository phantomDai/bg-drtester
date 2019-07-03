package cn.edu.ustb.mt4ws.servlet;

import java.io.IOException;
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

import cn.edu.ustb.mt4ws.javabean.SubBean;
import cn.edu.ustb.mt4ws.javabean.SubBeanF;
import cn.edu.ustb.mt4ws.javabean.MetamorphicRelation;

import cn.edu.ustb.mt4ws.tcg.SqlUtils;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;

public class AddToDBSub  extends HttpServlet{

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

		List<SubBean> tcsSubList = (List<SubBean>) session
				.getAttribute("tcsSubList");

		List<SubBeanF> tcsSubFList = getFollowUpList(tcsSubList,
				opFormat, mrList,session);

		session.setAttribute("tcsSubFList", tcsSubFList);

		System.out.println("********正在输入数据库*********");
		// 判断是何种OperationName
		System.out.println("operationName：" + operationName);
		if (operationName.equals("sub")) {

			SqlUtils sqlUtils = SqlUtils.getInstance();
			sqlUtils.insertToSubS(tcsSubList);
			sqlUtils.insertToSubF(tcsSubFList);

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
	public static List<SubBeanF> getFollowUpList(
			List<SubBean> tcsSubList, WsdlOperationFormat opFormat,
			List<MetamorphicRelation> mrList,HttpSession session) {
		List<SubBeanF> tcSubFList = new ArrayList<SubBeanF>();
		Iterator iter = tcsSubList.iterator();
		List<List<String>> tcsListF = new ArrayList<List<String>>();

		while (iter.hasNext()) {
			SubBean subBean = (SubBean) iter.next();
			MetamorphicRelation mr = mrList.get((subBean.getMrID() - 1));
			SubBeanF tcSubF = SubBeanF.generateFollowTC(subBean,
					opFormat, mr);
			tcSubFList.add(tcSubF);

			// 添加衍生测试用例
			List<String> tcListF = new ArrayList<String>();
			tcListF.add(String.valueOf(tcSubF.getsID()));// 有待改进
			tcListF.add(String.valueOf(tcSubF.getMrID()));
			tcListF.add(tcSubF.getA());
			tcListF.add(tcSubF.getB());
			tcsListF.add(tcListF);
		}

		session.setAttribute("tcsFList", tcsListF);
		
		return tcSubFList;
	}

	
}
