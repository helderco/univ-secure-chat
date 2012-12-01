package org.siriux.chat;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextHelper;
import org.siriux.chat.servant.m2m.ServiceEnabler;
import org.siriux.chat.servant.m2m.ServiceEnablerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws Exception{
        
        if (args.length != 2) {
            logger.error("Usage: jaco org.siriux.chat.Client <nick> <server>");
            System.exit(1);
        }

        ORB orb = ORB.init(args, null);
        Object objRef = orb.resolve_initial_references("NameService");
        
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
        
        ServiceEnabler se = ServiceEnablerHelper.narrow(ncRef.resolve_str("ServiceEnabler"));
        
        se.recordPeer("Alice");

    }
}
