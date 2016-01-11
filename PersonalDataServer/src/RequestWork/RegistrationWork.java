package RequestWork;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import DBWork.DataBaseAccess;
import EmailWork.EmailWorker;
import UserAccountWork.UserAccount;

public class RegistrationWork {
	
	private static List<UserAccWithCodeFromMail> listCodeConfirm = new ArrayList<UserAccWithCodeFromMail>();

	public static boolean startRegistration(UserAccount acc) {
		
		UserAccWithCodeFromMail accWithCodeFromMile = new UserAccWithCodeFromMail();
		accWithCodeFromMile.acc = acc;
		
		String mail = acc.getEmail();
		
		try{
		String codeToEmail = sendCodeToEmail(mail);
		
		accWithCodeFromMile.codeFromMail = codeToEmail;
		
		
		listCodeConfirm.add(accWithCodeFromMile);
		}catch(Exception e)
		{
			e.printStackTrace();
			
			return false;
		}
		
		
		return true;
	}
	
	public static String sendCodeToEmail(String email) throws Exception {
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
		
		System.out.println(code.toUpperCase());
		EmailWorker.sendMessageToEmail(email, code.toUpperCase());
        
		return code;
	}

	
	public static boolean addNewAccount(UserAccount acc, String codeFromMail) {

		for (UserAccWithCodeFromMail it : listCodeConfirm) {
			
			
			if( (acc.getLogin().equals( it.acc.getLogin() ) ) && (acc.getPassword().equals(it.acc.getPassword()))
			&& (it.acc.getCode().equals(acc.getCode()))
			&& (it.codeFromMail.toUpperCase().equals(codeFromMail)))
			{
				
				acc.setEmail(it.acc.getEmail());
				DataBaseAccess acces = new DataBaseAccess();
				acces.addNewAccount(acc);
				acces.addCodeToBD(acc);
				return true;
				
			}
			
		}
		return false;
		
	}

	
	
	
}
