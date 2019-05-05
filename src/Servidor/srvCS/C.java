package Servidor.srvCS;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class C {
	private static ServerSocket ss;	
	private static final String MAESTRO = "MAESTRO: ";
	private static X509Certificate certSer; /* acceso default */
	private static KeyPair keyPairServidor; /* acceso default */


	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

		System.out.println(MAESTRO + "Establezca puerto de conexion:");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		int ip = Integer.parseInt(br.readLine());
		System.out.println(MAESTRO + "Empezando servidor maestro en puerto " + ip);
		// Adiciona la libreria como un proveedor de seguridad.
		// Necesario para crear llaves.
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());		

		int idThread = 0;
		// Crea el socket que escucha en el puerto seleccionado.
		ss = new ServerSocket(ip);
		System.out.println(MAESTRO + "Socket creado.");

		//Crea el pool
		System.out.println("Introduzca el tamanio del pool");
		int tamThread=Integer.parseInt(br.readLine());
		ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(tamThread);

		System.out.println(MAESTRO + "Se creao pool de tamanio "+ tamThread);

		keyPairServidor = S.grsa();
		certSer = S.gc(keyPairServidor);
		double failed=0;
		double time=0;
		double executions=0;
		D.initCertificate(certSer, keyPairServidor);
		while (true) {
			System.out.println("Elemnts in queue: "+exec.getQueue().size());
			try {
				Socket sc = ss.accept();
				System.out.println(MAESTRO + "Cliente " + idThread + " aceptado.");
				D d = new D(sc,idThread);
				exec.execute(d);
				idThread++;
				//d.start();
			} catch (IOException e) {
				System.out.println(MAESTRO + "Error creando el socket cliente.");
				e.printStackTrace();
			}

		}
	}

	public static void registerTime(double time)
	{
		try {
			File file= new File("./timesCS.csv");
			if(!file.exists())
			{
				file.createNewFile();
			}
			PrintWriter pr = new PrintWriter(new FileWriter(file,true));
			pr.println(time);
			pr.close();
		}
		catch (Exception e)
		{

		}
	}
	public static void  registerCPUUsage(double cpu)
	{
		try {
			File file= new File("./cpuCS.csv");
			if(!file.exists())
			{
				file.createNewFile();
			}
			PrintWriter pr = new PrintWriter(new FileWriter(file,true));
			pr.println(cpu);
			pr.close();
		}
		catch (Exception e)
		{

		}
	}
	public static void registerLostTransaction()
	{
		try {
			File file= new File("./lostCS.csv");
			if(!file.exists())
			{
				file.createNewFile();
			}
			PrintWriter pr = new PrintWriter(new FileWriter(file,true));
			pr.println("failed");
			pr.close();
		}
		catch (Exception e)
		{

		}
	}
}
