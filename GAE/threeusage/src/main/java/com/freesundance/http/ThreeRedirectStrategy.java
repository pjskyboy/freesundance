package com.freesundance.http;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

public class ThreeRedirectStrategy implements RedirectStrategy {

	private static final Logger log = Logger
			.getLogger(ThreeRedirectStrategy.class);

	public boolean isRedirected(HttpRequest request, HttpResponse response,
			HttpContext context) throws ProtocolException {
		
		int sc = response.getStatusLine().getStatusCode();
		
		log.info("sc [" + sc + "]");

		return (sc >= 300 && sc < 400);
		
	}

	public HttpUriRequest getRedirect(HttpRequest request,
			HttpResponse response, HttpContext context)
			throws ProtocolException {
		
		String uri = response.getHeaders("Location")[0].getValue();
		
		if (log.isDebugEnabled()) {
			log.debug("returning [" + uri + "]");
		}
		return new HttpGet(uri);
	}

}
