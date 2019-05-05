package Servidor.srvSS;

		import java.io.*;
		import java.net.ServerSocket;
		import java.net.Socket;
		import java.security.KeyPair;
		import java.security.Security;
		import java.security.cert.X509Certificate;
		import java.util.concurrent.ExecutorService;
		import java.util.concurrent.Executors;

public class C {
	private static ServerSocket ss;
	private static final String MAESTRO = "MAESTRO SIN SEGURIDAD: ";
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

		//Crea el pool
		System.out.println("Introduzca el tamanio del pool");
		int tamPool = Integer.parseInt(br.readLine());
		ExecutorService exec = Executors.newFixedThreadPool(tamPool);
		System.out.println(MAESTRO + "Se creado pool de tamanio "+ tamPool);

		int idThread = 0;
		// Crea el socket que escucha en el puerto seleccionado.
		ss = new ServerSocket(ip);
		System.out.println(MAESTRO + "Socket creado.");

		keyPairServidor = S.grsa();
		certSer = S.gc(keyPairServidor);
		D.initCertificate(certSer, keyPairServidor);
		while (true) {
			try {
				Socket sc = ss.accept();
				System.out.println(MAESTRO + "Cliente " + idThread + " aceptado.");
				//D d = new D(sc,idThread);
				exec.execute(new D(sc,idThread));
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
			File file= new File("./timesSS.csv");
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
			File file= new File("./cpuSS.csv");
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
			File file= new File("./lostSS.csv");
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