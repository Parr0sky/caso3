package cliente;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Scanner;

import javax.xml.bind.DatatypeConverter;

public class MainSS {

	public final static String[] commands={"HOLA","ALGORITMOS","OK","ERROR"};
	public final static String[] separador={";", ","};
	public final static String separadorAlgoritmo=":";
	public final static String ALGs="AES";
	public final static String ALGa="RSA";
	public final static String ALGhmac="HMACSHA1";
	public static final String DIRECCION = "localhost";


	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
					if(strUsuario.equalsIgnoreCase(commands[0])) pw.println(commands[0]);
					if((strServidor=lector.readLine())!= null && strServidor.equals(commands[2])){
						//envio algs
						String algs=commands[1]+separadorAlgoritmo+ALGs+separadorAlgoritmo+ALGa+separadorAlgoritmo+ALGhmac;
						pw.println(algs);
						if((strServidor=lector.readLine())!= null && strServidor.equals(commands[2])){
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
								//enviar 128 bytes
								byte[] enviados=new byte[128];
								String palabra= generate16bytes(enviados);
								pw.println(palabra);
								
								//recibir 128 bytes
								if((strServidor=lector.readLine())!=null){
									if(strServidor.getBytes().length==128) {
										pw.println(commands[2]);
										pw.println(datos1);
										pw.println(datos1);
										if((strServidor=lector.readLine())!= null && strServidor.equals(commands[2])){
											System.out.println("Fin de la transacción");
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
			} catch (IOException    | IllegalStateException  | CertificateException e) {
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
