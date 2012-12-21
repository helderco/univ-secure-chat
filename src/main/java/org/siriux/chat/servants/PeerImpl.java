
package org.siriux.chat.servants;

public class PeerImpl extends PeerPOA {

    private String name;

    public PeerImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void send(String message, String from) {
        for (int i=0; i < ("@" + from + "> ").length(); i++){
            System.out.print("\b");
        }
        System.out.println(from + " says: " + message);
        System.out.print("> ");
    }
}
