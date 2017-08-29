/**
 * Cohesion Framework - JSON Library
 * Copyright (c) 2017 - Stewart Gebbie, Gethos. Licensed under the MIT licence.
 * vim: set ts=4 sw=0:
 */
package net.gethos.cohesion.graphs.json;

/**
 * Gethos Cohesion JSON implementation.
 * 
 * @author {@literal Stewart Gebbie <sgebbie@gethos.net>}
 *
 */
public abstract class JSONValue {

	/**
	 * Convert the object to is JSON representation.
	 */
	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		toString(text);
		return text.toString();
	}

	abstract void toString(StringBuilder text);

	@Override
	abstract public int hashCode();

	@Override
	abstract public boolean equals(Object x);

	// -- enum of type

	abstract public JSONType type();

	// -- obtain underlying type

	public JSONObject asObject() {
		return null;
	}

	public JSONArray asArray() {
		return null;
	}

	public JSONNumber asNumber() {
		return null;
	}

	public JSONString asString() {
		return null;
	}

	public JSONBoolean asBoolean() {
		return null;
	}

	public JSONNull asNull() {
		return null;
	}

	// -- check type

	public boolean isObject() {
		return asObject() != null;
	}

	public boolean isArray() {
		return asArray() != null;
	}

	public boolean isNumber() {
		return asNumber() != null;
	}

	public boolean isString() {
		return asString() != null;
	}

	public boolean isBoolean() {
		return asBoolean() != null;
	}

	public boolean isNull() {
		return asNull() != null;
	}
}
