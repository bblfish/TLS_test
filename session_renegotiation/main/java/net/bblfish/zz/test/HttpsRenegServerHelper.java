package net.bblfish.zz.test;

import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.engine.connector.Connection;
import org.restlet.engine.connector.InboundWay;
import org.restlet.ext.ssl.HttpsServerHelper;
import org.restlet.ext.ssl.internal.HttpsServerInboundWay;

/**
 * @author hjs
 * @created: 02/09/2011
 */
public class HttpsRenegServerHelper extends HttpsServerHelper {

	public HttpsRenegServerHelper(Server server) {
        super(server);
    }

	@Override
	public InboundWay createInboundWay(Connection<Server> connection, int bufferSize) {
		return new HttpsRenegServerInboundWay(connection, bufferSize);
	}


}
