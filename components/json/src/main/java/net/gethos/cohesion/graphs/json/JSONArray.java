/**
 * Cohesion Framework - JSON Library
 * Copyright (c) 2017 - Stewart Gebbie, Gethos. Licensed under the MIT licence.
 * vim: set ts=4 sw=0:
 */
package net.gethos.cohesion.graphs.json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Gethos Cohesion JSON implementation.
 * <p>
 * See: http://www.json.org/
 * 
 * @author {@literal Stewart Gebbie <sgebbie@gethos.net>}
 *
 */
public class JSONArray extends JSONValue implements Iterable<JSONValue> {

	private final List<JSONValue> values;

	private JSONType elementType;

	public JSONArray() {
		this.values = new ArrayList<JSONValue>();
		this.elementType = JSONType.NULL;
	}

	public JSONType elementType() {
		return elementType;
	}

	@Override
	public JSONType type() {
		return JSONType.ARRAY;
	}

	@Override
	public JSONArray asArray() {
		return this;
	}

	public int size() {
		return values.size();
	}

	public JSONArray add(JSONValue... x) {
		if (x == null || x.length == 0) return this;
		updateType(x[0]);
		for (JSONValue y : x)
			values.add(y);
		return this;
	}

	public JSONValue set(int idx, JSONValue x) {
		updateType(x);
		JSONValue y = values.size() > idx ? values.get(idx) : null;
		while(values.size() <= idx) values.add(null);
		values.set(idx, x);
		return y;
	}

	private void updateType(JSONValue x) {
		if (elementType == null && x != null && !x.isNull()) {
			elementType = x.type();
		} else if (elementType != null) {
			if (!x.isNull() && !elementType.equals(x.type())) elementType = JSONType.OBJECT;
		}
	}

	public JSONValue get(int idx) {
		return values.size() > idx ? values.get(idx) : null;
	}

	public JSONValue remove(int idx) {
		if (values.size() > idx) {
			return values.remove(idx);
		} else {
			return null;
		}
	}

	@Override
	void toString(StringBuilder text) {
		text.append("[");
		String sep = "";
		for(JSONValue v : values) {
			text.append(sep);
			if (v == null) v = new JSONNull();
			v.toString(text);
			sep = ",";
		}
		text.append("]");
	}

	@Override
	public Iterator<JSONValue> iterator() {
		return values.iterator();
	}

	@Override
	public int hashCode() {
		return values.hashCode();
	}

	@Override
	public boolean equals(Object x) {
		if (x != null && x instanceof JSONArray) {
			return values.equals(((JSONArray)x).values);
		}
		return false;
	}

	// -- convenience methods

	public JSONArray add(String... values) {
		if (values == null || values.length == 0) return this;
		for (String x : values) {
			add(x == null ? JSONNull.JSON_NULL : new JSONString(x));
		}
		return this;
	}

	public JSONArray add(Boolean... values) {
		if (values == null || values.length == 0) return this;
		for (Boolean x : values) {
			add(x == null ? JSONNull.JSON_NULL : new JSONBoolean(x));
		}
		return this;
	}

	public JSONArray add(BigDecimal... values) {
		if (values == null || values.length == 0) return this;
		for (BigDecimal x : values) {
			add(x == null ? JSONNull.JSON_NULL : new JSONNumber(x));
		}
		return this;
	}

	public JSONArray add(Double... values) {
		if (values == null || values.length == 0) return this;
		for (Double x : values) {
			add(x == null ? JSONNull.JSON_NULL : new JSONNumber(x));
		}
		return this;
	}

	public JSONArray add(Long... values) {
		if (values == null || values.length == 0) return this;
		for (Long x : values) {
			add(x == null ? JSONNull.JSON_NULL : new JSONNumber(x));
		}
		return this;
	}

}
