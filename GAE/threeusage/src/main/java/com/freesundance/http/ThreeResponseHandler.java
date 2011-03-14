package com.freesundance.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.log4j.Logger;

public class ThreeResponseHandler implements ResponseHandler<String> {

	private static Logger log = Logger.getLogger(ThreeResponseHandler.class);
	private String result = null;

	private HttpResponse lastResponse;

	public String handleResponse(HttpResponse response) {
		try {

			setLastResponse(response);
			StatusLine status = response.getStatusLine();

			log.info(status);
			HttpEntity entity = response.getEntity();

			result = IOUtils.toString(entity.getContent());
			// Message message = handler.obtainMessage();
			// Bundle bundle = new Bundle();
			// bundle.putString("RESPONSE", result);
			// message.setData(bundle);
			// handler.sendMessage(message);
			if (log.isDebugEnabled()) {
				log.debug(result);
			}

		} catch (Exception e) {
			log.error(e);
		}
		return result;
	}

	public String getResult() {
		return result;
	}

	public HttpResponse getLastResponse() {
		return lastResponse;
	}

	public void setLastResponse(HttpResponse lastResponse) {
		this.lastResponse = lastResponse;
	}
}
