package org.siriux.chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.siriux.chat.servant.sum.Sum;
import org.siriux.chat.servant.sum.SumHelper;

public class Client {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: jaco org.siriux.chat.Client <ior_file>" );
            System.exit(1);
        }

        File f = new File(args[0]);

        // check if file exists
        if (!f.exists()) {
            System.err.println("File " + args[0] + " does not exist.");
            System.exit( -1 );
        }

        // check if args[0] points to a directory
        if (f.isDirectory()) {
            System.err.println("File " + args[0] + " is a directory.");
            System.exit(-1);
        }

        ORB orb = ORB.init(args, null);

        BufferedReader br = new BufferedReader(new FileReader(f));

        // get object reference from command-line argument file
        Object obj = orb.string_to_object(br.readLine());
        br.close();

        // and narrow it to Sum
        // if this fails, a BAD_PARAM will be thrown
        Sum sum = SumHelper.narrow(obj);

        // invoke the operation and print the result
        System.out.println(sum.sum(12, 12));
    }
}
