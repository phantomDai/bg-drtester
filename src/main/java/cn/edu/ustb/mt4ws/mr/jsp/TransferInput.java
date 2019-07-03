package cn.edu.ustb.mt4ws.mr.jsp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.edu.ustb.mt4ws.mr.model.MetamorphicRelation;
import cn.edu.ustb.mt4ws.mr.model.RelationOfInput;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.tcg.XmlMessageFormat;

/*
 * 银行转账添加测试用例
 */
public class TransferInput extends HttpServlet {

	public static int count_testcase;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		count_testcase = 0;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			// TODO
		}
		System.out.println("正准备添加到测试用例集中……");
		// transfer操作
		if (((String) session.getAttribute("operationName")).equals("transfer")) {
			setTransfer(request, response, session);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("3.2.fill_tc_manualy.jsp");
			dispatcher.forward(request, response);
		}
		// Set操作
		else if (((String) session.getAttribute("operationName"))
				.equals("getRegion")) {
			setAboutSet(request, response, session);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("3.2.fill_tc_manualy_set.jsp");
			dispatcher.forward(request, response);
		}
		// rmb服务操作
		else if (((String) session.getAttribute("operationName"))
				.equals("rmbToGbbob")) {
			setRMB(request, response, session);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("3.2.fill_tc_manualy_rmb.jsp");
			dispatcher.forward(request, response);
		}
		// 地震查询操作
		else if (((String) session.getAttribute("operationName"))
				.equals("queryQuakes")||((String) session.getAttribute("operationName"))
				.equals("matchQuakes")) {
			setQuakes(request, response, session);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("3.2.fill_tc_manualy_quake.jsp");
			dispatcher.forward(request, response);
		} else {
			System.out.println("跳转页面出错");
		}

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * 设置rmb转换服务
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	public void setRMB(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		String MrName = (String) session.getAttribute("strmr");
		int mrID = Integer.parseInt(MrName.substring(2));
		String operationName = (String) session.getAttribute("operationName");
		Map<String, WsdlOperationFormat> opMap = (Map<String, WsdlOperationFormat>) session
				.getAttribute("operation_map");
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");
		RelationOfInput relationOfInput = mrList.get(mrID - 1)
				.getRelationOfInput();
		System.out.println(MrName);
		String RMB = request.getParameter("textbox_input1");

		XmlMessageFormat inputFormat = opMap.get(operationName).getInput().format;
		List<String> testcase = new ArrayList<String>();
		testcase.add(RMB);

		/*
		 * 验证输入的合法性 TODO
		 */

		Vector<RMBInputBean> rmbInputVector;
		RMBInputBeanSet rmbInputSet = new RMBInputBeanSet();

		if (RMB != "") {

			RMBInputBean rmbInput = new RMBInputBean(mrID, RMB);
			System.out.println("RMB:" + rmbInput.getRmb());
			if (count_testcase == 0) {
				rmbInputVector = new Vector<RMBInputBean>();
				rmbInputVector.add(rmbInput);
				session.setAttribute("rmbInputVector", rmbInputVector);
				System.out.println("已经添加到测试用例集中……");
			} else {
				rmbInputVector = (Vector<RMBInputBean>) session
						.getAttribute("rmbInputVector");
				rmbInputVector.add(rmbInput);
			}
			count_testcase++;
			System.out.println("count_testcase:" + count_testcase);
			rmbInputSet.setRmbinputbeanset(rmbInputVector);
			session.setAttribute("rmbInputSet", rmbInputSet);
			// System.out.println("AcountFrom:"+transferinputset.getTransferset().elementAt(count_testcase-1).getAccoutFrom());

		} else {
			System.out.println("输入不能为空……");
		}

	}

	/**
	 * 从页面中获取查询地震服务的相关输入
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	public void setQuakes(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		String MrName = (String) session.getAttribute("strmr");
		int mrID = Integer.parseInt(MrName.substring(2));
		String operationName = (String) session.getAttribute("operationName");
		Map<String, WsdlOperationFormat> opMap = (Map<String, WsdlOperationFormat>) session
				.getAttribute("operation_map");
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");
		RelationOfInput relationOfInput = mrList.get(mrID - 1)
				.getRelationOfInput();
		System.out.println(MrName);
		String maxd = request.getParameter("maxd");
		String mind = request.getParameter("mind");
		String maxlng = request.getParameter("maxlng");
		String minlng = request.getParameter("minlng");
		String maxlat = request.getParameter("maxlat");
		String minlat = request.getParameter("minlat");
		String maxmag = request.getParameter("maxmag");
		String minmag = request.getParameter("minmag");
		String maxdepth = request.getParameter("maxdepth");
		String mindepth = request.getParameter("mindepth");
		XmlMessageFormat inputFormat = opMap.get(operationName).getInput().format;
		List<String> testcase = new ArrayList<String>();
		testcase.add(maxd);
		testcase.add(mind);
		testcase.add(maxlng);
		testcase.add(minlng);
		testcase.add(maxlat);
		testcase.add(minlat);
		testcase.add(maxmag);
		testcase.add(minmag);
		testcase.add(maxdepth);
		testcase.add(mindepth);

		/*
		 * 验证输入的合法性 TODO
		 */

		Vector<QuakeInputBean> quakeInputVector;
		QuakeInputBeanSet quakeInputSet = new QuakeInputBeanSet();

		if (maxd != "" && mind != "" && maxlng != "" && minlng != ""
				&& maxlat != "" && minlat != "" && maxmag != "" && minmag != ""
				&& maxdepth != "" && mindepth != "") {

			QuakeInputBean quakeInput = new QuakeInputBean(mrID, maxd, mind,
					maxlng, minlng, maxlat, minlat, maxmag, minmag, maxdepth,
					mindepth);
			System.out.println("maxd:" + quakeInput.getMaxd());
			System.out.println("mind:" + quakeInput.getMind());
			System.out.println("maxlng:" + quakeInput.getMaxlng());
			System.out.println("minlng:" + quakeInput.getMinlng());
			System.out.println("maxlat:" + quakeInput.getMaxlat());
			System.out.println("minlat:" + quakeInput.getMinlat());
			System.out.println("maxmag:" + quakeInput.getMaxmag());
			System.out.println("minmag:" + quakeInput.getMinmag());
			System.out.println("maxdepth:" + quakeInput.getMaxdepth());
			System.out.println("mindepth:" + quakeInput.getMindepth());
			
			if (count_testcase == 0) {
				quakeInputVector = new Vector<QuakeInputBean>();
				quakeInputVector.add(quakeInput);
				session.setAttribute("quakeInputVector", quakeInputVector);
				System.out.println("已经添加到测试用例集中……");
			} else {
				quakeInputVector = (Vector<QuakeInputBean>) session
						.getAttribute("quakeInputVector");
				quakeInputVector.add(quakeInput);
			}
			count_testcase++;
			System.out.println("count_testcase:" + count_testcase);
			quakeInputSet.setQuakeinputbeanset(quakeInputVector);
			session.setAttribute("quakeInputSet", quakeInputSet);
			// System.out.println("AcountFrom:"+transferinputset.getTransferset().elementAt(count_testcase-1).getAccoutFrom());

		} else {
			System.out.println("输入不能为空……");
		}

	}

	/**
	 * 设置关于transfer的相关属性
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */

	public void setTransfer(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		String MrName = (String) session.getAttribute("strmr");
		int mrID = Integer.parseInt(MrName.substring(2));
		String operationName = (String) session.getAttribute("operationName");
		Map<String, WsdlOperationFormat> opMap = (Map<String, WsdlOperationFormat>) session
				.getAttribute("operation_map");
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");
		RelationOfInput relationOfInput = mrList.get(mrID - 1)
				.getRelationOfInput();
		System.out.println(MrName);
		String accountFrom = request.getParameter("textbox_input1");
		String accountTo = request.getParameter("textbox_input2");
		String amount = request.getParameter("textbox_input3");
		String mode = request.getParameter("textbox_input4");
		XmlMessageFormat inputFormat = opMap.get(operationName).getInput().format;
		List<String> testcase = new ArrayList<String>();
		testcase.add(accountFrom);
		testcase.add(accountTo);
		testcase.add(mode);
		testcase.add(amount);
		try {
			JspUtils jsputils = new JspUtils();
			boolean[] tcValid = new boolean[testcase.size()];
			//tcValid = jsputils.validateTestCase(inputFormat, relationOfInput,
				//	testcase);
			for (boolean elementIsValid : tcValid) {
				if (elementIsValid == false) {
					request.setAttribute("isValid", false);
					System.out.println("输入格式错误！！！");
					/*
					 * RequestDispatcher dispatcher = request
					 * .getRequestDispatcher("3.2.fill_tc_manualy.jsp");
					 * dispatcher.forward(request, response);
					 */
					return;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("isValid", false);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("3.2.fill_tc_manualy.jsp");
			dispatcher.forward(request, response);
		}

		Vector<TransferInputBean> transferinputvector;
		TransferInputBeanSet transferinputset = new TransferInputBeanSet();

		if (accountFrom != "" && accountTo != "" && amount != "" && mode != "") {

			TransferInputBean transferinput = new TransferInputBean(mrID,
					accountFrom, accountTo, mode, amount);

			if (count_testcase == 0) {
				transferinputvector = new Vector<TransferInputBean>();
				transferinputvector.add(transferinput);
				session
						.setAttribute("transferinputvector",
								transferinputvector);
				System.out.println("已经添加到测试用例集中……");
			} else {
				transferinputvector = (Vector<TransferInputBean>) session
						.getAttribute("transferinputvector");
				transferinputvector.add(transferinput);
			}
			count_testcase++;
			System.out.println("count_testcase:" + count_testcase);
			transferinputset.setTransferset(transferinputvector);
			session.setAttribute("transferinputset", transferinputset);
			// System.out.println("AcountFrom:"+transferinputset.getTransferset().elementAt(count_testcase-1).getAccoutFrom());

		} else {
			System.out.println("输入不能为空……");
		}
	}

	/**
	 * 设置关于Set的相关属性
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	public void setAboutSet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		String MrName = (String) session.getAttribute("strmr");
		int mrID = Integer.parseInt(MrName.substring(2));
		String operationName = (String) session.getAttribute("operationName");
		Map<String, WsdlOperationFormat> opMap = (Map<String, WsdlOperationFormat>) session
				.getAttribute("operation_map");
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");
		RelationOfInput relationOfInput = mrList.get(mrID - 1)
				.getRelationOfInput();
		System.out.println(MrName);
		String min = request.getParameter("textbox_input1");
		String max = request.getParameter("textbox_input2");

		XmlMessageFormat inputFormat = opMap.get(operationName).getInput().format;
		List<String> testcase = new ArrayList<String>();
		testcase.add(min);
		testcase.add(max);
		try {
			JspUtils jsputils = new JspUtils();
			boolean[] tcValid = new boolean[testcase.size()];
//			tcValid = jsputils.validateTestCase(inputFormat, relationOfInput,
//					testcase);
			for (boolean elementIsValid : tcValid) {
				if (elementIsValid == false) {
					request.setAttribute("isValid", false);
					RequestDispatcher dispatcher = request
							.getRequestDispatcher("3.2.fill_tc_manualy_set.jsp");
					dispatcher.forward(request, response);
					return;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("isValid", false);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("3.2.fill_tc_manualy_set.jsp");
			dispatcher.forward(request, response);
		}

		Vector<SetInputBean> setinputvector;
		SetInputBeanSet setinputset = new SetInputBeanSet();

		if (min != "" && max != "") {

			SetInputBean setinput = new SetInputBean(mrID, min, max);

			if (count_testcase == 0) {
				setinputvector = new Vector<SetInputBean>();
				setinputvector.add(setinput);
				session.setAttribute("transferinputvector", setinputvector);
				System.out.println("已经添加到测试用例集中……");
			} else {
				setinputvector = (Vector<SetInputBean>) session
						.getAttribute("setinputvector");
				setinputvector.add(setinput);
			}
			count_testcase++;
			System.out.println("count_testcase:" + count_testcase);
			setinputset.setSetinputbeanset(setinputvector);
			session.setAttribute("setinputset", setinputset);
			// System.out.println("AcountFrom:"+transferinputset.getTransferset().elementAt(count_testcase-1).getAccoutFrom());

		} else {
			System.out.println("输入不能为空……");
		}

	}

}
