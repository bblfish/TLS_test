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
