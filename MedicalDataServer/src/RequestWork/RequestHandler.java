package RequestWork;

import java.io.IOException;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import DBWork.DataBaseAccess;
import TokenWork.TokenWorker;
import UserAccountWork.PasswordСhangeWork;
import UserAccountWork.RegistrationDataChageWork;
import UserAccountWork.UserAccount;
import WorkWithServers.ServerData;

public class RequestHandler {

	//Обработчик запросов
	public static Object requestsHandler(String request) {
		
	
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			
			obj = parser.parse(request);
		
		
			
			
		} catch (ParseException e) {
			
			e.printStackTrace();
			return "{\"response\": \"Error parsing json!\"}";
		}
		JSONObject jsonObjectReq = (JSONObject)obj;
		//{request: ADD, record:{ UniqueId: 107, GUID: C290A815-BAAA-4455-82EA-F18F5D0CEF2E, 
		//UniqueIdEx: 24.335.55524.33, UniqueIdGR: 98, GR: false, Sort: 3,Title: Артрит (Воспаление сустава), 
		//Created: Илья Фомин, Changed:, System: false},type: BaseTable}
    	if (!(jsonObjectReq.containsKey("info")))
		{
			if (jsonObjectReq.get("request").equals("authorization"))//Авторизация пользователя на сервере
			{
				UserAccount acc = new UserAccount(jsonObjectReq.get("login").toString(),
						jsonObjectReq.get("password").toString(), 
						jsonObjectReq.get("dev").toString(), 
						"no email",
						1);
				
				
				
				String ansv = "";
				if (checkingAccount(acc))
				{
					ansv+= "{\"response\": \"Authorisation true\",";
					if (checkingDeviceCode(acc))
					{
						String token = TokenWorker.generateNewToken(acc); 
						ansv+=  "\"code\":\"is confirm\",\"token\":\"" + token + "\"}";
					
					}else
						ansv+=  "\"code\":\"is not confirm\"}";
					
					
					
					return (Object)ansv;
				}else{
					
					if (checkLogin(acc.getLogin()))
					{
						
						if (getAccountAccess(acc))
						{
							return (Object)"{\"response\": \"Authorisation Error, check login or password\"}";	
						}else
							return (Object)"{\"response\": \"Account is locked\"}";
						
					}else
						return (Object)"{\"response\": \"Authorisation Error\"}";
				}
				
				
			}
			//Registration work
			else if(jsonObjectReq.get("request").equals("registration"))
			{
				
				
				UserAccount acc = new UserAccount(jsonObjectReq.get("login").toString(),
						jsonObjectReq.get("password").toString(), 
						jsonObjectReq.get("dev").toString(),
						jsonObjectReq.get("mail").toString(),
						1);
				
				if (checkingAccount(acc))
				{	
					String ansv = "";
				
					if (checkingDeviceCode(acc))
					{
						
						if (checkMail(acc))
						{
							String token = TokenWorker.generateNewToken(acc); 
							
							ansv+=  "{\"response\": \"Authorisation true\",\"code\":\"is confirm\",\"token\":\"" + token + "\"}";
							return (Object)ansv;
						}else 
							return (Object)"{\"response\": \"no registration, check registration data\"}";
					}else 
					{
						if (CodeConfirmingWork.addCodeToConfirm(acc))
						{
							return (Object)"{\"response\": \"registration is true, need confirmation\"}";
						}else
							return (Object)"{\"response\": \"no registration, check registration data\"}";
						
					}
					
					
				}else {
				
				
					String ansv = "";
					
					if (RegistrationWork.startRegistration(acc))
					{
						ansv+= "{\"response\": \"registration is true, need confirmation\"}";
						
						return (Object)ansv;
					}else 
					{
						ansv+= "{\"response\": \"no registration, check registration data\"}";
						
						return (Object)ansv;
						
					}
				}
				
			}else if(jsonObjectReq.get("request").equals("confirmRegistration"))//внесение кода в бд если код из майла совподает с кодом в массиве
			{
			
				UserAccount acc = new UserAccount(jsonObjectReq.get("login").toString(),
						jsonObjectReq.get("password").toString(), 
						jsonObjectReq.get("dev").toString(), 
						"no email",
						1);
				
				String ansv = "";
				
				
				String codeFromMail = jsonObjectReq.get("codeFromMail").toString();
				if (checkLogin(jsonObjectReq.get("login").toString()))
				{	
					if (CodeConfirmingWork.addCodeForAccount(acc,codeFromMail))
					{
						ansv+= "{\"response\": \"сode is added\"}";
						
						
						
						return (Object)ansv;
					}else
						return (Object)"{\"response\": \"сode is not added\"}";
				}
				
				
				
				
	
				if (RegistrationWork.addNewAccount(acc,codeFromMail))
				{
					ansv+= "{\"response\": \"сode is added\"}";
					
					
					
					return (Object)ansv;
				}else{
					return (Object)"{\"response\": \"сode is not added\"}";
				
					
					
				}
			}
			//RegistrationData Change work
			else if(jsonObjectReq.get("request").equals("confirmRegistrationChange"))
			{
			
				UserAccount acc = new UserAccount(jsonObjectReq.get("login").toString(),
						jsonObjectReq.get("password").toString(), 
						jsonObjectReq.get("dev").toString(), 
						"no email",
						1);
				
				String ansv = "";
				
				
				String codeFromMail = jsonObjectReq.get("codeFromMail").toString();
				
				if (RegistrationDataChageWork.addChangetRegistrationData(acc, codeFromMail))
				{
					ansv+= "{\"response\": \"changes are add\"}";
					
					
					
					return (Object)ansv;
				}else
					return (Object)"{\"response\": \"changes are not add\"}";
			
				
				
				
			}else if(jsonObjectReq.get("request").equals("registrationChange"))
			{
				
				UserAccount acc = new UserAccount(jsonObjectReq.get("login").toString(),
						jsonObjectReq.get("password").toString(), 
						jsonObjectReq.get("dev").toString(), 
						"no email",
						1);
				
				String ansv = "";
				
				
				String email = jsonObjectReq.get("mail").toString();
				if (checkLogin(jsonObjectReq.get("login").toString()))
				{	
					if (RegistrationDataChageWork.confirmChangetRegistrationData(acc, email))
					{
						ansv+= "{\"response\": \"changes need to set up the confirmation\"}";
						
						
						
						return (Object)ansv;
					}else
						return (Object)"{\"response\": \"changes are not made\"}";
				}else {
					
					
					return (Object) "{\"response\": \"login is not found\"}";
				}
				
				
				
			}
			//Account access work
			else if(jsonObjectReq.get("request").equals("lockAccount"))
			{
			
				UserAccount acc = new UserAccount(jsonObjectReq.get("login").toString(),
						jsonObjectReq.get("password").toString(), 
						jsonObjectReq.get("dev").toString(), 
						"no email",
						1);
				
				String ansv = "";
				
				
				if (checkingAccount(acc))
				{	
					if (lockAccount(acc))
					{
						ansv+= "{\"response\": \"Account is locked\"}";
						
						
						
						return (Object)ansv;
					}else
						return (Object)"{\"response\": \"changes are not add\"}";
				}
				
				
				
			}
			//SinglePassword work
			else if (jsonObjectReq.get("request").equals("changePassword"))
			{
				UserAccount acc = new UserAccount(jsonObjectReq.get("login").toString(),
						"", 
						"", 
						"",
						1);
				String singlepassword = PasswordСhangeWork.generateSinglePassword();
			
				
				PasswordСhangeWork.addSinglePassword(singlepassword, acc);
			}
			//SecondTable work(get,add,update,delete)
			else if (jsonObjectReq.get("request").equals("ADD"))
			{
				String token = jsonObjectReq.get("token").toString();
				
				if (!(TokenWorker.checkToken(token)) )
				{
					return (Object)"{\"response\": \"invalid token\"}";
				}else {
					if ( !(TokenWorker.getObjectToken(token).guid.equals( (jsonObjectReq.get("guid")).toString()) ))
					{	
						return (Object)"{\"response\": \"invalid guid\"}";
					}
					
					if ( !(TokenWorker.getObjectToken(token).devCode.equals(jsonObjectReq.get("dev"))) )
						return (Object)"{\"response\": \"invalid device code\"}";
					
				}
				
				if (jsonObjectReq.get("type").equals("BaseTable"))
				{
					JSONObject joRecord = (JSONObject)jsonObjectReq.get("record");	
					DataBaseAccess access = new DataBaseAccess();
					access.addBaseTable(joRecord);
					
				
				}else if(jsonObjectReq.get("type").equals("BaseTable+"))
				{
					int i = 1;
					while (jsonObjectReq.get("record"+i) != null){
						
						JSONObject joRecord = (JSONObject) jsonObjectReq.get("record"+i);	
						DataBaseAccess access = new DataBaseAccess();
						access.addBaseTable(joRecord);
						i++;
						
					}
					
				}if (jsonObjectReq.get("type").equals("SecondTable"))
				{
					JSONObject joRecord = (JSONObject) jsonObjectReq.get("record");	
					DataBaseAccess access = new DataBaseAccess();
					access.addSecondTable(joRecord);
					
				
				}else if(jsonObjectReq.get("type").equals("SecondTable+"))
				{
					int i = 1;
					while (jsonObjectReq.get("record"+i) != null){
						
						JSONObject joRecord = (JSONObject) jsonObjectReq.get("record"+i);	
						DataBaseAccess access = new DataBaseAccess();
						access.addSecondTable(joRecord);
						i++;
						
					}
					
				}
				
				return (Object)"Data is add";
			}else if(jsonObjectReq.get("request").equals("GET"))
			{
				//{request:"GET",token:"adsdasd4554asd45", uniqueid:"asdasd", guid:"asdasd-asdasd-asdasd-asdasd",type:BaseTable }
				String token = jsonObjectReq.get("token").toString();
				if (!(TokenWorker.checkToken(token)) )
				{
					return (Object)"{\"response\": \"invalid token\"}";
				}else {
					if ( !(TokenWorker.getObjectToken(token).guid.equals( (jsonObjectReq.get("guid")).toString()) ))
					{	
						return (Object)"{\"response\": \"invalid guid\"}";
					}
					
					if ( !(TokenWorker.getObjectToken(token).devCode.equals(jsonObjectReq.get("dev"))) )
						return (Object)"{\"response\": \"invalid device code\"}";
					
				}
				
				if (jsonObjectReq.get("type").equals("BaseTable"))
				{
					
					DataBaseAccess access = new DataBaseAccess();
					return access.getJsonBaseTableByUidGUid(jsonObjectReq.get("UniqueId").toString(),jsonObjectReq.get("GUID").toString());
					
				}else if (jsonObjectReq.get("type").equals("SecondTable"))
				{
					DataBaseAccess access = new DataBaseAccess();
					return access.getJsonSecondTableByUidGUid(jsonObjectReq.get("UniqueId").toString(),jsonObjectReq.get("GUID").toString());
				}
				
				
			}else if(jsonObjectReq.get("request").equals("DELETE"))
			{
				String token = jsonObjectReq.get("token").toString();
				if (!(TokenWorker.checkToken(token)) )
				{
					return (Object)"{\"response\": \"invalid token\"}";
				}else {
					if ( !(TokenWorker.getObjectToken(token).guid.equals( (jsonObjectReq.get("guid")).toString()) ))
					{	
						return (Object)"{\"response\": \"invalid guid\"}";
					}
					
					if ( !(TokenWorker.getObjectToken(token).devCode.equals(jsonObjectReq.get("dev"))) )
						return (Object)"{\"response\": \"invalid device code\"}";
					
				}
				if (jsonObjectReq.get("type").equals("BaseTable"))
				{
					
					DataBaseAccess access = new DataBaseAccess();
					access.deleteJsonBaseTableByUidGUid(jsonObjectReq.get("UniqueId").toString(),jsonObjectReq.get("GUID").toString());
					return "{response:data is delete}";
				
				
				}else if (jsonObjectReq.get("type").equals("SecondTable"))
				{
					
					DataBaseAccess access = new DataBaseAccess();
					access.deleteJsonSecondTableByUidGUid(jsonObjectReq.get("UniqueId").toString(),jsonObjectReq.get("GUID").toString());
					return "{response:data is delete}";
				
				}
				
				return "{response:data is not delete}";
				
			}else if(jsonObjectReq.get("request").equals("UPDATE"))
			{
				String token = jsonObjectReq.get("token").toString();
				if (!(TokenWorker.checkToken(token)) )
				{
					return (Object)"{\"response\": \"invalid token\"}";
				}else {
					if ( !(TokenWorker.getObjectToken(token).guid.equals( (jsonObjectReq.get("guid")).toString()) ))
					{	
						return (Object)"{\"response\": \"invalid guid\"}";
					}
					
					if ( !(TokenWorker.getObjectToken(token).devCode.equals(jsonObjectReq.get("dev"))) )
						return (Object)"{\"response\": \"invalid device code\"}";
					
				}
				if (jsonObjectReq.get("type").equals("BaseTable"))
				{
					JSONObject joRecord = (JSONObject) jsonObjectReq.get("record");
					DataBaseAccess access = new DataBaseAccess();
					access.updateJsonBaseTable(joRecord);
					
					return "{response:data is update}";
					
				}else if (jsonObjectReq.get("type").equals("SecondTable"))
				{
					JSONObject joRecord = (JSONObject) jsonObjectReq.get("record");
					DataBaseAccess access = new DataBaseAccess();
					access.updateJsonSecondTable(joRecord);
					
					return "{response:data is update}";
					
					
				}
				
				
			}else if(jsonObjectReq.get("request").equals("addnewserver"))
			{
				String adminLogin = (DataBaseAccess.getAdminAcc()).getLogin();
				String adminPassword = (DataBaseAccess.getAdminAcc()).getPassword();
				
				
				if (jsonObjectReq.get("login").equals(adminLogin))
				{
					if (jsonObjectReq.get("password").equals(adminPassword))
					{
						JSONObject joServerRecord = (JSONObject) jsonObjectReq.get("serverdata");	
						String name = joServerRecord.get("name").toString();
						String ip = joServerRecord.get("ip").toString();
						String port = joServerRecord.get("port").toString();
						String token = joServerRecord.get("token").toString();
						String guid = joServerRecord.get("guid").toString();
						
						
						DataBaseAccess access = new DataBaseAccess();
						
						access.addServerData(name, ip, port, token, guid);
						return "{response:server is added}";
					}
				}
				
				
			}else if(jsonObjectReq.get("request").equals("authorizedThisServer"))
			{
				
				String adminLogin = (DataBaseAccess.getAdminAcc()).getLogin();
				String adminPassword = (DataBaseAccess.getAdminAcc()).getPassword();
				
				
				if (jsonObjectReq.get("login").equals(adminLogin))
				{
					if (jsonObjectReq.get("password").equals(adminPassword))
					{
						DataBaseAccess access = new DataBaseAccess();
						
						ServerData sd = access.getServerDataFromDB(jsonObjectReq.get("serverName").toString());
						System.out.println(sd.ip+" "+ sd.port);
						
						
						try {
							Socket s = new Socket(sd.ip, sd.port);

							s.getOutputStream().write( ("{\"request\":\"authorization\", \"login\":\"accountserver\", "
									+ "\"password\":\"werbyb25367811\", \"dev\":\"1111\"}").getBytes());
							
							byte buf[] = new byte[64*1024];
				            
							
							int r = s.getInputStream().read(buf);
				            
				            
				            String data = new String(buf, 0, r);
							
				            
				            s.close();
				            
				    		try {
				    			
				    			obj = parser.parse(data);
				    		
				    		} catch (ParseException e) {
				    			
				    			e.printStackTrace();
				    			return "{\"response\": \"Error parsing json!\"}";
				    		}
				    		JSONObject jsonObjectReq2 = (JSONObject)obj;
				            
				            String guid = access.getGUIDServer(jsonObjectReq.get("serverName").toString());
				    		access.updateServerToken(guid, jsonObjectReq2.get("token").toString());
				    		
				    		
				    		return (Object)data;
			            
			            } catch (IOException e) {
							
							e.printStackTrace();
						}
						
						
						return "{response:authorize is true}";
					}
				}
				
				
			}
		
			
			
			
			
			
		}else if (jsonObjectReq.containsKey("info"))
		{
			String servername = jsonObjectReq.get("info").toString();
			ServerData sd = new DataBaseAccess().getServerDataFromDB(servername);
			System.out.println(sd.ip + " " + sd.port + " " + sd.name + " " + sd.token);
			
			DataBaseAccess dataBaseAccess = new DataBaseAccess();
			
			jsonObjectReq.remove("token");
			jsonObjectReq.put("token", sd.token);
			jsonObjectReq.remove("info");
			
			if (jsonObjectReq.containsKey("type"))
			{
				jsonObjectReq.remove("guid");
				jsonObjectReq.put("guid", dataBaseAccess.getGUIDServer(servername) );
				jsonObjectReq.remove("info");
				
			}
			
			try {
				Socket s = new Socket(sd.ip, sd.port);

				s.getOutputStream().write(jsonObjectReq.toJSONString().getBytes());
				
				byte buf[] = new byte[64*1024];
	            
				
				int r = s.getInputStream().read(buf);
	            
	            
	            String data = new String(buf, 0, r);
				
	            
	            s.close();
	            return (Object)data;
            
            } catch (IOException e) {
				
				e.printStackTrace();
			}

		}
		
		
		
		return (Object)"{\"request\":\"Error\"}";
	}
	
	
	private static boolean getAccountAccess(UserAccount acc) {
		DataBaseAccess dataBaseAccess = new DataBaseAccess();
		if (dataBaseAccess.getAccountAccess(acc))
		{
			return true;
		}	
		return false;
	}


	private static boolean lockAccount(UserAccount acc) {
		
		DataBaseAccess dataBaseAccess = new DataBaseAccess();
		if (dataBaseAccess.lockAccount(acc))
		{
			return true;
		}
		return false;
		
	}


	private static boolean checkMail(UserAccount acc) {
		DataBaseAccess dataBaseAccess = new DataBaseAccess();
		if (acc.getEmail().equals(dataBaseAccess.getMailFromDb(acc)))
		{
			return true;
		}
		return false;
	}


	private static boolean checkLogin(String login) {
		DataBaseAccess dataBaseAccess = new DataBaseAccess();
		
		return dataBaseAccess.checkLogin(login);
	}


	//Проверка аккаунта
	private static boolean checkingAccount(UserAccount account)
	{
		DataBaseAccess dataBaseAccess = new DataBaseAccess();
		
		return dataBaseAccess.accountValidate(account); 
	}
	
	//Проверка компьютера является ли он доверенным
	private static boolean checkingDeviceCode(UserAccount account)
	{
		DataBaseAccess dataBaseAccess = new DataBaseAccess();
		
		
		return dataBaseAccess.deviceCodeValidate(account);
	}
	

}

