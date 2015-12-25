package com.vipkid.service.param;

import java.net.URLDecoder;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.vipkid.util.CharSet;

public abstract class AbstractParam<V> {
	private final V value;

	public AbstractParam(String param) throws WebApplicationException {
		try {
			param = URLDecoder.decode(param, CharSet.UTF_8);
			value = parse(param);
		} catch (Throwable e) {
			throw new WebApplicationException(onError(param, e));
		}
	}

	public V getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	protected abstract V parse(String param) throws Throwable;

	protected Response onError(String param, Throwable e) {
		return Response.status(Status.BAD_REQUEST).entity(getErrorMessage(param, e)).build();
	}

	protected String getErrorMessage(String param, Throwable e) {
		return "Invalid parameter: " + param + " (" + e.getMessage() + ")";
	}
}