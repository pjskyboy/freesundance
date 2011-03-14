package com.freesundance.http.gae;

/*
 ESXX - The friendly ECMAscript/XML Application Server
 Copyright (C) 2007-2010 Martin Blom <martin@blom.org>

 This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.


 PLEASE NOTE THAT THIS FILE'S LICENSE IS DIFFERENT FROM THE REST OF ESXX!
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.params.HttpParams;

public class GAEConnectionManager implements ClientConnectionManager {

	private SchemeRegistry schemeRegistry;

	public GAEConnectionManager() {
		SchemeSocketFactory no_socket_factory = new SchemeSocketFactory() {

			public boolean isSecure(Socket s) {
				return false;
			}

			@Override
			public Socket createSocket(HttpParams params) throws IOException {
				return null;
			}

			@Override
			public Socket connectSocket(Socket sock,
					InetSocketAddress remoteAddress,
					InetSocketAddress localAddress, HttpParams params)
					throws IOException, UnknownHostException,
					ConnectTimeoutException {
				return null;
			}
		};

		schemeRegistry = new SchemeRegistry();

		schemeRegistry.register(new Scheme("http", 80, no_socket_factory));
		schemeRegistry.register(new Scheme("https", 443, no_socket_factory));
	}

	@Override
	public SchemeRegistry getSchemeRegistry() {
		return schemeRegistry;
	}

	@Override
	public ClientConnectionRequest requestConnection(final HttpRoute route,
			final Object state) {
		return new ClientConnectionRequest() {
			public void abortRequest() {
				// Nothing to do
			}

			public ManagedClientConnection getConnection(long timeout,
					TimeUnit tunit) {
				return GAEConnectionManager.this.getConnection(route, state);
			}
		};
	}

	@Override
	public void releaseConnection(ManagedClientConnection conn,
			long validDuration, TimeUnit timeUnit) {
	}

	@Override
	public void closeIdleConnections(long idletime, TimeUnit tunit) {
	}

	@Override
	public void closeExpiredConnections() {
	}

	@Override
	public void shutdown() {
	}

	private ManagedClientConnection getConnection(HttpRoute route, Object state) {
		return new GAEClientConnection(this, route, state);
	}

}
