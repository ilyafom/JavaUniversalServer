package RequestWork;

import java.util.ArrayList;
import java.util.List;
import DBWork.DataBaseAccess;
import EmailWork.EmailWorker;

import java.util.Random;

import javax.mail.*;

import UserAccountWork.UserAccount;

public class CodeConfirmingWork {

	private static List<UserAccWithCodeFromMail> listCodeConfirm = new ArrayList<UserAccWithCodeFromMail>();
	

	public static boolean addCodeToConfirm(UserAccount acc) {
		
		UserAccWithCodeFromMail accWithCodeFromMile = new UserAccWithCodeFromMail();
		accWithCodeFromMile.acc = acc;
		
		DataBaseAccess db = new DataBaseAccess();
		String mail = db.getMailFromDb(acc);
		if (mail.equals("no mail")) return false;
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

	
	public static boolean addCodeForAccount(UserAccount acc, String codeFromMail) {
		
		
		for (UserAccWithCodeFromMail it : listCodeConfirm) {
			if( (acc.getLogin().equals(it.acc.getLogin())) && (acc.getPassword().equals(it.acc.getPassword()))
			&&  (it.codeFromMail.toUpperCase().equals(codeFromMail)))
			{
				
				
				DataBaseAccess acces = new DataBaseAccess();
				acces.addCodeToBD(acc);
				
				return true;
				
			}
			
		}
		
		
		
		return false;
	}


	
}



class MyAuthenticator extends Authenticator { 
    private String user; 
    private String password; 
 
    MyAuthenticator(String user, String password) { 
        this.user = user; 
        this.password = password; 
    } 
 
    public PasswordAuthentication getPasswordAuthentication() { 
        String user = this.user; 
        String password = this.password; 
        return new PasswordAuthentication(user, password); 
    } 
}
