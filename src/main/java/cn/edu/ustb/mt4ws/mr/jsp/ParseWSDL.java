package cn.edu.ustb.mt4ws.mr.jsp;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.edu.ustb.mt4ws.configuration.Project;
import cn.edu.ustb.mt4ws.configuration.Workspace;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.wsdl.parser.WsdlReader11;

//import cn.edu.ustb.mt4ws.configuration.Project;
//import cn.edu.ustb.mt4ws.configuration.Workspace;
//import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
//import cn.edu.ustb.mt4ws.wsdl.parser.WsdlReader11;

public class ParseWSDL extends HttpServlet {

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
		HttpSession session = request.getSession(true);
		if(!session.isNew())//清空session
		{
			session.invalidate();
			session = request.getSession(true);
			
		}
		System.out.println("开始测试……解析WSDL文件");
		String wsdlurl=request.getParameter("textbox_wsdlURI");
		System.out.println("wsdlURI为："+wsdlurl);
	    
		WsdlReader11 wsdlreader11=new WsdlReader11();
		
		Map<String, WsdlOperationFormat> operationMap=wsdlreader11.parseWSDL(wsdlurl);
		Set<String> opList = operationMap.keySet();
		
		session.setAttribute("operation_map", operationMap);
		session.setAttribute("operationList", opList);
		
		Project project = new Project();
		Workspace workspace = new Workspace();
		String servletPath = request.getSession().getServletContext().getRealPath("/").replace('\\', '/');
        System.out.println("The path of Servlet："+servletPath);
        workspace.setPath(servletPath);
		System.out.println("The path of workspace:" + workspace.getPath());
        
		project.setWSDLUri(wsdlurl);
		project.setProjectName(wsdlurl.substring(wsdlurl.lastIndexOf('/')+1,wsdlurl.lastIndexOf('?')));
		workspace.setUserName("admin");
		project.setWorkspace(workspace);
		
		session.setAttribute("project", project);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("1.operationSelect.jsp");
        dispatcher.forward(request, response);	
		
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	
}
