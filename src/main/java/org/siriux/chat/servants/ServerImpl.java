
package org.siriux.chat.servants;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.RSAPublicKeyStructure;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.asn1.x509.X509CertificateStructure;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

public class ServerImpl extends ServerPOA {

    private static Logger logger = LoggerFactory.getLogger(ServerImpl.class);
    private ArrayList<String> peers = new ArrayList<>();
    private static String ks = null;
    private static String ksPass = null;
    
    /** This holds the certificate of the CA used to sign the new certificate.*/
    private X509Certificate caCert;
    
    /** This holds the private key of the CA used to sign the new certificate.*/
    private RSAPrivateCrtKeyParameters caPrivateKey;
    
    private RSAPrivateCrtKey privKey = null;
    
    
    // 'XXX' get this name dynamically
    private static String caAlias = "Server";

    
    public ServerImpl(String ks, String ksPass) {
        ServerImpl.ks = ks;
        ServerImpl.ksPass = ksPass;
        /*
        // load the key entry from the keystore
	Key Key = null;
        try {
            Key = ks.getKey(caAlias, ksPass.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
            logger.debug(ex.getLocalizedMessage(), ex);
        }
	//if (Key == null) {
	//	throw new RuntimeException("Got null key from keystore!"); 
        //}
        
        
        privKey = (RSAPrivateCrtKey) Key;
        caPrivateKey = new RSAPrivateCrtKeyParameters(privKey.getModulus(), privKey.getPublicExponent(), privKey.getPrivateExponent(),
                        privKey.getPrimeP(), privKey.getPrimeQ(), privKey.getPrimeExponentP(), privKey.getPrimeExponentQ(), privKey.getCrtCoefficient());
        try {
            // and get the certificate
            caCert = (X509Certificate) ks.getCertificate(caAlias);
        } catch (KeyStoreException ex) {
            logger.debug(ex.getLocalizedMessage(), ex);
        }
        if (caCert == null) {
                throw new RuntimeException("Got null cert from keystore!"); 
        }
        
        logger.info("Verifying CA...");
        try {
            caCert.verify(caCert.getPublicKey());
        } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException
                | NoSuchProviderException | SignatureException ex) {
            logger.debug(ex.getLocalizedMessage(), ex);
        }
        logger.info("CA OK.");
        */
    }

    @Override
    public void join(String name) throws NameAlreadyUsed {
        if (peers.contains(name)) {
            throw new NameAlreadyUsed(name + " is already in use.");
        }
        peers.add(name);
        logger.info("Added peer {}.", name);
    }

    @Override
    public void leave(String name) throws UnknownPeer {
        if (!peers.contains(name)) {
            throw new UnknownPeer("User " + name + " is not connected.");
        }
        peers.remove(name);
        logger.info("Removed peer {}.", name);
    }

    @Override
    public String[] getConnectedPeers() {
        return peers.toArray(new String[peers.size()]);
    }
    
    @Override
    @SuppressWarnings("empty-statement")
    public String genKeyPair(String peerName/*, int vDays*/, String nbits, String password) {
        try {
            //        //Decode skey with privKey
            //        byte[] encryptionByte = null;
            //        Cipher cipher = null;
            //        try {
            //            cipher = Cipher.getInstance("RSA");
            //        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            //            logger.debug(ex.getLocalizedMessage(), ex);
            //        }
            //        try {
            //            cipher.init(Cipher.DECRYPT_MODE, privKey);
            //        } catch (InvalidKeyException ex) {
            //            logger.debug(ex.getLocalizedMessage(), ex);
            //        }
            //        try {
            //            encryptionByte = cipher.doFinal(encrypted);
            //        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            //            logger.debug(ex.getLocalizedMessage(), ex);
            //        }
            //        
            //        //Got sKey
            //        logger.info("Got Session Key for {}...", peerName);
            //        
                    
            //        Runtime.getRuntime().exec("keytool -genkey -keyalg RSA -keysize "+nbits+" -alias server -validity 25000 " +
            //                                                "-keystore server_ks -storepass storepassword " +
            //                                                "-keypass "+password+" " +
            //                                                " -dname \"CN=JSSE SSL Demo Server, O=JacORB\"");
                    
                    //Request Keys
                    Runtime.getRuntime().exec("keytool -keystore "+ks+" -storetype jceks -storepass "+ksPass+" -keypass "+
                                              password+" -genkey -keyalg RSA -alias "+peerName+" -dname \"CN="+peerName+", OU=Development, "
                                              +"O=org.siriux, L=Anytown, S=DMat, C=PT\"");
                    
                    //Create CSR 
                    Runtime.getRuntime().exec("keytool -certreq -file "+peerName+".csr -alias "+ peerName +" -keypass "+ password +" -storetype jceks"+
                                              " -keystore "+ks+" -storepass "+ksPass);
                    
                    //keytool -gencert -keystore server.jck -storetype jceks
                    //-storepass storepass -alias Alice -keypass 123456
                    //-infile Alice.csr -outfile Alice_signed.pem -rfc
                    //Runtime.getRuntime().exec("keytool -gencert -keystore "+ks+" -storetype jceks -storepass "+ksPass+
                     //       " -alias "+peerName+ " -keypass "+ password+" -infile "+peerName+".csr -outfile "+peerName+"_signed.pem -rfc");
                    
                    //keytool -importkeystore -srckeystore server.jck -destkeystore alice.p12
                    //-srcstoretype jceks -deststoretype pkcs12 -srcstorepass storepass
                    //-deststorepass 123456 -srcalias Alice -destalias Alice -srckeypass 123456 -destkeypass 123456 -noprompt
                    
                    Runtime.getRuntime().exec("keytool -importkeystore -srckeystore "+ks+" -destkeystore "+peerName+".p12 "
                                             +"-srcstoretype jceks -deststoretype pkcs12 -srcstorepass "+ksPass
                                              +" -deststorepass "+password+" -srcalias "+peerName+
                                              "-destalias "+peerName+" -srckeypass "+password+" -destkeypass "+password+" -noprompt");
                    
                    Runtime.getRuntime().exec("openssl pkcs12 -in "+peerName+".p12 -out "+peerName+".pem -passin pass:"+password+" -passout pass:"+password);                    
                    
                    
                    
                    
                    //openssl pkcs12 -in mystore.p12 -out mystore.pem -passin pass:mysecret -passout pass:mysecret
                    
                    //Extract key and Certificate
                    //Runtime.getRuntime().exec("keytool -importkeystore -srckeystore "+ks+
                    //                         "-destkeystore "+peerName+".p12 -deststoretype PKCS12");
                    /*
                    try {
                        //Create Certificate
                        logger.info("Creating certificate for {}...", peerName);
                        this.createCertificate(peerName, vDays, nbits, password);
                        
                    } catch (IOException | InvalidKeyException | SecurityException
                            | SignatureException | NoSuchAlgorithmException | DataLengthException
                            | CryptoException | KeyStoreException | NoSuchProviderException
                            | CertificateException | InvalidKeySpecException ex) {
                        logger.debug(ex.getLocalizedMessage(), ex);
                    }
                           
                    Key peerKey = null;
                    try {
                        peerKey = ks.getKey(peerName, password.toCharArray());
                    } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
                        logger.debug(ex.getLocalizedMessage(), ex);
                    }
                    String b64 = new BASE64Encoder().encode(peerKey.getEncoded());
                    System.out.println("-----BEGIN PRIVATE KEY-----");
                    System.out.println(b64);
                    System.out.println("-----END PRIVATE KEY-----");
                    * 
                    */
                    
                    //Send privKey and Cert
                    return "";
        }
        
        //   private boolean createCertificate(String dn, int validityDays, int nbits,
        //                                     String exportPassword) throws
        //			IOException, InvalidKeyException, SecurityException, SignatureException, NoSuchAlgorithmException, DataLengthException, CryptoException, KeyStoreException, NoSuchProviderException, CertificateException, InvalidKeySpecException {
        //		logger.info("Generating certificate for distinguished subject name '" +
        //				dn + "', valid for " + validityDays + " days");
        //		SecureRandom sr = new SecureRandom();
        //
        //		PublicKey pubKey;
        //		PrivateKey prKey;
        //
        //		logger.debug("Creating RSA keypair");
        //		// generate the keypair for the new certificate
        //
        //			// this is the JSSE way of key generation
        //			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        //			keyGen.initialize(nbits, sr);
        //			KeyPair keypair = keyGen.generateKeyPair();
        //			prKey = keypair.getPrivate();
        //			pubKey = keypair.getPublic();
        //
        //		Calendar expiry = Calendar.getInstance();
        //		expiry.add(Calendar.DAY_OF_YEAR, validityDays);
        //
        //		X509Name x509Name = new X509Name("CN=" + dn);
        //
        //		V3TBSCertificateGenerator certGen = new V3TBSCertificateGenerator();
        //                certGen.setSerialNumber(new ASN1Integer(BigInteger.valueOf(System.currentTimeMillis())));
        //		certGen.setIssuer(PrincipalUtil.getSubjectX509Principal(caCert));
        //		certGen.setSubject(x509Name);
        //		DERObjectIdentifier sigOID;
        //                sigOID = new DERObjectIdentifier("SHA1WithRSAEncryption");
        //		AlgorithmIdentifier sigAlgId = new AlgorithmIdentifier(sigOID, new DERNull());
        //		certGen.setSignature(sigAlgId);
        //		certGen.setSubjectPublicKeyInfo(new SubjectPublicKeyInfo((ASN1Sequence)new ASN1InputStream(
        //                new ByteArrayInputStream(pubKey.getEncoded())).readObject()));
        //		certGen.setStartDate(new Time(new Date(System.currentTimeMillis())));
        //		certGen.setEndDate(new Time(expiry.getTime()));
        //
        //		logger.debug("Certificate structure generated, creating SHA1 digest");
        //		// attention: hard coded to be SHA1+RSA!
        //		SHA1Digest digester = new SHA1Digest();
        //		AsymmetricBlockCipher rsa = new PKCS1Encoding(new RSAEngine());
        //		TBSCertificate tbsCert;
        //                tbsCert = certGen.generateTBSCertificate();
        //
        //		ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        //		DEROutputStream         dOut = new DEROutputStream(bOut);
        //		dOut.writeObject(tbsCert);
        //
        //		// and now sign
        //		byte[] signature;
        //
        //			// or the JCE way
        //	        PrivateKey caPrivKey = KeyFactory.getInstance("RSA").generatePrivate(
        //	        		new RSAPrivateCrtKeySpec(caPrivateKey.getModulus(), caPrivateKey.getPublicExponent(),
        //	        				caPrivateKey.getExponent(), caPrivateKey.getP(), caPrivateKey.getQ(),
        //	        				caPrivateKey.getDP(), caPrivateKey.getDQ(), caPrivateKey.getQInv()));
        //
        //	        Signature sig = Signature.getInstance(sigOID.getId());
        //	        sig.initSign(caPrivKey, sr);
        //	        sig.update(bOut.toByteArray());
        //	        signature = sig.sign();
        //
        //		//logger.debug("SHA1/RSA signature of digest is '" + new String(Hex.encodeHex(signature)) + "'");
        //
        //		// and finally construct the certificate structure
        //        ASN1EncodableVector  v = new ASN1EncodableVector();
        //
        //        v.add(tbsCert);
        //        v.add(sigAlgId);
        //        v.add(new DERBitString(signature));
        //
        //        X509CertificateObject clientCert = new X509CertificateObject(new X509CertificateStructure(new DERSequence(v)));
        //        logger.debug("Verifying certificate for correct signature with CA public key");
        //        clientCert.verify(caCert.getPublicKey());
        //
        //        // and export as PKCS12 formatted file along with the private key and the CA certificate
        //        logger.debug("Exporting certificate in PKCS12 format");
        //
        //        PKCS12BagAttributeCarrier bagCert = clientCert;
        //        bagCert.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
        //        		new DERBMPString("Certificate for "+dn));
        //        bagCert.setBagAttribute(
        //                PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
        //                new SubjectKeyIdentifierStructure(pubKey));
        //
        //        X509Certificate[] chain = new X509Certificate[2];
        //        // first the client, then the CA certificate
        //        chain[0] = clientCert;
        //        chain[1] = caCert;
        //
        //        ks.setKeyEntry(dn, prKey, exportPassword.toCharArray(), chain);
        //
        //        //FileOutputStream fOut = new FileOutputStream(exportFile);
        //
        //        //store.store(fOut, exportPassword.toCharArray());
        //
        //        return true;
        //	}
        catch (IOException ex) {
            
        }
        String ret = null;
        try {
            //Cert
            ret= readFile(peerName+".pem");
        } catch (IOException ex) {
            
        }
        return ret.toString();
    }

    private String readFile( String file ) throws IOException {
    BufferedReader reader = new BufferedReader( new FileReader (file));
    String         line = null;
    StringBuilder  stringBuilder = new StringBuilder();
    String         ls = System.getProperty("line.separator");

    while( ( line = reader.readLine() ) != null ) {
        stringBuilder.append( line );
        stringBuilder.append( ls );
    }

    return stringBuilder.toString();
}

    
//   private boolean createCertificate(String dn, int validityDays, int nbits,
//                                     String exportPassword) throws 
//			IOException, InvalidKeyException, SecurityException, SignatureException, NoSuchAlgorithmException, DataLengthException, CryptoException, KeyStoreException, NoSuchProviderException, CertificateException, InvalidKeySpecException {
//		logger.info("Generating certificate for distinguished subject name '" + 
//				dn + "', valid for " + validityDays + " days");
//		SecureRandom sr = new SecureRandom();
//		
//		PublicKey pubKey;
//		PrivateKey prKey;
//		
//		logger.debug("Creating RSA keypair");
//		// generate the keypair for the new certificate
//		
//			// this is the JSSE way of key generation
//			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//			keyGen.initialize(nbits, sr);
//			KeyPair keypair = keyGen.generateKeyPair();
//			prKey = keypair.getPrivate();
//			pubKey = keypair.getPublic();
//	    
//		Calendar expiry = Calendar.getInstance();
//		expiry.add(Calendar.DAY_OF_YEAR, validityDays);
// 
//		X509Name x509Name = new X509Name("CN=" + dn);
//
//		V3TBSCertificateGenerator certGen = new V3TBSCertificateGenerator();
//                certGen.setSerialNumber(new ASN1Integer(BigInteger.valueOf(System.currentTimeMillis())));
//		certGen.setIssuer(PrincipalUtil.getSubjectX509Principal(caCert));
//		certGen.setSubject(x509Name);
//		DERObjectIdentifier sigOID;
//                sigOID = new DERObjectIdentifier("SHA1WithRSAEncryption");
//		AlgorithmIdentifier sigAlgId = new AlgorithmIdentifier(sigOID, new DERNull());
//		certGen.setSignature(sigAlgId);
//		certGen.setSubjectPublicKeyInfo(new SubjectPublicKeyInfo((ASN1Sequence)new ASN1InputStream(
//                new ByteArrayInputStream(pubKey.getEncoded())).readObject()));
//		certGen.setStartDate(new Time(new Date(System.currentTimeMillis())));
//		certGen.setEndDate(new Time(expiry.getTime()));
//		
//		logger.debug("Certificate structure generated, creating SHA1 digest");
//		// attention: hard coded to be SHA1+RSA!
//		SHA1Digest digester = new SHA1Digest();
//		AsymmetricBlockCipher rsa = new PKCS1Encoding(new RSAEngine());
//		TBSCertificate tbsCert;
//                tbsCert = certGen.generateTBSCertificate();
//
//		ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
//		DEROutputStream         dOut = new DEROutputStream(bOut);
//		dOut.writeObject(tbsCert);
//
//		// and now sign
//		byte[] signature;
//		
//			// or the JCE way
//	        PrivateKey caPrivKey = KeyFactory.getInstance("RSA").generatePrivate(
//	        		new RSAPrivateCrtKeySpec(caPrivateKey.getModulus(), caPrivateKey.getPublicExponent(),
//	        				caPrivateKey.getExponent(), caPrivateKey.getP(), caPrivateKey.getQ(), 
//	        				caPrivateKey.getDP(), caPrivateKey.getDQ(), caPrivateKey.getQInv()));
//			
//	        Signature sig = Signature.getInstance(sigOID.getId());
//	        sig.initSign(caPrivKey, sr);
//	        sig.update(bOut.toByteArray());
//	        signature = sig.sign();
//		
//		//logger.debug("SHA1/RSA signature of digest is '" + new String(Hex.encodeHex(signature)) + "'");
//
//		// and finally construct the certificate structure
//        ASN1EncodableVector  v = new ASN1EncodableVector();
//
//        v.add(tbsCert);
//        v.add(sigAlgId);
//        v.add(new DERBitString(signature));
//
//        X509CertificateObject clientCert = new X509CertificateObject(new X509CertificateStructure(new DERSequence(v))); 
//        logger.debug("Verifying certificate for correct signature with CA public key");
//        clientCert.verify(caCert.getPublicKey());
//
//        // and export as PKCS12 formatted file along with the private key and the CA certificate 
//        logger.debug("Exporting certificate in PKCS12 format");
//
//        PKCS12BagAttributeCarrier bagCert = clientCert;
//        bagCert.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
//        		new DERBMPString("Certificate for "+dn));
//        bagCert.setBagAttribute(
//                PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
//                new SubjectKeyIdentifierStructure(pubKey));
//
//        X509Certificate[] chain = new X509Certificate[2];
//        // first the client, then the CA certificate
//        chain[0] = clientCert;
//        chain[1] = caCert;
//        
//        ks.setKeyEntry(dn, prKey, exportPassword.toCharArray(), chain);
//
//        //FileOutputStream fOut = new FileOutputStream(exportFile);
//
//        //store.store(fOut, exportPassword.toCharArray());
//		
//        return true;
//	}
    
}
