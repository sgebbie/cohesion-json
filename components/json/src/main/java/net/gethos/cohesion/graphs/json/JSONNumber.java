/**
 * Cohesion Framework - JSON Library
 * Copyright (c) 2017 - Stewart Gebbie, Gethos. Licensed under the MIT licence.
 * vim: set ts=4 sw=0:
 */
package net.gethos.cohesion.graphs.json;

import java.math.BigDecimal;

/**
 * Gethos Cohesion JSON implementation.
 * <p>
 * See: http://www.json.org/
 * 
 * @author {@literal Stewart Gebbie <sgebbie@gethos.net>}
 *
 */
public class JSONNumber extends JSONValue {

	public final BigDecimal value;

	public JSONNumber(long value) {
		this.value = BigDecimal.valueOf(value);
	}

	public JSONNumber(double value) {
		this.value = BigDecimal.valueOf(value);
	}

	public JSONNumber(BigDecimal value) {
		this.value = value;
	}

	@Override
	public JSONType type() {
		return JSONType.NUMBER;
	}

	@Override
	public JSONNumber asNumber() {
		return this;
	}

	@Override
	void toString(StringBuilder text) {
		text.append(value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object x) {
		if (x != null && x instanceof JSONNumber) {
			// note, use compareTo so that the 'scale' of the BigDecimal does not affect the equality check
			return value.compareTo(((JSONNumber)x).value) == 0;
		}
		return false;
	}
}
