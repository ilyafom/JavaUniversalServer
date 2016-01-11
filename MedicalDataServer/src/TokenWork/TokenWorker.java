package TokenWork;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import DBWork.DataBaseAccess;
import UserAccountWork.UserAccount;


public class TokenWorker {

	private static List<Token> listTokens = new ArrayList<Token>();

	public static String generateNewToken(UserAccount acc) {
		
		
		String tokenStr = "";
		Random rand = new Random();
		
		for (int i = 0; i < 25; i++) {
			
			if (rand.nextInt(100)>40)
			{
				if (rand.nextInt(100)>40)
				{
				
					tokenStr += ""+(char)(rand.nextInt(25) + 97);
					
				}else
				{
					tokenStr += ""+(char)(rand.nextInt(25) + 65);
					
				}
			}else
			{
				tokenStr += ""+(rand.nextInt(9));
				
			}
		
		}
		System.out.println(tokenStr);
		
		
		
		Token token = new Token();
		token.tokenStr = tokenStr;
		token.login = acc.getLogin();
		token.devCode = acc.getCode();
		DataBaseAccess access =  new DataBaseAccess();
		token.guid = access.getGUIDUserByLogin(new UserAccount(acc.getLogin(),acc.getPassword(),acc.getCode(),acc.getEmail(),1));
		token.time = UpdateTokenTime();

		token.isSingle = true;
		listTokens.add(token);
		
		return tokenStr;
	}
	
	private static Date UpdateTokenTime() {
		
		Calendar cal = Calendar.getInstance();
		
		
		return cal.getTime();
	}

	public static boolean checkToken(String tokenStr)
	{
		Calendar cal = Calendar.getInstance();
        
		for (Token token : listTokens) {
			if (token.tokenStr.equals(tokenStr))
			{
				
				
				
				if ( (cal.getTime().getTime() - token.time.getTime()) < 7200000)
				{
					System.out.println("niga bitch");
					token.time = UpdateTokenTime();
					return true;
				}
				
			}
		}
		
		return false;
	}
	
	
	
	public static Token getObjectToken(String tokenStr)
	{
		for (Token token : listTokens) {
			if (token.tokenStr.equals(tokenStr))
				return token;
		}
		return null;
	}
	
	
	public static Token getTokenByLogin(String login)
	{
		for (Token token : listTokens) {
			if (token.login.equals(login))
				return token;
		}
		return null;		
	}
	
	
	
	
	
	
}
