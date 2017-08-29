/**
 * Cohesion Framework - JSON Library
 * Copyright (c) 2017 - Stewart Gebbie, Gethos. Licensed under the MIT licence.
 * vim: set ts=4 sw=0:
 */
package net.gethos.cohesion.graphs.json;

/**
 * Gethos Cohesion JSON implementation.
 * <p>
 * See: http://www.json.org/
 * 
 * @author {@literal Stewart Gebbie <sgebbie@gethos.net>}
 *
 */
public class JSONNull extends JSONValue {

	public static final String NULL = "null";

	public static final JSONNull JSON_NULL = new JSONNull();

	public final Object value;

	public JSONNull() {
		this.value = null;
	}

	@Override
	public JSONType type() {
		return JSONType.NULL;
	}

	@Override
	public JSONNull asNull() {
		return this;
	}

	@Override
	void toString(StringBuilder text) {
		text.append(NULL);
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object x) {
		return (x != null && x instanceof JSONNull);
	}
}
