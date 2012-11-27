/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.siriux.chat.servant.m2m;

/**
 *
 * @author mluis
 */
public class ServiceEnablerImpl extends ServiceEnablerPOA {
    
    private int MaxRegPeers=65535;
    private int numRegPeers=0;
    
    public void register(String peerName){
     
        this.numRegPeers++;
    }
    
}