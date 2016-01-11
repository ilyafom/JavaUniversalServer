package DBWork;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;

import UserAccountWork.UserAccount;
import WorkWithServers.ServerData;

public class DataBaseAccess {
	
	private static Connection connection; 
	
	public static void initConection()
	{
		
		try {			
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/medcarddb","postgres", "25367811");
		
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
		}
		
	}

	
	//Work with other servers
	public ServerData getServerDataFromDB(String servarname)
	{
		ResultSet rs;
		ResultSet rs2;
		ServerData sd = new ServerData();

		try {
			sd.name = servarname;
			Statement st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM basetable WHERE title='" + servarname + "';");
			if (rs.next())
			{
				rs2 = st.executeQuery("SELECT * FROM secondtable WHERE guid='" + rs.getString("guid") + "';");
			
				while(rs2.next()){
					
					if (rs2.getString("dataname").equals("ServerIP"))
					{
						sd.ip = rs2.getString("datavalue");
						
					}
					if (rs2.getString("dataname").equals("ServerToken"))
					{
						sd.token =rs2.getString("datavalue");
						
					}
					if (rs2.getString("dataname").equals("ServerPort"))
					{
						sd.port = Integer.parseInt(rs2.getString("datavalue"));
						
					}
					
				}
				
				return sd;	
			} 
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return null;
		
		
	}
	public void addServerData(String name, String ip, String port, String token, String guid) {
		try {
			Statement st = connection.createStatement();
			st.executeUpdate("INSERT INTO basetable(guid, title) values('"+guid+"','"+ name +"');");
			st.close();
			
			
			Statement st1 = connection.createStatement();
			st1.executeUpdate("INSERT INTO secondtable(guid, dataname, datavalue) values('"+guid+"','ServerPort','"+ port +"');");
			st1.close();
			
			
			Statement st2 = connection.createStatement();
			st2.executeUpdate("INSERT INTO secondtable(guid, dataname, datavalue) values('"+guid+"','ServerIP','"+ ip +"');");
			st2.close();
			
			
			Statement st3 = connection.createStatement();
			
			st3.executeUpdate("INSERT INTO secondtable(guid, dataname, datavalue) values('"+guid+"','ServerToken','"+ token +"');");
			st3.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public void updateServerToken(String guid, String token) {
		
		try {
			Statement st = connection.createStatement();
			st.executeUpdate("DELETE FROM secondtable WHERE guid='"+guid+"' AND dataname='ServerToken';");
			st.close();
		
			Statement st2 = connection.createStatement();
			st2.executeUpdate("INSERT INTO secondtable(guid, dataname, datavalue) values('"+guid+"','ServerToken','"+ token +"');");
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	
	//Work with accounts
	public boolean accountValidate(UserAccount usacc)
	{
		ResultSet rs,rs2;
		try {
			
			
		Statement st = connection.createStatement();
		
		rs = st.executeQuery("SELECT guid,dataname,datavalue FROM secondtable;");
		
		String guid1 = "1";
        String guid2 = "2";
        
		while (rs.next()) {
			
			
			
			if ( ((  rs.getString("dataname").equals("login"))
					&& (rs.getString("datavalue").equals(usacc.getLogin()))) 
					|| ((  rs.getString("dataname").equals("email")) && (rs.getString("datavalue").equals(usacc.getLogin()))  ) )
			{
				guid1 = rs.getString("guid");
				
			}
			
			if ( (rs.getString("dataname").equals("password")) && (rs.getString("datavalue").equals(usacc.getPassword())) )
			{
				guid2 = rs.getString("guid");
				if ( (rs.getString("dataname").equals("SinglePassword")) && (rs.getString("datavalue").equals(usacc.getPassword())) )
				{
					
					guid2 = rs.getString("guid");
					deleteSinglePassword(guid2);
				}
				
			}
			
			
        	if (guid1.equals(guid2))
        	{
        		rs.close();
        		st.close();
        		return true;
        	}
	        
	       
	     }
		 
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	private void deleteSinglePassword(String guid) {
		try {
			Statement st = connection.createStatement();
			
			st.executeUpdate("DELETE FROM secondtable WHERE dataname='SinglePassword' AND guid='"+guid+"';");
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public boolean deviceCodeValidate(UserAccount usacc) {
		
		List<String> codelist = accountValidateGetListCodeDevice(usacc);
		/*
		for (String string : codelist) {
			System.out.println(string);
			
		}
		*/
		for (String string : codelist) {
			if (string.equals(usacc.getCode()))
				return true;
		}
		
		
		return false;
	}
	private List<String> accountValidateGetListCodeDevice(UserAccount usacc) {
		ResultSet rs;
		
		List<String> list = new ArrayList<String>();
		
		String guid = getGUIDUserByLogin(usacc);

		try {
			
			
			Statement st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM secondtable WHERE guid='" + guid + "';");
			
			
			
			while(rs.next()){
				if (rs.getString("dataname").equals("device code"))
					list.add(rs.getString("datavalue"));
			}
			
				
		      
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return list;
	}
	public String getGUIDUserByLogin(UserAccount usacc) {
		ResultSet rs;
		try {
			Statement st = connection.createStatement();
			rs = st.executeQuery("SELECT guid FROM secondtable WHERE dataname = 'login' and datavalue = '"+usacc.getLogin()+"';");
			
			if (rs.next())
				return rs.getString("guid");
				
				
		       
		     
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public String getMailFromDb(UserAccount acc) {
		ResultSet rs;
		
		String guid = getGUIDUserByLogin(acc);
		try {
			Statement st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM secondtable WHERE guid='"+guid+"' AND dataname = 'email';");
			
			
			if (rs.next())
			{
				System.out.println(rs.getString("datavalue"));
				return rs.getString("datavalue");
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "no mail";
	}
	public void addCodeToBD(UserAccount acc) {

		
		try {
			String guid = getGUIDUserByLogin(acc);
			Statement st = connection.createStatement();
			
			st.executeUpdate("INSERT INTO secondtable (id,guid,uniqid,idgr,gr,sort,"
					+ " date, dataname, uniqidex, datavalue, edizm, created,  changed,  system) "
					+ "values('"+ 
					1                  			+"','"+ //remake
					guid                  	    +"','"+ 
					1    					  	+"','"+ //remake 
					1      						+"','"+ //remake
					false       				+"','"+ //remake
					1      						+"','"+ //remake 
					"0001-01-01"      			+"','"+ //remake 
					"device code"  				+"','"+ 
					1  							+"','"+ //remake 
					acc.getCode() 				+"','"+ 
					1     						+"','"+ //remake
					"0001-01-01"   				+"','"+ //remake
					1   						+"','"+ 
					1   						+"');");//remake
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public void addNewAccount(UserAccount acc) {
		
			
		UUID guid =  UUID.randomUUID();
    	while (checkGUID(guid.toString()))
    	{
    		guid =  UUID.randomUUID();
    	}
			
    	String GUID_STR = guid.toString().toUpperCase();
    	try {
    		Statement st = connection.createStatement();
		
    		st.executeUpdate("INSERT INTO basetable (guid,title) values('"+GUID_STR+"','MasterUser');");
    	
		
		
			
			
			st.executeUpdate("INSERT INTO secondtable (id,guid,uniqid,idgr,gr,sort,"
					+ " date, dataname, uniqidex, datavalue, edizm, created,  changed,  system) "
					+ "values('"+ 
					1                  			+"','"+ //remake
					GUID_STR                  	+"','"+ 
					1    					  	+"','"+ //remake 
					1      						+"','"+ //remake
					false       				+"','"+ //remake
					1      						+"','"+ //remake 
					"0001-01-01"      			+"','"+ //remake 
					"login"  					+"','"+ 
					1  							+"','"+ //remake 
					acc.getLogin() 				+"','"+ 
					1     						+"','"+ //remake
					"0001-01-01"   				+"','"+ //remake
					1   						+"','"+ 
					1   						+"');");//remake
			
			st.executeUpdate("INSERT INTO secondtable (id,guid,uniqid,idgr,gr,sort,"
					+ " date, dataname, uniqidex, datavalue, edizm, created,  changed,  system) "
					+ "values('"+ 
					1                  			+"','"+ //remake
					GUID_STR                  	+"','"+ 
					1    					  	+"','"+ //remake 
					1      						+"','"+ //remake
					false       				+"','"+ //remake
					1      						+"','"+ //remake 
					"0001-01-01"      			+"','"+ //remake 
					"password"					+"','"+ 
					1  							+"','"+ //remake 
					acc.getPassword() 			+"','"+ 
					1     						+"','"+ //remake
					"0001-01-01"   				+"','"+ //remake
					1   						+"','"+ 
					1   						+"');");//remake
			
			
			st.executeUpdate("INSERT INTO secondtable (id,guid,uniqid,idgr,gr,sort,"
					+ " date, dataname, uniqidex, datavalue, edizm, created,  changed,  system) "
					+ "values('"+ 
					1                  			+"','"+ //remake
					GUID_STR                  	+"','"+ 
					1    					  	+"','"+ //remake 
					1      						+"','"+ //remake
					false       				+"','"+ //remake
					1      						+"','"+ //remake 
					"0001-01-01"      			+"','"+ //remake 
					"email"						+"','"+ 
					1  							+"','"+ //remake 
					acc.getEmail() 						+"','"+ 
					1     						+"','"+ //remake
					"0001-01-01"   				+"','"+ //remake
					1   						+"','"+ 
					1   						+"');");//remake
			
			st.executeUpdate("INSERT INTO secondtable (guid, dataname, datavalue) values('"+ GUID_STR+"','accountAccess','open');");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	private boolean checkGUID(String guid) {
		ResultSet rs;
		
		try {
			Statement st = connection.createStatement();
			
			rs = st.executeQuery("SELECT * FROM secondtable WHERE guid = '"+guid+"';");
			
			if (rs.next())
				return true;
			else 
				return false;
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public boolean checkLogin(String login) {
		ResultSet rs;
		
		try {
			Statement st = connection.createStatement();
			
			rs = st.executeQuery("SELECT guid FROM secondtable WHERE dataname = 'login' AND datavalue = '"+login+"';");
			if(rs.next())
			{
				
				return true;
			}else return false;
			
		} catch (SQLException e) {
			
			return false;
			
			
		}
		
	}
	public boolean updateAccountData(UserAccount account)
	{
		String guid = getGUIDUserByLogin(account);
		try {
			Statement st = connection.createStatement();
			st.executeUpdate("DELETE FROM secondtable WHERE guid='"+guid+"' AND dataname='email';");
			
			st.executeUpdate("DELETE FROM secondtable WHERE guid='"+guid+"' AND dataname='password';");
			
		
			System.out.println(account.getEmail());
			System.out.println(account.getPassword());
			
			
			st.executeUpdate("INSERT INTO secondtable(guid, dataname, datavalue) values('"+guid+"','email','"+ account.getEmail() +"');");
			
			st.executeUpdate("INSERT INTO secondtable(guid, dataname, datavalue) values('"+guid+"','password','"+ account.getPassword() +"');");
			
			
			
			
			st.close();
			
			return true;
			
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	//BaseTable
	
	public void addBaseTable(JSONObject joRecord) {
		
		try {
			Statement st = connection.createStatement();
			
			st.executeUpdate("INSERT INTO basetable values('"+ joRecord.get("UniqueId") +"', '"+ joRecord.get("GUID") +"','"+ joRecord.get("UniqueIdEx") +"','"+ joRecord.get("UniqueIdGR") +"','"+ joRecord.get("GR")+"','"+ joRecord.get("Sort") +"','"+ joRecord.get("Title") +"','"+ joRecord.get("Created") +"','"+ joRecord.get("Changed") +"','"+ joRecord.get("System") +"');");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Object getJsonBaseTableByUidGUid(String uid, String guid) {
		
		ResultSet rs;
		
		try {
			Statement st = connection.createStatement();
			
			rs = st.executeQuery("SELECT * FROM basetable WHERE uniqueid='"+uid+"' AND guid='"+guid+"';");
			
			
			
			/*
			int i = 1;
			while (rs.next())
			{
				if(i>1)
					jsonStr+="\", record"+i+"\":";
				else
				jsonStr+="\"record"+i+"\":";
				jsonStr+= " {\"UniqueId\":"+rs.getInt("uniqueid")+", "
						+ " \"GUID\": \""+rs.getString("guid")+"\", "
						+ " \"UniqueIdEx\":\""+rs.getString("uniqueidex")+"\", "
						+ " \"UniqueIdGR\":"+rs.getInt("uniqueidgr")+", "
						+ " \"GR\":"+rs.getBoolean("gr")+", "
						+ " \"Sort\":"+rs.getBoolean("sort")+", "
						+ " \"Title\":\""+rs.getString("title")+"\", "
						+ " \"Created\":\""+rs.getString("created")+"\", "
						+ " \"Changed\":\""+rs.getString("changed")+"\", "
						+ " \"System\":"+rs.getBoolean("system")+"} ";
				
				i++;	
			}
			*/
			rs.next();
			String jsonStr = "{\"responce\":\"getingData\", \"record\":";
			jsonStr+= " {\"UniqueId\":"+rs.getInt("uniqueid")+", "
					+ " \"GUID\": \""+rs.getString("guid")+"\", "
					+ " \"UniqueIdEx\":\""+rs.getString("uniqueidex")+"\", "
					+ " \"UniqueIdGR\":"+rs.getInt("uniqueidgr")+", "
					+ " \"GR\":"+rs.getBoolean("gr")+", "
					+ " \"Sort\":"+rs.getBoolean("sort")+", "
					+ " \"Title\":\""+rs.getString("title")+"\", "
					+ " \"Created\":\""+rs.getString("created")+"\", "
					+ " \"Changed\":\""+rs.getString("changed")+"\", "
					+ " \"System\":"+rs.getBoolean("system")+"} ";
			
			jsonStr+=", \"type\":\"BaseTable\"}";
			System.out.println(jsonStr);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
	public void deleteJsonBaseTableByUidGUid(String uid, String guid) {
	
		
		try {
			Statement st = connection.createStatement();
			
			st.executeUpdate("DELETE FROM basetable WHERE uniqueid='"+uid+"' AND guid='"+guid+"';");
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	public void updateJsonBaseTable(JSONObject joRecord) {
		
		deleteJsonBaseTableByUidGUid(joRecord.get("UniqueId").toString(),joRecord.get("GUID").toString());
		
		addBaseTable(joRecord);
		
	}
	
	
	//SecondTable
	public void addSecondTable(JSONObject joRecord) {
		try {
			Statement st = connection.createStatement();
			
			st.executeUpdate("INSERT INTO secondtable values('"+ joRecord.get("id") +"', '"+ joRecord.get("guid") +"', '"+ joRecord.get("uniqid") +"','"+ 
					joRecord.get("idgr") +"','"+ joRecord.get("gr") +"','"+
					joRecord.get("sort")+"','"+ joRecord.get("date") +"','"+ 
					joRecord.get("dataname") +"','"+ joRecord.get("uniqidex") +"','"+ 
					joRecord.get("datavalue") +"','"+ joRecord.get("edizm")+"','"+ 
					joRecord.get("created")+"','"+ joRecord.get("changed")+"','"+ 
					joRecord.get("system") +"');");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public Object getJsonSecondTableByUidGUid(String uid, String guid) {
		
		ResultSet rs;
		
		try {
			Statement st = connection.createStatement();
			
			rs = st.executeQuery("SELECT * FROM secondtable WHERE uniqid='"+uid+"' AND guid='"+guid+"';");
			
			
			
			/*
			int i = 1;
			while (rs.next())
			{
				System.out.println(rs.());
				if(i>1)
					jsonStr+="\",record"+i+"\":";
				else
				jsonStr+="\"record"+i+"\":";
				jsonStr+= " {\"Id\":"+rs.getInt("id")+", "
						+ " \"GUID\": "+rs.getString("guid")+", "
						+ " \"UniqId\": "+rs.getInt("uniqid")+", "
						+ " \"idGR\": "+rs.getInt("idgr")+", "
					    + " \"GR\": "+rs.getBoolean("gr")+", "
					    + " \"Sort\": "+rs.getInt("sort")+", "
					    + " \"Date\": \""+rs.getDate("date")+"\", "
					    + " \"DataName\": \""+rs.getString("dataname")+"\", "
					    + " \"UniqIdex\": "+rs.getInt("uniqidex")+", "
					    + " \"DataValue\": \""+rs.getString("datavalue")+"\", "
					    + " \"EdIzm\": \""+rs.getString("edizm")+"\", "
						+ " \"Created\": \""+rs.getDate("created")+"\", "
						+ " \"Changed\": \""+rs.getString("changed")+"\", "
						+ " \"System\": "+rs.getInt("system")+"} ";
						
				
				i++;	
			}
			*/
			rs.next();
			String jsonStr = "{\"responce\":\"getingData\", \"record\":";
			jsonStr+= " {\"Id\":"+rs.getInt("id")+", "
					+ " \"GUID\": "+rs.getString("guid")+", "
					+ " \"UniqId\": "+rs.getInt("uniqid")+", "
					+ " \"idGR\": "+rs.getInt("idgr")+", "
				    + " \"GR\": "+rs.getBoolean("gr")+", "
				    + " \"Sort\": "+rs.getInt("sort")+", "
				    + " \"Date\": \""+rs.getDate("date")+"\", "
				    + " \"DataName\": \""+rs.getString("dataname")+"\", "
				    + " \"UniqIdex\": "+rs.getInt("uniqidex")+", "
				    + " \"DataValue\": \""+rs.getString("datavalue")+"\", "
				    + " \"EdIzm\": \""+rs.getString("edizm")+"\", "
					+ " \"Created\": \""+rs.getDate("created")+"\", "
					+ " \"Changed\": \""+rs.getString("changed")+"\", "
					+ " \"System\": "+rs.getInt("system")+"} ";
					
			
			
			jsonStr+=", \"type\":\"BaseTable\"}";
			return jsonStr;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return null;
	}
	public void deleteJsonSecondTableByUidGUid(String uid, String guid) {
		try {
			Statement st = connection.createStatement();
			
			st.executeUpdate("DELETE FROM secondtable WHERE uniqueid='"+uid+"' AND guid='"+guid+"';");
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void updateJsonSecondTable(JSONObject joRecord) {
		deleteJsonSecondTableByUidGUid(joRecord.get("UniqueId").toString(),joRecord.get("GUID").toString());
		
		addSecondTable(joRecord);
		
	}

	//Token work
	public String getGUIDServer(String servername) {
		
		ResultSet rs;
		
		try {
			
			
			
			Statement st = connection.createStatement();
			
			rs = st.executeQuery("SELECT guid FROM basetable WHERE title = '"+servername+"';");
			
			if(rs.next())
			{
				return rs.getString("guid");
			}
			
		} catch (SQLException e) {
			
			
			
		}
		return "no guid";
	}
	
	
	//Administrator work
	public static UserAccount getAdminAcc() {
		
		ResultSet rs,rs2;
		
		
		String userlogin="",userpassword="";
		try {
			
			
			
			Statement st = connection.createStatement();
			
			rs = st.executeQuery("SELECT * FROM secondtable WHERE dataname = 'adminLogin';");
			
			
			if(rs.next())
			{
				userlogin = rs.getString("datavalue");
				
			}
			rs.close();
			rs2 = st.executeQuery("SELECT * FROM secondtable WHERE dataname = 'adminPassword';");
			
			if(rs2.next())
			{
				userpassword = rs2.getString("datavalue");
			}
			rs2.close();
			UserAccount us = new UserAccount(userlogin, userpassword, "1", "", 1);
			return us;
			
		} catch (SQLException e) {
			System.out.println("fuck");
			e.printStackTrace();
			
		}
		return null;
	}

	
	//Password work
	public void addSinglePasswordForAccount(String password, UserAccount acc) {
		String guid = getGUIDUserByLogin(acc);
		
		try {
			Statement st = connection.createStatement();
			st.executeUpdate("DELETE FROM secondtable WHERE guid='"+guid+"' AND dataname='SinglePassword';");
			st.close();
		
			Statement st2 = connection.createStatement();
			st2.executeUpdate("INSERT INTO secondtable(guid, dataname, datavalue) values('"+guid+"','SinglePassword','"+ password +"');");
			st.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}


	public boolean lockAccount(UserAccount acc) {
		
		String guid = getGUIDUserByLogin(acc);
		try {
			Statement st = connection.createStatement();
			st.executeUpdate("DELETE FROM secondtable WHERE guid='"+guid+"' AND dataname='accountAccess';");
			
			
			
			st.executeUpdate("INSERT INTO secondtable(guid, dataname, datavalue) values('"+guid+"','accountAccess','lock');");
			
			
			
			
			
			st.close();
			
			return true;
			
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
	}


	public boolean getAccountAccess(UserAccount acc) {
		
		
		
		ResultSet rs;
		String guid = getGUIDUserByLogin(acc);
		try {
			Statement st = connection.createStatement();
			
			rs = st.executeQuery("select * from secondtable where guid = '"+guid+"' and dataname = 'accountAccess';");
			
			
			if(rs.next())
			{
				if (rs.getString("datavalue").equals("lock"))
				{
					
					return false;
				}else {
					return true;
			    }
				
				
			}
			
			
			st.close();
			
			return false;
			
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

		
}