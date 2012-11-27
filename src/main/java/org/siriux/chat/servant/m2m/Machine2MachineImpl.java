/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.siriux.chat.servant.m2m;

/**
 *
 * @author mluis
 */
public class Machine2MachineImpl extends Machine2MachinePOA{
    
    public String sendMsg2Machine(String msg, String machine ){
        return "ok";
    }
}
