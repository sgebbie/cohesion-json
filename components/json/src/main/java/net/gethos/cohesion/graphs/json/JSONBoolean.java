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
public class JSONBoolean extends JSONValue {

	public static final String TRUE = "true";
	public static final String FALSE = "false";

	public static final JSONBoolean JSON_TRUE = new JSONBoolean(true);
	public static final JSONBoolean JSON_FALSE = new JSONBoolean(false);

	public final boolean value;

	public JSONBoolean(boolean value) {
		this.value = value;
	}

	@Override
	public JSONType type() {
		return JSONType.BOOLEAN;
	}

	@Override
	public JSONBoolean asBoolean() {
		return this;
	}

	@Override
	void toString(StringBuilder text) {
		text.append(value ? TRUE : FALSE);
	}

	@Override
	public int hashCode() {
		return value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
	}

	@Override
	public boolean equals(Object x) {
		if (x != null && x instanceof JSONBoolean) {
			return value == ((JSONBoolean)x).value;
		}
		return false;
	}
}
