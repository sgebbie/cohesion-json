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
public class JSONString extends JSONValue {

	private static final char UNICODEHEX = 'u'; // 0x75 i.e. \u0075

	private static final char BACKSLASH = 0x5C; // '\\'
	private static final char FORWARDSLASH = 0x2F; // '/'
	private static final char FORMFEED = 0x0C; // '\f'
	private static final char NEWLINE = 0x0A; // '\n'
	private static final char CARRIAGERETURN = 0x0D; // '\r'
	private static final char BACKSPACE = 0x08; // '\b'
	private static final char DOUBLEQUOTE = 0x22; // '"'
	private static final char TAB = 0x09; // '\t'

	private static final char ESC_BACKSLASH = '\\';
	private static final char ESC_FORWARDSLASH = '/';
	private static final char ESC_FORMFEED = 'f';
	private static final char ESC_NEWLINE = 'n';
	private static final char ESC_CARRIAGERETURN = 'r';
	private static final char ESC_BACKSPACE = 'b';
	private static final char ESC_DOUBLEQUOTE = '"';
	private static final char ESC_TAB = 't';


	public final String value;
	private String escaped;

	public JSONString(String value) {
		this.value = value;
		this.escaped = null;
	}

	@Override
	public JSONType type() {
		return JSONType.STRING;
	}

	@Override
	public JSONString asString() {
		return this;
	}

	@Override
	void toString(StringBuilder text) {
		escape();
		text.append("\"");
		text.append(escaped);
		text.append("\"");
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object x) {
		if (x != null && x instanceof JSONString) {
			return value.equals(((JSONString)x).value);
		}
		return false;
	}

	private void escape() {
		if (escaped == null) escaped = escape(value);
	}

	public static String escape(String unescaped) {
		if (unescaped == null) return null;
		StringBuilder x = new StringBuilder(unescaped);
		loop:
			for(int i = 0; i < x.length(); i++) {
				char c = x.charAt(i);
				char r;
				switch(c) {
					case BACKSLASH:
						r = ESC_BACKSLASH;
						break;
					case DOUBLEQUOTE:
						r = ESC_DOUBLEQUOTE;
						break;
					case NEWLINE:
						r = ESC_NEWLINE;
						break;
					case CARRIAGERETURN:
						r = ESC_CARRIAGERETURN;
						break;
					case BACKSPACE:
						r = ESC_BACKSPACE;
						break;
					case FORMFEED:
						r = ESC_FORMFEED;
						break;
					case TAB:
						r = ESC_TAB;
						break;
					default:
						continue loop;
				}

				// insert a '\' and replace the character with its escaped version
				x.insert(i, BACKSLASH);
				i++;
				x.setCharAt(i, r);
			}

		if (x.length() == unescaped.length()) return unescaped;
		else return x.toString();
	}

	public static String unescape(String escaped) {
		return unescape(escaped,0,escaped.length());
	}

	public static String unescape(String escaped, int from, int to) {
		if (escaped == null) return null;
		StringBuilder x = new StringBuilder();
		char[] chars = new char[to-from];
		escaped.getChars(from, to, chars, 0);
		x.append(chars);
		unescape(x);
		if (x.length() == escaped.length()) return escaped;
		else return x.toString();
	}

	static void unescape(StringBuilder x) {
		for(int i = 0; i < x.length(); i++) {
			if (x.charAt(i) == BACKSLASH) {
				x.deleteCharAt(i); // delete the '\'
				char c = x.charAt(i);
				char r;
				switch(c) {
					case ESC_BACKSLASH:
						r = BACKSLASH;
						break;
					case ESC_FORWARDSLASH:
						r = FORWARDSLASH;
						break;
					case ESC_DOUBLEQUOTE:
						r = DOUBLEQUOTE;
						break;
					case ESC_NEWLINE:
						r = NEWLINE;
						break;
					case ESC_CARRIAGERETURN:
						r = CARRIAGERETURN;
						break;
					case ESC_BACKSPACE:
						r = BACKSPACE;
						break;
					case ESC_FORMFEED:
						r = FORMFEED;
						break;
					case ESC_TAB:
						r = TAB;
						break;
					case UNICODEHEX:
					{
						String hex = x.substring(i+1, i+5);
						x.delete(i, i+4);
						try {
							int l = Integer.parseInt(hex, 16);
							r = (char)l;
						} catch (NumberFormatException e) {
							throw new JSONException(String.format("Unexpected parsable hex code escape character {%s} at index [%d]",hex,i),e);
						}
						break;
					}
					default:
						throw new JSONException(String.format("Unexpected escape character {%s} at index [%d]",c,i));
				}
				x.setCharAt(i, r);
			}
		}
	}
}
