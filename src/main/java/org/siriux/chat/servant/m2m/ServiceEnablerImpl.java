/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.siriux.chat.servant.m2m;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author mluis
 */
public class ServiceEnablerImpl extends ServiceEnablerPOA {
    
    private ArrayList<String> peerList = new ArrayList<String>();
    
    private static Logger logger = LoggerFactory.getLogger(ServiceEnablerImpl.class);
    
    /**
     *
     * @param peerName
     */
    public void recordPeer(String peerName){
        //Record Peer
        peerList.add(peerName);
        logger.info("{} recorded...", peerName);
        
               
        //Announce to other peers
        for(String peer: peerList){
            
        }
    }
    
}