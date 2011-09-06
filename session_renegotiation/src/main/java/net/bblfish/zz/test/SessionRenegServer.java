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

import org.jsslutils.keystores.KeyStoreLoader;
import org.jsslutils.sslcontext.X509SSLContextFactory;
import org.jsslutils.sslcontext.X509TrustManagerWrapper;
import org.jsslutils.sslcontext.trustmanagers.TrustAllClientsWrappingTrustManager;
import org.restlet.Context;
import org.restlet.Server;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.engine.ConnectorHelper;
import org.restlet.engine.Engine;
import org.restlet.ext.ssl.HttpsServerHelper;
import org.restlet.ext.ssl.JsslutilsSslContextFactory;
import org.restlet.ext.ssl.SslContextFactory;
import org.restlet.util.Series;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author hjs
 * @created: 01/09/2011
 */
public class SessionRenegServer  {

   public static void main(String[] args) throws Exception {
	   final List<ConnectorHelper<Server>> registeredServers = Engine.getInstance().getRegisteredServers();
	   final Iterator<ConnectorHelper<Server>> helperIterator = registeredServers.iterator();
	   final List<ConnectorHelper<Server>> newServers = new ArrayList<ConnectorHelper<Server>>(registeredServers.size());
	   while(helperIterator.hasNext()) {
		   final ConnectorHelper<Server> helper = helperIterator.next();
		   if (! (helper instanceof HttpsServerHelper))newServers.add(helper);
	   }
	   newServers.add(new HttpsRenegServerHelper(null));
	   Engine.getInstance().setRegisteredServers(newServers);

	   // Create the HTTP server and listen on port 8443
	   Server srv = new Server(Protocol.HTTPS, 8443, HelloWorldResource.class);
	   srv.setContext(new Context());
	   Series<Parameter> parameters = srv.getContext().getParameters();
	   parameters.set("keystorePath", SessionRenegServer.class.getResource("/localhost.jks").getFile());
	   parameters.set("keystorePassword","secret");
	   parameters.set("keyPassword", "secret");
	   parameters.set("keystoreType", "JKS");
	   parameters.set("maxIoIdleTimeMs", "0"); //no timeout for debug purposes


	   KeyStoreLoader ksloader = new KeyStoreLoader();
	   ksloader.setKeyStoreType("JKS");
	   ksloader.setKeyStorePath(SessionRenegServer.class.getResource("/localhost.jks").getFile());
	   ksloader.setKeyStorePassword("secret");
	   final KeyStore keyStore = ksloader.loadKeyStore();
	   final X509SSLContextFactory contextFactory = new X509SSLContextFactory(keyStore, "secret", keyStore);
	   contextFactory.setTrustManagerWrapper(new X509TrustManagerWrapper() {
		   public X509TrustManager wrapTrustManager(X509TrustManager trustManager) {
			   return new TrustAllClientsWrappingTrustManager(trustManager) {
				   @Override
				   public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					   System.out.println("testing certificate with DN="+chain[0].getSubjectDN());
				   }
			   };
		   }
	   });
	   final JsslutilsSslContextFactory jsslutilsSslContextFactory = new JsslutilsSslContextFactory(contextFactory);
	   srv.getContext().getAttributes().put("sslContextFactory", jsslutilsSslContextFactory);

	   srv.start();

   }


}

