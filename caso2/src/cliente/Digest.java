
package cliente;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
public class Digest {

	public Digest() {
		// TODO Auto-generated constructor stub
	}
	public static byte[] getDigest(String alg, byte[] buf){
		try{
			MessageDigest digest=MessageDigest.getInstance(alg);
			digest.update(buf);
			return digest.digest();
		}catch(Exception e){
			return null;
		}
	}
	public static void imprimirHexa(byte[] byteArray){
		String out="";
		for(int i=0; i<byteArray.length;i++){
			if((byteArray[i] & 0xff)<=0xf) out+="0";
			out+=Integer.toHexString(byteArray[i] & 0xff).toLowerCase();
		}
		System.out.println(out);
	}

	public static String hMacSha1(String msg, SecretKey key) {
		String digest = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(key);

			byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));
			StringBuffer hash = new StringBuffer();

			for (int i = 0; i < bytes.length; i++) {
				String hex = Integer.toHexString(0xFF & bytes[i]);
				if (hex.length() == 1) {
					hash.append('0');
				}
				hash.append(hex);
			}

			digest = hash.toString();
		} catch (Exception e) {
		} 
		return digest;
	}
}
