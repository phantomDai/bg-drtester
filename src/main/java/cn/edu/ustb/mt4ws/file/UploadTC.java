package cn.edu.ustb.mt4ws.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.edu.ustb.mt4ws.configuration.Project;
import cn.edu.ustb.mt4ws.mr.model.MetamorphicRelation;

public class UploadTC extends HttpServlet{
	/*
	 * 构造函数
	 */
	public UploadTC(){
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("进入servlet……");
		HttpSession session = request.getSession(false);
		Project project = (Project) session.getAttribute("project");
		String operationName = (String) session.getAttribute("operationName");

		String filePath = project.getWorkspace().getPath()
				+ project.getProjectName();
		File file = new File(filePath);
		// 判断文件夹是否存在，不存在则新建
		if (!file.exists()) {
			file.mkdir();
		}

		String xmlPath = filePath + "/ATMTestCaseSet.xml";// MRDL文件存放在服务器中的位置
		String xsdPath = filePath + "/ATMTestCase.xsd";

		System.out.println("MRDL文件存放在服务器中的位置:" + xmlPath);

		ValidatorXML validatorXML = new ValidatorXML();
		DomXML domXML = new DomXML();

		try {
			DoUpload doUpload = new DoUpload();
			doUpload.upload(request, response);
			// 判断xml文件是否符合xsd规格
			if (validatorXML.validateXml(xsdPath, xmlPath)) {
				System.out.println("xml文件符合xsd规格");

				RequestDispatcher dispatcher = request
						.getRequestDispatcher("3.1.tcg_method.jsp");
				//dispatcher.forward(request, response);

			} else {
				System.out.println("xml文件不符合xsd规格，请重新上传");
				//若不符合xsd规格，则删除该文件
				File fileTC=new File(xmlPath);
				fileTC.delete();
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("2.1.input_MR.jsp");
				dispatcher.forward(request, response);
			}

		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		doPost(request, response);
	}
	

}
