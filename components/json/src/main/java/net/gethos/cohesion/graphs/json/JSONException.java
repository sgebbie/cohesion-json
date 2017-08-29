/**
 * Cohesion Framework - JSON Library
 * Copyright (c) 2017 - Stewart Gebbie, Gethos. Licensed under the MIT licence.
 * vim: set ts=4 sw=0:
 */
package net.gethos.cohesion.graphs.json;

/**
 * @author {@literal Stewart Gebbie <sgebbie@gethos.net>}
 *
 */
public class JSONException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public JSONException(String msg) {
		super(msg);
	}
	
	public JSONException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
