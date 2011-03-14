package com.freesundance.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

public class ThreeUsageDUD {

	private static Logger log = Logger.getLogger(ThreeUsageDUD.class);

	public static String THREE_INVALID_USERNAME_OR_PASSWORD_MESSAGE = "Sorry, you've entered an invalid 3 mobile number or password";

	private static final String LT_VALUE_STR = "<input type=\"hidden\" name=\"lt\" value=\"";
	private static final String ST_VALUE_FOR_INDEX_STR = "https://my3.three.co.uk/myaccount/index.do?ticket";
	private static final String ST_VALUE_FOR_POSTPAY_STR = "https://my3.three.co.uk/myaccount/postPayFreeUnits.do?ticket";

	private static final String NOTE_STR = "<p><caption><span class=\"note\">* This information was correct at";

	private Map<String, ThreeAllowanceItem> allowance = new HashMap<String, ThreeAllowanceItem>();

	static ThreeUsageDUD doIt(String username, String password)
			throws ThreeUsageCredentialException, Exception {
		return doIt(username, password, null);
	}

	static ThreeUsageDUD doIt(String username, String password,
			ClientConnectionManager connectionManager)
			throws ThreeUsageCredentialException, Exception {

		ThreeResponseHandler responseHandler = new ThreeResponseHandler();

		DefaultHttpClient client = new DefaultHttpClient(connectionManager);
		ThreeRedirectStrategy redirectStrategy = new ThreeRedirectStrategy();
		client.setRedirectStrategy(redirectStrategy);

		HttpContext localContext = new BasicHttpContext();
		CookieStore cookieStore = new BasicCookieStore();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

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
		formparams.add(new BasicNameValuePair("username", username));
		formparams.add(new BasicNameValuePair("password", password));
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

		checkLoginSuccessful(responseHandler.getResult());

		NameValuePair ticketParam = getTicket(client, localContext,
				responseHandler);

		return processFreeUnits(client, localContext, responseHandler,
				ticketParam);
	}

	private static ThreeUsageDUD processFreeUnits(DefaultHttpClient client,
			HttpContext localContext, ThreeResponseHandler responseHandler,
			NameValuePair ticketParam) throws ClientProtocolException,
			IOException, URISyntaxException, XPathExpressionException {

		ThreeUsageDUD threeUsage = new ThreeUsageDUD();

		// ***********************************************************
		// follow link
		//
		log.info("Open index.do");

		HttpGet httpGet = new HttpGet(
				"https://my3.three.co.uk/myaccount/postPayFreeUnits.do?ticket="
						+ ticketParam.getValue());
		// List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		// qparams.add(ticketParam);
		// URI uri = URIUtils.createURI("https", "my3.three.co.uk", -1,
		// "myaccount/index.do",
		// URLEncodedUtils.format(qparams, "UTF-8"),
		// null);
		// httpGet = new HttpGet(uri);
		MyHttpParams params = new MyHttpParams();
		params.setParameter("http.protocol.handle-redirects", true);
		updateHeaders(httpGet, params);

		httpGet.addHeader(
				"Referer",
				"https://my3.three.co.uk/mylogin//login?service=https%3A%2F%2Fmy3.three.co.uk%2Fmyaccount%2FpostPayFreeUnits.do");
		dumpRequest(httpGet);

		HttpResponse response = client.execute(new HttpHost("my3.three.co.uk"),
				httpGet, localContext);
		responseHandler.handleResponse(response);
		dumpCookies(localContext);

		if (responseHandler.getResult().contains("Check my usage")) {
			String table = retrieveUsageTable(responseHandler.getResult());

			if (log.isDebugEnabled()) {
				log.debug(table);
			}

			table = table.replace("&nbsp;", "");

			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPath = xPathFactory.newXPath();

			Map<String, ThreeAllowanceItem> allowanceMap = new HashMap<String, ThreeAllowanceItem>();

			for (int row = 1; row < 4; row++) {

				XPathExpression xPathExpression = xPath
						.compile("/table/tbody/tr[" + row
								+ "]/td[1]/span/text()");
				Object heading = xPathExpression.evaluate(new InputSource(
						IOUtils.toInputStream(table)));

				String headingString = heading.toString().trim();

				xPathExpression = xPath.compile("/table/tbody/tr[" + row
						+ "]/td[2]/span/text()");
				Object value = xPathExpression.evaluate(new InputSource(IOUtils
						.toInputStream(table)));

				ThreeAllowanceItem allowanceItem = new ThreeAllowanceItem(
						headingString, value.toString().trim());
				if (log.isDebugEnabled()) {
					log.debug(allowanceItem.toString());
				}
				allowanceMap.put(headingString, allowanceItem);
			}

			List<String> splitters = new ArrayList<String>();
			splitters.add("<");
			String datetime = findBreadcrumbValue(responseHandler.getResult(),
					NOTE_STR, splitters);

			allowanceMap.put("datetime", new ThreeAllowanceItem("datetime",
					datetime));
			threeUsage.setAllowance(allowanceMap);
		}
		return threeUsage;
	}

	private static NameValuePair getTicket(DefaultHttpClient client,
			HttpContext localContext, ThreeResponseHandler responseHandler)
			throws ClientProtocolException, IOException, URISyntaxException {

		log.info("request service postPayFreeUnits.do and return ticket");

		// HttpGet httpGet = new
		// HttpGet("https://my3.three.co.uk/mylogin//login");
		// NameValuePair service = new BasicNameValuePair("service",
		// "https://my3.three.co.uk/myaccount/postPayFreeUnits.do");
		//
		// List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		// qparams.add(service);
		URI uri = URIUtils.createURI("https", "my3.three.co.uk", -1,
				"/myaccount/postPayFreeUnits.do", null, null);
		HttpGet httpGet = new HttpGet(uri);

		MyHttpParams params = new MyHttpParams();
		params.setParameter("http.protocol.handle-redirects", true);
		updateHeaders(httpGet, params);
		dumpRequest(httpGet);

		HttpResponse response = client.execute(new HttpHost("my3.three.co.uk"),
				httpGet, localContext);
		responseHandler.handleResponse(response);
		dumpCookies(localContext);

		List<String> splitters = new ArrayList<String>();
		splitters.add("\"");
		splitters.add("=");
		String ticket = findBreadcrumbValue(responseHandler.getResult(),
				ST_VALUE_FOR_INDEX_STR, splitters);
		return new BasicNameValuePair("ticket", ticket);
	}

	private static void checkLoginSuccessful(String html)
			throws ThreeUsageCredentialException {

		if (html.contains(THREE_INVALID_USERNAME_OR_PASSWORD_MESSAGE)) {
			throw new ThreeUsageCredentialException(
					THREE_INVALID_USERNAME_OR_PASSWORD_MESSAGE);
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

		if (log.isDebugEnabled()) {
			log.debug("Looking for [" + breadcrumb + "]");
		}
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
		request.addHeader(
				"User-Agent",
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
		if (log.isDebugEnabled()) {
			log.debug("#################### Cookies #########################");

			List<Cookie> cookies = ((CookieStore) ctx
					.getAttribute(ClientContext.COOKIE_STORE)).getCookies();
			if (cookies.isEmpty()) {
				log.debug("No cookies");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					log.debug("[" + cookies.get(i).toString() + "]");
				}
			}
			log.debug("######################################################");
		}
	}

	static void dumpRequest(HttpRequestBase request) {
		Header[] headers = request.getAllHeaders();

		if (log.isDebugEnabled()) {
			log.debug("**************** Request ***********************");
			log.debug(request.getRequestLine());

			StringBuffer sw = new StringBuffer();
			for (Header header : headers) {
				sw.append(header.getName()).append(" : ")
						.append(header.getValue());
				log.debug(sw.toString());
				sw.delete(0, sw.length());
			}

			// HttpParams params = request.getParams();
			// for (String name : params.getParameterNames()) {
			// sw.append(name).append(" : ").append(params.getParameter(name));
			// log.debug(sw.toString());
			// sw.delete(0, sw.length());
			// }
			log.debug("*************************************************");
		}
	}

	public void setAllowance(Map<String, ThreeAllowanceItem> allowance) {
		this.allowance = allowance;
	}

	public Map<String, ThreeAllowanceItem> getAllowance() {
		return allowance;
	}

	public static void main(String[] args) throws Exception {
		log.warn(ThreeUsageDUD.doIt(args[0], args[1]).getAllowance().toString());
	}

}
