package org.siriux.chat;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.siriux.chat.servant.p2p.Peer2PeerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws Exception {
        ORB orb = ORB.init(args, null);
        
        Object objRef = orb.resolve_initial_references("RootPOA");
        POA poa = POAHelper.narrow(objRef);
        poa.the_POAManager().activate();

        Object obj = poa.servant_to_reference(new Peer2PeerImpl());

        // Get reference to Name Service
	Object nsRef = orb.resolve_initial_references("NameService");
	NamingContextExt ncRef = NamingContextExtHelper.narrow(nsRef);
        
	ncRef.rebind(ncRef.to_name("Peer2Peer"), obj);

        logger.info("Waiting for client requests...");
	orb.run();
    }
}
