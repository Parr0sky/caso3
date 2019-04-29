/**
 * Parte del codigo fue tomado de http://www.bouncycastle.org/wiki/display/JA1/X.509+Public+Key+Certificate+and+Certification+Request+Generation
 */
package cliente;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V1CertificateGenerator;



public class Cliente {

	private KeyPair keyPair;
	private X509Certificate certificado;

	public Cliente() {
		// TODO Auto-generated constructor stub

		try {
			KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA", new BouncyCastleProvider());
			rsa.initialize(1024,new SecureRandom());
			keyPair = rsa.generateKeyPair();
			certificado=generarCertificado(keyPair);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static X509Certificate generarCertificado(KeyPair keyPair) throws Exception {
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		String strFechaIni = "30-03-2019 00:00:00"; 
		String strFechaFin = "31-12-2019 23:59:59";

		Date fechaIni = formatoFecha.parse(strFechaIni); 
		Date fechaFin = formatoFecha.parse(strFechaFin);

		BigInteger serialNumber = BigInteger.valueOf(1l);     // serial number for certificate

		@SuppressWarnings("deprecation")
		X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
		@SuppressWarnings("deprecation")
		X509Principal dnName = new X509Principal("CN=Test CA Certificate");
		certGen.setSerialNumber(serialNumber);
		certGen.setIssuerDN(dnName);
		certGen.setNotBefore(fechaIni);
		certGen.setNotAfter(fechaFin);
		certGen.setSubjectDN(dnName);                       // note: same as issuer
		certGen.setPublicKey(keyPair.getPublic());
		certGen.setSignatureAlgorithm("MD2withRSA");
		X509Certificate cert = certGen.generate(keyPair.getPrivate());
		return cert;
	}

	public X509Certificate getCertificado() {
		return certificado;
	}

	public KeyPair getKeyPair() {
		return keyPair;
	}



}
