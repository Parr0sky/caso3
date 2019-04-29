package cliente;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.util.encoders.Base64;

public class Main {

	public final static String[] commands={"HOLA","ALGORITMOS","OK","ERROR"};
	public final static String[] separador={";", ","};
	public final static String separadorAlgoritmo=":";
	public final static String ALGs="AES";
	public final static String ALGa="RSA";
	public final static String ALGhmac="HMACSHA1";
	public static final String DIRECCION = "localhost";


	public static void imprimir(byte[] contenido){
		int i=0;
		for(;i<contenido.length-1;i++){
			System.out.print(contenido[i]+" ");
		}
		System.out.println(contenido[i]+" ");
	}

	public static void main(String[] args){

		String datos1=15+separador[0]+"44 11.4561"+separador[1]+"13 10.5974";
		Cliente cliente=null;
		Socket socket=null;
		String strUsuario="";
		BufferedReader bf=null;
		BufferedReader lector = null;
		PrintWriter pw=null;
		X509Certificate certServer = null;
		String strServidor="";
		PublicKey pk=null;
		//coneccion de server y texto estandar
		try
		{
			socket = new Socket(DIRECCION, 8080);
			bf= new BufferedReader(new InputStreamReader(System.in));
			lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw=new PrintWriter(socket.getOutputStream(),true);
		}
		catch (Exception e)
		{
			System.err.println("Exception: " + e.getMessage());
			System.exit(1);
		}


		System.out.println("Escriba el comando:");
		try {
			strUsuario=bf.readLine();
			if(strUsuario!="" && strUsuario!=null){
				if(strUsuario.equalsIgnoreCase(commands[0]))
				{
					pw.println(commands[0]);
					strServidor=lector.readLine();
					System.out.println(strServidor);
					System.out.println("Escriba el comando");
					if(strServidor!= null && strServidor.equals(commands[2])){
						//envio algs
						String algs=commands[1]+separadorAlgoritmo+ALGs+separadorAlgoritmo+ALGa+separadorAlgoritmo+ALGhmac;
						pw.println(algs);
						strServidor=lector.readLine();
						if(strServidor!= null && strServidor.equals(commands[2])){
							System.out.println(strServidor);
							//certificado del cliente
							cliente=new Cliente();
							X509Certificate certCliente= cliente.getCertificado();

							//envio certificado del cliente
							byte[] certBytes=certCliente.getEncoded();
							String certString= DatatypeConverter.printHexBinary(certBytes);
							pw.println(certString);
							String certServerSTR;
							if((strServidor = lector.readLine()) !=null){
								//recepcion certificado del server
								certServerSTR=strServidor;
								byte[] x509cert = DatatypeConverter.parseHexBinary(certServerSTR);

								CertificateFactory cf=CertificateFactory.getInstance("X.509");
								InputStream in = new ByteArrayInputStream(x509cert);
								certServer = (X509Certificate)cf.generateCertificate(in);
								pk = certServer.getPublicKey();
								//enviar localizacion cifrada	
								KeyGenerator kg;
								kg = KeyGenerator.getInstance("AES");
								SecretKey sk=kg.generateKey();
								pw.println(Asimetrico.cifrar(certServer.getPublicKey(), Base64.toBase64String(sk.getEncoded())));
								strServidor=lector.readLine();
								System.out.println(strServidor);
								//recibir 128 bytes
								if(strServidor!=null){
									String des=Asimetrico.descifrar(strServidor, cliente.getKeyPair().getPrivate());
									if(des.equals(DatatypeConverter.printHexBinary(sk.getEncoded()))) {
										pw.println(Simetrico.cifrar(sk, datos1));
										String sha=Digest.hMacSha1(datos1, sk);
										pw.println(sha);
										System.out.println("sha");
										strServidor=lector.readLine();
										if(strServidor!= null ){
											if(strServidor.equals(commands[3]))
											{
												System.out.println("Error");
											}
											else
											{
												String strDescifrado=Asimetrico.descifrarP(strServidor, certServer.getPublicKey());
												if(strDescifrado.equals(sha))
													System.out.println("Fin de la transacción");
											}
										}
										else if((strServidor=lector.readLine())!= null && strServidor.equals(commands[3])){
											System.out.println("error en la transaccion");
										}
									}
								}												

							}
						}
						else if((strServidor=lector.readLine())!= null && strServidor.equals(commands[3])){
							System.out.println("error en el envio de algoritmos");
						}
					}

				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}







		// cierre el socket y la entrada estándar
		try {
			bf.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String generate16bytes(byte[] param){
		String a="a";
		String palabra=a;
		for (byte b : param) {
			b=(new Byte(a)).byteValue();
			palabra+=a;
		}
		return palabra;
	}
}
