
package org.siriux.chat;

import org.jacorb.orb.listener.SSLSessionEvent;
import org.jacorb.orb.listener.SSLSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionListener implements SSLSessionListener {

    private static Logger logger = LoggerFactory.getLogger(SessionListener.class);

    @Override
    public void sessionCreated(SSLSessionEvent e) {
        logger.debug(e.getCause().getLocalizedMessage());
    }

    @Override
    public void handshakeException(SSLSessionEvent e) {
        logger.debug(e.getCause().getLocalizedMessage());
    }

    @Override
    public void keyException(SSLSessionEvent e) {
        logger.debug(e.getCause().getLocalizedMessage());
    }

    @Override
    public void peerUnverifiedException(SSLSessionEvent e) {
        logger.debug(e.getCause().getLocalizedMessage());
    }

    @Override
    public void protocolException(SSLSessionEvent e) {
        logger.debug(e.getCause().getLocalizedMessage());
    }

    @Override
    public void sslException(SSLSessionEvent e) {
        logger.debug(e.getCause().getLocalizedMessage());
    }

}
