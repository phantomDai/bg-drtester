package cn.edu.ustb.mt4ws.tcg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.SchemaType;

import cn.edu.ustb.mt4ws.javabean.*;

public class SqlUtils {

	private volatile static SqlUtils instance = null;

	public static final String DBDRIVER = "org.gjt.mm.mysql.Driver";
	public static final String DBURL = "jdbc:mysql://localhost:3306/mydb";
	public static final String DBUSER = "root";
	public static final String DBPASS = "root";

	private static Connection conn = null;

	private static Statement stmt = null;

	private SqlUtils() {
		try {
			Class.forName(DBDRIVER);
			conn = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
			stmt = conn.createStatement();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Singleton模式
	 * 
	 * @return
	 */
	public static SqlUtils getInstance() {
		if (instance == null) {
			synchronized (SqlUtils.class) {
				if (instance == null)
					instance = new SqlUtils();
			}
		}
		return instance;
	}

	/**
	 * 根据operation的名称生成MySQL数据表
	 * 
	 * @param opFormat
	 */
	public void createTableByOperation(Map<String, WsdlOperationFormat> opFormat) {
		Set<Map.Entry<String, WsdlOperationFormat>> entry = opFormat.entrySet();
		Iterator<Map.Entry<String, WsdlOperationFormat>> iter = entry
				.iterator();
		Map.Entry<String, WsdlOperationFormat> myentry = null;
		StringBuffer createTableSQL = null;
		while (iter.hasNext()) {
			myentry = iter.next();
			createTableSQL = new StringBuffer();
			/*
			 * 根据operationName建表
			 */
			createTableSQL.append("CREATE TABLE " + myentry.getKey() + " (");
			/*
			 * 所有simple type作为表的列
			 */
			Set<Map.Entry<String, SchemaType>> entryInput = myentry.getValue()
					.getInput().format.getSimpleTypes().entrySet();
			Iterator<Map.Entry<String, SchemaType>> iterInput = entryInput
					.iterator();
			Map.Entry<String, SchemaType> myentryInput = null;
			createTableSQL
					.append("id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,");
			createTableSQL.append("mrID INT,");
			while (iterInput.hasNext()) {
				myentryInput = iterInput.next();
				createTableSQL.append(myentryInput.getKey() + " ").append(
						checkTypeForSQL(myentryInput.getValue()));
				if (iterInput.hasNext())
					createTableSQL.append(",");
			}
			/*
			 * 所有simple type 作为表的列
			 */
			// entryInput =
			// myentry.getValue().getInput().format.getSimpleTypes()
			// .entrySet();
			// iterInput = entryInput.iterator();
			// myentryInput = null;
			// while (iterInput.hasNext()) {
			// myentryInput = iterInput.next();
			// createTableSQL.append(myentryInput.getKey() + " ").append(
			// checkTypeForSQL(myentryInput.getValue()));
			// if (iterInput.hasNext())
			// createTableSQL.append(",");
			// }

			createTableSQL.append(");");
			// System.out.println("&&&&&&&&");
			// System.out.println(createTableSQL.toString());
			try {
				System.out.println("**********" + createTableSQL);
				stmt.execute(createTableSQL.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 向数据库插入一个随机生成的TC
	 * 
	 * @param operationName
	 *            在哪个operation上产生test case
	 * @param format
	 *            operation的输入输出格式
	 * @return true成功,false失败
	 */
	public boolean insertRandomTC(String operationName,
			WsdlOperationFormat opFormat) {
		XmlTestCaseGenerator tcGen = new XmlTestCaseGenerator();
		XmlTestCase tc = tcGen.genWithRandomValue(operationName, opFormat);
		String sqlInsert = tcGen.convertToSQLStatement(tc);
		try {
			return stmt.execute(sqlInsert);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 向数据库插入一个有特殊要求的test case
	 * 
	 * @param operationName
	 *            在哪个operation上产生test case
	 * @param opFormat
	 *            operation的输入输出格式
	 * @param relationOfInput
	 *            某个MR的relation of input元素
	 * @return true成功,false失败
	 */
	public boolean insertLimitedTC(String operationName,
			WsdlOperationFormat opFormat, RelationOfInput relationOfInput,
			int mrID) {
		XmlTestCaseGenerator tcGen = new XmlTestCaseGenerator();
		XmlTestCase tc = tcGen.genWithLimitation(operationName, opFormat
				.getInput().format, relationOfInput, mrID);
		String sqlInsert = tcGen.convertToSQLStatement(tc);
		try {
			return stmt.execute(sqlInsert);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 得到衍生测试用例
	 * 
	 * @param operationName
	 * @param colName
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getValueFromDB(String operationName, String colName, int id)
			throws Exception {

		try {
			conn = SqlUtils.getConnection();
			stmt = conn.createStatement();
			//add和 sub 为保留字
			if (operationName.equals("add") || operationName.equals("sub")) {
				operationName = operationName + "_";
			}
			String sql = "SELECT " + colName + " FROM " + operationName
					+ " WHERE id=" + id + ";";
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next())
				return rs.getObject(colName).toString();
			else
				return null;
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			instance.closeConnection();
		}
		
		return null;
	}

	public String getValueFromDB_F(String operationName, String colName,
			int mrID, int sID) throws Exception {
		try {
			conn = SqlUtils.getConnection();
			stmt = conn.createStatement();
			//add和 sub 为保留字
			if (operationName.equals("add") || operationName.equals("sub")) {
				operationName = operationName + "_";
			}
			String sql = "SELECT " + colName + " FROM " + operationName + "F"
					+ " WHERE mrID=" + mrID + " AND sID=" + sID + ";";
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next())
				return rs.getObject(colName).toString();
			else
				return null;
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
			instance.closeConnection();
		}
		return null;
	}

	public List<Integer> searchTCForMR(String operationName, int mrID)
			throws SQLException {
		try {
			conn=SqlUtils.getConnection();
			stmt=conn.createStatement();
			//add和 sub 为保留字
			if(operationName.equals("add")||operationName.equals("sub")){
				operationName=operationName+"_";
			}
			
			List<Integer> result = new ArrayList<Integer>();
			String searchSQL = "SELECT id FROM " + operationName + " WHERE mrID="
					+ mrID + ";";
			ResultSet rs = stmt.executeQuery(searchSQL);
			while (rs.next()) {
				result.add(rs.getInt("id"));
			}
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			instance.closeConnection();
		}
		return null;
	}

	private String checkTypeForSQL(SchemaType type) {
		String SQLType = "";
		switch (type.getPrimitiveType().getBuiltinTypeCode()) {
		case SchemaType.BTC_DATE_TIME: {
			SQLType = "";
			break;
		}
		case SchemaType.BTC_TIME: {
			SQLType = "";
			break;
		}
		case SchemaType.BTC_DATE: {
			SQLType = "";
			break;
		}
		case SchemaType.BTC_G_YEAR_MONTH: {
			SQLType = "";
			break;
		}
		case SchemaType.BTC_G_YEAR: {
			SQLType = "";
			break;
		}
		case SchemaType.BTC_G_MONTH_DAY: {
			SQLType = "";
			break;
		}
		case SchemaType.BTC_G_DAY: {
			SQLType = "";
			break;
		}
		case SchemaType.BTC_G_MONTH: {
			SQLType = "";
			break;
		}

		case SchemaType.BTC_NOT_BUILTIN:
			return "";

		case SchemaType.BTC_ANY_TYPE:
		case SchemaType.BTC_ANY_SIMPLE:

		case SchemaType.BTC_BOOLEAN:

		case SchemaType.BTC_BASE_64_BINARY: {
		}

		case SchemaType.BTC_HEX_BINARY:

		case SchemaType.BTC_ANY_URI:

		case SchemaType.BTC_QNAME:

		case SchemaType.BTC_NOTATION:

		case SchemaType.BTC_FLOAT:
			SQLType = "FLOAT";
			break;
		case SchemaType.BTC_DOUBLE:
			SQLType = "DOUBLE";
			break;
		case SchemaType.BTC_DECIMAL:
			switch (new XmlTcgUtils().closestBuiltin(type).getBuiltinTypeCode()) {
			case SchemaType.BTC_SHORT:
			case SchemaType.BTC_UNSIGNED_SHORT:
			case SchemaType.BTC_BYTE:
			case SchemaType.BTC_UNSIGNED_BYTE:
			case SchemaType.BTC_INT:
				SQLType = "INT";
				break;
			case SchemaType.BTC_UNSIGNED_INT:
			case SchemaType.BTC_LONG:
			case SchemaType.BTC_UNSIGNED_LONG:
			case SchemaType.BTC_INTEGER:
			case SchemaType.BTC_NON_POSITIVE_INTEGER:
			case SchemaType.BTC_NEGATIVE_INTEGER:
			case SchemaType.BTC_NON_NEGATIVE_INTEGER:
			case SchemaType.BTC_POSITIVE_INTEGER:
			case SchemaType.BTC_DECIMAL:
			}

		case SchemaType.BTC_STRING: {
			switch (new XmlTcgUtils().closestBuiltin(type).getBuiltinTypeCode()) {
			case SchemaType.BTC_STRING:
				SQLType = "VARCHAR(20)";
				break;
			case SchemaType.BTC_NORMALIZED_STRING:
				break;

			case SchemaType.BTC_TOKEN:
				break;
			}
		}

		case SchemaType.BTC_DURATION:

		}
		return SQLType;

	}

	public AccountManager getAccountManager() {
		return new AccountManager();
	}

	public void closeConnection() throws SQLException {
		stmt.close();
		conn.close();
	}

	// 以下函数是为了ATM实验方便
	public class AccountManager {

		String[] bank = { "ICBC", "BOC" };
		String[] branch = { "Beijing", "Shanghai" };

		public void accountTtoDB(String tableName, int testcaseID)
				throws SQLException, ClassNotFoundException {

			try {
				conn = SqlUtils.getConnection();
				stmt = conn.createStatement();
				String sql;
				ResultSet rs = null;
				sql = "SELECT * FROM " + tableName + " WHERE id=" + testcaseID;// TODO确定数据表的列名称
				rs = stmt.executeQuery(sql);
				if (rs.next()) {
					String accountA = rs.getString("AccountFrom");
					String accountB = rs.getString("AccountTo");
					int relation = rs.getInt("mode");

					String[] bankABAndBranchAB = convert(relation);
					String bankA = bankABAndBranchAB[0];
					String bankB = bankABAndBranchAB[1];
					String branchA = bankABAndBranchAB[2];
					String branchB = bankABAndBranchAB[3];
					insertAccount(accountA, "123", 20000000, "100000000000000000",
							bankA, branchA);
					insertAccount(accountB, "123", 20000000, "100000000000000000",
							bankB, branchB);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				instance.closeConnection();
			}
		}

		public void testRubbishDisposal(String tableName, int testcaseID)
				throws SQLException, ClassNotFoundException {
			String sql;
			ResultSet rs = null;
			sql = "SELECT * FROM " + tableName + " WHERE id=" + testcaseID;// TODO确定数据表的列名称
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				String accountA = rs.getString("AccountFrom");
				String accountB = rs.getString("AccountTo");
				deleteAccount(accountA);
				deleteAccount(accountB);
			}
		}

		private boolean insertAccount(String accountID, String password,
				double balance, String userID, String bank, String branch)
				throws SQLException, ClassNotFoundException {
			int rsresult = 0;

			String sql;

			ResultSet rs = null;

			sql = "SELECT Balance FROM Account WHERE AccountID=" + accountID;
			rs = stmt.executeQuery(sql);
			if (rs.next()) {

			} else {
				sql = "INSERT INTO Account (accountID,password,balance,userID,bank,branch)VALUES ('"
						+ accountID
						+ "','"
						+ password
						+ "','"
						+ balance
						+ "','" + userID + "','" + bank + "','" + branch + "')";
				rsresult = stmt.executeUpdate(sql);
				if (rsresult == 1) {
					rs.close();
					return true;
				} else {
					rs.close();
					return false;
				}
			}
			rs.close();
			return false;
		}

		private boolean deleteAccount(String accountID) throws SQLException,
				ClassNotFoundException {

			int rsresult = 0;
			String sql;
			ResultSet rs = null;

			sql = "SELECT Balance FROM Account WHERE AccountID=" + accountID;
			rs = stmt.executeQuery(sql);
			if (!rs.next()) {
			} else {
				sql = "DELETE FROM Account WHERE AccountID=" + accountID;
				rsresult = stmt.executeUpdate(sql);
				if (rsresult == 1) {
					rs.close();
					return true;
				} else {
					rs.close();
					return false;
				}
			}
			rs.close();
			return false;

		}

		private String[] convert(int relation) {
			String bankABAndBranchAB[] = new String[4];
			String bankA = null, bankB = null, branchA = null, branchB = null;
			switch (relation) {
			case 0: {
				bankA = bank[0];
				bankB = bank[0];
				branchA = branch[0];
				branchB = branch[0];
				break;
			}
			case 1: {
				bankA = bank[0];
				bankB = bank[1];
				branchA = branch[0];
				branchB = branch[0];
				break;
			}
			case 2: {
				bankA = bank[0];
				bankB = bank[0];
				branchA = branch[0];
				branchB = branch[1];
				break;
			}
			case 3: {
				bankA = bank[0];
				bankB = bank[1];
				branchA = branch[0];
				branchB = branch[1];
				break;
			}

			}
			bankABAndBranchAB[0] = bankA;
			bankABAndBranchAB[1] = bankB;
			bankABAndBranchAB[2] = branchA;
			bankABAndBranchAB[3] = branchB;

			return bankABAndBranchAB;
		}

	}
	
	
	/**
	 * 将原始测试用例添加入数据库中
	 * BPEL
	 * 
	 * @param tcsProcessList
	 */
	public void insertToProcessS(List<ProcessBean> tcsProcessList) {
		String tablename = "process";
		//SqlUtils sqlUtils = SqlUtils.getInstance();

		ResultSet rs = null;

		if (tcsProcessList == null) {
			System.out.println("测试用例集为空!!!");
		} else {
			String sqlDel = "truncate table mydb.process";
			try {
				conn=instance.getConnection();
				stmt=conn.createStatement();
				
				stmt.executeUpdate(sqlDel);

				// 遍历测试用例集，逐个输入
				for (ProcessBean processBean : tcsProcessList) {
					int id = processBean.getId();
					int mrID = processBean.getMrID();
					String a = processBean.getA();
					String b = processBean.getB();
					String type = processBean.getType();


					String sqlstr1 = "INSERT INTO "
							+ tablename
							+ "(id,mrID,a,b,type)values('"
							+ id + "','" + mrID + "','" + a + "','"
							+ b + "','" + type + "')";
					// 执行更新
					stmt.executeUpdate(sqlstr1);
				}
				// 打印数据库中的所有数据
				String sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					instance.closeConnection();
					rs.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}

			}
		}
	}
	
	/**
	 * 将衍生测试用例添加入数据库中
	 * BPEL
	 * @param tcsTransferList
	 */
	public void insertToProcessF(List<ProcessBeanF> tcsProcessFList) {
		String tablename = "processF";
		//SqlUtils sqlUtils = SqlUtils.getInstance();
		

		ResultSet rs = null;

		if (tcsProcessFList == null) {
			System.out.println("测试用例集为空!!!");
		} else {
			String sqlDel = "truncate table mydb.processF";
			try {
				conn=instance.getConnection();
				stmt=conn.createStatement();
				stmt.executeUpdate(sqlDel);

				// 遍历测试用例集，逐个输入
				for (ProcessBeanF tcProcessF : tcsProcessFList) {
					int sID = tcProcessF.getsID();
					int mrID = tcProcessF.getMrID();
					String a = tcProcessF.getA();
					String b = tcProcessF.getB();
					String type = tcProcessF.getType();

					String sqlstr1 = "INSERT INTO "
							+ tablename
							+ "(sID,mrID,a,b,type)values('"
							+ sID + "','" + mrID + "','" + a + "','"
							+ b + "','" + type + "')";
					// 执行更新
					stmt.executeUpdate(sqlstr1);
				}
				// 打印数据库中的所有数据
				String sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					instance.closeConnection();
					rs.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}

			}
		}

	}
	/**
	 * 将原始测试用例添加入数据库中
	 * BPEL
	 * 
	 * @param tcsCheckQuantityList
	 */
	public void insertToCheckQuantityS(List<CheckQuantityBean> tcsCheckQuantityList) {
		String tablename = "CheckQuantity";
		//SqlUtils sqlUtils = SqlUtils.getInstance();

		ResultSet rs = null;

		if (tcsCheckQuantityList == null) {
			System.out.println("测试用例集为空!!!");
		} else {
			String sqlDel = "truncate table mydb.CheckQuantity";
			try {
				conn=instance.getConnection();
				stmt=conn.createStatement();
				
				stmt.executeUpdate(sqlDel);

				// 遍历测试用例集，逐个输入
				for (CheckQuantityBean checkQuantityBean : tcsCheckQuantityList) {
					int id = checkQuantityBean.getId();
					int mrID = checkQuantityBean.getMrID();
					String name = checkQuantityBean.getName();
					String amount = checkQuantityBean.getAmount();


					String sqlstr1 = "INSERT INTO "
							+ tablename
							+ "(id,mrID,name,amount)values('"
							+ id + "','" + mrID + "','" + name + "','"
							+ amount + "')";
					// 执行更新
					stmt.executeUpdate(sqlstr1);
				}
				// 打印数据库中的所有数据
				String sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					instance.closeConnection();
					rs.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}

			}
		}
	}
	
	/**
	 * 将衍生测试用例添加入数据库中
	 * BPEL
	 * @param tcsCheckQuantityList
	 */
	public void insertToCheckQuantityF(List<CheckQuantityBeanF> tcsCheckQuantityFList) {
		String tablename = "CheckQuantityF";
		//SqlUtils sqlUtils = SqlUtils.getInstance();
		

		ResultSet rs = null;

		if (tcsCheckQuantityFList == null) {
			System.out.println("测试用例集为空!!!");
		} else {
			String sqlDel = "truncate table mydb.CheckQuantityF";
			try {
				conn=instance.getConnection();
				stmt=conn.createStatement();
				stmt.executeUpdate(sqlDel);

				// 遍历测试用例集，逐个输入
				for (CheckQuantityBeanF tcCheckQuantityF : tcsCheckQuantityFList) {
					int sID = tcCheckQuantityF.getsID();
					int mrID = tcCheckQuantityF.getMrID();
					String name = tcCheckQuantityF.getName();
					String amount = tcCheckQuantityF.getAmount();

					String sqlstr1 = "INSERT INTO "
							+ tablename
							+ "(sID,mrID,name,amount)values('"
							+ sID + "','" + mrID + "','" + name + "','"
						 + amount + "')";
					// 执行更新
					stmt.executeUpdate(sqlstr1);
				}
				// 打印数据库中的所有数据
				String sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					instance.closeConnection();
					rs.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}

			}
		}

	}

	/**
	 * 将原始测试用例添加入数据库中
	 * BPEL
	 * 
	 * @param tcsAddList
	 */
	public void insertToAddS(List<AddBean> tcsAddList) {
		String tablename = "add_";
		//SqlUtils sqlUtils = SqlUtils.getInstance();

		ResultSet rs = null;

		if (tcsAddList == null) {
			System.out.println("测试用例集为空!!!");
		} else {
			String sqlDel = "truncate table mydb.add_";
			try {
				conn=instance.getConnection();
				stmt=conn.createStatement();
				
				stmt.executeUpdate(sqlDel);

				// 遍历测试用例集，逐个输入
				for (AddBean addBean : tcsAddList) {
					int id = addBean.getId();
					int mrID = addBean.getMrID();
					String a = addBean.getA();
					String b = addBean.getB();
					


					String sqlstr1 = "INSERT INTO "
							+ tablename
							+ "(id,mrId,a,b)values("
							+ id + ",'" + mrID + "','" + a + "','"
							+ b + "')";
					// 执行更新
					stmt.executeUpdate(sqlstr1);
				}
				// 打印数据库中的所有数据
				String sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					instance.closeConnection();
					rs.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}

			}
		}
	}
	
	/**
	 * 将衍生测试用例添加入数据库中
	 * BPEL
	 * @param tcsTransferList
	 */
	public void insertToAddF(List<AddBeanF> tcsAddFList) {
		String tablename = "add_f";
		//SqlUtils sqlUtils = SqlUtils.getInstance();
		

		ResultSet rs = null;

		if (tcsAddFList == null) {
			System.out.println("测试用例集为空!!!");
		} else {
			String sqlDel = "truncate table mydb.add_f";
			try {
				conn=instance.getConnection();
				stmt=conn.createStatement();
				stmt.executeUpdate(sqlDel);

				// 遍历测试用例集，逐个输入
				for (AddBeanF tcAddF : tcsAddFList) {
					int sID = tcAddF.getsID();
					int mrID = tcAddF.getMrID();
					String a = tcAddF.getA();
					String b = tcAddF.getB();
					
					String sqlstr1 = "INSERT INTO "
							+ tablename
							+ "(sId,mrId,a,b)values("
							+ sID + "," + mrID + ",'" + a + "','"
							+ b + "')";
					// 执行更新
					stmt.executeUpdate(sqlstr1);
				}
				// 打印数据库中的所有数据
				String sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					instance.closeConnection();
					rs.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}

			}
		}

	}

	/**
	 * 将原始测试用例添加入数据库中
	 * BPEL
	 * 
	 * @param tcsAddList
	 */
	public void insertToSubS(List<SubBean> tcsSubList) {
		String tablename = "sub_";
		//SqlUtils sqlUtils = SqlUtils.getInstance();

		ResultSet rs = null;

		if (tcsSubList == null) {
			System.out.println("测试用例集为空!!!");
		} else {
			String sqlDel = "truncate table mydb.sub_";
			try {
				conn=instance.getConnection();
				stmt=conn.createStatement();
				
				stmt.executeUpdate(sqlDel);

				// 遍历测试用例集，逐个输入
				for (SubBean subBean : tcsSubList) {
					int id = subBean.getId();
					int mrID = subBean.getMrID();
					String a = subBean.getA();
					String b = subBean.getB();
					


					String sqlstr1 = "INSERT INTO "
							+ tablename
							+ "(id,mrId,a,b)values("
							+ id + ",'" + mrID + "','" + a + "','"
							+ b + "')";
					// 执行更新
					stmt.executeUpdate(sqlstr1);
				}
				// 打印数据库中的所有数据
				String sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					instance.closeConnection();
					rs.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}

			}
		}
	}
	
	/**
	 * 将衍生测试用例添加入数据库中
	 * BPEL
	 * @param tcsTransferList
	 */
	public void insertToSubF(List<SubBeanF> tcsSubFList) {
		String tablename = "sub_f";
		//SqlUtils sqlUtils = SqlUtils.getInstance();
		

		ResultSet rs = null;

		if (tcsSubFList == null) {
			System.out.println("测试用例集为空!!!");
		} else {
			String sqlDel = "truncate table mydb.sub_f";
			try {
				conn=instance.getConnection();
				stmt=conn.createStatement();
				stmt.executeUpdate(sqlDel);

				// 遍历测试用例集，逐个输入
				for (SubBeanF tcSubF : tcsSubFList) {
					int sID = tcSubF.getsID();
					int mrID = tcSubF.getMrID();
					String a = tcSubF.getA();
					String b = tcSubF.getB();
					
					String sqlstr1 = "INSERT INTO "
							+ tablename
							+ "(sId,mrId,a,b)values("
							+ sID + "," + mrID + ",'" + a + "','"
							+ b + "')";
					// 执行更新
					stmt.executeUpdate(sqlstr1);
				}
				// 打印数据库中的所有数据
				String sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					instance.closeConnection();
					rs.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}

			}
		}

	}


	/**
	 * 将原始测试用例添加入数据库中
	 * 
	 * @param tcsTransferList
	 */
	public void insertToTransferS(List<TCTransfer> tcsTransferList) {
		String tablename = "transfer";

		ResultSet rs = null;

		if (tcsTransferList == null) {
			System.out.println("测试用例集为空!!!");
		} else {
			String sqlDel = "truncate table mydb.transfer";
			try {
				conn=instance.getConnection();
				stmt=conn.createStatement();
				stmt.executeUpdate(sqlDel);

				// 遍历测试用例集，逐个输入
				for (TCTransfer tcTransfer : tcsTransferList) {
					int id = tcTransfer.getId();
					int mrID = tcTransfer.getMrID();
					String accountFrom = tcTransfer.getAccountFrom();
					String accountTo = tcTransfer.getAccountTo();
					String mode = tcTransfer.getMode();
					String amount = tcTransfer.getAmount();

					String sqlstr1 = "INSERT INTO "
							+ tablename
							+ "(id,mrID,accountFrom,accountTo,amount,mode)values('"
							+ id + "','" + mrID + "','" + accountFrom + "','"
							+ accountTo + "','" + amount + "','" + mode + "')";
					// 执行更新
					stmt.executeUpdate(sqlstr1);
				}
				// 打印数据库中的所有数据
				String sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					instance.closeConnection();
					rs.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}

			}
		}
	}

	/**
	 * 将衍生测试用例添加入数据库中
	 * 
	 * @param tcsTransferList
	 */
	public void insertToTransferF(List<TCTransferF> tcsTransferFList) {
		String tablename = "transferF";
		
		ResultSet rs = null;

		if (tcsTransferFList == null) {
			System.out.println("测试用例集为空!!!");
		} else {
			String sqlDel = "truncate table mydb.transferF";
			try {
				conn=instance.getConnection();
				stmt=conn.createStatement();
				stmt.executeUpdate(sqlDel);

				// 遍历测试用例集，逐个输入
				for (TCTransferF tcTransferF : tcsTransferFList) {
					int sID = tcTransferF.getsID();
					int mrID = tcTransferF.getMrID();
					String accountFrom = tcTransferF.getAccountFrom();
					String accountTo = tcTransferF.getAccountTo();
					String mode = tcTransferF.getMode();
					String amount = tcTransferF.getAmount();

					String sqlstr1 = "INSERT INTO "
							+ tablename
							+ "(sID,mrID,accountFrom,accountTo,amount,mode)values('"
							+ sID + "','" + mrID + "','" + accountFrom + "','"
							+ accountTo + "','" + amount + "','" + mode + "')";
					// 执行更新
					stmt.executeUpdate(sqlstr1);
				}
				// 打印数据库中的所有数据
				String sqlstr = "select * from " + tablename;
				rs = stmt.executeQuery(sqlstr);
				// session.removeAttribute("transferinputset");//vector中的属性清空。
				ResultSetMetaData rsmd = rs.getMetaData();// 获取元数据
				int j = 0;
				j = rsmd.getColumnCount();// 获得结果集的行数
				for (int k = 0; k < j; k++) {
					System.out.print(rsmd.getColumnName(k + 1));
					System.out.print("\t");
				}
				System.out.print("\n");
				while (rs.next()) {
					for (int i = 0; i < j; i++) {
						System.out.print(rs.getString(i + 1));
						System.out.print("\t");
					}
					System.out.print("\n");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					instance.closeConnection();
					rs.close();
				} catch (SQLException e) {
					System.out.println(e.toString());
				}

			}
		}

	}
	
	public static Connection getConnection(){
		try {
			Class.forName(DBDRIVER);
			Connection con = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
			return con;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
