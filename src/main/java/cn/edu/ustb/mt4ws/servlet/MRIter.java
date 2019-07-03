package cn.edu.ustb.mt4ws.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.edu.ustb.mt4ws.javabean.MetamorphicRelation;
import cn.edu.ustb.mt4ws.javabean.TCTransfer;
import cn.edu.ustb.mt4ws.javabean.TCTransferF;
import cn.edu.ustb.mt4ws.tcg.SqlUtils;
import cn.edu.ustb.mt4ws.tcg.WsdlOperationFormat;
import cn.edu.ustb.mt4ws.tcg.XmlTestCase;
import cn.edu.ustb.mt4ws.tcg.XmlTestCaseGenerator;
import cn.edu.ustb.mt4ws.tcg.XmlVariable;

public class MRIter  extends HttpServlet{
	
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
	private void autoMethodIter_1(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			String operationName, WsdlOperationFormat opFormat,
			List<MetamorphicRelation> mrList) throws IOException {
		
		String numStr = request.getParameter("num_text");
		int num = Integer.parseInt(numStr);//待生成的数目
		
		if (((String) session.getAttribute("operationName"))// transfer业务自动生成测试用例
				.equals("transfer") && num > 0) {
			//初试化得到tcsTransferList，tcsTransferFList，tcsListS，tcsListF
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
				//若原始测试用例集不为空，则将衍生测试用例集全部清空。
				tcsTransferFList = new ArrayList<TCTransferF>();
//				tcsTransferFList = (List<TCTransferF>) session
//						.getAttribute("tcsTransferFList");
	
				tcsListS = (List<List<String>>) session.getAttribute("tcsList");
				// TODO
//				tcsListF = (List<List<String>>) session
//						.getAttribute("tcsFList");
				tcsListF = new ArrayList<List<String>>();
				id = tcsTransferList.size() + 1;
			}
			
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
						TCTransfer tcTransfer=null;
						if(tcsTransferList.size()!=0){
							tcTransfer=tcsTransferList.get(0);
						} else {
							XmlTestCase newTC = tcGen.genWithLimitation(
									operationName, opFormat.getInput().format,
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
							tcTransfer = new TCTransfer(id, mrID, values[0],
									values[1], values[2], values[3]);
							tempTC = new TCTransfer(id, mrID, values[0],
									values[1], values[2], values[3]);
							tcsTransferList.add(tcTransfer);
						}
						
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
		
			
		}else if (((String) session.getAttribute("operationName"))// 地震服务随机生成原始测试用例测试
				.equals("matchQuakes") && num > 0) {
			// TODO
		} else if (((String) session.getAttribute("operationName"))// RMB转换服务随机生成原始测试用例测试
				.equals("rmbToGbbob") && num > 0) {
			// TODO
		} else {
			System.out.println("跳转错误！！！");
		}
		
		
		
		
		
		
	}


}
