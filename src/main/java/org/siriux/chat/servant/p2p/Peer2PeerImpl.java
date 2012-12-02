/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.siriux.chat.servant.p2p;

import org.siriux.chat.servant.m2m.Peer2PeerPOA;

/**
 *
 * @author mluis
 */
public class Peer2PeerImpl extends Peer2PeerPOA{
    
    public void sendMsg2Peer(String msg, String peer) {
        for(int i=0; i < new String("@" + peer + ">").length(); i++){
            System.out.print("\b");
        }
        System.out.println(peer + " says: " + msg);
        System.out.print("@" + peer + ">");
    }
    
}
