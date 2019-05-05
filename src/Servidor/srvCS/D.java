package Servidor.srvCS;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.security.KeyPair;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.bind.DatatypeConverter;

public class D extends Thread {

	public static final String OK = "OK";
	public static final String ALGORITMOS = "ALGORITMOS";
	public static final String CERTSRV = "CERTSRV";
	public static final String CERCLNT = "CERCLNT";
	public static final String SEPARADOR = ":";
	public static final String HOLA = "HOLA";
	public static final String INICIO = "INICIO";
	public static final String ERROR = "ERROR";
	public static final String REC = "recibio-";

	// Atributos
	private Socket sc = null;
	private String dlg;
	private byte[] mybyte;
	private static X509Certificate certSer;
	private static KeyPair keyPairServidor;
	public  static double failed;
	public static double time;


	public D (Socket csP, int idP) {
		sc = csP;
		failed=0;
		dlg = new String("delegado " + idP + ": ");
		try {
		mybyte = new byte[520]; 
		mybyte = certSer.getEncoded( );
		} catch (Exception e) {
			System.out.println("Error creando encoded del certificado para el thread" + dlg);
			e.printStackTrace();
		}
	}
	
	public static void initCertificate(X509Certificate pCertSer, KeyPair pKeyPairServidor) {
		certSer = pCertSer;
		keyPairServidor = pKeyPairServidor;
	}
	
	private boolean validoAlgHMAC(String nombre) {
		return ((nombre.equals(S.HMACMD5) || 
			 nombre.equals(S.HMACSHA1) ||
			 nombre.equals(S.HMACSHA256) ||
			 nombre.equals(S.HMACSHA384) ||
			 nombre.equals(S.HMACSHA512)
			 ));
	}

	public void run() {
		String linea;
	    System.out.println(dlg + "Empezando atencion.");
	        try {

				PrintWriter ac = new PrintWriter(sc.getOutputStream() , true);
				BufferedReader dc = new BufferedReader(new InputStreamReader(sc.getInputStream()));

				/***** Fase 1:  *****/
				linea = dc.readLine();
				if (!linea.equals(HOLA)) {
					ac.println(ERROR);
				    sc.close();
					throw new Exception(dlg + ERROR + REC + linea +"-terminando.");
				} else {
					ac.println(OK);
					System.out.println(dlg + REC + linea + "-continuando.");
				}
				
				/***** Fase 2:  *****/
				linea = dc.readLine();
				if (!(linea.contains(SEPARADOR) && linea.split(SEPARADOR)[0].equals(ALGORITMOS))) {
					ac.println(ERROR);
					sc.close();
					throw new Exception(dlg + ERROR + REC + linea +"-terminando.");
				}
				
				String[] algoritmos = linea.split(SEPARADOR);
				if (!algoritmos[1].equals(S.DES) && !algoritmos[1].equals(S.AES) &&
					!algoritmos[1].equals(S.BLOWFISH) && !algoritmos[1].equals(S.RC4)){
					ac.println(ERROR);
					sc.close();
					throw new Exception(dlg + ERROR + "Alg.Simetrico" + REC + algoritmos + "-terminando.");
				}
				if (!algoritmos[2].equals(S.RSA) ) {
					ac.println(ERROR);
					sc.close();
					throw new Exception(dlg + ERROR + "Alg.Asimetrico." + REC + algoritmos + "-terminando.");
				}
				if (!validoAlgHMAC(algoritmos[3])) {
					ac.println(ERROR);
					sc.close();
					throw new Exception(dlg + ERROR + "AlgHash." + REC + algoritmos + "-terminando.");
				}
				System.out.println(dlg + REC + linea + "-continuando.");
				ac.println(OK);

				/***** Fase 3:  *****/				
				String strCertificadoCliente = dc.readLine();				
				byte[] certificadoClienteBytes = new byte[520];
				certificadoClienteBytes = toByteArray(strCertificadoCliente);
				CertificateFactory creador = CertificateFactory.getInstance("X.509");
				InputStream in = new ByteArrayInputStream(certificadoClienteBytes);
				X509Certificate certificadoCliente = (X509Certificate)creador.generateCertificate(in);
				System.out.println(dlg + "recibio certificado del cliente. continuando.");
				//ac.println(OK);
				
				/***** Fase 4:  *****/
				ac.println(toHexString(mybyte));
				System.out.println(dlg + "envio certificado del servidor. continuando.");				

				/***** Fase 5: *****/
				long startTime = System.currentTimeMillis();
				linea = dc.readLine();
				byte[] llaveSimetrica = S.ad(
						toByteArray(linea), 
						keyPairServidor.getPrivate(), algoritmos[2] );
				SecretKey simetrica = new SecretKeySpec(llaveSimetrica, 0, llaveSimetrica.length, algoritmos[1]);
				System.out.println(dlg + "creo llave simetrica de dato recibido. continuando.");				
				
				
				/***** Fase 6:  *****/
				
				byte [ ] ciphertext1 = S.ae(simetrica.getEncoded(), 
		                 certificadoCliente.getPublicKey(), algoritmos[2]);
				ac.println(toHexString(ciphertext1));
				System.out.println(dlg + "envio llave simetrica al cliente. continuado.");

				linea = dc.readLine();
				if (!(linea.equals(OK))) {
					sc.close();
					throw new Exception(dlg + ERROR + "en confirmacion de llave simetrica." + REC + "-terminando.");
				}
				
				/***** Fase 7:  *****/

				linea = dc.readLine();				
				String datos = new String(S.sd(
						toByteArray(linea), simetrica, algoritmos[1]));
				linea = dc.readLine();
				
				byte[] hmac = toByteArray(linea);
				
				/***** Fase 8:  *****/
				boolean verificacion = S.vi(
						                 datos.getBytes(), simetrica, algoritmos[3], hmac);
				if (verificacion) {
					System.out.println(dlg + "verificacion de integridad:OK. -continuado.");
					byte[] recibo = S.ae(hmac, keyPairServidor.getPrivate(), algoritmos[2]);
					ac.println(toHexString(recibo));
				} else {
					ac.println(ERROR);
					throw new Exception(dlg + "Error en verificacion de integridad. -terminando.");
				}
				
		        sc.close();
		        double cpuTot=getProcessCpuLoad();
		        long totalSum= (System.currentTimeMillis()-startTime);
		        System.out.println(dlg + "Termino exitosamente.");
		        System.out.println("Uso de CPU: "+cpuTot);
		        C.registerTime(totalSum);
		        C.registerCPUUsage(cpuTot);
		        System.out.println("Tiempo de ejecucion: "+totalSum);
		        time=totalSum;
				
	        } catch (Exception e) {
	        	C.registerLostTransaction();
	          	e.printStackTrace();
	        }
	}
	
	public static String toHexString(byte[] array) {
	    return DatatypeConverter.printHexBinary(array);
	}

	public static byte[] toByteArray(String s) {
	    return DatatypeConverter.parseHexBinary(s);
	}
	
	public static double getProcessCpuLoad() throws Exception {

	    MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
	    ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
	    AttributeList list = mbs.getAttributes(name, new String[]{ "SystemCpuLoad" });

	    if (list.isEmpty())     return Double.NaN;

	    Attribute att = (Attribute)list.get(0);
	    Double value  = (Double)att.getValue();

	    // usually takes a couple of seconds before we get real values
	    if (value == -1.0)      return Double.NaN;
	    // returns a percentage value with 1 decimal point precision
	    return ((int)(value * 1000) / 10.0);
	}
	
}
