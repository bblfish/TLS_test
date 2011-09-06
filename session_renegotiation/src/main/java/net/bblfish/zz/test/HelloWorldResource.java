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

import org.restlet.Request;
import org.restlet.ext.ssl.internal.HttpsInboundRequest;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * @author hjs
 * @created: 01/09/2011
 */
public class HelloWorldResource extends ServerResource {

   @Get
   public String toString() {
	   final Request request = getRequest();
	   if (request instanceof HttpsInboundRequest) {
		   HttpsInboundRequest httpsReq = (HttpsInboundRequest)request;
		   final javax.security.cert.X509Certificate[] chain;
		   try {
			   chain = httpsReq.getConnection().getSslEngine().getSession().getPeerCertificateChain();
			   if (chain != null && chain.length > 0) {
				   return chain[0].toString();
			   }
		   } catch (SSLPeerUnverifiedException e) {
			   return "We could not authenticate you. \r\n" +
					   "Trying to access your certificate we caught the following exception\r\n"+e.toString();
		   }
	   }
      return "hello, world";
   }

}
