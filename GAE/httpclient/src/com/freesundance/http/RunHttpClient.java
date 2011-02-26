package com.freesundance.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

public class RunHttpClient {

	private static Logger log = Logger.getLogger(RunHttpClient.class);

	private static final String LT_VALUE_STR = "<input type=\"hidden\" name=\"lt\" value=\"";
	private static final String ST_VALUE_STR = "window.location.href=\"https://my3.three.co.uk/myaccount/postPayFreeUnits.do?ticket";

	static void doIt() throws Exception {

		ThreeResponseHandler responseHandler = new ThreeResponseHandler();

		DefaultHttpClient client = new DefaultHttpClient();
		ThreeRedirectStrategy redirectStrategy = new ThreeRedirectStrategy();
		client.setRedirectStrategy(redirectStrategy);
		
		log.info("Cookie policy ["
				+ client.getParams().getParameter(ClientPNames.COOKIE_POLICY)
				+ "]");
		
		HttpContext localContext = new BasicHttpContext();
		CookieStore cookieStore = new BasicCookieStore();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		CookieOrigin cookieOrigin = (CookieOrigin) localContext
				.getAttribute(ClientContext.COOKIE_ORIGIN);
		log.info("Cookie origin: " + cookieOrigin);

		CookieSpec cookieSpec = (CookieSpec) localContext
				.getAttribute(ClientContext.COOKIE_SPEC);
		log.info("Cookie spec used: " + cookieSpec);

		MyHttpParams params = new MyHttpParams();
		params.setParameter("http.protocol.handle-redirects", true);

		// ***********************************************************
		// get connected
		//
		log.info("Initial connect");

		HttpGet httpGet = new HttpGet("https://my3.three.co.uk/mylogin//login");
		updateHeaders(httpGet, params);

		dumpRequest(httpGet);
		HttpResponse response = client.execute(httpGet, localContext);
		responseHandler.handleResponse(response);

		dumpCookies(localContext);

		// ***********************************************************
		// login form submission
		//
		log.info("Submit form");

		List<String> splitters = new ArrayList<String>();
		splitters.add("\"");
		String ltValue = findBreadcrumbValue(responseHandler.getResult(),
				LT_VALUE_STR, splitters);

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("username", ""));
		formparams.add(new BasicNameValuePair("password", ""));
		formparams.add(new BasicNameValuePair("lt", ltValue));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
				"UTF-8");

		HttpPost httpPost = new HttpPost(
				"https://my3.three.co.uk/mylogin//login");

		// params.setParameter("service", URLEncoder.encode(
		// "https://my3.three.co.uk/myaccount/postPayFreeUnits.do",
		// "UTF-8"));
		updateHeaders(httpPost, params);

		httpPost.setEntity(entity);

		dumpRequest(httpPost);

		response = client.execute(httpPost, localContext);
		responseHandler.handleResponse(response);
		dumpCookies(localContext);


		// ***********************************************************
		// get a ticket
		//
		log.info("Get ticket for postPayFreeUnits.do");
		httpGet = new HttpGet(
				"https://my3.three.co.uk/myaccount/postPayFreeUnits.do");
		updateHeaders(httpGet, params);
		dumpRequest(httpGet);

		response = client.execute(httpGet, localContext);
		responseHandler.handleResponse(response);
		dumpCookies(localContext);

		splitters.add("=");
		String ticket = findBreadcrumbValue(responseHandler.getResult(),
				ST_VALUE_STR, splitters);

		// ***********************************************************
		// follow link
		//
		log.info("Open postPayFreeUnits.do");
		
		params.removeParameter("service");
		httpGet = new HttpGet(
				"https://my3.three.co.uk/myaccount/postPayFreeUnits.do?ticket="
						+ ticket);
		// params.setParameter("ticket", ticket);
		updateHeaders(httpGet, params);

		httpGet.addHeader(
				"Referer",
				"https://my3.three.co.uk/mylogin//login?service=https%3A%2F%2Fmy3.three.co.uk%2Fmyaccount%2FpostPayFreeUnits.do");
		dumpRequest(httpGet);

		response = client.execute(httpGet, localContext);
		responseHandler.handleResponse(response);
		dumpCookies(localContext);


		if (responseHandler.getResult().contains("Check my usage")) {
			String table = retrieveUsageTable(responseHandler.getResult());
			log.debug(table);

			table = table.replace("&nbsp;", "");

			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPath = xPathFactory.newXPath();

			XPathExpression xPathExpression = xPath
					.compile("/table/tbody/tr[2]/td[1]/span/text()");
			Object heading = xPathExpression.evaluate(new InputSource(IOUtils
					.toInputStream(table)));
			xPathExpression = xPath
					.compile("/table/tbody/tr[2]/td[2]/span/text()");
			Object value = xPathExpression.evaluate(new InputSource(IOUtils
					.toInputStream(table)));

			log.info(heading.toString().trim() + " : ["
					+ value.toString().trim() + "]");
		}


	}

	static String retrieveUsageTable(String html) {

		String xmlSnippet = null;

		int ix1 = html.indexOf("<table class=\"bill\">");
		int ix2 = html.indexOf("</table>") + "</table>".length();

		xmlSnippet = html.substring(ix1, ix2);

		return xmlSnippet;
	}

	static String findBreadcrumbValue(String html, String breadcrumb,
			List<String> splitList) {
		int ix = html.indexOf(breadcrumb);

		html = html.substring(ix + breadcrumb.length(), html.length());
		String result = html;
		ix = 0;
		for (String splitter : splitList) {
			result = result.split(splitter)[ix];
			ix++;
		}

		log.info("result [" + result + "]");
		return result;
	}

	static void updateHeaders(HttpRequestBase request, MyHttpParams params)
			throws UnsupportedEncodingException {
		request.addHeader("User-Agent",
				"Mozilla/5.0 (X11; U; Linux i686; en-GB; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.04 (lucid) Firefox/3.6.13 GTB7.1");
		request.addHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.addHeader("Accept-Language", "en-gb,en;q=0.5");
		request.addHeader("Accept-Encoding", "gzip,deflate");
		request.addHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		request.addHeader("Keep-Alive", "115");
		request.addHeader("Connection", "keep-alive");
		request.setParams(params);

	}

	static void dumpCookies(HttpContext ctx) {
		log.info("#################### Cookies #########################");
		List<Cookie> cookies = ((CookieStore) ctx
				.getAttribute(ClientContext.COOKIE_STORE)).getCookies();
		if (cookies.isEmpty()) {
			log.info("No cookies");
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				log.info("[" + cookies.get(i).toString() + "]");
			}
		}
		log.info("######################################################");
	}

	static void dumpRequest(HttpRequestBase request) {
		Header[] headers = request.getAllHeaders();

		log.info("**************** Request ***********************");
		log.info(request.getRequestLine());

		StringBuffer sw = new StringBuffer();
		for (Header header : headers) {
			sw.append(header.getName()).append(" : ").append(header.getValue());
			log.info(sw.toString());
			sw.delete(0, sw.length());
		}

		MyHttpParams params = ((MyHttpParams) request.getParams());
		for (String name : params.getParameterNames()) {
			sw.append(name).append(" : ").append(params.getParameter(name));
			log.info(sw.toString());
			sw.delete(0, sw.length());
		}
		log.info("*************************************************");
	}

	public static void main(String[] args) throws Exception {
		RunHttpClient.doIt();
	}
}
