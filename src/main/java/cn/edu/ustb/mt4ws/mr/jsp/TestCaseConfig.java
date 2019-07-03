package cn.edu.ustb.mt4ws.mr.jsp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.edu.ustb.mt4ws.javabean.MetamorphicRelation;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.testcase.Method;
import cn.edu.ustb.mt4ws.testcase.TcGenerator;

public class TestCaseConfig  extends HttpServlet{

	public void init(ServletConfig config) throws ServletException {
		super.init(config); 
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		List<String> str_selected_mr=(List<String>)session.getAttribute("str_selected_mr");
		//获得选择测试用例的种类
		List<String> testcase_config=new ArrayList<String>();
		
		if(str_selected_mr==null){
			System.out.println("没有配置如何生成测试用例……");
		}
		else{
			for(int i=0;i<str_selected_mr.size();i++){
	        	String temp_type=request.getParameter(str_selected_mr.get(i));//临时存储种类
	        	testcase_config.add(temp_type);
	        	System.out.println("配置测试用例种类为："+temp_type);
	        	if(temp_type.equals("0")){
	        		System.out.println("所有测试用例");
	        		
	        	}
	        	else if(temp_type.equals("1")){
	        		System.out.println("随机产生测试用例");
	        		
	        	}
	        	else if(temp_type.equals("2")){
	        		System.out.println("手动输入产生测试用例");
	        	}
	        }
		}
		
		String temp_method = request.getParameter("method");
		System.out.println("temp_method="+temp_method);
		session.setAttribute("testcase_add_method", temp_method);
		String temp_num = request.getParameter("num_text");
		if(temp_num == null)
			temp_num = "";
		session.setAttribute("testcase_num", temp_num);
		
		TcGenerator tcGen = new TcGenerator();
		String operationName = (String) session.getAttribute("operationName");

		WsdlOperationFormat opFormat = ((Map<String, WsdlOperationFormat>) session
				.getAttribute("operation_map")).get(operationName);
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");
		if(temp_method.equals("0"))
			tcGen.autoMethod(request, response, session, operationName, opFormat, mrList);
		else
			tcGen.autoMethodIter(request, response, session, operationName, opFormat, mrList);
		
		

		RequestDispatcher dispatcher = request
				.getRequestDispatcher("4.start_test.jsp");
		dispatcher.forward(request, response);

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
}
