
package org.siriux.chat.cli;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.siriux.chat.servants.ServerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        try {
            ORB orb = ORB.init(args, null);

            // Activate manager
            Object poaRef = orb.resolve_initial_references("RootPOA");
            POA poa = POAHelper.narrow(poaRef);
            poa.the_POAManager().activate();

            // Get name service
            Object nsRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(nsRef);

            // Bind Server servant
            Object obj = poa.servant_to_reference(new ServerImpl());
            ncRef.rebind(ncRef.to_name("Server"), obj);

            // Create context for peers
            ncRef.rebind_context(ncRef.to_name("Peers"), ncRef);

            // Wait for requests
            logger.info("Waiting for client requests...");
            orb.run();
        }
        catch (Exception ex) {
            logger.error("Could not run ORB.", ex);
        }
    }
}
