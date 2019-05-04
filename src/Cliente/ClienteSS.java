package Cliente;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.bind.DatatypeConverter;

public class ClienteSS {

	public final static String SERVIDOR = "SERVIDOR";
	public final static String ALGORITMOS = "ALGORITMOS";
	public final static String RSA = "RSA";
	public final static String SIMETRICO = "AES";
	public final static String HMAC = "HMACSHA1";
	public final static String HOST = "localhost";
	public final static int PUERTO = 8080;

	private Socket socket;
	private  PrintWriter escritor;
	private  BufferedReader lector;
	private static Scanner scanner;
	private static X509Certificate certificadoCliente;
	private static X509Certificate certificadoServidor;
	private static KeyPair parLlaves;
	private static String simetrico;
	private static String hmac;

	public static int contadorTransacciones=0;
	public static long sumaTiemposVerificacion=0l;
	public static long sumaTiemposConsulta=0l;
	public static double cantTomadaCpu=0;
	public static double sumaTiempoCpu=0;


	public ClienteSS() {
		long tiempoVerificacion=-1l;
		long tiempoConsulta=-1l;

		try {
			scanner = new Scanner(System.in);
			parLlaves = crearLlavesAsimetricas();
			certificadoCliente = Certificado.generarV3Certificado(parLlaves);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			socket = new Socket("localhost", PUERTO);
			escritor = new PrintWriter(socket.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			escritor.println("HOLA");
			String respuesta = lector.readLine();
			System.out.println("Respuesta del servidor: " + respuesta);

			escritor.println(respuestaAlgoritmos());
			respuesta = lector.readLine();
			System.out.println("Respuesta del servidor: " + respuesta);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String certificado = DatatypeConverter.printHexBinary(certificadoCliente.getEncoded());
			System.out.println("Certificado del Cliente: "+certificadoCliente.toString() + "\n");
			escritor.println(certificado);

			byte[] certificadoDelServidor = DatatypeConverter.parseHexBinary(lector.readLine());
			certificadoServidor = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certificadoDelServidor));
			System.out.println("Certificado del Servidor: "+certificadoServidor.toString() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String llave = DatatypeConverter.printHexBinary(certificadoCliente.getPublicKey().getEncoded());

			escritor.println(llave);

			System.out.println("Llave del cliente: "+ llave);

			String respuestaServidor = lector.readLine();

			System.out.println("Respuesta del servidor: " + respuestaServidor);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			escritor.println("OK");
			System.out.println("Ingrese los datos que desea enviar:");
			String datos = scanner.nextLine();

			escritor.println(datos);

			escritor.println(datos);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String respuesta = lector.readLine();
			System.out.println("Respuesta del servidor: " + respuesta);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			System.out.println("Se está cerrando la conexion");
			lector.close(); 	
			scanner.close();
			socket.close();
			escritor.close();
		} catch (Exception e) {e.printStackTrace();
		}

	}

	public static KeyPair crearLlavesAsimetricas() throws NoSuchAlgorithmException {
		KeyPairGenerator llaves = KeyPairGenerator.getInstance(RSA);
		llaves.initialize(1024, new SecureRandom());
		return llaves.generateKeyPair();
	}

	public static String respuestaAlgoritmos() {
		simetrico = null;
		hmac = null;
		boolean acepto = false;
		while(!acepto) {
			System.out.println("Seleccione los algoritmos que se van a utilizar: ");
			System.out.println("1. Algoritmos Simétricos disponibles (ALGs): AES, Blowfish");

			simetrico = scanner.nextLine();

			if(simetrico.equals("AES") || simetrico.equals("BLOWFISH")) {
				acepto = true;
				System.out.println("Algoritmo aceptado");
			}
			else {
				System.out.println("El algoritmo seleccionado no es valido.");
			}
		}

		System.out.println("2. Algoritmos Asimétricos a utilizar: RSA");

		acepto = false;
		while(!acepto) {
			System.out.println("3. HMAC disponibles: HMACSHA1, HMACSHA256, HMACSHA384, HMACSHA512");

			hmac = scanner.nextLine();

			if(hmac.equals("HMACSHA1") || hmac.equals("HMACSHA256") || hmac.equals("HMACSHA384") || hmac.equals("HMACSHA512")) {
				acepto = true;
				System.out.println("Algoritmo aceptado");
			}
			else {
				System.out.println("El algoritmo seleccionado no es valido.");
			}
		}

		String mensajeAServidor = ALGORITMOS + ":" + simetrico + ":" + RSA + ":" + hmac;
		return mensajeAServidor;
	}
	public double getSystemCpuLoad() throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name, new String[]{ "SystemCpuLoad" });
		if (list.isEmpty()) return Double.NaN;
		Attribute att = (Attribute)list.get(0);
		Double value = (Double)att.getValue();
		// usually takes a couple of seconds before we get real values
		if (value == -1.0) return Double.NaN;
		// returns a percentage value with 1 decimal point precision
		sumaTiempoCpu+=((int)(value * 1000) / 10.0);
		cantTomadaCpu++;
		return ((int)(value * 1000) / 10.0);
	}

}