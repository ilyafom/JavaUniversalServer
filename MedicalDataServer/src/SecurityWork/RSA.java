package SecurityWork;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;


public class RSA {
	public static String enc() throws Exception
	{
		byte[] modulusBytes = Base64.getDecoder().decode("MIGJAoGBAO27pSj76MF0pNCyJwlu7ujdLZ14Xel11gN4Dy3/6ERO+WjMVKnTeXlrW0sCdUaJOOkbTStJQO0TYOZ6nnGTDsXcYVJgyVv/dLYKP7meV5YSj67tmGbaz/mGLm7H5zgQHSu3q2oyrM7kCrtJWA2ILcqk4sz48sBi/fnIOINTwp7hAgMBAAE=");
		byte[] exponentBytes = Base64.getDecoder().decode("AQAB");
		BigInteger modulus = new BigInteger(1, modulusBytes );
		BigInteger exponent = new BigInteger(1, exponentBytes);

		RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, exponent);
		KeyFactory fact = KeyFactory.getInstance("RSA");
		PublicKey pubKey = fact.generatePublic(rsaPubKey);

		System.out.println("Key ---> " + new String(pubKey.getEncoded()) + " keysize ----> "+  pubKey.getEncoded().length);
		
		
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);

		byte[] plainBytes = new String("big kitty dancing").getBytes("UTF-8");
		byte[] cipherData = cipher.doFinal( plainBytes );
		String encryptedString = new String(Base64.getEncoder().encode(cipherData));
		return encryptedString;
		
		
	}
}