package net.bblfish.zz.test;

import org.restlet.Response;
import org.restlet.Server;
import org.restlet.data.Reference;
import org.restlet.engine.connector.Connection;
import org.restlet.engine.connector.HttpServerInboundWay;
import org.restlet.engine.header.Header;
import org.restlet.ext.ssl.internal.HttpsServerInboundWay;
import org.restlet.ext.ssl.internal.SslConnection;
import org.restlet.util.Series;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.io.IOException;

/**
 * @author hjs
 * @created: 02/09/2011
 */
public class HttpsRenegServerInboundWay extends HttpsServerInboundWay {

	public HttpsRenegServerInboundWay(Connection<Server> connection, int bufferSize) {
		super(connection, bufferSize);
	}


	@Override
	protected void onReceived(Response message) {
		super.onReceived(message);
		final Reference ref = message.getRequest().getResourceRef();
		if (ref.getIdentifier().contains("reneg")) {
			final SslConnection connection = (SslConnection) getConnection();
			try {
				//some doc: http://onjava.com/onjava/2004/11/03/ssl-nio.html
				final SSLEngine sslEngine = connection.getSslEngine();
				sslEngine.setWantClientAuth(true);
				sslEngine.beginHandshake();
			} catch (SSLException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
	}

}
