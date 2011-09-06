/*
 * BSD like license.
 *
 * Copyright (c) 2011 Henry Story.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by the Henry Story.  The name of Henry Story may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */

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
