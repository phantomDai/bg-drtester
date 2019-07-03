package cn.edu.ustb.mt4ws.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.edu.ustb.mt4ws.configuration.Project;
import cn.edu.ustb.mt4ws.file.WriteXML;
import cn.edu.ustb.mt4ws.javabean.MetamorphicRelation;


public class MrsToXML extends HttpServlet{

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("正在产生XML文件……");
		HttpSession session = request.getSession(false);
		List<MetamorphicRelation> mrList=null;
		Project project = (Project) session.getAttribute("project");
		String operationName = (String) session.getAttribute("operationName");
		
		String filePath = project.getWorkspace().getPath()+ project.getProjectName();
		File file = new File(filePath);
		//判断文件夹是否存在，不存在则新建
		if(!file.exists()){
			file.mkdir();
		}		
		String mrdlLoc = filePath + "/mrdl_" + operationName + ".xml";// MRDL文件存放在服务器中的位置

		System.out.println("MRDL文件存放在服务器中的位置:" + mrdlLoc);
		
		// 判断list是否为空
		if ((List<MetamorphicRelation>) session.getAttribute("MR_List") == null) {
			System.out.println("******mrList 为空，请先输入MR******");			
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("2.1.input_MR.jsp");
			dispatcher.forward(request, response);
			
		} else {
			mrList = (List<MetamorphicRelation>) session
					.getAttribute("MR_List");
			//写成xml文件
			
			FileOutputStream fout = new FileOutputStream(mrdlLoc);
			OutputStreamWriter writer=new OutputStreamWriter(fout,"utf-8");
			
			//TODO
			WriteXML writeXML = new WriteXML();
			writeXML.writeMrList(writer, mrList);
			
			writer.close();
			fout.close();
			System.out.println("已经产生XML文件……");
			
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("3.1.tcg_method.jsp");
			dispatcher.forward(request, response);
		}
		
		
		
		
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	
	
}
