package cn.edu.ustb.mt4ws.mr.jsp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class TransferIntoSQL extends HttpServlet{
	
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);	
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		System.out.println("正在链接数据库……");
		HttpSession session = request.getSession(false);
		if(session==null){
			//TODO
		}
		if (session.getAttribute("transferinputset") == null) {
	  		System.out.println("请输入测试用例……");
	  	} else {
		String user="root";
		String password="root";
		String url="jdbc:mysql://localhost:3306/mydb";
		String driver="org.gjt.mm.mysql.Driver";
		String tablename="transfer";
		String sqlstr;
		String sqlstr1;
		Connection con=null;
		Statement stmt=null;
		ResultSet rs=null;
		try{
			TransferInputBeanSet transferinputset = (TransferInputBeanSet)session.getAttribute("transferinputset");
			int num_testcase = transferinputset.getTransferset().size();
			
			Class.forName(driver);
			con=DriverManager.getConnection(url, user, password);
			stmt=con.createStatement();
			for(int i=0;i<num_testcase;i++){
			
				int mrID=transferinputset.getTransferset().get(i).getMrID();
				String accountFrom = transferinputset.getTransferset().elementAt(i).getAccoutFrom();
				String accountTo = transferinputset.getTransferset().elementAt(i).getAccoutTo();
				String amount = transferinputset.getTransferset().elementAt(i).getAmount();
				String mode = transferinputset.getTransferset().elementAt(i).getMode();
			    sqlstr1="insert into "+tablename+"(MrID,AccountFrom,AccountTo,Amount,Mode)values('" +mrID+"','"+accountFrom +"','" + accountTo +"','"+amount+"','"+mode+"')";
			    stmt.executeUpdate(sqlstr1);
			}
			sqlstr="select * from "+tablename;
            rs=stmt.executeQuery(sqlstr);
			session.removeAttribute("transferinputset");//vector中的属性清空。
			ResultSetMetaData rsmd=rs.getMetaData();//获取元数据
			int j=0;
			j=rsmd.getColumnCount();//获得结果集的行数
			for(int k=0;k<j;k++){
				System.out.print(rsmd.getColumnName(k+1));
				System.out.print("\t");
			}
			System.out.print("\n");
			while(rs.next()){
				for(int i=0;i<j;i++){
					System.out.print(rs.getString(i+1));
					System.out.print("\t");
				}
				System.out.print("\n");
			}
		}catch(ClassNotFoundException e1){
			System.out.println("数据库驱动不存在");
			System.out.println(e1.toString());
		}
		catch(SQLException e2){
			System.out.println("数据库异常");
			System.out.println(e2.toString());
		}
		finally{
			try{
				if(rs!=null)
					rs.close();
				if(stmt!=null)
					stmt.close();
				if(con!=null)
					con.close();
			}
			catch(SQLException e){
				System.out.println(e.toString());
			}
		}
	  	}
		
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("4.start_test.jsp");
        dispatcher.forward(request, response);	
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
       doPost(request, response);
       }
	
}
