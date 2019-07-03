package cn.edu.ustb.webservicetester.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.edu.ustb.webservicetester.model.*;

public class ServiceInvoker {
	
	public String invoke(String serviceURI, OperationInfo operaInfo, TestCase tc) {
		String result = null;
		StringBuffer requestStrBuf = new StringBuffer(serviceURI);
		requestStrBuf.append("/");
		requestStrBuf.append(operaInfo.getOpName());
		requestStrBuf.append("?");
		java.util.List<ParameterInfo> paraInfoList = operaInfo.getParaList();
		java.util.Iterator<ParameterInfo> paraInfoIter = paraInfoList.iterator();
		while (paraInfoIter.hasNext()) {
			ParameterInfo pi = paraInfoIter.next();
			requestStrBuf.append(pi.getName());
			requestStrBuf.append("=");
			requestStrBuf.append(tc.getValueOfParameter(pi));
			if (paraInfoIter.hasNext()) {
				requestStrBuf.append("&");
			}
		}
		try {
			URL serviceRequestUrl = new URL(requestStrBuf.toString());
			HttpURLConnection urlcon = (HttpURLConnection)serviceRequestUrl.openConnection();
			urlcon.connect();
			InputStream is = urlcon.getInputStream();
	        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
	        StringBuffer serviceResponseBuf = new StringBuffer();
	        String l = null;
	        while ((l = buffer.readLine()) != null) {
	        	serviceResponseBuf.append(l);
	        }
	        String[] ts = serviceResponseBuf.toString().split("<.*?>");
	        for (String e : ts) {
	        	if (e.length() > 0) {
	        		result = e;
	        	}
	        }
		} catch (MalformedURLException e) {
			System.out.println("Error: wrong url for web service!");
			e.printStackTrace();
			result = null;
		} catch (IOException e1) {
			System.out.println("Error: fail to connect Web Service!");
			e1.printStackTrace();
			result = null;
		}
		return result;
	}
	
	
	public static void main(String[] args) {
		WsdlParser wp = new WsdlParser();
		String serviceUri = "http://localhost:8080/axis2/services/AddService";
		java.util.List<OperationInfo> oil = wp.parseWsdl(serviceUri + "?wsdl");
		OperationInfo oi = null;
		for (OperationInfo t : oil) {
			if ("add".equals(t.getOpName())) {
				oi = t;
			}
		}
		ServiceInvoker serviceInvoker = new ServiceInvoker();
		TestSetFileParser tsfParser = new TestSetFileParser();
		tsfParser.setParaInfoList(oi.getParaList());
		TestSet ts = tsfParser.parseFile("testset4addservice$add.xml");
		for (int i = 0; i < ts.numOfTestCases(); ++i) {
			TestCase tc = ts.get(i);
			System.out.print("服务请求结果：");
			System.out.println(serviceInvoker.invoke(serviceUri, oi, tc));
			System.out.print("预期结果：");
			System.out.println(tc.getExpectedResult());
		}
	}

}
