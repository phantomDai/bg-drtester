package cn.edu.ustb.mt4ws.mr.jsp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.edu.ustb.mt4ws.configuration.Project;
import cn.edu.ustb.mt4ws.mr.model.MetamorphicRelation;

public class GenerateXML extends HttpServlet {

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("正在产生XML文件……");
		AddMrs.count = 0;
		HttpSession session = request.getSession(false);
		if (session == null) {
			// TODO
		}
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");
		if (mrList == null || mrList.size() == 0) {
			// TODO
		}
		Project project = (Project) session.getAttribute("project");
		if (project == null) {
			// TODO
		}
		String operationName = (String) session.getAttribute("operationName");
		if(operationName==null){
			System.out.println("operationName is null");
			//TODO
		}else if(operationName.equals("transfer")){
			//operationName 为transfer时，在各个inputbeanSet
			//TODO			
		}else if(operationName.equals("rmbToGbbob")){
			//rmb
			//TODO		
		}else if(operationName.equals("matchQuakes")){
			//quake
			//TODO		
		}

		/*String mrdlLoc = project.getWorkspace().getPath()
				+ project.getProjectName() + "/mrdl_" + operationName + ".xml";// MRDL文件存放在服务器中的位置
*/		
		String filePath = project.getWorkspace().getPath()+ project.getProjectName();
		File file = new File(filePath);
		//判断文件夹是否存在，不存在则新建
		if(!file.exists()){
			file.mkdir();
		}
		
		String mrdlLoc = filePath + "/mrdl_" + operationName + ".xml";// MRDL文件存放在服务器中的位置

		System.out.println("MRDL文件存放在服务器中的位置:" + mrdlLoc);
		
		FileOutputStream fout = new FileOutputStream(mrdlLoc);
		OutputStreamWriter writer=new OutputStreamWriter(fout,"utf-8");
		//PrintWriter writer = new PrintWriter(fout);
		new ExportXML().exportXML(writer, mrList);
		writer.close();
		fout.close();
		System.out.println("已经产生XML文件……");
		// session.removeAttribute("mrsset"); //删除蜕变关系
		// System.out.println("session.getAttribute():"+session.getAttribute("mrsset"));
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("3.1.tcg_method.jsp");
		dispatcher.forward(request, response);

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

}
