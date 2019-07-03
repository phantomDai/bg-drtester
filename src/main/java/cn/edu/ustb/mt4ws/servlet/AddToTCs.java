package cn.edu.ustb.mt4ws.servlet;

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

import cn.edu.ustb.mt4ws.javabean.TCTransfer;
import cn.edu.ustb.mt4ws.mr.jsp.JspUtils;
import cn.edu.ustb.mt4ws.javabean.*;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.tcg.XmlMessageFormat;

public class AddToTCs extends HttpServlet {
	

	/**
	 * 初始化
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	/**
	 * doPost方法
	 * @param request
	 * @param response
	 * @return void
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		System.out.println("*************添加TCs*************");
		
		String operationName = (String) session.getAttribute("operationName");
		
		// 根据OperationName进行跳转
		if (operationName.equals("transfer")) {
			//将tc存到List中
			setTransferTC(request, response, session, operationName);

		}  else if (operationName.equals("process")) {
			
			setProcessTC(request, response, session, operationName);
		}  else if (operationName.equals("add")) {
			
			setAddTC(request, response, session, operationName);
		} else if (operationName.equals("sub")) {
			
			setSubTC(request, response, session, operationName);
		}  else if (operationName.equals("checkQuantity")) {
			
			setCheckQuantityTC(request, response, session, operationName);
		} else if (operationName.equals("rmbToGbbob")) {
			
			//TODO
			response.sendRedirect("3.2.fill_tc_manualy_rmb.jsp");
		} else if (operationName.equals("queryQuakes")
				|| operationName.equals("matchQuakes")) {
			
			//TODO
			response.sendRedirect("3.2.fill_tc_manualy_quake.jsp");
		} else {
			//TODO			
			System.out.println("跳转错误!!!");
			response.sendRedirect("3.2.fill_tc_manualy.jsp");
		}

	}
	
	
	
	/**
	 * doGet方法
	 * @param request
	 * @param response
	 * @return void
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
		
	}
	
	
	/**
	 * BPEL测试用例录入
	 * operationName:process
	 * @param request
	 * @param response
	 * @param session
	 * @param operationName
	 */
	public void setProcessTC(HttpServletRequest request, HttpServletResponse response,HttpSession session,String operationName){
		String MrName = (String) session.getAttribute("strmr");
		int mrID = Integer.parseInt(MrName.substring(2));
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");
		RelationOfInput relationOfInput = mrList.get(mrID - 1)
				.getRelationOfInput();
		System.out.println(MrName);
		String a = request.getParameter("textbox_input1");
		String b = request.getParameter("textbox_input2");
		String type = request.getParameter("textbox_input3");
		int id=1;
		List<ProcessBean> tcsProcessList=null;
		//tcsList以字符串形式存储所有的testcase，每组testcase存入List<String>中
		List<List<String>> tcsList=null;
		if(session.getAttribute("tcsProcessList")==null){
			System.out.println("第一个tc");
			tcsProcessList=new ArrayList<ProcessBean>();
			tcsList=new ArrayList<List<String>>();
		}else {
			tcsProcessList = (List<ProcessBean>) session.getAttribute("tcsProcessList");
			tcsList = (List<List<String>>) session.getAttribute("tcsList");
			id=tcsProcessList.size()+1;
		}
		
		if (a != "" && b != "" && type != "") {

			ProcessBean tcProcess = new ProcessBean(id, mrID, a,b,type);

			// 验证输入是否有效
			
			//TODO
			boolean isValid=true;


			// 如果有效，则将tc输入到tcList中
			if (isValid) {
				System.out.println("tc" + id + "有效");
				tcsProcessList.add(tcProcess);
				//以字符串形式存储到tcList中，便于界面的读取
				List<String> tcList=new ArrayList<String>();
				tcList.add(String.valueOf(id));
				tcList.add(String.valueOf(mrID));
				tcList.add(a);
				tcList.add(b);
				tcList.add(type);
				tcsList.add(tcList);
				
				session.setAttribute("tcsList", tcsList);
				session.setAttribute("tcsProcessList", tcsProcessList);

				try {
					response.sendRedirect("3.2.fill_tc_manualy_process.jsp");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("tc" + id + "无效，请重新输入testcase");
				try {
					response.sendRedirect("3.2.fill_tc_manualy_process.jsp");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			System.out.println("输入不能为空!!!");
		}
				
	}
	
	/**
	 * BPEL测试用例录入
	 * operationName:add
	 * @param request
	 * @param response
	 * @param session
	 * @param operationName
	 */
	public void setAddTC(HttpServletRequest request, HttpServletResponse response,HttpSession session,String operationName){
		String MrName = (String) session.getAttribute("strmr");
		int mrID = Integer.parseInt(MrName.substring(2));
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");
		RelationOfInput relationOfInput = mrList.get(mrID - 1)
				.getRelationOfInput();
		System.out.println(MrName);
		String a = request.getParameter("textbox_input1");
		String b = request.getParameter("textbox_input2");
		int id=1;
		List<AddBean> tcsAddList=null;
		//tcsList以字符串形式存储所有的testcase，每组testcase存入List<String>中
		List<List<String>> tcsList=null;
		if(session.getAttribute("tcsAddList")==null){
			System.out.println("第一个tc");
			tcsAddList=new ArrayList<AddBean>();
			tcsList=new ArrayList<List<String>>();
		}else {
			tcsAddList = (List<AddBean>) session.getAttribute("tcsAddList");
			tcsList = (List<List<String>>) session.getAttribute("tcsList");
			id=tcsAddList.size()+1;
		}
		
		if (a != "" && b != "" ) {

			AddBean tcAdd = new AddBean(id, mrID, a, b);

			// 验证输入是否有效
			
			//TODO
			boolean isValid=true;


			// 如果有效，则将tc输入到tcList中
			if (isValid) {
				System.out.println("tc" + id + "有效");
				tcsAddList.add(tcAdd);
				//以字符串形式存储到tcList中，便于界面的读取
				List<String> tcList=new ArrayList<String>();
				tcList.add(String.valueOf(id));
				tcList.add(String.valueOf(mrID));
				tcList.add(a);
				tcList.add(b);
				tcsList.add(tcList);
				
				session.setAttribute("tcsList", tcsList);
				session.setAttribute("tcsAddList", tcsAddList);

				try {
					response.sendRedirect("3.2.fill_tc_manualy_add.jsp");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("tc" + id + "无效，请重新输入testcase");
				try {
					response.sendRedirect("3.2.fill_tc_manualy_add.jsp");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			System.out.println("输入不能为空!!!");
		}
				
	}
	
	/**
	 * BPEL测试用例录入
	 * operationName:checkQuantity
	 * @param request
	 * @param response
	 * @param session
	 * @param operationName
	 */
	public void setCheckQuantityTC(HttpServletRequest request, HttpServletResponse response,HttpSession session,String operationName){
		String MrName = (String) session.getAttribute("strmr");
		int mrID = Integer.parseInt(MrName.substring(2));
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");
		RelationOfInput relationOfInput = mrList.get(mrID - 1)
				.getRelationOfInput();
		System.out.println(MrName);
		String name = request.getParameter("textbox_input1");
		String amount = request.getParameter("textbox_input2");
		int id=1;
		List<CheckQuantityBean> tcsCheckQuantityList=null;
		//tcsList以字符串形式存储所有的testcase，每组testcase存入List<String>中
		List<List<String>> tcsList=null;
		if(session.getAttribute("tcsList")==null){
			System.out.println("第一个tc");
			tcsCheckQuantityList=new ArrayList<CheckQuantityBean>();
			tcsList=new ArrayList<List<String>>();
		}else {
			tcsCheckQuantityList = (List<CheckQuantityBean>) session.getAttribute("tcsCheckQuantityList");
			tcsList = (List<List<String>>) session.getAttribute("tcsList");
			id=tcsCheckQuantityList.size()+1;
		}
		
		if (name != "" && amount != "" ) {

			CheckQuantityBean tcCheckQuantity = new CheckQuantityBean(id, mrID, name, amount);

			// 验证输入是否有效
			
			//TODO
			boolean isValid=true;


			// 如果有效，则将tc输入到tcList中
			if (isValid) {
				System.out.println("tc" + id + "有效");
				tcsCheckQuantityList.add(tcCheckQuantity);
				//以字符串形式存储到tcList中，便于界面的读取
				List<String> tcList=new ArrayList<String>();
				tcList.add(String.valueOf(id));
				tcList.add(String.valueOf(mrID));
				tcList.add(name);
				tcList.add(amount);
				tcsList.add(tcList);
				
				session.setAttribute("tcsList", tcsList);
				session.setAttribute("tcsCheckQuantityList", tcsCheckQuantityList);

				try {
					response.sendRedirect("3.2.fill_tc_manualy_checkQuantity.jsp");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("tc" + id + "无效，请重新输入testcase");
				try {
					response.sendRedirect("3.2.fill_tc_manualy_checkQuantity.jsp");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			System.out.println("输入不能为空!!!");
		}
				
	}
	
	/**
	 * BPEL测试用例录入
	 * operationName:sub
	 * @param request
	 * @param response
	 * @param session
	 * @param operationName
	 */
	public void setSubTC(HttpServletRequest request, HttpServletResponse response,HttpSession session,String operationName){
		String MrName = (String) session.getAttribute("strmr");
		int mrID = Integer.parseInt(MrName.substring(2));
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");
		RelationOfInput relationOfInput = mrList.get(mrID - 1)
				.getRelationOfInput();
		System.out.println(MrName);
		String a = request.getParameter("textbox_input1");
		String b = request.getParameter("textbox_input2");
		int id=1;
		List<SubBean> tcsSubList=null;
		//tcsList以字符串形式存储所有的testcase，每组testcase存入List<String>中
		List<List<String>> tcsList=null;
		if(session.getAttribute("tcsSubList")==null){
			System.out.println("第一个tc");
			tcsSubList=new ArrayList<SubBean>();
			tcsList=new ArrayList<List<String>>();
		}else {
			tcsSubList = (List<SubBean>) session.getAttribute("tcsSubList");
			tcsList = (List<List<String>>) session.getAttribute("tcsList");
			id=tcsSubList.size()+1;
		}
		
		if (a != "" && b != "" ) {

			SubBean tcSub = new SubBean(id, mrID, a, b);

			// 验证输入是否有效
			
			//TODO
			boolean isValid=true;


			// 如果有效，则将tc输入到tcList中
			if (isValid) {
				System.out.println("tc" + id + "有效");
				tcsSubList.add(tcSub);
				//以字符串形式存储到tcList中，便于界面的读取
				List<String> tcList=new ArrayList<String>();
				tcList.add(String.valueOf(id));
				tcList.add(String.valueOf(mrID));
				tcList.add(a);
				tcList.add(b);
				tcsList.add(tcList);
				
				session.setAttribute("tcsList", tcsList);
				session.setAttribute("tcsSubList", tcsSubList);

				try {
					response.sendRedirect("3.2.fill_tc_manualy_sub.jsp");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("tc" + id + "无效，请重新输入testcase");
				try {
					response.sendRedirect("3.2.fill_tc_manualy_sub.jsp");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			System.out.println("输入不能为空!!!");
		}
				
	}
	
	/**
	 * 录入手动输入的测试用例
	 * transfer
	 * @param request
	 * @param response
	 * @param session
	 * @param operationName
	 */
	public void setTransferTC(HttpServletRequest request, HttpServletResponse response,HttpSession session,String operationName){
		String MrName = (String) session.getAttribute("strmr");
		int mrID = Integer.parseInt(MrName.substring(2));
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");
		RelationOfInput relationOfInput = mrList.get(mrID - 1)
				.getRelationOfInput();
		System.out.println(MrName);
		String accountFrom = request.getParameter("textbox_input1");
		String accountTo = request.getParameter("textbox_input2");
		String amount = request.getParameter("textbox_input3");
		String mode = request.getParameter("textbox_input4");
		int id=1;
		List<TCTransfer> tcsTransferList=null;
		//tcsList以字符串形式存储所有的testcase，每组testcase存入List<String>中
		List<List<String>> tcsList=null;
		if(session.getAttribute("tcsTransferList")==null){
			System.out.println("第一个tc");
			tcsTransferList=new ArrayList<TCTransfer>();
			tcsList=new ArrayList<List<String>>();
		}else {
			tcsTransferList = (List<TCTransfer>) session.getAttribute("tcsTransferList");
			tcsList = (List<List<String>>) session.getAttribute("tcsList");
			id=tcsTransferList.size()+1;
		}
		
		if (accountFrom != "" && accountTo != "" && amount != "" && mode != "") {

			TCTransfer tcTransfer = new TCTransfer(id, mrID, accountFrom,
					accountTo,mode, amount);

			// 验证输入是否有效
			
			//TODO
			boolean isValid=true;
			try {
				isValid = checkTC(tcTransfer,session,operationName,relationOfInput);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// 如果有效，则将tc输入到tcList中
			if (isValid) {
				System.out.println("tc" + id + "有效");
				tcsTransferList.add(tcTransfer);
				//以字符串形式存储到tcList中，便于界面的读取
				List<String> tcList=new ArrayList<String>();
				tcList.add(String.valueOf(id));
				tcList.add(String.valueOf(mrID));
				tcList.add(accountFrom);
				tcList.add(accountTo);
				tcList.add(mode);
				tcList.add(amount);
				tcsList.add(tcList);
				
				session.setAttribute("tcsList", tcsList);
				session.setAttribute("tcsTransferList", tcsTransferList);

				try {
					response.sendRedirect("3.2.fill_tc_manualy.jsp");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("tc" + id + "无效，请重新输入testcase");
				try {
					response.sendRedirect("3.2.fill_tc_manualy.jsp");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			System.out.println("输入不能为空!!!");
		}
				
	}
	
	/**
	 * 检查TC是否符合MRDL规范
	 * @param tcTransfer
	 * @return
	 * @throws Exception 
	 */
	public boolean checkTC(TCTransfer tcTransfer,HttpSession session,String operationName,RelationOfInput relationOfInput) throws Exception{
		boolean isValid = true;
		Map<String, WsdlOperationFormat> opMap = (Map<String, WsdlOperationFormat>) session
				.getAttribute("operation_map");
		XmlMessageFormat inputFormat = opMap.get(operationName).getInput().format;	
		
		List<String> tcListStr = new ArrayList<String>();
		tcListStr.add(tcTransfer.getAccountFrom());
		tcListStr.add(tcTransfer.getAccountTo());
		tcListStr.add(tcTransfer.getMode());
		tcListStr.add(tcTransfer.getAmount());
		
		JspUtils jsputils = new JspUtils();
		boolean[] tcValid = new boolean[tcListStr.size()];
		tcValid=jsputils.validateTC(inputFormat, tcListStr);
		for (boolean elementIsValid : tcValid) {
			if (elementIsValid == false) {
				System.out.println("输入格式错误!!!");
				
				return false;
				}
			}
			
		
		//TODO
		
		
		return isValid;		
	}
	
	
	


}
