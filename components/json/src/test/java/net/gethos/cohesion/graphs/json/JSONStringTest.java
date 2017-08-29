/**
 * Cohesion Framework - JSON Library
 * Copyright (c) 2017 - Stewart Gebbie, Gethos. Licensed under the MIT licence.
 * vim: set ts=4 sw=0:
 */
package net.gethos.cohesion.graphs.json;

import static org.junit.Assert.*;

import net.gethos.cohesion.graphs.json.JSONString;

import org.junit.Test;

/**
 * @author {@literal Stewart Gebbie <sgebbie@gethos.net>}
 *
 */
public class JSONStringTest {
	
	private static String CORPUS_ESCAPED_ALT = "hello\\nworld\\tmo\\u006fn\\tmars.";
	private static String CORPUS_ESCAPED = "hello\\nworld\\tmoon\\tmars.";
	private static String CORPUS_UNESCAPED = "hello\nworld\tmoon\tmars.";

	@Test
	public void unescape() {
		String unescaped = JSONString.unescape(CORPUS_ESCAPED_ALT);
		assertEquals(CORPUS_UNESCAPED, unescaped);
	}
	
	@Test
	public void unescapeRange() {
		String unescaped = JSONString.unescape("junk" + CORPUS_ESCAPED_ALT + "stuff", 4, 4 + CORPUS_ESCAPED_ALT.length());
		assertEquals(CORPUS_UNESCAPED, unescaped);
	}
	
	@Test
	public void escape() {
		String escaped = JSONString.escape(CORPUS_UNESCAPED);
		assertEquals(CORPUS_ESCAPED, escaped);
	}
	
	@Test
	public void noChange() {
		String stuff = "stuff";
		String escaped = JSONString.escape(stuff);
		assertSame(stuff,escaped);
		String unescaped = JSONString.unescape(stuff);
		assertSame(stuff,unescaped);
	}
}
