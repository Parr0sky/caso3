package Cliente;

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

public class ClienteCS {

    public final static String[] commands = {"HOLA", "ALGORITMOS", "OK", "ERROR"};
    public final static String[] separador = {";", ","};
    public final static String separadorAlgoritmo = ":";
    public final static String ALGs = "AES";
    public final static String ALGa = "RSA";
    public final static String ALGhmac = "HMACSHA1";
    public static final String DIRECCION = "localhost";


    public static void imprimir(byte[] contenido) {
        int i = 0;
        for (; i < contenido.length - 1; i++) {
            System.out.print(contenido[i] + " ");
        }

    }

    public ClienteCS() {

        String datos1 = 15 + separador[0] + "44 11.4561" + separador[1] + "13 10.5974";
        Cliente cliente = null;
        Socket socket = null;
        String strUsuario = "";
        BufferedReader bf = null;
        BufferedReader lector = null;
        PrintWriter pw = null;
        X509Certificate certServer = null;
        String strServidor = "";
        PublicKey pk = null;
        //coneccion de server y texto estandar
        try {
            socket = new Socket(DIRECCION, 8080);
            bf = new BufferedReader(new InputStreamReader(System.in));
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            System.exit(1);
        }



        try {
           // strUsuario = bf.readLine();
        //    if (strUsuario != "" && strUsuario != null) {
                if (/*strUsuario.equalsIgnoreCase(commands[0])*/true) {
                    pw.println(commands[0]);
                    strServidor = lector.readLine();


                    if (strServidor != null && strServidor.equals(commands[2])) {
                    	String algs = commands[1] + separadorAlgoritmo + ALGs + separadorAlgoritmo + ALGa + separadorAlgoritmo + ALGhmac;
                        pw.println(algs);
                        strServidor = lector.readLine();
                        if (strServidor != null && strServidor.equals(commands[2])) {

                            //certificado del cliente
                            cliente = new Cliente();
                            X509Certificate certCliente = cliente.getCertificado();

                            //envio certificado del cliente
                            byte[] certBytes = certCliente.getEncoded();
                            String certString = DatatypeConverter.printHexBinary(certBytes);
                            pw.println(certString);
                            String certServerSTR;
                            if ((strServidor = lector.readLine()) != null) {
                                //recepcion certificado del server
                                certServerSTR = strServidor;
                                byte[] x509cert = DatatypeConverter.parseHexBinary(certServerSTR);

                                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                                InputStream in = new ByteArrayInputStream(x509cert);
                                certServer = (X509Certificate) cf.generateCertificate(in);
                                pk = certServer.getPublicKey();
                                //Enviar llave sesion

                                KeyGenerator keygen = KeyGenerator.getInstance(ALGs);
                                SecretKey secretKey = keygen.generateKey();

                                pw.println(DatatypeConverter.printHexBinary(Asimetrico.cifrarBt(pk, secretKey.getEncoded())));
                                //recibir llave cifrada
                                if ((strServidor = lector.readLine()) != null) {

									/*verificar la llave
									if(Asimetrico.descifrarBtp(DatatypeConverter.parseHexBinary(strServidor),certCliente.getPublicKey()).equals(secretKey.getEncoded()))
									{


									}
									else {

									}*/


                                    //enviar confirmacion
                                    pw.println(commands[2]);

                                    //enviar localizacion cifrada
                                    pw.println(DatatypeConverter.printHexBinary( Simetrico.cifrarbt(secretKey, datos1.getBytes())));


                                    pw.println(Digest.hMacSha1(datos1,secretKey)/*HMAC_LS(datos1.getBytes)*/);



                                }

                            }
                        } else if ((strServidor = lector.readLine()) != null && strServidor.equals(commands[3])) {

                        }
                    }

                }

            //}
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // cierre el socket y la entrada est�ndar
        try {
            bf.close();
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String generate16bytes(byte[] param) {
        String a = "a";
        String palabra = a;
        for (byte b : param) {
            b = (new Byte(a)).byteValue();
            palabra += a;
        }
        return palabra;
    }
}