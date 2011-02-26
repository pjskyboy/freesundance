package com.freesundance.http;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.params.AbstractHttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class MyHttpParams extends AbstractHttpParams implements Serializable, Cloneable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3711321632744683910L;

	/** Map of HTTP parameters that this collection contains. */
	private final Map<String, Object> parameters = new HashMap<String, Object>();

	public MyHttpParams() {
		super();
	}

	public Set<String> getParameterNames() {
		return parameters.keySet();
	}

	public Object getParameter(final String name) {
		return this.parameters.get(name);
	}

	public HttpParams setParameter(final String name, final Object value) {
		this.parameters.put(name, value);
		return this;
	}

	public boolean removeParameter(String name) {
		// this is to avoid the case in which the key has a null value
		if (this.parameters.containsKey(name)) {
			this.parameters.remove(name);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Assigns the value to all the parameter with the given names
	 * 
	 * @param names
	 *            array of parameter names
	 * @param value
	 *            parameter value
	 */
	public void setParameters(final String[] names, final Object value) {
		for (int i = 0; i < names.length; i++) {
			setParameter(names[i], value);
		}
	}

	/**
	 * Is the parameter set?
	 * <p>
	 * Uses {@link #getParameter(String)} (which is overrideable) to fetch the
	 * parameter value, if any.
	 * <p>
	 * Also @see {@link #isParameterSetLocally(String)}
	 * 
	 * @param name
	 *            parameter name
	 * @return true if parameter is defined and non-null
	 */
	public boolean isParameterSet(final String name) {
		return getParameter(name) != null;
	}

	/**
	 * Is the parameter set in this object?
	 * <p>
	 * The parameter value is fetched directly.
	 * <p>
	 * Also @see {@link #isParameterSet(String)}
	 * 
	 * @param name
	 *            parameter name
	 * @return true if parameter is defined and non-null
	 */
	public boolean isParameterSetLocally(final String name) {
		return this.parameters.get(name) != null;
	}

	/**
	 * Removes all parameters from this collection.
	 */
	public void clear() {
		this.parameters.clear();
	}

	/**
	 * Creates a copy of these parameters. The implementation here instantiates
	 * {@link BasicHttpParams}, then calls {@link #copyParams(HttpParams)} to
	 * populate the copy.
	 * 
	 * @return a new set of params holding a copy of the <i>local</i> parameters
	 *         in this object.
	 * 
	 * @deprecated
	 */
	public HttpParams copy() {
		try {
			return (HttpParams) clone();
		} catch (CloneNotSupportedException ex) {
			throw new UnsupportedOperationException("Cloning not supported");
		}
	}

	public Object clone() throws CloneNotSupportedException {
		BasicHttpParams clone = (BasicHttpParams) super.clone();
		copyParams(clone);
		return clone;
	}

	/**
	 * Copies the locally defined parameters to the argument parameters. This
	 * method is called from {@link #copy()}.
	 * 
	 * @param target
	 *            the parameters to which to copy
	 */
	@SuppressWarnings("rawtypes")
	protected void copyParams(HttpParams target) {
		Iterator iter = parameters.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry me = (Map.Entry) iter.next();
			if (me.getKey() instanceof String)
				target.setParameter((String) me.getKey(), me.getValue());
		}
	}
	
}
