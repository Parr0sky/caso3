package cliente;

import javax.crypto.Cipher;

import javax.crypto.SecretKey;

public class Simetrico {
	private final static String PADDING="AES/ECB/PKCS5Padding";

	public static String cifrar(SecretKey llave, String texto){
		byte[] textoCifrado;
		try{
			Cipher cifrador=Cipher.getInstance(PADDING);
			byte[] textoClaro=texto.getBytes();
			cifrador.init(Cipher.ENCRYPT_MODE, llave);
			textoCifrado=cifrador.doFinal(textoClaro);
			return new String(textoCifrado);
		}catch(Exception e){
			System.out.println("Exception: "+e.getMessage());
			return null;
		}
	}
	
	
	public static String descifrar(SecretKey llave, String txt){
		byte[] txtBytes=txt.getBytes();
		byte[] txtClaro;
		try{
			Cipher cifrador=Cipher.getInstance(PADDING);
			cifrador.init(Cipher.DECRYPT_MODE, llave);
			txtClaro=cifrador.doFinal(txtBytes);
			
		}catch(Exception e){
			System.out.println();
			return null;
		}
		return new String(txtClaro);
	}
}

