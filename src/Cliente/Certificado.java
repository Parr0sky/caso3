/**
 * Parte del codigo fue tomado de http://www.bouncycastle.org/wiki/display/JA1/X.509+Public+Key+Certificate+and+Certification+Request+Generation
 */
package Cliente;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;



@SuppressWarnings("deprecation")
public class Certificado {

	public static X509Certificate generarV3Certificado(KeyPair pair) throws IllegalStateException, CertificateEncodingException, InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException {

		Security.addProvider(new BouncyCastleProvider());
		// yesterday
		Date validityBeginDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
		// in 2 years
		//        Date validityEndDate = new Date(System.currentTimeMillis() + 2 * 365 * 24 * 60 * 60 * 1000);
		Date validityEndDate = new Date(System.currentTimeMillis() + 10000);
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		X500Principal dnName = new X500Principal("CN=Certificado");

		certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		certGen.setSubjectDN(dnName);
		certGen.setIssuerDN(dnName); // use the same
		certGen.setNotBefore(validityBeginDate);
		certGen.setNotAfter(validityEndDate);
		certGen.setPublicKey(pair.getPublic());
		certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

		X509Certificate cert = certGen.generate(pair.getPrivate(), "BC");
		return cert;
	}

}
