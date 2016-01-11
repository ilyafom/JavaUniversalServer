package SecurityWork;



import java.security.Security;

import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Aes {
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	private byte[] key = "16bytesrandmassv".getBytes();
	private byte[] iv = "16bytesrandmassv".getBytes();				
	
	
	public void setKey(String key)
	{
		this.key = key.getBytes();
	
	}
	
	
	
	public  byte[] encrypt(byte[] str1) 
    { 
        try 
        { 
           
            byte header[] = "".getBytes(); 
           
            // encrypt 
            AEADParameters parameters = new AEADParameters(new KeyParameter(key), 128, iv, header); 
            GCMBlockCipher gcmEngine = new GCMBlockCipher(new AESFastEngine()); 
            gcmEngine.init(true, parameters); 

            byte[] encMsg = new byte[gcmEngine.getOutputSize(str1.length)]; 
            int encLen = gcmEngine.processBytes(str1, 0, str1.length, encMsg, 0); 
            encLen += gcmEngine.doFinal(encMsg, encLen); 

            
            
            System.out.println("1 " + new String (encMsg));
            return encMsg;
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
        }
		return str1; 
    } 
	
	
	
	
	public  byte[] decrypt(byte[] str1) 
    { 
        try 
        { 
            byte header[] = "".getBytes(); 

           
    		
            
            AEADParameters parameters = new AEADParameters(new KeyParameter(key), 128, iv, header); 
            GCMBlockCipher gcmEngine = new GCMBlockCipher(new AESFastEngine()); 
            
           
            // decrypt 
            gcmEngine.init(false, parameters); 
            System.out.println(str1.length);
            byte[] decMsg = new  byte[gcmEngine.getOutputSize(str1.length)]; 
            int decLen = gcmEngine.processBytes(str1, 0, str1.length, decMsg, 0); 
            System.out.println(decLen);
            decLen += gcmEngine.doFinal(decMsg, decLen); 
            System.out.println(decLen);

            System.out.println("MSG===" + new String(decMsg)); 
            return decMsg;
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
        }
		return str1; 
    } 
	
	
	
	
	
	
	
	
	
	
	
}
