package org.siriux.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.siriux.chat.servant.m2m.ServiceEnabler;
import org.siriux.chat.servant.m2m.ServiceEnablerHelper;
import org.siriux.chat.servant.p2p.Peer2PeerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class RunnableThread implements Runnable {

	Thread runner;
        ORB orb;
	public RunnableThread() {
	}
	public RunnableThread(String threadName, ORB orb) {
		runner = new Thread(this, threadName); // (1) Create a new thread.
		System.out.println(runner.getName());
		runner.start(); // (2) Start the thread.
                this.orb = orb;
	}
	public void run() {
		orb.run();
	}
}

public class Client {

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws Exception{
        
        if (args.length != 1) {
            logger.error("Usage: jaco org.siriux.chat.Client <nick>");
            System.exit(1);
        }

        //Initialization
        ORB orb = ORB.init(args, null);
        
        Object objRef = orb.resolve_initial_references("RootPOA");
        POA poa = POAHelper.narrow(objRef);
        poa.the_POAManager().activate();

        Object srvObj = poa.servant_to_reference(new Peer2PeerImpl());
        
        //Name Service
        objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
        
        //Server Interface
        ncRef.rebind(ncRef.to_name(args[0]), srvObj);
        
        RunnableThread server = new RunnableThread("AliceORB", orb);
                
        //Client Interface 
        ServiceEnabler se = ServiceEnablerHelper.narrow(ncRef.resolve_str("ServiceEnabler"));
        
        Client.CLI(se, args[0]);
    }

    private static void CLI(ServiceEnabler se, String clientName) throws IOException {
        String CurLine; // Line read from standard in
        int isToExit = 0; 
        
        System.out.println("Entering CLI: ");
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        
        
        do{
            System.out.print(">");
            CurLine = in.readLine();
            
            if( CurLine.equals("/exit") ){
                isToExit = 1;
            }
            
            if( CurLine.equals("/join") ){
                se.recordPeer(clientName);
            }
            
            if( CurLine.equals("/users") ){
                System.out.println(se.getConnectedPeers());
            }
            
        }while (isToExit != 1);
            
    }
}
