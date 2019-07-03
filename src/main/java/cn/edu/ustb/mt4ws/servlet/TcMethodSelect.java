package cn.edu.ustb.mt4ws.servlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cn.edu.ustb.mt4ws.configuration.Project;
import cn.edu.ustb.mt4ws.file.DomATMTC;
import cn.edu.ustb.mt4ws.mr.jsp.QuakeInputBean;
import cn.edu.ustb.mt4ws.mr.jsp.QuakeInputBeanSet;
import cn.edu.ustb.mt4ws.mr.jsp.RMBInputBean;
import cn.edu.ustb.mt4ws.mr.jsp.RMBInputBeanSet;
import cn.edu.ustb.mt4ws.mr.jsp.TransferInputBean;
import cn.edu.ustb.mt4ws.mr.jsp.TransferInputBeanSet;
import cn.edu.ustb.mt4ws.javabean.*;

import cn.edu.ustb.mt4ws.tcg.SqlUtils;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.tcg.XmlTestCase;
import cn.edu.ustb.mt4ws.tcg.XmlTestCaseGenerator;
import cn.edu.ustb.mt4ws.tcg.XmlVariable;
import cn.edu.ustb.mt4ws.testcase.Method;

/**
 * 根据所选的method，进行相应操作
 * 
 * @author qing.wen
 * 
 */
public class TcMethodSelect extends HttpServlet {

	TransferInputBeanSet transferinputset = null;
	Vector<TransferInputBean> transferInputList = null;

	QuakeInputBeanSet quakeInputSet = null;
	Vector<QuakeInputBean> quakeInputList = null;

	RMBInputBeanSet rmbInputSet = null;
	Vector<RMBInputBean> rmbInputList = null;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		// 选择TC产生的method
		String methodType = request.getParameter("RadioGroup1");
		String operationName = (String) session.getAttribute("operationName");

		WsdlOperationFormat opFormat = ((Map<String, WsdlOperationFormat>) session
				.getAttribute("operation_map")).get(operationName);
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");

		Project project = (Project) session.getAttribute("project");
		String projectPath = project.getWorkspace().getPath()
				+ project.getProjectName();
		String filePath = projectPath + "/ATMTestCaseSet.xml";
		// 上传过来的testcase文件存放在服务器中的位置
		System.out.println("Testcase文件存放在服务器中的位置:" + filePath);

		// 自动生成TC
		if (methodType.equals("auto")) {
			String numStr = request.getParameter("num_text");
			session.setAttribute("testcase_num", numStr);
			Method method=new Method();
			method.autoMethod(request, response, session, operationName, opFormat, mrList);
			
			/*autoMethodIter(request, response, session, operationName, opFormat,
					mrList);*/

		} else if (methodType.equals("manual")) {
			// 手动输入
			manualMethod(response, operationName);

		} else if (methodType.equals("import")) {
			// 文件导入
			importMethod(response, operationName, session, filePath, mrList,opFormat);
		}

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * 选择manual时 ，根据operationName的不同进行操作
	 * 
	 * @param request
	 * @param operationName
	 * @throws IOException
	 */
	private void manualMethod(HttpServletResponse response, String operationName)
			throws IOException {
		if (operationName.equals("transfer")) {
			response.sendRedirect("3.2.fill_tc_manualy.jsp");// transfer跳转的页面
		} else if (operationName.equals("process")) {
			response.sendRedirect("3.2.fill_tc_manualy_process.jsp");// Caculator跳转的页面
		} else if (operationName.equals("add")) {
			response.sendRedirect("3.2.fill_tc_manualy_add.jsp");// add跳转的页面
		}else if (operationName.equals("sub")) {
			response.sendRedirect("3.2.fill_tc_manualy_sub.jsp");// sub跳转的页面
		} else if (operationName.equals("checkQuantity")) {
			response.sendRedirect("3.2.fill_tc_manualy_checkQuantity.jsp");// Caculator跳转的页面
		} else if (operationName.equals("rmbToGbbob")) {
			response.sendRedirect("3.2.fill_tc_manualy_rmb.jsp");// 人民币转换跳转的页面
		} else if (operationName.equals("queryQuakes")
				|| operationName.equals("matchQuakes")) {
			response.sendRedirect("3.2.fill_tc_manualy_quake.jsp");// 地震查询跳转的页面
		} else {
			System.out.println("跳转错误！！！");
			response.sendRedirect("3.1.tcg_method.jsp");
		}

	}

	/**
	 * 选择导入文件时，需要进行的跳转
	 * 
	 * @param response
	 * @param operationName
	 * @param session
	 * @param filePath
	 * @param List
	 *            <MetamorphicRelation>
	 * @throws IOException
	 */
	private void importMethod(HttpServletResponse response,
			String operationName, HttpSession session, String filePath,
			List<MetamorphicRelation> mrList,WsdlOperationFormat opFormat) throws IOException {

		Iterator<MetamorphicRelation> iterMR = mrList.iterator();

		if (operationName.equals("transfer")) {
			int mrID = 0;
			int id = 1;
			List<TCTransfer> tcsTransferList = null;
			// tcsList以字符串形式存储所有的testcase，每组testcase存入List<String>中
			List<List<String>> tcsList = null;
			if (session.getAttribute("tcsTransferList") == null) {
				System.out.println("第一个tc");
				tcsTransferList = new ArrayList<TCTransfer>();
				tcsList = new ArrayList<List<String>>();
			} else {
				tcsTransferList = (List<TCTransfer>) session
						.getAttribute("tcsTransferList");
				tcsList = (List<List<String>>) session.getAttribute("tcsList");
				id = tcsTransferList.size() + 1;
			}

			DomATMTC domTC = new DomATMTC();
			// 从待解析的TestCase文件中提取List<TransferInputBean>
			try {
				// 初始的存储testcase的List
				File file = new File(filePath);
				if (!file.exists()) {
					System.out.println("上传文件未成功!!!请重新上传!!!");
				} else {
					List<TCTransfer> transferInputList_1 = domTC
							.parseXML(filePath);
					
					while (iterMR.hasNext()) {
						mrID++;
						// 对于每个mr，进行testcase赋值
						MetamorphicRelation mr = iterMR.next();

						for (int i = 0; i < transferInputList_1.size(); i++, id++) {
							TCTransfer testcase = transferInputList_1.get(i);
							testcase.setMrID(mrID);
							testcase.setId(id);
							tcsTransferList.add(testcase);
						
							//原始测试用例
							List<String> tcList = new ArrayList<String>();
							tcList.add(String.valueOf(id));
							tcList.add(String.valueOf(mrID));
							tcList.add(testcase.getAccountFrom());
							tcList.add(testcase.getAccountTo());
							tcList.add(testcase.getMode());
							tcList.add(testcase.getAmount());
							tcsList.add(tcList);
			
						}
			
					}
					
					List<TCTransferF> tcsTransferFList = AddToDB
							.getFollowUpList(tcsTransferList, opFormat, mrList,
									session);
					session.setAttribute("tcsTransferFList", tcsTransferFList);
					
					session.setAttribute("tcsList", tcsList);
					session.setAttribute("tcsTransferList", tcsTransferList);
					

					// 添加到数据库中
					SqlUtils sqlUtils = SqlUtils.getInstance();
					sqlUtils.insertToTransferS(tcsTransferList);
					sqlUtils.insertToTransferF(tcsTransferFList);
				}
				response.sendRedirect("4.start_test.jsp");// transfer跳转的页面

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (operationName.equals("rmbToGbbob")) {
			response.sendRedirect("");// 人民币转换跳转的页面
		} else if (operationName.equals("queryQuakes")
				|| operationName.equals("matchQuakes")) {
			response.sendRedirect("");// 地震查询跳转的页面
		} else {
			System.out.println("跳转错误！！！");
		}

	}

	/**
	 * 随机自动生成原始测试用例
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param operationName
	 * @param opFormat
	 * @param mrList
	 * @throws IOException
	 */
	private void autoMethod(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			String operationName, WsdlOperationFormat opFormat,
			List<MetamorphicRelation> mrList) throws IOException {
		Iterator<MetamorphicRelation> iterMR = mrList.iterator();

		String numStr = request.getParameter("num_text");
		int num = Integer.parseInt(numStr);

		if (((String) session.getAttribute("operationName"))// transfer业务自动生成测试用例
				.equals("transfer") && num > 0) {
			int mrID = 0;
			int id = 1;
			List<TCTransfer> tcsTransferList = null;
			// tcsList以字符串形式存储所有的testcase，每组testcase存入List<String>中
			List<List<String>> tcsList = null;
			if (session.getAttribute("tcsTransferList") == null) {
				System.out.println("第一个tc");
				tcsTransferList = new ArrayList<TCTransfer>();
				tcsList = new ArrayList<List<String>>();
			} else {
				tcsTransferList = (List<TCTransfer>) session
						.getAttribute("tcsTransferList");
				tcsList = (List<List<String>>) session.getAttribute("tcsList");
				id = tcsTransferList.size() + 1;
			}
			XmlTestCaseGenerator tcGen = new XmlTestCaseGenerator();
			while (iterMR.hasNext()) {
				mrID++;
				MetamorphicRelation mr = iterMR.next();
				for (int j = 0; j < num; j++) {
					// 先从mt4ws生成xmltestcase，再转换成TransferInputBean
					XmlTestCase newTC = tcGen.genWithLimitation(operationName,
							opFormat.getInput().format,
							mr.getRelationOfInput(), mrID);

					Set<Map.Entry<XmlVariable, String>> entry = newTC
							.getInput().entrySet();
					Iterator<Map.Entry<XmlVariable, String>> iter = entry
							.iterator();
					Map.Entry<XmlVariable, String> myentry = null;
					int i = 0;
					String values[] = new String[entry.size()];
					while (iter.hasNext()) {
						myentry = iter.next();
						values[i] = myentry.getValue();
						i++;
					}
					tcsTransferList.add(new TCTransfer(id, mrID, values[0],
							values[1], values[2], values[3]));

					// 在jsp界面中显示
					List<String> tcList = new ArrayList<String>();
					tcList.add(String.valueOf(id));
					tcList.add(String.valueOf(mrID));
					tcList.add(values[0]);
					tcList.add(values[1]);
					tcList.add(values[2]);
					tcList.add(values[3]);
					tcsList.add(tcList);
					id++;
				}
			}

			session.setAttribute("tcsList", tcsList);
			session.setAttribute("tcsTransferList", tcsTransferList);

			// 添加到数据库中
			AddToDB.insertTransferDB(tcsTransferList);

			response.sendRedirect("4.start_test.jsp");

		} else if (((String) session.getAttribute("operationName"))// 地震服务随机生成原始测试用例测试
				.equals("matchQuakes") && num > 0) {
			// TODO
		} else if (((String) session.getAttribute("operationName"))// RMB转换服务随机生成原始测试用例测试
				.equals("rmbToGbbob") && num > 0) {
			// TODO
		} else {
			System.out.println("跳转错误！！！");
		}
	}

	/**
	 * 迭代蜕变测试方法 自动生成原始测试用例
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param operationName
	 * @param opFormat
	 * @param mrList
	 * @throws IOException
	 */
	private void autoMethodIter(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			String operationName, WsdlOperationFormat opFormat,
			List<MetamorphicRelation> mrList) throws IOException {

		

		String numStr = request.getParameter("num_text");
		int num = Integer.parseInt(numStr);

		if (((String) session.getAttribute("operationName"))// transfer业务自动生成测试用例
				.equals("transfer") && num > 0) {
			int mrID = 0;
			int id = 1;
			List<TCTransfer> tcsTransferList = null;
			List<TCTransferF> tcsTransferFList= null;
			// tcsList以字符串形式存储所有的testcase，每组testcase存入List<String>中
			List<List<String>> tcsListS = null;// 原始测试用例
			List<List<String>> tcsListF = null;//衍生测试用例
			if (session.getAttribute("tcsTransferList") == null) {
				System.out.println("第一个tc");
				tcsTransferList = new ArrayList<TCTransfer>();
				tcsTransferFList = new ArrayList<TCTransferF>();
				tcsListS = new ArrayList<List<String>>();
				// TODO 用来存储衍射测试用例FollowUp
				tcsListF = new ArrayList<List<String>>();
			} else {
				tcsTransferList = (List<TCTransfer>) session
						.getAttribute("tcsTransferList");
				tcsTransferFList = (List<TCTransferF>) session
						.getAttribute("tcsTransferFList");
	
				tcsListS = (List<List<String>>) session.getAttribute("tcsList");
				// TODO
				tcsListF = (List<List<String>>) session
						.getAttribute("tcsFList");
				id = tcsTransferList.size() + 1;
			}

			// 对于输入的num，则每条mr都需要产生num个测试用例（原始和衍生）
			XmlTestCaseGenerator tcGen = new XmlTestCaseGenerator();
			for (int mrCount = 0; mrCount < num; mrCount++) {
				TCTransfer tempTC=null;
				TCTransferF tempTCF=null;
				mrID=0;
				Iterator<MetamorphicRelation> iterMR = mrList.iterator();
				// 遍历每个mr，生成相应的followUp
				while (iterMR.hasNext()) {
					
					mrID++;
					
					MetamorphicRelation mr = iterMR.next();
					// 产生第一个Source case
					if (mrID == 1 && mrCount == 0) {
						XmlTestCase newTC = tcGen.genWithLimitation(
								operationName, opFormat.getInput().format, mr
										.getRelationOfInput(), mrID);
						Set<Map.Entry<XmlVariable, String>> entry = newTC
								.getInput().entrySet();
						Iterator<Map.Entry<XmlVariable, String>> iter = entry
								.iterator();
						Map.Entry<XmlVariable, String> myentry = null;
						int i = 0;
						String values[] = new String[entry.size()];
						while (iter.hasNext()) {
							myentry = iter.next();
							values[i] = myentry.getValue();
							i++;
						}
						TCTransfer tcTransfer=new TCTransfer(id, mrID, values[0],
								values[1], values[2], values[3]);
						tempTC=new TCTransfer(id, mrID, values[0],
								values[1], values[2], values[3]);
						tcsTransferList.add(tcTransfer);
						
						//产生衍生测试用例
						//TODO
						TCTransferF tcTransferF=TCTransferF.generateFollowTC(tcTransfer,opFormat,mr);
						tempTCF=tcTransferF;
						tcsTransferFList.add(tcTransferF);						
					
					}else if(mrID==1){
						//原始测试用例
						TCTransferF tcTransferF=tcsTransferFList.get(mrCount-1);
						/*
						 * TODO 验证衍生测试用例是否满足输入要求，不满足则舍去
						 */
						TCTransfer tcTransfer=tcTransferF.tranferToS();
						
						tcTransfer.setId(id);
						tcTransfer.setMrID(mrID);
						tempTC = new TCTransfer(tcTransfer.getId(), tcTransfer
								.getMrID(), tcTransfer.getAccountFrom(),
								tcTransfer.getAccountTo(),
								tcTransfer.getMode(), tcTransfer.getAmount());
						//tempTC=tcTransfer;
						tcsTransferList.add(tcTransfer);
						//产生衍生测试用例
						tcTransferF=TCTransferF.generateFollowTC(tcTransfer,opFormat,mr);
						tempTCF=tcTransferF;
						tcsTransferFList.add(tcTransferF);
				
					}else {
						//原始测试用例
						//重新设置mrID
						tempTC.setMrID(mrID);
						tempTC.setId(id);
						TCTransfer tcTransfer = new TCTransfer(tempTC.getId(),
								tempTC.getMrID(), tempTC.getAccountFrom(),
								tempTC.getAccountTo(), tempTC.getMode(), tempTC
										.getAmount());
						tcsTransferList.add(tcTransfer);
						// 产生衍生测试用例
						TCTransferF tcTransferF = TCTransferF.generateFollowTC(
								tcTransfer, opFormat, mr);
						tempTCF = tcTransferF;
						tcsTransferFList.add(tcTransferF);

					}
					
					
					// 在jsp界面中显示
					List<String> tcList = new ArrayList<String>();
					tcList.add(String.valueOf(id));
					tcList.add(String.valueOf(mrID));
					tcList.add(tempTC.getAccountFrom());
					tcList.add(tempTC.getAccountTo());
					tcList.add(tempTC.getMode());
					tcList.add(tempTC.getAmount());
					tcsListS.add(tcList);
					//添加衍生测试用例
					List<String> tcListF = new ArrayList<String>();
					tcListF.add(String.valueOf(tempTCF.getsID()));//有待改进
					tcListF.add(String.valueOf(mrID));
					tcListF.add(tempTCF.getAccountFrom());
					tcListF.add(tempTCF.getAccountTo());
					tcListF.add(tempTCF.getMode());
					tcListF.add(tempTCF.getAmount());
					tcListF.add(String.valueOf(tempTCF.getsID()));
					tcsListF.add(tcListF);
						
					id++;
				}

			}

			//存储在session中
			session.setAttribute("tcsList", tcsListS);
			session.setAttribute("tcsTransferList", tcsTransferList);
			session.setAttribute("tcsFList", tcsListF);
			session.setAttribute("tcsTransferFList", tcsTransferFList);
			
			// 添加到数据库中
			SqlUtils sqlUtils=SqlUtils.getInstance();
			sqlUtils.insertToTransferS(tcsTransferList);
			sqlUtils.insertToTransferF(tcsTransferFList);

			response.sendRedirect("4.start_test.jsp");

		} else if (((String) session.getAttribute("operationName"))// 地震服务随机生成原始测试用例测试
				.equals("matchQuakes") && num > 0) {
			// TODO
		} else if (((String) session.getAttribute("operationName"))// RMB转换服务随机生成原始测试用例测试
				.equals("rmbToGbbob") && num > 0) {
			// TODO
		} else {
			System.out.println("跳转错误！！！");
		}

	}

	/**
	 * 随机产生一个在min和max之间的float数
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	@SuppressWarnings("unused")
	private String randomFloat(String min, String max) {
		float minflo = Float.parseFloat(min);
		float maxflo = Float.parseFloat(max);
		if (minflo > maxflo) {
			return null;
		} else {
			Random rand = new Random();
			float flo = minflo + rand.nextFloat() * (maxflo - minflo);
			// System.out.println(String.valueOf(flo));
			return String.valueOf(flo);
		}
	}

	/**
	 * 随机生成beginDate和endDate之间的时间
	 * 
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@SuppressWarnings("unused")
	private String randomDate(String beginDate, String endDate) {
		try {
			SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date start = fm.parse(beginDate);// 构造开始日期
			Date end = fm.parse(endDate);// 构造结束日期
			// getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
			if (start.getTime() >= end.getTime()) {
				return null;
			}
			long date = random(start.getTime(), end.getTime());

			return fm.format(new Date(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static long random(long begin, long end) {
		long rtn = begin + (long) (Math.random() * (end - begin));
		// 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
		if (rtn == begin || rtn == end) {
			return random(begin, end);
		}
		return rtn;
	}


}
