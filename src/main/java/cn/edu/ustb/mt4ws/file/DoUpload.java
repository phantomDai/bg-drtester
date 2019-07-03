package cn.edu.ustb.mt4ws.file;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.edu.ustb.mt4ws.configuration.Project;
import cn.edu.ustb.mt4ws.javabean.MetamorphicRelation;

public class DoUpload extends HttpServlet {
	/**
	 * 构造函数
	 */
	public DoUpload() {

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

		String xmlPath = filePath + "/mrdl_" + operationName + ".xml";// MRDL文件存放在服务器中的位置
		String xsdPath = filePath + "/MRDL.xsd";

		System.out.println("MRDL文件存放在服务器中的位置:" + xmlPath);

		ValidatorXML validatorXML = new ValidatorXML();
		DomXML domXML = new DomXML();

		try {
			upload(request, response);
			// 判断xml文件是否符合xsd规格
			if (validatorXML.validateXml(xsdPath, xmlPath)) {
				System.out.println("xml文件符合xsd规格");

				List<MetamorphicRelation> MRs = domXML.parserMrSet(xmlPath);
				session.setAttribute("MR_List", MRs);

				RequestDispatcher dispatcher = request
						.getRequestDispatcher("3.1.tcg_method.jsp");
				//dispatcher.forward(request, response);

			} else {
				System.out.println("xml文件不符合xsd规格，请重新上传");
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

	/**
	 * 上传文件
	 * 
	 * @param request
	 * @param response
	 */
	public void upload(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		Project project = (Project) session.getAttribute("project");

		int MAX_SIZE = 102400 * 102400;
		// 创建根路径的保存变量
		String rootPath;
		// 声明文件读入类
		DataInputStream in = null;
		FileOutputStream fileOut = null;
		// 取得客户端的网络地址
		String remoteAddr = request.getRemoteAddr();
		// 获得服务器的名字
		String serverName = request.getServerName();

		// 取得互联网程序的绝对地址
		String realPath = request.getRealPath(serverName);
		realPath = realPath.substring(0, realPath.lastIndexOf("\\"));
		// 创建文件的保存目录

		rootPath = realPath + "\\" + project.getProjectName() + "\\";
		System.out.println("rootPath：" + rootPath);
		File file1 = new File(rootPath);
		// 判断文件夹是否存在，不存在则新建
		if (!file1.exists()) {
			file1.mkdir();
		}

		// 取得客户端上传的数据类型
		String contentType = request.getContentType();
		try {
			if (contentType.indexOf("multipart/form-data") >= 0) {
				// 读入上传的数据
				in = new DataInputStream(request.getInputStream());
				int formDataLength = request.getContentLength();
				if (formDataLength > MAX_SIZE) {
					System.out.println("上传的文件字节数不可以超过" + MAX_SIZE);
					return;
				}
				// 保存上传文件的数据
				byte dataBytes[] = new byte[formDataLength];

				int byteRead = 0;
				int totalBytesRead = 0;
				// 上传的数据保存在byte数组
				while (totalBytesRead < formDataLength) {
					byteRead = in.read(dataBytes, totalBytesRead,
							formDataLength);
					totalBytesRead += byteRead;
				}
				// 根据byte数组创建字符串
				String file = new String(dataBytes);
				// out.println(file);
				// 取得上传的数据的文件名
				String saveFile = file
						.substring(file.indexOf("filename=\"") + 10);
				saveFile = saveFile.substring(0, saveFile.indexOf("\n"));
				saveFile = saveFile.substring(saveFile.lastIndexOf("\\") + 1,
						saveFile.indexOf("\""));
				int lastIndex = contentType.lastIndexOf("=");
				// 取得数据的分隔字符串
				String boundary = contentType.substring(lastIndex + 1,
						contentType.length());
				// 创建保存路径的文件名
				String fileName = rootPath + saveFile;
				// out.print(fileName);
				int pos;
				pos = file.indexOf("filename=\"");
				pos = file.indexOf("\n", pos) + 1;
				pos = file.indexOf("\n", pos) + 1;
				pos = file.indexOf("\n", pos) + 1;
				int boundaryLocation = file.indexOf(boundary, pos) - 4;
				// out.println(boundaryLocation);
				// 取得文件数据的开始的位置
				int startPos = ((file.substring(0, pos)).getBytes()).length;
				// out.println(startPos);
				// 取得文件数据的结束的位置
				int endPos = ((file.substring(0, boundaryLocation)).getBytes()).length;
				// out.println(endPos);
				// 检查上载文件是否存在
				File checkFile = new File(fileName);
				if (checkFile.exists()) {
					System.out.println(saveFile + "文件已经存在");
				}
				// 检查上载文件的目录是否存在
				File fileDir = new File(rootPath);
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}
				// 创建文件的写出类
				fileOut = new FileOutputStream(fileName);
				// 保存文件的数据
				fileOut.write(dataBytes, startPos, (endPos - startPos));
				fileOut.close();
				System.out.println(saveFile + "文件成功上载");
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("3.1.tcg_method.jsp");
				dispatcher.forward(request, response);

			} else {
				String content = request.getContentType();
				System.out.println("上传的数据类型不是multipart/form-data");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
