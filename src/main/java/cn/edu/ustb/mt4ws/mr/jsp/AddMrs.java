package cn.edu.ustb.mt4ws.mr.jsp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.edu.ustb.mt4ws.mr.model.Function;
import cn.edu.ustb.mt4ws.mr.model.MetamorphicRelation;
import cn.edu.ustb.mt4ws.mr.model.Operator;
import cn.edu.ustb.mt4ws.mr.model.Relation;
import cn.edu.ustb.mt4ws.mr.model.RelationOfInput;
import cn.edu.ustb.mt4ws.mr.model.RelationOfOutput;

@SuppressWarnings("serial")
public class AddMrs extends HttpServlet {

	public static int count;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		count = 0;

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		
		HttpSession session = request.getSession(true);
		System.out.println("链接成功……");
		String R_F_1 = request.getParameter("textbox_R_F_1");

		String relation_R_1 = request.getParameter("select_relation_R_1");
		String R_S_1 = request.getParameter("textbox_R_S_1");
		String RF_F_1 = request.getParameter("textbox_RF_F_1");
		String relation_R_4 = request.getParameter("select_relation_R_4");
		String RF_S_1 = request.getParameter("textbox_RF_S_1");

		String R_F_2 = request.getParameter("textbox_R_F_2");
		String relation_R_2 = request.getParameter("select_relation_R_2");
		String R_S_2 = request.getParameter("textbox_R_S_2");
		String RF_F_2 = request.getParameter("textbox_RF_F_2");
		String relation_R_5 = request.getParameter("select_relation_R_5");
		String RF_S_2 = request.getParameter("textbox_RF_S_2");

		String R_F_3 = request.getParameter("textbox_R_F_3");
		String relation_R_3 = request.getParameter("select_relation_R_3");
		String R_S_3 = request.getParameter("textbox_R_S_3");
		String RF_F_3 = request.getParameter("textbox_RF_F_3");
		String relation_R_6 = request.getParameter("select_relation_R_6");
		String RF_S_3 = request.getParameter("textbox_RF_S_3");

		if (R_F_1 == "" || R_S_1 == "" || RF_F_1 == "" || RF_S_1 == "") {
			System.out.println("Input1或Output1没有输入……");
		} else {

			// relationofInput��

			System.out.println("R_F_1 + op + R_S_1:" + R_F_1 + relation_R_1
					+ R_S_1);
			Function functionfollowup_input1 = new Function(R_F_1);
			Function functionsource_input1 = new Function(R_S_1);
			Operator op_input1 = new Operator(relation_R_1);
			Relation relation_input1 = new Relation(functionfollowup_input1,
					op_input1, functionsource_input1);
			List<Relation> relationofInput = new Vector<Relation>();
			relationofInput.add(relation_input1);
			// String relationofinput = null;
			// session.setAttribute(relationofinput, relationofInput);
			if (R_F_2 != "" && R_S_2 != "") {
				Function functionfollowup_input2 = new Function(R_F_2);
				Function functionsource_input2 = new Function(R_S_2);
				Operator op_input2 = new Operator(relation_R_2);
				Relation relation_input2 = new Relation(
						functionfollowup_input2, op_input2,
						functionsource_input2);
				System.out.println("R_F_2 + op2 + R_S_2:" + R_F_2
						+ relation_R_2 + R_S_2);
				relationofInput.add(relation_input2);

				if (R_F_3 != "" && R_S_3 != "") {
					Function functionfollowup_input3 = new Function(R_F_3);
					Function functionsource_input3 = new Function(R_S_3);
					Operator op_input3 = new Operator(relation_R_3);
					Relation relation_input3 = new Relation(
							functionfollowup_input3, op_input3,
							functionsource_input3);
					System.out.println("R_F_3 + op3 + R_S_3:" + R_F_3
							+ relation_R_3 + R_S_3);
					relationofInput.add(relation_input3);
				} else {
					System.out.println("Input3没有输入……");
				}

			} else {
				System.out.println("Input2没有输入……");
			}

			RelationOfInput RelationofInput = new RelationOfInput();
			RelationofInput.setRelationOfInput(relationofInput);

			// relationofOutput:
			System.out.println("RF_F_1 + op4 + RF_S_1:" + RF_F_1 + relation_R_4
					+ RF_S_1);
			Function functionfollowup_output1 = new Function(RF_F_1);
			Function functionsource_output1 = new Function(RF_S_1);
			Operator op_output1 = new Operator(relation_R_4);
			Relation relation_output1 = new Relation(functionfollowup_output1,
					op_output1, functionsource_output1);
			List<Relation> relationofOutput = new Vector<Relation>();
			// String relationofoutput = null;
			// session.setAttribute(relationofoutput, relationofOutput);
			relationofOutput.add(relation_output1);
			if (RF_F_2 != "" && RF_S_2 != "") {
				Function functionfollowup_output2 = new Function(RF_F_2);
				Function functionsource_output2 = new Function(RF_S_2);
				Operator op_output2 = new Operator(relation_R_5);
				Relation relation_output2 = new Relation(
						functionfollowup_output2, op_output2,
						functionsource_output2);
				relationofOutput.add(relation_output2);
				System.out.println("RF_F_2 + op5 + RF_S_2:" + RF_F_2
						+ relation_R_5 + RF_S_2);

				if (RF_F_3 != "" && RF_S_3 != "") {
					Function functionfollowup_output3 = new Function(RF_F_3);
					Function functionsource_output3 = new Function(RF_S_3);
					Operator op_output3 = new Operator(relation_R_6);
					Relation relation_output3 = new Relation(
							functionfollowup_output3, op_output3,
							functionsource_output3);
					relationofOutput.add(relation_output3);
					System.out.println("RF_F_3 + op6 + RF_S_3:" + RF_F_3
							+ relation_R_6 + RF_S_3);

				} else {
					System.out.println("Output3没有输入……");
				}
			} else {
				System.out.println("Output2 is empty……");
			}

			RelationOfOutput RelationofOutput = new RelationOfOutput();
			RelationofOutput.setRelationOfOutput(relationofOutput);

			// MetamorphicRelation:
			MetamorphicRelation MR = new MetamorphicRelation(RelationofInput,
					RelationofOutput);
			List<MetamorphicRelation> MRs = null;

			if (count == 0) {
				System.out.println("First MR……");
				MRs = new Vector<MetamorphicRelation>();
				MRs.add(MR);
				session.setAttribute("MR_List", MRs);

			} else {
				MRs = (List<MetamorphicRelation>) session
						.getAttribute("MR_List");
				MRs.add(MR);
				System.out.println("MR数为：" + MRs.size());
				session.setAttribute("MR_List", MRs);
			}

		}

		System.out.println("*********");
		count++;
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
		} else if (operationName.equals("queryQuakes")||operationName.equals("matchQuakes")) {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("2.1.input_MR_Quake.jsp");
			dispatcher.forward(request, response);
		} else {
			System.out.println("跳转错误！！！");
		}

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

}
