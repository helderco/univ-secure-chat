
package org.siriux.chat.cli;

import java.util.Arrays;
import java.util.Scanner;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.siriux.chat.servants.NameAlreadyUsed;
import org.siriux.chat.servants.Peer;
import org.siriux.chat.servants.PeerHelper;
import org.siriux.chat.servants.PeerImpl;
import org.siriux.chat.servants.Server;
import org.siriux.chat.servants.ServerHelper;
import org.siriux.chat.servants.UnknownPeer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Client.class);
    private static Scanner in = new Scanner(System.in);

    protected static ORB orb;

    @Override
    public void run() {
        orb.run();
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.printf("Usage: jaco %s <nick>\n", Client.class.getName());
            System.exit(1);
        }

        // Initialization
        orb = ORB.init(args, null);
        NamingContextExt ncRef = null;
        Server server = null;

        try {
            // Activate manager
            Object poaRef = orb.resolve_initial_references("RootPOA");
            POA poa = POAHelper.narrow(poaRef);
            poa.the_POAManager().activate();
            logger.debug("Activated POA manager.");

            // Get name service
            Object nsRef = orb.resolve_initial_references("NameService");
            ncRef = NamingContextExtHelper.narrow(nsRef);
            logger.debug("Narrowed name service.");

            // Get server servant
            server = ServerHelper.narrow(ncRef.resolve_str("Server"));
            logger.debug("Narrowed server servant.");

            // Create peer servant
            Object srvRef = poa.servant_to_reference(new PeerImpl(args[0]));
            ncRef.rebind(ncRef.to_name("Peers/" + args[0]), srvRef);
        }
        catch (NotFound ex) {
            logger.error("Server not found.");
            System.err.println("Server not found.");
            System.exit(1);
        }
        catch (Exception ex) {
            logger.debug(ex.getLocalizedMessage(), ex);
            logger.error(ex.getLocalizedMessage());
            System.err.println("Couldn't start chat client. See log for details.");
            System.exit(1);
        }

        // Attempt to join
        try {
            server.join(args[0]);
            logger.info("Joined with the name `{}`.", args[0]);
        }
        catch (NameAlreadyUsed ex) {
            System.err.println("Name already in use.");
            System.exit(1);
        }
        catch (Exception ex) {
            logger.debug(ex.getLocalizedMessage(), ex);
            logger.error(ex.getLocalizedMessage());
            System.err.println("Couldn't connect to server.");
            System.exit(1);
        }

        // Setup thread to allow to receive responses
        Thread t = new Thread(new Client());

        try {
            // Start interface
            System.out.println("Entering CLI (use '/help' for a command list):");
            t.start();

            String input;

            while (true) {
                input = askInput("> ");

                if (input.startsWith("/help")) {
                    System.out.println("  /users          Gets a list of connected users");
                    System.out.println("  /chat <nick>    Chat with user");
                    System.out.println("  /keypair        Ask server for a keypair");
                    System.out.println("  /help           Show this help");
                    System.out.println("  /quit           Quit application");
                    System.out.println();
                }
                else if (input.startsWith("/quit")) {
                    break;
                }
                else if (input.startsWith("/users")) {
                    for (String user : Arrays.asList(server.getConnectedPeers())) {
                        if (!user.equals(args[0])) {
                            System.out.println(": " + user);
                        }
                    }
                }
                else if (input.startsWith("/chat")) {
                    Peer peer = null;
                    String user = null;

                    try {
                        user = input.split(" ")[1];
                        peer = PeerHelper.narrow(ncRef.resolve_str("Peers/"+user));
                        System.out.printf("Your're now connected with %s. Type `/close` to close this chat session.\n", user);
                    }
                    catch (Exception ex) {
                        logger.warn("Couldn't start chat with {}.", user);
                        logger.debug(ex.getLocalizedMessage(), ex);
                        System.err.printf("Couldn't Start chat user %s.", user);
                    }

                    // Start chat session
                    while (true) {
                        String msg = prompt("@" + user + "> ");

                        if (msg.startsWith("/close")) {
                            System.out.printf("You've left the chat with %s.\n", user);
                            break;
                        }

                        peer.send(msg, args[0]);
                    }
                }else if (input.startsWith("/keypair")) {
                    String keyPairReturn = server.genKeyPair(args[0], 30, 2048, "passw0rd");
                    System.out.println("DEBUG: " + keyPairReturn);
                }
            }

            System.out.println("Bye!");
        }
        finally {
            try {
                server.leave(args[0]);
            }
            catch (UnknownPeer ex) {
                logger.warn("Unkown peer", ex);
            }

            orb.destroy();

            try {
                t.join();
            }
            catch (InterruptedException ex) {
                // Nothing to do here
            }
        }
    }

    private static String askInput(String prompt) {
        System.out.print(prompt);
        return in.nextLine();
    }

    private static String askCleanInput(String prompt) {
        String input = askInput(prompt);

        if (in.hasNextLine()) {
            in.nextLine();
        }

        return input;
    }

    private static String prompt(String prompt) {
        String input = askInput(prompt);

        for (int i = 0; i < (prompt + input).length(); i++) {
            System.out.print("\b");
        }

        return input;
    }
}
