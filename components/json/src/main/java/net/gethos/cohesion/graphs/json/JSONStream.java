/**
 * Cohesion Framework - JSON Library
 * Copyright (c) 2017 - Stewart Gebbie, Gethos. Licensed under the MIT licence.
 * vim: set ts=4 sw=0:
 */
package net.gethos.cohesion.graphs.json;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.util.Iterator;

/**
 * Gethos Cohesion JSON Stream implementation, parsing sequential JSON values
 * off a reader.
 * <p>
 * See: http://www.json.org/
 * 
 * @author {@literal Stewart Gebbie <sgebbie@gethos.net>}
 * @see JSON
 *
 */
public class JSONStream implements Iterable<JSONValue> {

	private static class Holder<T> {
		public T value;
	}

	private final PushbackReader json;
	private final char[] keywordCharacters;
	private final CharBuffer keyword;

	private int jsonPos;

	public JSONStream(Reader reader) {
		this.keywordCharacters = new char[5];
		this.keyword = CharBuffer.wrap(keywordCharacters);
		this.json = new PushbackReader(reader, keywordCharacters.length);
		this.jsonPos = 0;
	}

	public JSONValue next() {
		return parse();
	}

	private JSONValue parse() {
		Holder<JSONValue> h = new Holder<JSONValue>();
		try {
			jsonPos = parseValue(h, jsonPos);
		} catch (IOException e) {
			throw new JSONException(String.format("I/O Error while parsing JSON from stream somewhere close to [%d]",jsonPos), e);
		}
		return h.value;
	}

	private int skipWhitespace(int from) throws IOException {
		int pos = from;
		for(;;) {
			int c = json.read();
			pos++;
			if (!Character.isWhitespace(c)) {
				if (c != -1) {
					json.unread(c);
					pos--;
				}
				break;
			}
		}
		return pos;
	}

	private int parseValue(Holder<JSONValue> holder, int from) throws IOException {

		int pos = skipWhitespace(from);

		keyword.clear();
		int r = json.read(keyword);
		if (r == -1) { holder.value = null; return pos; } // end-of-stream
		pos += r;
		if (r == 4 || r == 5) {
			String k = new String(keywordCharacters);
			if (k.startsWith(JSONNull.NULL))     { holder.value = JSONNull.JSON_NULL;     json.unread(keywordCharacters, 4, r-4); return from + 4; }
			if (k.startsWith(JSONBoolean.FALSE)) { holder.value = JSONBoolean.JSON_FALSE; json.unread(keywordCharacters, 5, r-5); return from + 5; }
			if (k.startsWith(JSONBoolean.TRUE))  { holder.value = JSONBoolean.JSON_TRUE;  json.unread(keywordCharacters, 4, r-4); return from + 4; }
		}
		// not a keyword
		pos -= r;
		json.unread(keywordCharacters, 0, r);


		int c = json.read(); pos++;
		json.unread(c); pos--;
		if (c == -1) { holder.value = null; return pos; }  // end-of-stream
		if (c == '"')                           { return parseString(holder, pos); }
		if (c == '-' || (c >= '0' && c <= '9')) { return parseNumber(holder, pos); }
		if (c == '{')                           { return parseObject(holder, pos); }
		if (c == '[')                           { return parseArray (holder, pos); }

		throw new JSONException(String.format("Unexpected character {%s} while parsing value [%d]",(char)c,pos));
	}

	private int parseObject(Holder<JSONValue> holder, int from) throws IOException {
		int pos = from;
		int c;
		c = json.read(); pos++;
		if (c != '{') {
			pos--;
			json.unread(c);
			throw new JSONException(String.format("No object while parsing [%d]",from));
		}

		JSONObject x = new JSONObject();

		for(;;) {
			pos = skipWhitespace(pos);
			c = json.read(); pos++;
			if (c == '}') {
				break;
			} else {
				pos--;
				json.unread(c);
			}
			pos = parseString(holder, pos);
			if (holder.value == null || !holder.value.isString() || holder.value.asString().value == null) throw new JSONException(String.format("Unable to read member name while parsing at %d in [%d]",pos,from));
			String member = holder.value.asString().value;
			pos = skipWhitespace(pos);
			c = json.read(); pos++;
			if (c != ':') {
				pos--;
				json.unread(c);
				throw new JSONException(String.format("Unexpected character {%s} (expected ':') while parsing array at %d in [%d]",c,pos-1,from));
			}
			pos = skipWhitespace(pos);
			pos = parseValue(holder, pos);
			x.put(member,holder.value);
			pos = skipWhitespace(pos);
			c = json.read(); pos++;
			if (c == ',') continue;
			else if (c == '}') break;
			else {
				pos--;
				json.unread(c);
				throw new JSONException(String.format("Unexpected character {%s} (expected ',' or '}') while parsing array at %d in [%d]",c,pos,from));
			}
		}

		holder.value = x;

		return pos;
	}

	private int parseArray(Holder<JSONValue> holder, int from) throws IOException {

		int pos = from;
		int c;
		c = json.read(); pos++;
		if (c != '[') {
			pos--;
			json.unread(c);
			throw new JSONException(String.format("No array while parsing [%d]",from));
		}

		JSONArray x = new JSONArray();

		for(;;) {
			pos = skipWhitespace(pos);
			c = json.read(); pos++;
			if (c == ']') {
				break;
			} else {
				pos--;
				json.unread(c);
			}
			pos = parseValue(holder, pos);
			x.add(holder.value);
			pos = skipWhitespace(pos);
			c = json.read(); pos++;
			if (c == ',') continue;
			else if (c == ']') break;
			else throw new JSONException(String.format("Unexpected character {%s} (expected ',' or ']') while parsing array at %d in [%d].",c,pos,from));
		}

		holder.value = x;

		return pos;
	}

	private int parseNumber(Holder<JSONValue> holder, int from) throws IOException {

		int pos = from;

		int c;
		StringBuilder x = new StringBuilder();
		number:
		{
			// find the end of the number
			c = json.read(); pos++;
			if (c == -1) break number;
			if (c == '-') { x.append((char)c); c = json.read(); pos++; } // is negative
			if (c == -1) break number;
			if (c == '0') { x.append((char)c); c = json.read(); pos++; } // is zero
			if (c == -1) break number;
			if (c >= '1' && c <= '9') do { x.append((char)c); c = json.read(); pos++; } while (c != -1 && c >= '0' && c <= '9'); // significand
			if (c == -1) break number;
			if (c == '.') {
				do { x.append((char)c); c = json.read(); pos++; } while (c != -1 && c >= '0' && c <= '9'); // fractional significand
			}
			if (c == -1) break number;
			if (c == 'e' || c == 'E') { // exponent
				x.append((char)c);
				c = json.read(); pos++;
				// check for sign
				if (c == '+') { x.append((char)c); c = json.read(); pos++; }
				else if (c == '-') { x.append((char)c); c = json.read(); pos++; }
				while (c != -1 && c >= '0' && c <= '9') { x.append((char)c); c = json.read(); pos++; } // exponent value
			}
			if (c != -1) { json.unread((char)c); pos--; }
		}

		// parse out the number
		BigDecimal n = new BigDecimal(x.toString());

		holder.value = new JSONNumber(n);

		return pos;
	}

	private int parseString(Holder<JSONValue> holder, int from) throws IOException {

		int pos = from;
		int c = json.read(); pos++;
		if (c == -1) throw new JSONException(String.format("End-of-stream while pasring string [%d].", from));
		if (c != '"') {
			pos--;
			json.unread(c);
			throw new JSONException(String.format("No string while parsing [%d]",from));
		}

		StringBuilder escaped = new StringBuilder();
		for(;;) {
			c = json.read(); pos++;
			if (c == -1) throw new JSONException(String.format("End-of-stream while pasring string [%d].", from));
			if (c == '"') break;
			escaped.append((char)c);
			if (c == '\\') { c = json.read(); pos++; escaped.append((char)c); }
		}

		JSONString.unescape(escaped);
		holder.value = new JSONString(escaped.toString());

		return pos;
	}

	@Override
	public Iterator<JSONValue> iterator() {
		return new Iterator<JSONValue>() {

			private JSONValue next;

			{
				next = parse();
			}

			@Override
			public boolean hasNext() {
				return next == null ? false : true;
			}

			@Override
			public JSONValue next() {
				JSONValue n = next;
				next = parse();
				return n;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
