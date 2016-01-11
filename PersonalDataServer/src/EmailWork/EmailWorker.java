package EmailWork;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailWorker {

	public static boolean sendMessageToEmail(String email,String text)
	{
		String subject = "Medical card Suppot"; 
		String content = "Verification code: " + text; 
         
        String smtpHost="smtp.yandex.ru"; 
        String login="fominiliyawar@yandex.ru"; 
        String password="werbyb25367811"; 
        String smtpPort="25";  
        
        Authenticator auth = new MyAuthenticator(login, password); 
        
        Properties props = System.getProperties(); 
        props.put("mail.smtp.port", smtpPort); 
        props.put("mail.smtp.host", smtpHost); 
        props.put("mail.smtp.auth", "true"); 
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.mime.charset", "UTF-8"); 
        Session session = Session.getDefaultInstance(props, auth); 
 
        
        Message msg = new MimeMessage(session); 
        try {
			msg.setFrom(new InternetAddress("fominiliyawar@yandex.ru"));
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email)); 
        	msg.setSubject(subject); 
        	msg.setText(content); 
        	Transport.send(msg);
        	return true;
        } catch (Exception e) {
		
        	
        	e.printStackTrace();
        	return false;
		}
		
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
