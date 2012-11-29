package org.siriux.chat;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.siriux.chat.servant.sum.Sum;
import org.siriux.chat.servant.sum.SumHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            logger.error("Usage: jaco org.siriux.chat.Client <num1> <num2>");
            System.exit(1);
        }

        ORB orb = ORB.init(args, null);

        Object objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

        Sum sum = SumHelper.narrow(ncRef.resolve_str("Sum"));

        // invoke the operation and print the result
        System.out.println(sum.sum(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
    }
}
