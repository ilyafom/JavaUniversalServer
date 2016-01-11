package UserAccountWork;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import DBWork.DataBaseAccess;
import EmailWork.EmailWorker;
import RequestWork.UserAccWithCodeFromMail;

public class RegistrationDataChageWork {

	
	
	private static List<UserAccWithCodeFromMail> useraccs = new ArrayList<UserAccWithCodeFromMail>();
	
	public static boolean confirmChangetRegistrationData(UserAccount acc, String email) {
		//Create code
		Random rand = new Random();
		
		String code = "";
		for (int i = 0; i < 6; i++) {
			
			if (rand.nextInt(100)>40)
			{
			
				code += ""+(char)(rand.nextInt(25) + 97);
				
			}else
			{
				code += ""+(rand.nextInt(9));
				
			}
		
		}
		
		
		
		DataBaseAccess db = new DataBaseAccess();


		UserAccWithCodeFromMail acc1 = new UserAccWithCodeFromMail();
		acc1.acc = acc;
		acc1.codeFromMail = code;
		useraccs.add(acc1);
		
		
		String emailFromDb = db.getMailFromDb(acc);
		
		
		
		
		
				
		return EmailWorker.sendMessageToEmail(emailFromDb, code);

	}
	
	
	public static boolean addChangetRegistrationData(UserAccount us, String codeFromMail)
	{
		
		
		
		
		for (UserAccWithCodeFromMail u : useraccs) {
			
			System.out.println("1 --- "  + codeFromMail);
			System.out.println("2 --- "  + u.codeFromMail);
			if ((us.getLogin().equals(u.acc.getLogin()))
					
				&& (codeFromMail.equals(u.codeFromMail)	))
			{
				DataBaseAccess acc = new DataBaseAccess();
				acc.updateAccountData(us);
				return true;
			}
			
			
		}
		return false;
	}
	
	
	
	
}
