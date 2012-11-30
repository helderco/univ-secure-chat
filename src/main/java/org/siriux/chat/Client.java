package org.siriux.chat;

import java.util.logging.Level;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
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

        ORB orb;
        orb = ORB.init(args, null);
        Object objRef;
        objRef = orb.resolve_initial_references("NameService");
        NamingContext ncRef = NamingContextHelper.narrow(objRef);
        
        NameComponent nc = new NameComponent ("ServiceEnabler", "");
        NameComponent path[] = {nc};
        
        ServiceEnabler se;
        se = ServiceEnablerHelper.narrow(ncRef.resolve(path));
        se.recordPeer("AliceServer");
        
        //orb.run();
    }
}
