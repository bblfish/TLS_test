/*
 *  New BSD license: http://opensource.org/licenses/bsd-license.php
 * 
 * Copyright (c) 2010
 * Henry Story
 * http://bblfish.net/
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * - Neither the name of bblfish.net, Inc. nor the names of its contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.

 */
package net.bblfish.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.jsslutils.sslcontext.SSLContextFactory.SSLContextFactoryException;
import org.jsslutils.sslcontext.PKIXSSLContextFactory;
import org.jsslutils.sslcontext.X509SSLContextFactory;
import org.jsslutils.sslcontext.trustmanagers.TrustAllClientsWrappingTrustManager;
import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.handler.ContextHandler;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.jsslutils.sslcontext.X509TrustManagerWrapper;
//import org.mortbay.jetty.security.UserRealm;
//import org.mortbay.jetty.servlet.ServletHandler;

/**
 * A very simple server that listens securely (TLS) on port 8443, and generates a web page
 * with which the user can from his browser invalidate the TLS Session and specify what
 * error message the server should send at the next SSL Connection. Error messages are sent
 * by throwing an exception.  
 *
 * @author Henry Story
 */
public class SSLTestServer extends AbstractHandler {

	static String keyStorePassword = "secret";
	static int counter = 0;
	static SSLContext sslContext;
	String sslsession;
	static TestTrustManager trustMgr = new TestTrustManager();

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			  throws IOException, ServletException {
		//we don't have any mappings. all urls return the same page

		String type = (String) request.getParameter("do");
		trustMgr.setActionType(type);
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		sslsession = (String) request.getAttribute("javax.servlet.request.ssl_session_id");
		X509Certificate[] certificates = (X509Certificate[]) request
                .getAttribute("javax.servlet.request.X509Certificate");
		String dn = "no certificate?";
		if (certificates != null) {
			X509Certificate cert=certificates[0];
			dn=cert.getSubjectDN().toString();
		}
		final String reset = request.getParameter("reset");
		if ("off".equals(reset)) {
			SSLSessionContext cntxt = sslContext.getServerSessionContext();
			if (null == cntxt) {
				System.out.println("null context!");
			} else {
				SSLSession s = cntxt.getSession(new BigInteger(sslsession, 16).toByteArray());
				if (null == s) {
					System.out.println("could not find session " + sslsession);
				} else {
					s.invalidate();
					sslsession = "invalidated";
				}
			}

		}
		PrintWriter w = response.getWriter();
		w.println("<html><head></head><body><h1>Test SSL - page " + counter++ + "</h1>\n"
				  + "<p>This service allows you to  invalidate the TLS Session and specify what error message the server should "
				  + " send at the next SSL Connection. "
				  + " Error messages are sent  by throwing an exception.  Note that it will require 1 connection to send the "
				  + " message to close down the session and another connection to allow server to close it.</p>"
				  + "<p> SSL Session ID is: " + sslsession + "</p>\n"
				  + "<p> received certificate with DN="+dn+"</p>"
				  + "<p>action=" + type + " and reset="+reset+"</p>\n"
				  + "<p>Set the behavior of the SSL stack: </p>\n"
				  + "<form action='/"+counter+"' method='POST'>\n"
				  + "<input type='radio' name='do' value='CertificateException'>throw <a href='http://download.oracle.com/javase/6/docs/api/java/security/cert/CertificateException.html'>CertificateException</a><br/>\n"
				  + "<input type='radio' name='do' value='CertificateNotYetValidException'>throw <a href='http://download.oracle.com/javase/6/docs/api/java/security/cert/CertificateNotYetValidException.html'>CertificateNotYetValidException</a><br>\n"
				  + "<input type='radio' name='do' value='CertificateExpiredException'>throw <a href='http://download.oracle.com/javase/6/docs/api/java/security/cert/CertificateExpiredException.html'>CertificateExpiredException</a><br>\n"
				  + "<input type='radio' name='do' value='CertificateEncodingException'>throw <a href='http://download.oracle.com/javase/6/docs/api/java/security/cert/CertificateEncodingException.html'>CertificateEncodingException</a><br>\n"
				  + "<input type='radio' name='do' value='CertificateParsingException'>throw <a href='http://download.oracle.com/javase/6/docs/api/java/security/cert/CertificateParsingException.html'>CertificateParsingException</a><br>\n"
				  + "<input type='checkbox' name='reset' value='off'>reset ssl session</input><br>"
				  + "<input type='submit' value='set'>"
				  + "</form>"
				  + "<p>Go to <a href='/"+counter+"'>next page</a></p>"
				  + "</body></html>");
		baseRequest.setHandled(true);

	}

	/**
	 * Code copied from http://wiki.eclipse.org/Jetty/Tutorial/Jetty_and_Maven_HelloWorld
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Server server = null;
		server = new Server();
		SSLTestServer sslTestServer = new SSLTestServer();
		server.setHandler(sslTestServer);
		trustMgr.setServlet(sslTestServer);

		//initialisation
		final KeyStore keystore = KeyStore.getInstance("JKS");

		InputStream ksInputStream = SSLTestServer.class.getResourceAsStream("/localhost.jks");
		keystore.load(ksInputStream, keyStorePassword.toCharArray());
		ksInputStream.close();

		X509SSLContextFactory trust = new PKIXSSLContextFactory(keystore, keyStorePassword, null);

		trust.setTrustManagerWrapper(trustMgr);
		sslContext = trust.buildSSLContext();
//		sslContext.getServerSessionContext().setSessionTimeout(1);
		SslSocketConnector sslConnector = new SslSocketConnector();
		sslConnector.setPort(8443);
		sslConnector.setMaxIdleTime(3000);
		sslConnector.setHandshakeTimeout(10000);
		sslConnector.setSslContext(sslContext);
		sslConnector.setWantClientAuth(true);


//      SocketConnector socketConnector = new SocketConnector();
//      socketConnector.setPort(8080);
//      server.addConnector(socketConnector);

		server.addConnector(sslConnector);

		//also add an http connector
//     SelectChannelConnector httpConnector = new SelectChannelConnector();
//     httpConnector.setPort(8844);
//     httpConnector.setMaxIdleTime(3000);
//     server.addConnector(httpConnector);

//		ServletHandler handler = new ServletHandler();
//
//		ContextHandler context = new ContextHandler("/context");
//		context.setHandler(handler);
//
//
//		handler.addServletWithMapping(CheckClient.class, "/servlet/CheckClient");
//		handler.addServletWithMapping(CreateCertSPKAC.class, "/servlet/CreateCertSPKAC");
//		server.addHandler(handler);

//		UserRealm realm = null;


//		server.addUserRealm(realm);
		server.start();
		server.join();


	}

	static class TestTrustManager implements X509TrustManagerWrapper {

		SSLTestServer server;
		String type;

		void setServlet(SSLTestServer server) {
			this.server = server;
		}

		public void setActionType(String type) {
			if (type == null) return;
			System.out.println("setting action to type=" + type);
			this.type = type;
		}

		public X509TrustManager wrapTrustManager(X509TrustManager trustManager) {
			return new TrustAllClientsWrappingTrustManager(trustManager) {

				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					System.out.println("in action. Type=" + type);
					try {
						CertificateException e =null;
						if ("CertificateException".equals(type)) {
							 e = new CertificateException("reset");
						} else if ("CertificateNotYetValidException".equals(type)) {
							 e = new CertificateNotYetValidException();
						} else if ("CertificateExpiredException".equals(type)) {
							e = new CertificateExpiredException();
						} else if ("CertificateEncodingException".equals(type)) {
							e = new CertificateEncodingException();
						} else if ("CertificateParsingException".equals(type)) {
							e = new CertificateParsingException();
						} 
						if (e!=null) throw e;
					} finally {
						type = null;
					}
				}
			};
		}
	}
}
