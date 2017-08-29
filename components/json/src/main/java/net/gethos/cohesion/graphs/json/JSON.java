/**
 * Cohesion Framework - JSON Library
 * Copyright (c) 2017 - Stewart Gebbie, Gethos. Licensed under the MIT licence.
 * vim: set ts=4 sw=0:
 */
package net.gethos.cohesion.graphs.json;

import java.math.BigDecimal;

/**
 * Gethos Cohesion JSON parser implementation.
 * <p>
 * See: http://www.json.org/
 *
 * @author {@literal Stewart Gebbie <sgebbie@gethos.net>}
 * @see JSONStream
 *
 */
public class JSON {

	private static class Holder<T> {
		public T value;
	}

	/**
	 * Parse a text JSON representation into a JSON value.
	 *
	 * @param json
	 * @return JSON value representing the parsed data
	 */
	public static JSONValue parse(String json) {
		if (json == null) return null;
		Holder<JSONValue> h = new Holder<JSONValue>();
		parseValue(h, json, 0, json.length());
		return h.value;
	}

	private static int skipWhitespace(String json, int from, int to) {
		int pos = from;
		while(pos < to && Character.isWhitespace(json.charAt(pos))) pos++;
		return pos;
	}

	private static int parseValue(Holder<JSONValue> holder, String json, int from, int to) {

		from = skipWhitespace(json,from,to);

		if (json.startsWith(JSONNull.NULL,     from)) { holder.value = JSONNull.JSON_NULL;     return from + 4; }
		if (json.startsWith(JSONBoolean.FALSE, from)) { holder.value = JSONBoolean.JSON_FALSE; return from + 5; }
		if (json.startsWith(JSONBoolean.TRUE,  from)) { holder.value = JSONBoolean.JSON_TRUE;  return from + 4; }
		char c = json.charAt(from);
		if (c == '"')                           { return parseString(holder, json, from, to); }
		if (c == '-' || (c >= '0' && c <= '9')) { return parseNumber(holder, json, from, to); }
		if (c == '{')                           { return parseObject(holder, json, from, to); }
		if (c == '[')                           { return parseArray (holder, json, from, to); }

		throw new JSONException(String.format("Unexpected character {%s} while parsing value [%d,%d) in '...%s...' in %s",c,from,to,errorSnippet(json, from, to), json));
	}

	private static String errorSnippet(String json, int from, int to) {
		return json.substring(Math.max(0, from-10),Math.min(json.length()-1,from+10));
	}

	private static int parseString(Holder<JSONValue> holder, String json, int from, int to) {

		if (json.charAt(from) != '"') throw new JSONException(String.format("No string while parsing [%d,%d) in {%s}",from,to,json));

		int end;
		for (end = from+1; (end < to) && (json.charAt(end) != '"'); end++) if (json.charAt(end) == '\\') end++;
		String unescaped = JSONString.unescape(json,from+1,end);
		holder.value = new JSONString(unescaped);

		return end+1;
	}

	private static int parseNumber(Holder<JSONValue> holder, String json, int from, int to) {

		int pos = from;

		// find the end of the number
		if (pos < to && json.charAt(pos)=='-') {pos++;}	// is negative
		if (pos < to && json.charAt(pos)=='0') {pos++;}	// is zero

		if (pos < to && (json.charAt(pos)>='1' && json.charAt(pos)<='9'))	do {pos++;} while (pos < to && json.charAt(pos)>='0' && json.charAt(pos)<='9');	// significand
		if (pos < to && json.charAt(pos)=='.') {pos++; do {pos++;} while (pos < to && json.charAt(pos)>='0' && json.charAt(pos)<='9');}	// fractional significand

		if (pos < to && (json.charAt(pos)=='e' || json.charAt(pos)=='E')) // exponent
		{
			pos++;
			// check for sign
			if (json.charAt(pos)=='+') pos++;
			else if (json.charAt(pos)=='-') {pos++;}
			while (pos < to && json.charAt(pos)>='0' && json.charAt(pos)<='9') { pos++;} // exponent value
		}

		// parse out the number
		BigDecimal n = new BigDecimal(json.substring(from, pos));

		holder.value = new JSONNumber(n);

		return pos;
	}

	private static int parseArray(Holder<JSONValue> holder, String json, int from, int to) {

		if (json.charAt(from) != '[') throw new JSONException(String.format("No array while parsing [%d,%d) in {%s}",from,to,json));

		JSONArray x = new JSONArray();

		int pos = from+1;
		char c;

		while(pos < to) {
			pos = skipWhitespace(json, pos, to);
			c = json.charAt(pos);
			if (c == ']') { pos++; break; }
			pos = parseValue(holder, json, pos, to);
			x.add(holder.value);
			pos = skipWhitespace(json, pos, to);
			c = json.charAt(pos++);
			if (c == ',') continue;
			else if (c == ']') break;
			else throw new JSONException(String.format("Unexpected character {%s} (expected ',' or ']') while parsing array at %d in [%d,%d) of '...%s...' in %s",c,pos,from,to,errorSnippet(json, from, to), json));
		}

		holder.value = x;

		return pos;
	}

	private static int parseObject(Holder<JSONValue> holder, String json, int from, int to) {
		if (json.charAt(from) != '{') throw new JSONException(String.format("No object while parsing [%d,%d) in {%s}",from,to,json));

		JSONObject x = new JSONObject();

		char c;
		int pos = from+1;

		while(pos < to) {
			pos = skipWhitespace(json, pos, to);
			c = json.charAt(pos);
			if (c == '}') { pos++; break; }
			pos = parseString(holder, json, pos, to);
			if (holder.value == null || !holder.value.isString() || holder.value.asString().value == null) throw new JSONException(String.format("Unable to read member name while parsing at %d in [%d,%d) of '...%s...' in %s",pos,from,to,errorSnippet(json, from, to),json));
			String member = holder.value.asString().value;
			pos = skipWhitespace(json, pos, to);
			c = json.charAt(pos++);
			if (c != ':') throw new JSONException(String.format("Unexpected character {%s} (expected ':') while parsing object at %d in [%d,%d) of '...%s...' in %s",c,pos-1,from,to,errorSnippet(json, from, to),json));
			pos = skipWhitespace(json, pos, to);
			pos = parseValue(holder, json, pos, to);
			x.put(member,holder.value);
			pos = skipWhitespace(json, pos, to);
			c = json.charAt(pos++);
			if (c == ',') continue;
			else if (c == '}') break;
			else throw new JSONException(String.format("Unexpected character {%s} (expected ',' or '}') while parsing object at %d in [%d,%d) of '...%s...' in %s",c,pos-1,from,to,errorSnippet(json, from, to),json));
		}

		holder.value = x;

		return pos;
	}

	/*
	private static int _parseNumber(Holder<JSONValue> holder, String json, int from, int to) {

		double n=0,nSign=1,scale=0;
		int subscale=0,subscaleSign=1;

		int pos = from;

		if (json.charAt(pos)=='-') {nSign=-1;pos++;}	// is negative
		if (json.charAt(pos)=='0') {pos++;}			// is zero

		if (json.charAt(pos)>='1' && json.charAt(pos)<='9')	do {n=(n*10.0)+(json.charAt(pos)-'0'); pos++; } while (json.charAt(pos)>='0' && json.charAt(pos)<='9');	// significand
		if (json.charAt(pos)=='.') {pos++; do {n=(n*10.0)+(json.charAt(pos) -'0');pos++;scale--;} while (json.charAt(pos)>='0' && json.charAt(pos)<='9');}	// fractional significand

		if (json.charAt(pos)=='e' || json.charAt(pos)=='E') // exponent
		{
			pos++;

			// check for sign
			if (json.charAt(pos)=='+') pos++;
			else if (json.charAt(pos)=='-') {subscaleSign=-1;pos++;}

			while (json.charAt(pos)>='0' && json.charAt(pos)<='9') { subscale=(subscale*10)+(json.charAt(pos) - '0');pos++;}	// exponent number
		}

		n=nSign*n*Math.pow(10.0,(scale+subscale*subscaleSign));	// n = +/- significand * 10^+/- (exponent including scale)

		holder.value = new JSONNumber(n);

		return pos;
	}
	 */
}
