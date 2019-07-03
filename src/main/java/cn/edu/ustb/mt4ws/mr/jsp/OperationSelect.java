package cn.edu.ustb.mt4ws.mr.jsp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;

public class OperationSelect extends HttpServlet {

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			// 请从头开始测试！
		}
		String operationName = request.getParameter("radio_operations");
		session.setAttribute("operationName", operationName);
		
		Map<String, WsdlOperationFormat> operationMap = (Map<String, WsdlOperationFormat>)session.getAttribute("operation_map");
		WsdlOperationFormat wsOpFormat = operationMap.get(operationName);
		Iterator<String> iter = wsOpFormat.getInput().format.getSimpleTypes().keySet().iterator();
		//解析WSDL，将输入的变量名称存入List里面
		List<String> inputListStr = new ArrayList<String>();
		int i=0;
		while(iter.hasNext()){
			String tempStr=iter.next().toString();
			System.out
					.println("SimpleInput" + i + ":" + tempStr);
			inputListStr.add(tempStr);
			i++;
		}
		//将输入的变量的List存放在session中
		session.setAttribute("inputListStr", inputListStr);		
		
		// 根据operationName来跳转页面
		if (operationName.equals("transfer")) {
			System.out.println("选择的operation为：" + operationName);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("2.1.input_MR.jsp");
			dispatcher.forward(request, response);
		} else if (operationName.equals("process")||operationName.equals("add")||operationName.endsWith("sub")) {
			System.out.println("选择的operation为：" + operationName);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("2.1.input_MR.jsp");
			dispatcher.forward(request, response);
		} else if (operationName.equals("checkQuantity")) {
			System.out.println("选择的operation为：" + operationName);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("2.1.input_MR.jsp");
			dispatcher.forward(request, response);
		}else if (operationName.equals("getList")) {
			System.out.println("选择的operation为：" + operationName);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("2.1.input_MR_Set.jsp");
			dispatcher.forward(request, response);
		} else if (operationName.equals("queryQuakes")||operationName.equals("matchQuakes")) {
			System.out.println("选择的operation为：" + operationName);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("2.1.input_MR_Quake.jsp");
			dispatcher.forward(request, response);
		} else if (operationName.equals("rmbToGbbob")) {
			System.out.println("选择的operation为：" + operationName);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("2.1.input_MR_rmbToGbbob.jsp");
			dispatcher.forward(request, response);
		} else {
			System.out.println("请选择其他操作……");
		}

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

}
