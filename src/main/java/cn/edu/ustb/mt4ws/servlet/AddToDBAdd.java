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

import cn.edu.ustb.mt4ws.javabean.AddBean;
import cn.edu.ustb.mt4ws.javabean.AddBeanF;
import cn.edu.ustb.mt4ws.javabean.MetamorphicRelation;

import cn.edu.ustb.mt4ws.tcg.SqlUtils;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;

public class AddToDBAdd  extends HttpServlet{

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

		List<AddBean> tcsAddList = (List<AddBean>) session
				.getAttribute("tcsAddList");

		List<AddBeanF> tcsAddFList = getFollowUpList(tcsAddList,
				opFormat, mrList,session);

		session.setAttribute("tcsAddFList", tcsAddFList);

		System.out.println("********正在输入数据库*********");
		// 判断是何种OperationName
		System.out.println("operationName：" + operationName);
		if (operationName.equals("add")) {

			SqlUtils sqlUtils = SqlUtils.getInstance();
			sqlUtils.insertToAddS(tcsAddList);
			sqlUtils.insertToAddF(tcsAddFList);

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
	public static List<AddBeanF> getFollowUpList(
			List<AddBean> tcsAddList, WsdlOperationFormat opFormat,
			List<MetamorphicRelation> mrList,HttpSession session) {
		List<AddBeanF> tcAddFList = new ArrayList<AddBeanF>();
		Iterator iter = tcsAddList.iterator();
		List<List<String>> tcsListF = new ArrayList<List<String>>();

		while (iter.hasNext()) {
			AddBean addBean = (AddBean) iter.next();
			MetamorphicRelation mr = mrList.get((addBean.getMrID() - 1));
			AddBeanF tcAddF = AddBeanF.generateFollowTC(addBean,
					opFormat, mr);
			tcAddFList.add(tcAddF);

			// 添加衍生测试用例
			List<String> tcListF = new ArrayList<String>();
			tcListF.add(String.valueOf(tcAddF.getsID()));// 有待改进
			tcListF.add(String.valueOf(tcAddF.getMrID()));
			tcListF.add(tcAddF.getA());
			tcListF.add(tcAddF.getB());
			tcsListF.add(tcListF);
		}

		session.setAttribute("tcsFList", tcsListF);
		
		return tcAddFList;
	}

	
}
