package cn.edu.ustb.mt4ws.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.edu.ustb.mt4ws.javabean.Function;
import cn.edu.ustb.mt4ws.javabean.MetamorphicRelation;
import cn.edu.ustb.mt4ws.javabean.OpAndRelation;
import cn.edu.ustb.mt4ws.javabean.Operator;
import cn.edu.ustb.mt4ws.javabean.Relation;
import cn.edu.ustb.mt4ws.javabean.RelationOfInput;
import cn.edu.ustb.mt4ws.javabean.RelationOfOutput;

public class AddToMrs extends HttpServlet {

	/**
	 * 初始化
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	/**
	 * 处理post方法
	 */
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		HttpSession session = request.getSession(true);
		System.out.println("*************添加MRs*************");

		int numOfRMr = Integer.parseInt(request.getParameter("Row_R_Num"));
		System.out.println("num of R mr：" + numOfRMr);
		int numOfRFMr = Integer.parseInt(request.getParameter("Row_RF_Num"));
		System.out.println("num of RF mr：" + numOfRFMr);

		// 从前台获取Form中的内容
		// String resOp_R_1 = request.getParameter("resOp_R_1");		
		String relationOp_R_1 = request.getParameter("relationOp_R_1");
		// 判断checkbox是否选中
		if (relationOp_R_1 == null) {
			relationOp_R_1 = "POS";
		}
		String textbox_R_F_1 = request.getParameter("textbox_R_F_1");
		String select_relation_R_1 = request
				.getParameter("select_relation_R_1");
		String textbox_R_S_1 = request.getParameter("textbox_R_S_1");

		// String resOp_RF_1 = request.getParameter("resOp_RF_1");
		String relationOp_RF_1 = request.getParameter("relationOp_RF_1");
		if(relationOp_RF_1==null){
			relationOp_RF_1="POS";
		}
		String textbox_RF_F_1 = request.getParameter("textbox_RF_F_1");
		String select_relation_RF_1 = request
				.getParameter("select_relation_RF_1");
		String textbox_RF_S_1 = request.getParameter("textbox_RF_S_1");

		// 打印第一条Input的Mr
		System.out.println("the first mr Of Input: " + " " + relationOp_R_1
				+ " " + textbox_R_F_1 + select_relation_R_1 + textbox_R_S_1);

		// 打印第一条Input的Mr
		System.out.println("the first mr Of Output: " + " " + relationOp_RF_1
				+ " " + textbox_RF_F_1 + select_relation_RF_1 + textbox_RF_S_1);

		// 判断第一条InputMr和OutputMr是否为空
		if (textbox_R_F_1.isEmpty() || textbox_R_S_1.isEmpty()) {
			System.out.println("resOp_R_1 or textbox_R_S_1 is empty");
		} else if (textbox_RF_F_1.isEmpty() || textbox_RF_S_1.isEmpty()) {
			System.out.println("resOp_RF_1 or textbox_RF_S_1 is empty");
		} else {
			System.out.println("第一条的InputMr和OutputMr不为空!!!");
			
			//Input:
			RelationOfInput relationOfInput = new RelationOfInput();
			List<OpAndRelation> listOpAndRelation_R = new ArrayList<OpAndRelation>();
			
			//Output:
			RelationOfOutput relationOfOutput = new RelationOfOutput();
			List<OpAndRelation> listOpAndRelation_RF = new ArrayList<OpAndRelation>();
			
			//MetamorphicRelation:
			MetamorphicRelation metamorphicRelation = new MetamorphicRelation();
			
			//MR list
			
			List<MetamorphicRelation> mrList = null;
			//判断是否为第一次
			if ((List<MetamorphicRelation>) session.getAttribute("MR_List") == null) {
				System.out.println("加入的mr为第一条!!!");
				mrList = new ArrayList<MetamorphicRelation>();
			} else {
				mrList = (List<MetamorphicRelation>) session
						.getAttribute("MR_List");
				System.out.println("这是第" + (mrList.size()+1) + "个mr!!!");
			}

			//第一条relationInput
			Function function_R_F_1 = new Function(textbox_R_F_1);
			Function function_R_S_1 = new Function(textbox_R_S_1);
			Operator op_R_1 = new Operator(select_relation_R_1);
			Operator relation_Op_R_1 = new Operator(relationOp_R_1);
			Relation relation_R_1 = new Relation(function_R_F_1,
					function_R_S_1, op_R_1, relation_Op_R_1);
			
			
			//第一条relationOutput
			Function function_RF_F_1 = new Function(textbox_RF_F_1);
			Function function_RF_S_1 = new Function(textbox_RF_S_1);
			Operator op_RF_1 = new Operator(select_relation_RF_1);
			Operator relation_Op_RF_1 = new Operator(relationOp_RF_1);
			Relation relation_RF_1 = new Relation(function_RF_F_1,
					function_RF_S_1, op_RF_1, relation_Op_RF_1);
			relationOfInput.setRelation(relation_R_1);
			
			// 判断InputMr为几条
			// 为一条时：
			if (numOfRMr == 1) {
				System.out.println("InputMr只有一条");
				
			} else {
				for (int i = 2; i <= numOfRMr; i++) {
					// relation
					Function function_R_F = new Function(request
							.getParameter("textbox_R_F_" + String.valueOf(i)));
					Function function_R_S = new Function(request
							.getParameter("textbox_R_S_" + String.valueOf(i)));
					Operator op_R = new Operator(request
							.getParameter("select_relation_R_"
									+ String.valueOf(i)));
					//判断checkbox是否选中
					String relationOp_R = request.getParameter("relationOp_R_"
							+ String.valueOf(i));
					if (relationOp_R == null) {
						relationOp_R = "POS";
					}
					Operator relation_Op_R = new Operator(relationOp_R);
					Operator resOp_R = new Operator(request
							.getParameter("resOp_R_" + String.valueOf(i)));
					Relation relation_R = new Relation(function_R_F,
							function_R_S, op_R, relation_Op_R);
					
					//打印Input的第二条以上mr
					System.out.println("the "
							+ i
							+ " mr Of Input: "
							+ " "
							+ request.getParameter("resOp_R_"
									+ String.valueOf(i))
							+ " "
							+ relationOp_R
							+ " "
							+ request.getParameter("textbox_R_F_"
									+ String.valueOf(i))
							+ " "
							+ request.getParameter("select_relation_R_"
									+ String.valueOf(i))
							+ " "
							+ request.getParameter("textbox_R_S_"
									+ String.valueOf(i)));

					// opAndRelation
					OpAndRelation opAndRelation = new OpAndRelation(resOp_R,
							relation_R);
					
					listOpAndRelation_R.add(opAndRelation);					
				}
			}
			//设置relationInput中的list
			relationOfInput.setListOpAndRe(listOpAndRelation_R);

			relationOfOutput.setRelation(relation_RF_1);
			if (numOfRFMr == 1) {
				System.out.println("OutputMr只有一条");				
			} else {
				for (int i = 2; i <= numOfRFMr; i++) {

					// relation
					Function function_RF_F = new Function(request
							.getParameter("textbox_RF_F_" + String.valueOf(i)));
					Function function_RF_S = new Function(request
							.getParameter("textbox_RF_S_" + String.valueOf(i)));
					Operator op_RF = new Operator(request
							.getParameter("select_relation_RF_"
									+ String.valueOf(i)));
					//判断checkbox是否选中
					String relationOp_RF = request.getParameter("relationOp_RF_"
							+ String.valueOf(i));
					if (relationOp_RF == null) {
						relationOp_RF = "POS";
					}
					
					Operator relation_Op_RF = new Operator(relationOp_RF);
					Operator resOp_RF = new Operator(request
							.getParameter("resOp_RF_" + String.valueOf(i)));
					Relation relation_RF = new Relation(function_RF_F,
							function_RF_S, op_RF, relation_Op_RF);

					// opAndRelation
					OpAndRelation opAndRelation = new OpAndRelation(resOp_RF,
							relation_RF);

					listOpAndRelation_RF.add(opAndRelation);
				}

			}
			//设置relationOutput中的list
			relationOfOutput.setListOpAndRe(listOpAndRelation_RF);
			
			//设置metamorphicRelation
			metamorphicRelation.setRelationOfInput(relationOfInput);
			metamorphicRelation.setRelaitonOfOutput(relationOfOutput);
			
			mrList.add(metamorphicRelation);
			session.setAttribute("MR_List", mrList);

			
			
			String operationName = (String) session.getAttribute("operationName");

			// 根据OperationName进行跳转
			if (operationName.equals("transfer")) {
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("2.1.input_MR.jsp");
				dispatcher.forward(request, response);
			} else if (operationName.equals("rmbToGbbob")) {
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("2.1.input_MR_rmbToGbbob.jsp");
				dispatcher.forward(request, response);
			} else if (operationName.equals("queryQuakes")
					|| operationName.equals("matchQuakes")) {
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("2.1.input_MR_Quake.jsp");
				dispatcher.forward(request, response);
			} else if (operationName.equals("process")||operationName.equals("add")||operationName.endsWith("sub")) {
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("2.1.input_MR.jsp");
				dispatcher.forward(request, response);
			} else if (operationName.equals("checkQuantity")) {
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("2.1.input_MR.jsp");
				dispatcher.forward(request, response);
			} else {
				System.out.println("跳转错误！！！");
			}

		}

	}

	/**
	 * 处理get方法
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

}
