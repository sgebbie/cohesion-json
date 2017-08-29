/**
 * Cohesion Framework - JSON Library
 * Copyright (c) 2017 - Stewart Gebbie, Gethos. Licensed under the MIT licence.
 * vim: set ts=4 sw=0:
 */
package net.gethos.cohesion.graphs.json;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Gethos Cohesion JSON implementation.
 * <p>
 * See: http://www.json.org/
 * 
 * @author {@literal Stewart Gebbie <sgebbie@gethos.net>}
 *
 */
public class JSONObject extends JSONValue {

	private final Map<String, JSONValue> values;

	public JSONObject() {
		this.values = new TreeMap<String, JSONValue>();
	}

	@Override
	public JSONType type() {
		return JSONType.OBJECT;
	}

	public int size() {
		return values.size();
	}

	public boolean contains(String member) {
		return values.containsKey(member);
	}

	public JSONValue put(String member, JSONValue value) {
		return values.put(member, value == null ? JSONNull.JSON_NULL : value);
	}

	public JSONValue get(String member) {
		return values.get(member);
	}

	public JSONValue remove(String member) {
		return values.remove(member);
	}

	public Set<String> members() {
		return values.keySet();
	}

	@Override
	public JSONObject asObject() {
		return this;
	}

	@Override
	void toString(StringBuilder text) {
		text.append('{');
		String sep = "";
		for(Map.Entry<String, JSONValue> v : values.entrySet()) {
			text.append(sep);
			text.append('"').append(JSONString.escape(v.getKey())).append('"');
			text.append(':');
			JSONValue x = v.getValue();
			if (x == null) x = JSONNull.JSON_NULL;
			x.toString(text);

			sep = ",";
		}
		text.append('}');
	}

	@Override
	public int hashCode() {
		return values.hashCode();
	}

	@Override
	public boolean equals(Object x) {
		if (x != null && x instanceof JSONObject) {
			return values.equals(((JSONObject)x).values);
		}
		return false;
	}

	// -- convenience methods

	public JSONValue put(String member, String value) {
		return put(member, value == null ? JSONNull.JSON_NULL : new JSONString(value));
	}

	public JSONValue put(String member, Boolean value) {
		return put(member, value == null ? JSONNull.JSON_NULL : new JSONBoolean(value));
	}

	public JSONValue put(String member, BigDecimal value) {
		return put(member, value == null ? JSONNull.JSON_NULL : new JSONNumber(value));
	}

	public JSONValue put(String member, Double value) {
		return put(member, value == null ? JSONNull.JSON_NULL : new JSONNumber(value));
	}

	public JSONValue put(String member, Long value) {
		return put(member, value == null ? JSONNull.JSON_NULL : new JSONNumber(value));
	}

}
