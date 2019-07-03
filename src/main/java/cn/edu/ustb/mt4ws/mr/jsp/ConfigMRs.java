package cn.edu.ustb.mt4ws.mr.jsp;

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

public class ConfigMRs extends HttpServlet {
	public void init(ServletConfig config) throws ServletException {
		super.init(config);	
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		System.out.println("正在配置选中的MR……");
		String[] selected_mr = request.getParameterValues("mr_select_config");
		List<String> str_selected_mr=new ArrayList<String>();
		List<Integer> selected_mr_mrid_list= new ArrayList<Integer>();
		if(selected_mr==null){
			System.out.println("没有选中MR，请重新选中");
		}
		else{
        for(int i=0;i<selected_mr.length;i++){
        	System.out.println("选中的MR为："+selected_mr[i]); 
        	str_selected_mr.add(selected_mr[i]);
        	selected_mr_mrid_list.add(Integer.parseInt(selected_mr[i].substring(2)));
        }
		session.setAttribute("str_selected_mr", str_selected_mr);
		session.setAttribute("mrIDs",selected_mr_mrid_list);
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("configuration.jsp");
        dispatcher.forward(request, response);	
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
       doPost(request, response);
       }

}
