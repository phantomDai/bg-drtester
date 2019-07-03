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

import cn.edu.ustb.mt4ws.javabean.CheckQuantityBean;
import cn.edu.ustb.mt4ws.javabean.CheckQuantityBeanF;
import cn.edu.ustb.mt4ws.javabean.MetamorphicRelation;
import cn.edu.ustb.mt4ws.javabean.ProcessBean;
import cn.edu.ustb.mt4ws.javabean.ProcessBeanF;
import cn.edu.ustb.mt4ws.tcg.SqlUtils;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;

public class AddToDBCheckQuantity extends HttpServlet{

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

		List<CheckQuantityBean> tcsCheckQuantityList = (List<CheckQuantityBean>) session
				.getAttribute("tcsCheckQuantityList");

		List<CheckQuantityBeanF> tcsCheckQuantityFList = getFollowUpList(tcsCheckQuantityList,
				opFormat, mrList,session);

		session.setAttribute("tcsCheckQuantityFList", tcsCheckQuantityFList);

		System.out.println("********正在输入数据库*********");
		// 判断是何种OperationName
		System.out.println("operationName：" + operationName);
		if (operationName.equals("checkQuantity")) {

			SqlUtils sqlUtils = SqlUtils.getInstance();
			sqlUtils.insertToCheckQuantityS(tcsCheckQuantityList);
			sqlUtils.insertToCheckQuantityF(tcsCheckQuantityFList);

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
	public static List<CheckQuantityBeanF> getFollowUpList(
			List<CheckQuantityBean> tcsCheckQuantityList, WsdlOperationFormat opFormat,
			List<MetamorphicRelation> mrList,HttpSession session) {
		List<CheckQuantityBeanF> tcCheckQuantityFList = new ArrayList<CheckQuantityBeanF>();
		Iterator iter = tcsCheckQuantityList.iterator();
		List<List<String>> tcsListF = new ArrayList<List<String>>();

		while (iter.hasNext()) {
			CheckQuantityBean checkQuantityBean = (CheckQuantityBean) iter.next();
			MetamorphicRelation mr = mrList.get((checkQuantityBean.getMrID() - 1));
			CheckQuantityBeanF tcCheckQuantityF = CheckQuantityBeanF.generateFollowTC(checkQuantityBean,
					opFormat, mr);
			tcCheckQuantityFList.add(tcCheckQuantityF);

			// 添加衍生测试用例
			List<String> tcListF = new ArrayList<String>();
			tcListF.add(String.valueOf(tcCheckQuantityF.getsID()));// 有待改进
			tcListF.add(String.valueOf(tcCheckQuantityF.getMrID()));
			tcListF.add(tcCheckQuantityF.getName());
			tcListF.add(tcCheckQuantityF.getAmount());
			tcListF.add(String.valueOf(tcCheckQuantityF.getsID()));
			tcsListF.add(tcListF);
		}

		session.setAttribute("tcsFList", tcsListF);
		
		return tcCheckQuantityFList;
	}
	
}
