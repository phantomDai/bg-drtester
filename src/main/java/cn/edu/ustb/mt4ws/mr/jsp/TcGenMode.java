package cn.edu.ustb.mt4ws.mr.jsp;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cn.edu.ustb.mt4ws.configuration.Project;
import cn.edu.ustb.mt4ws.file.DomATMTC;
import cn.edu.ustb.mt4ws.file.ParserATMTCTest;
import cn.edu.ustb.mt4ws.mr.RMBExpTest;
import cn.edu.ustb.mt4ws.mr.model.MetamorphicRelation;
import cn.edu.ustb.mt4ws.mr.model.Relation;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.tcg.XmlTestCase;
import cn.edu.ustb.mt4ws.tcg.XmlTestCaseGenerator;
import cn.edu.ustb.mt4ws.tcg.XmlVariable;

public class TcGenMode extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			// 请从头开始测试！
		}
		String tcGenMode = request.getParameter("RadioGroup1");
		String operationName = (String) session.getAttribute("operationName");
		WsdlOperationFormat opFormat = ((Map<String, WsdlOperationFormat>) session
				.getAttribute("operation_map")).get(operationName);
		List<MetamorphicRelation> mrList = (List<MetamorphicRelation>) session
				.getAttribute("MR_List");

		Project project = (Project) session.getAttribute("project");
		String projectPath = project.getWorkspace().getPath()
				+ project.getProjectName();
		String filePath = projectPath + "/ATMTestCaseSet.xml";// 上传过来的testcase文件存放在服务器中的位置

		System.out.println("Testcase文件存放在服务器中的位置:" + filePath);

		Iterator<MetamorphicRelation> iterMR = mrList.iterator();
		TransferInputBeanSet transferinputset = null;
		Vector<TransferInputBean> transferInputList = null;

		QuakeInputBeanSet quakeInputSet = null;
		Vector<QuakeInputBean> quakeInputList = null;

		RMBInputBeanSet rmbInputSet = null;
		Vector<RMBInputBean> rmbInputList = null;

		if ("auto".equals(tcGenMode)) {
			String numStr = request.getParameter("num_text");
			int num = Integer.parseInt(numStr);

			int mrID = 0;
			if (((String) session.getAttribute("operationName"))// transfer业务自动生成测试用例
					.equals("transfer") && num > 0) {
//				if (session.getAttribute("transferinputset") != null) {
//					transferinputset = (TransferInputBeanSet) session
//							.getAttribute("transferinputset");
//					transferInputList = transferinputset.getTransferset();
//				} else {
//					transferinputset = new TransferInputBeanSet();
//					transferInputList = new Vector<TransferInputBean>();
//				}
//				XmlTestCaseGenerator tcGen = new XmlTestCaseGenerator();
//				while (iterMR.hasNext()) {
//					mrID++;
//					MetamorphicRelation mr = iterMR.next();
//					for (int j = 0; j < num; j++) {
//						// 先从mt4ws生成xmltestcase，再转换成TransferInputBean
//						XmlTestCase newTC = tcGen.genWithLimitation(
//								operationName, opFormat.getInput().format, mr
//										.getRelationOfInput(), mrID);
//						Set<Map.Entry<XmlVariable, String>> entry = newTC
//								.getInput().entrySet();
//						Iterator<Map.Entry<XmlVariable, String>> iter = entry
//								.iterator();
//						Map.Entry<XmlVariable, String> myentry = null;
//						int i = 0;
//						String values[] = new String[entry.size()];
//						while (iter.hasNext()) {
//							myentry = iter.next();
//							values[i] = myentry.getValue();
//							i++;
//						}
//						transferInputList.add(new TransferInputBean(mrID,
//								values[0], values[1], values[2], values[3]));
//					}
//				}
//				transferinputset.setTransferset(transferInputList);
//				session.setAttribute("transferinputset", transferinputset);
//				RequestDispatcher dispatcher = request
//						.getRequestDispatcher("4.start_test.jsp");
//				dispatcher.forward(request, response);
			} else if (((String) session.getAttribute("operationName"))// 地震服务随机生成原始测试用例测试
					.equals("matchQuakes") && num > 0) {
				if (session.getAttribute("quakeInputSet") != null) {
					quakeInputSet = (QuakeInputBeanSet) session
							.getAttribute("quakeInputSet");
					quakeInputList = quakeInputSet.getQuakeinputbeanset();
				} else {
					quakeInputSet = new QuakeInputBeanSet();
					quakeInputList = new Vector<QuakeInputBean>();
				}
				String maxd1_ = "2002-09-01 00:00:00";
				String maxd2_ = "2003-09-01 00:00:00";
				String mind1_ = "2001-01-01 00:00:00";
				String mind2_ = "2002-01-01 00:00:00";
				String maxlat1_ = "0";
				String maxlat2_ = "90";
				String minlat1_ = "-90";
				String minlat2_ = "0";
				String maxlng1_ = "140";
				String maxlng2_ = "180";
				String minlng1_ = "-180";
				String minlng2_ = "-140";
				String maxdepth1_ = "300";
				String maxdepth2_ = "10000";
				String mindepth1_ = "0";
				String mindepth2_ = "200";
				String maxmag1_ = "6";
				String maxmag2_ = "9";
				String minmag1_ = "4";
				String minmag2_ = "6";
				while (iterMR.hasNext()) {
					mrID++;
					MetamorphicRelation mr = iterMR.next();
					// followUpStr为蜕变关系中的第一个定义关系
					String followUpStr = mr.getRelationOfInput()
							.getRelationOfInput().get(0).getFunctionFollowUp()
							.getFunctionDescription();
					String sourceStr = mr.getRelationOfInput()
							.getRelationOfInput().get(0).getFunctionSource()
							.getFunctionDescription();
					// 判断FollowUp中是否有null，若有null，则min=max均为最小值,实验中只涉及到depth和mag
					if (followUpStr.equals("null")) {
						if (sourceStr.contains("depth"))
							// 包含depth
							for (int j = 0; j < num; j++) {
								String maxd = randomDate(mind1_, maxd2_);
								String mind = randomDate(mind1_, maxd);
								String maxlat = randomFloat(minlat1_, maxlat2_);
								String minlat = randomFloat(minlat1_, maxlat);
								String maxlng = randomFloat(minlng1_, maxlng2_);
								String minlng = randomFloat(minlng1_, maxlng);
								String maxdepth = mindepth1_;
								String mindepth = mindepth1_;
								String maxmag = randomFloat(minmag1_, maxmag2_);
								String minmag = randomFloat(minmag1_, maxmag);
								QuakeInputBean quakeInput = new QuakeInputBean(
										mrID, maxd, mind, maxlng, minlng,
										maxlat, minlat, maxmag, minmag,
										maxdepth, mindepth);
								quakeInputList.add(quakeInput);

							}
						else if (sourceStr.contains("mag"))
							// 包含mag
							for (int j = 0; j < num; j++) {
								String maxd = randomDate(mind1_, maxd2_);
								String mind = randomDate(mind1_, maxd);
								String maxlat = randomFloat(minlat1_, maxlat2_);
								String minlat = randomFloat(minlat1_, maxlat);
								String maxlng = randomFloat(minlng1_, maxlng2_);
								String minlng = randomFloat(minlng1_, maxlng);
								String maxdepth = randomFloat(mindepth1_,
										maxdepth2_);
								String mindepth = randomFloat(mindepth1_,
										maxdepth);
								String maxmag = minmag1_;
								String minmag = minmag1_;
								QuakeInputBean quakeInput = new QuakeInputBean(
										mrID, maxd, mind, maxlng, minlng,
										maxlat, minlat, maxmag, minmag,
										maxdepth, mindepth);
								quakeInputList.add(quakeInput);

							}

					} else {
						for (int j = 0; j < num; j++) {
							String maxd = randomDate(mind1_, maxd2_);
							String mind = randomDate(mind1_, maxd);
							String maxlat = randomFloat(minlat1_, maxlat2_);
							String minlat = randomFloat(minlat1_, maxlat);
							String maxlng = randomFloat(minlng1_, maxlng2_);
							String minlng = randomFloat(minlng1_, maxlng);
							// String maxdepth = "2000";
							// String mindepth = "20";
							String maxdepth = randomFloat(mindepth1_,
									maxdepth2_);
							String mindepth = randomFloat(mindepth1_, maxdepth);
							// String maxmag = "6";
							// String minmag = "4";
							String maxmag = randomFloat(minmag1_, maxmag2_);
							String minmag = randomFloat(minmag1_, maxmag);
							QuakeInputBean quakeInput = new QuakeInputBean(
									mrID, maxd, mind, maxlng, minlng, maxlat,
									minlat, maxmag, minmag, maxdepth, mindepth);
							quakeInputList.add(quakeInput);
						}
					}

				}

				quakeInputSet.setQuakeinputbeanset(quakeInputList);
				session.setAttribute("quakeInputSet", quakeInputSet);
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("4.start_test.jsp");
				dispatcher.forward(request, response);

			} else if (((String) session.getAttribute("operationName"))// RMB转换服务随机生成原始测试用例测试
					.equals("rmbToGbbob") && num > 0) {
				if (session.getAttribute("rmbInputSet") != null) {
					rmbInputSet = (RMBInputBeanSet) session
							.getAttribute("rmbInputSet");
					rmbInputList = rmbInputSet.getRmbinputbeanset();
				} else {
					rmbInputSet = new RMBInputBeanSet();
					rmbInputList = new Vector<RMBInputBean>();
				}
				String RMB = "";

				while (iterMR.hasNext()) {
					mrID++;
					MetamorphicRelation mr = iterMR.next();
					Iterator<Relation> iterRelation = mr.getRelationOfInput()
							.getRelationOfInput().iterator();
					Relation relation = new Relation();
					for (int j = 0; j < num; j++) {
						RMBExpTest mrTemp = new RMBExpTest();
						while (iterRelation.hasNext()) {
							relation = iterRelation.next(); // 在本次实验中，只有一个relation，故只循环一次
						}
						boolean isSatisfied = mrTemp.checkInputExp(relation);
						// 判断relation中的蜕变关系是否为实验要求输入的数据
						if (isSatisfied) {
							if (RMBExpTest.MRType == "2"
									|| RMBExpTest.MRType == "3"
									|| RMBExpTest.MRType == "4") {
								RMB = mrTemp.randomInteger();// 整型
							} else
								RMB = mrTemp.randomFloat();// float类型

							System.out.println("输入的蜕变关系符合实验要求！！！");
						} else {
							System.out.println("输入的蜕变关系不符合实验要求！！！");
							System.exit(0);
						}
						RMBInputBean rmbInput = new RMBInputBean(mrID, RMB);
						rmbInputList.add(rmbInput);

					}

				}
				rmbInputSet.setRmbinputbeanset(rmbInputList);
				session.setAttribute("rmbInputSet", rmbInputSet);
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("4.start_test.jsp");
				dispatcher.forward(request, response);

			} else {
				System.out.println("跳转错误！！！");
			}

		} else if ("manual".equals(tcGenMode)) {
			if (((String) session.getAttribute("operationName"))
					.equals("transfer")) {
				response.sendRedirect("3.2.fill_tc_manualy.jsp");// transfer跳转的页面
			} else if (((String) session.getAttribute("operationName"))
					.equals("rmbToGbbob")) {
				response.sendRedirect("3.2.fill_tc_manualy_rmb.jsp");// 人民币转换跳转的页面
			} else if (((String) session.getAttribute("operationName"))
					.equals("queryQuakes")
					|| ((String) session.getAttribute("operationName"))
							.equals("matchQuakes")) {
				response.sendRedirect("3.2.fill_tc_manualy_quake.jsp");// 地震查询跳转的页面
			} else if (((String) session.getAttribute("operationName"))
					.equals("getList")) {
				response.sendRedirect("3.2.fill_tc_manualy_set.jsp");// getList跳转的页面
			} else {
				System.out.println("跳转错误！！！");
			}
		} else if ("import".equals(tcGenMode)) {
			// TODO import a xml file
			if (((String) session.getAttribute("operationName"))
					.equals("transfer")) {
				/*int mrID = 0;
				if (session.getAttribute("transferinputset") != null) {
					transferinputset = (TransferInputBeanSet) session
							.getAttribute("transferinputset");
					transferInputList = transferinputset.getTransferset();
				} else {
					transferinputset = new TransferInputBeanSet();
					transferInputList = new Vector<TransferInputBean>();
				}

				DomATMTC domTC = new DomATMTC();
				// 从待解析的TestCase文件中提取List<TransferInputBean>
				try {
					// 初始的存储testcase的List
					Vector<TransferInputBean> transferInputList_1 = domTC
							.parseXML(filePath);

					while (iterMR.hasNext()) {
						mrID++;
						// 对于每个mr，进行testcase赋值
						MetamorphicRelation mr = iterMR.next();
						
						for (int i = 0; i < transferInputList_1.size(); i++) {
							TransferInputBean testcase = transferInputList_1.get(i);
							testcase.setMrID(mrID);
							transferInputList.add(testcase);							
						}
					}
					
					transferinputset.setTransferset(transferInputList);
					session.setAttribute("transferinputset", transferinputset);
										
					response.sendRedirect("4.start_test.jsp");// transfer跳转的页面

				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

			} else if (((String) session.getAttribute("operationName"))
					.equals("rmbToGbbob")) {
				response.sendRedirect("");// 人民币转换跳转的页面
			} else if (((String) session.getAttribute("operationName"))
					.equals("queryQuakes")
					|| ((String) session.getAttribute("operationName"))
							.equals("matchQuakes")) {
				response.sendRedirect("");// 地震查询跳转的页面
			} else if (((String) session.getAttribute("operationName"))
					.equals("getList")) {
				response.sendRedirect("");// getList跳转的页面
			} else {
				System.out.println("跳转错误！！！");
			}

		}
	}

	/**
	 * 随机生成beginDate和endDate之间的时间
	 * 
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
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

	/**
	 * 随机产生一个在min和max之间的float数
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
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

}
