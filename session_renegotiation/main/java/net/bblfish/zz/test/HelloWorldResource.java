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
			   e.printStackTrace();
		   }
	   }
      return "hello, world";
   }

}
