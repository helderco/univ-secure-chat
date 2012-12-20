
package org.siriux.chat.cli;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.siriux.chat.servants.ServerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server implements Runnable {


    @Override
    public void run() {
        // Wait for requests
        logger.info("Waiting for client requests...");
        System.out.println("Waiting for client requests...");
        orb.run();
    }
    
    protected static ORB orb;
    private static Scanner in = new Scanner(System.in);
    private static KeyStore ks = null;
    private static String ksFile = null;
    private static String ksPass = null;
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        
        if (args.length < 1) {
            System.err.printf("Usage: jaco %s KeyStorePassword [KeyStore]\n", Server.class.getName());
            System.exit(1);
        
        }
        
        try {
            ks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException ex) {
            logger.debug(ex.getLocalizedMessage(), ex);
        }
                //'XXX'
        ksPass = args[0].toString();
        
        try {
            
            //Load Keystore
            logger.info("Loading a new KeyStore...");
            ks.load(null, null);
            //ks.store( new FileOutputStream( "ServerKeyStore" ), ksPass.toCharArray() );
            
            
            orb = ORB.init(args, null);

            // Activate manager
            Object poaRef = orb.resolve_initial_references("RootPOA");
            POA poa = POAHelper.narrow(poaRef);
            poa.the_POAManager().activate();

            // Get name service
            Object nsRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(nsRef);

            

            
            Thread t = new Thread(new Server());
            
            try {
            // Start interface
            System.out.println("Entering CLI (use '/help' for a command list):");
            

            String input;
            boolean tIsRunning = false;

            while (true) {
                input = askInput("> ");

                if (input.startsWith("/help")) {
                    System.out.println("  /ssc <nbits>       Generates a self signed certificate");
                    System.out.println("  /quit              Quit application");
                    System.out.println();
                }
                else if (input.startsWith("/quit")) {
                    break;
                }
                else if (input.startsWith("/ssc")) {
                    
//                    Runtime.getRuntime().exec("keytool -genkey -keyalg RSA -keysize 1024 -alias server -validity 25000 " +
//                                                "-keystore server_ks -storepass storepassword " +
//                                                "-keypass keypassword " +
//                                                " -dname \"CN=JSSE SSL Demo Server, O=JacORB\"");
                    //'XXX' get <nbits>
                    Runtime.getRuntime().exec("keytool -genkeypair -alias serverkey -dname cn=org.siriux.chat.Server" + 
                                              " -validity 365 -keyalg RSA -keysize 1024 " +
                                              " -keypass serverkeypass -storetype jceks -keystore server.jck -storepass storepass ");
                    
                    /*
                    // 'XXX' GET SECOND ARGUMENT
                    int nbits = Integer.parseInt("1024");
                    String domainName = "Server";
                    // 'XXX'
                    
                    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");  
                    keyPairGenerator.initialize(nbits);  
                    KeyPair KPair = keyPairGenerator.generateKeyPair();
                   
                    // 'XXX' Generate SSC
                    X509Certificate crt;
                    crt = Server.genSSC(KPair, domainName);
                    
                   ks.setKeyEntry(domainName, KPair.getPrivate(),  
                                    ksPass.toCharArray(),  
                                    new java.security.cert.Certificate[]{crt});
                   */
                }else if (input.startsWith("/run")) {
                    if(tIsRunning == false){
                        // Bind Server servant
                    Object obj;
                    //obj = poa.servant_to_reference(new ServerImpl(ks, ksPass));
                    obj = poa.servant_to_reference(new ServerImpl("server.jck", "storepass"));
                    ncRef.rebind(ncRef.to_name("Server"), obj);

                    // Create context for peers
                    ncRef.rebind_context(ncRef.to_name("Peers"), ncRef);
                        t.start();
                        tIsRunning = true;
                    }
                    System.out.println("Server is running.");
                }
            }
            System.out.println("Bye!");
        }
        finally {

            //ks.store( new FileOutputStream( "ServerKeyStore" ), ksPass.toCharArray() );     
            
            orb.destroy();

            try {
                t.join();
            }
            catch (InterruptedException ex) {
                // Nothing to do here
            }
        }
           
        }
        
        catch (InvalidName | AdapterInactive | ServantNotActive
                | WrongPolicy | org.omg.CosNaming.NamingContextPackage.InvalidName
                | NotFound | CannotProceed | IOException | NoSuchAlgorithmException
                | CertificateException | NumberFormatException ex) {
            logger.debug(ex.getLocalizedMessage(), ex);
            logger.error(ex.getLocalizedMessage());
            System.err.println("Couldn't start server. See log for details.");
        }
    }
    
    private static String askInput(String prompt) {
        System.out.print(prompt);
        return in.nextLine();
    }
     
//    private static X509Certificate genSSC(KeyPair kp, String domainName){
//        X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
//        
//        // Serial number, issuer, validity period and Subject
//            //'XXX' Must be a positive integer
//        int sr = -1;
//        while( sr < 0 ){
//            sr = new SecureRandom().nextInt();
//        }
//        v3CertGen.setSerialNumber(BigInteger.valueOf(sr)); 
//        
//        
//        
//        v3CertGen.setIssuerDN(new X509Principal("CN=" + domainName + ", OU=None, O=None L=None, C=None"));  
//        v3CertGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30));  
//        v3CertGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365*10)));  
//        v3CertGen.setSubjectDN(new X509Principal("CN=" + domainName + ", OU=None, O=None L=None, C=None"));
//        
//        // Set the public key of the key pair and the signing algorithm to the cert generator
//        v3CertGen.setPublicKey(kp.getPublic());  
//        v3CertGen.setSignatureAlgorithm("MD5WithRSAEncryption");
//        
//        X509Certificate PKCertificate = null;
//        try {
//            // Generate Certificate
//            PKCertificate = v3CertGen.generateX509Certificate(kp.getPrivate());
//        } catch (SecurityException | SignatureException | InvalidKeyException ex) {
//            logger.debug(ex.getLocalizedMessage(), ex);
//        }
//        return PKCertificate;
//    }
}
