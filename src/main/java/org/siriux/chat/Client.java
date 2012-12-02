package org.siriux.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.siriux.chat.servant.m2m.Peer2Peer;
import org.siriux.chat.servant.m2m.Peer2PeerHelper;
import org.siriux.chat.servant.m2m.ServiceEnabler;
import org.siriux.chat.servant.m2m.ServiceEnablerHelper;
import org.siriux.chat.servant.p2p.Peer2PeerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



class RunnableThread implements Runnable {

    Thread runner;
    ORB orb;
        
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
        
        Client.CLI(se, args[0], server, ncRef);
    }

    private static void CLI(ServiceEnabler se, String clientName, RunnableThread server, NamingContextExt ncRef) throws IOException {
        String CurLine; // Line read from standard in
        boolean isToExit = false;
        Peer2Peer p2p = null;
        
        System.out.println("Entering CLI: (use 'help' for a command list)");
        
        java.util.Scanner input = new java.util.Scanner(System.in);
        
        do{
            System.out.print(">");
            CurLine = input.nextLine();
            
            if( CurLine.startsWith("help")  ){
                System.out.println("exit   :Exits the application");
                System.out.println("join   :Joins the Server");
                System.out.println("users  :Gets a users list");
                System.out.println("help   :Shows this list");
            }else
            if( CurLine.startsWith("exit")  ){
                isToExit = true;
                server.orb.destroy();
            }else
            if( CurLine.startsWith("join") ){
                System.out.println(se.recordPeer(clientName));
            
            }else
            if( CurLine.startsWith("users") ){
                for (String user : Arrays.asList(se.getConnectedPeers())) {
                    System.out.println(user);                
                }
            }else
            if( CurLine.startsWith("chat ") ){
                String[] tokens = CurLine.split(" ");
                boolean _isToExit = false;
                try {
                    //Connect to Peer
                    p2p = Peer2PeerHelper.narrow(ncRef.resolve_str(tokens[1]));
                } catch (NotFound ex) {
                    java.util.logging.Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CannotProceed ex) {
                    java.util.logging.Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidName ex) {
                    java.util.logging.Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }

                do{
                    System.out.print("@" + tokens[1] + ">");
                    CurLine = input.nextLine();
                    if (input.hasNextLine()) {
                        input.nextLine();
                    }
                    for(int i=0; i < new String("@" + tokens[1] + ">" + CurLine).length(); i++){
                            System.out.print("\b");
                    }
                    if( CurLine.startsWith("/close") ){
                        //'XXX' Cancel Connection to Peer
                        //'XXX' Close session
                        System.out.println("Closing session...");
                        System.out.println("Session closed.");
                        _isToExit = true;
                    }else{
                        p2p.sendMsg2Peer(CurLine, clientName);
                                
//                        for(int i=1; i < new String("@" + tokens[1] + ">").length(); i++){
//                            System.out.print("\b");
//                        }
                        System.out.println(clientName + " says: " + CurLine.toString());
                        //System.out.print("@" + tokens[1] + ">");
                    }
                 
                }while(!_isToExit);
            }
                
            
        }while (!isToExit);
        
        System.out.print("bye!"); 
    }
}
