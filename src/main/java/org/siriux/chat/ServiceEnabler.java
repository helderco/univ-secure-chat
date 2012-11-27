/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.siriux.chat;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.siriux.chat.servant.m2m.ServiceEnablerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author mluis
 */
public class ServiceEnabler {
    
    public static void main(String[] args) throws Exception {
        ORB orb = ORB.init(args, null);
        Object objRef = orb.resolve_initial_references("RootPOA");
        
        POA poa = POAHelper.narrow(objRef);
        poa.the_POAManager().activate();

        Object obj = poa.servant_to_reference(new ServiceEnablerImpl());

        // Get reference to Name Service
	Object nsRef = orb.resolve_initial_references("NameService");
	NamingContextExt ncRef = NamingContextExtHelper.narrow(nsRef);

	// Bind object to name service
	NameComponent [] name = new NameComponent[1];
	name[0] = new NameComponent("ServiceEnabler", "");
	ncRef.rebind(name, obj);

        logger.info("Waiting for client requests...");
	orb.run();
    }

    private static Logger logger = LoggerFactory.getLogger(Server.class);

}
