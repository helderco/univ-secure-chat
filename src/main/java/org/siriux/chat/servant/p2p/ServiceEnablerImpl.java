/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.siriux.chat.servant.p2p;
import java.util.ArrayList;
import org.siriux.chat.servant.m2m.ServiceEnablerPOA;
import org.siriux.chat.servant.m2m.ServiceEnablerPOA;
import org.siriux.chat.servant.m2m.ServiceEnablerPOA;
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
    }
    
    public String[] getConnectedPeers(){
        //Announce to other peers
        return (String []) peerList.toArray (new String [peerList.size ()]);
    }
    
}