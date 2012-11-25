package org.siriux.chat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.siriux.chat.servant.sum.SumImpl;

public class Server {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: jaco org.siriux.chat.Server <ior_file>" );
            System.exit(1);
        }

        ORB orb = ORB.init(args, null);

        Object objRef = orb.resolve_initial_references("RootPOA");
        POA poa = POAHelper.narrow(objRef);
        poa.the_POAManager().activate();

        Object obj = poa.servant_to_reference(new SumImpl());


        PrintWriter ps = new PrintWriter(new FileOutputStream(new File( args[0] )));
        ps.println( orb.object_to_string( obj ) );
        ps.close();

        System.out.println("Waiting for client requests");
	    orb.run();
    }
}
