/**
 * Cohesion Framework - JSON Library
 * Copyright (c) 2017 - Stewart Gebbie, Gethos. Licensed under the MIT licence.
 * vim: set ts=4 sw=0:
 */
package net.gethos.cohesion.graphs.json;

import java.util.ArrayList;
import java.util.List;

/**
 * @author {@literal Stewart Gebbie <sgebbie@gethos.net>}
 *
 */
public class JSONUtils {

	public static class JSONArraySpecification {
		JSONType elementType;
		int[] cardinals;
		int dimensions;
	}

	static public JSONArraySpecification getArraySpecification(JSONValue v) {
		if (v == null || !v.isArray()) return null;
		JSONArraySpecification spec = new JSONArraySpecification();
		List<Integer> collected = new ArrayList<Integer>();
		JSONType t = JSONType.OBJECT;
		JSONArray x = v.asArray();
		while(true) {
			if (x == null) break;
			int s = x.size();
			collected.add(s);
			if (s > 0) {
				v = x.get(0);
				if (v != null) {
					t = v.type();
					x = v.asArray();
				}
			} else {
				x = null;
			}
		}

		int[] cardinals = new int[collected.size()];
		for (int i = 0; i < collected.size(); i++) cardinals[i] = collected.get(i);

		spec.cardinals = cardinals;
		spec.dimensions = cardinals.length;
		spec.elementType = t;

		return spec;
	}

	static public int[] getCardinality(JSONValue v) {
		JSONArraySpecification spec = getArraySpecification(v);
		return spec == null ? null : spec.cardinals;
	}

	static public int getDimensions(JSONValue v) {
		JSONArraySpecification spec = getArraySpecification(v);
		return spec == null ? -1 : spec.dimensions;
	}

	static public JSONType getElementType(JSONValue v) {
		JSONArraySpecification spec = getArraySpecification(v);
		return spec == null ? null : spec.elementType;
	}
}
