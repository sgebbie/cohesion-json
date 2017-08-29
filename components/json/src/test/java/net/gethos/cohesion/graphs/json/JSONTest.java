/**
 * Cohesion Framework - JSON Library
 * Copyright (c) 2017 - Stewart Gebbie, Gethos. Licensed under the MIT licence.
 * vim: set ts=4 sw=0:
 */
package net.gethos.cohesion.graphs.json;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.gethos.cohesion.graphs.json.JSON;
import net.gethos.cohesion.graphs.json.JSONArray;
import net.gethos.cohesion.graphs.json.JSONBoolean;
import net.gethos.cohesion.graphs.json.JSONNull;
import net.gethos.cohesion.graphs.json.JSONNumber;
import net.gethos.cohesion.graphs.json.JSONObject;
import net.gethos.cohesion.graphs.json.JSONStream;
import net.gethos.cohesion.graphs.json.JSONString;
import net.gethos.cohesion.graphs.json.JSONUtils;
import net.gethos.cohesion.graphs.json.JSONValue;

import org.junit.Test;

/**
 * @author {@literal Stewart Gebbie <sgebbie@gethos.net>}
 *
 */
public class JSONTest {
	
	private static final String TEST_JSON_WHITESPACE = "{\n\t\"myArray\":[\"hello\",null,\"world\"],\n\t\"myBoolean\":true,\n\t\"myNull\":null,\n\t\"myNumber\":11.23,\n\t\"myText\":\"hello\\nworld\"\n}";
	private static final String TEST_JSON = "{\"myArray\":[\"hello\",null,\"world\"],\"myBoolean\":true,\"myNull\":null,\"myNumber\":11.23,\"myText\":\"hello\\nworld\"}";
	private static final String TEST_ARRAY = "[1,2,3,4,5,6]";
	
	@Test
	public void asString() {
		JSONArray myArray = new JSONArray();
		myArray.set(0, new JSONString("hello"));
		myArray.set(2, new JSONString("world"));
		
		JSONObject x = new JSONObject();
		x.put("myBoolean", new JSONBoolean(true));
		x.put("myText", new JSONString("hello\nworld"));
		x.put("myNull", new JSONNull());
		x.put("myNumber", new JSONNumber(11.23));
		x.put("myArray", myArray);
		
		String json = x.toString();
//		System.out.println(json);
		assertEquals(TEST_JSON,json);
	}
	
	@Test
	public void parseGraph() {
		JSONValue v = JSON.parse(TEST_JSON_WHITESPACE);
		assertNotNull(v);
		String again = v.toString();
//		System.out.println(TEST_JSON_WHITESPACE);
//		System.out.println(again);
		assertEquals(TEST_JSON, again);
		
		assertTrue(v.isObject());
		JSONObject vo = v.asObject();
		String[] members = new String[]{"myBoolean","myText","myNull","myNumber","myArray"};
		for(String m : members) assertTrue(vo.contains(m));
	}
	
	@Test
	public void parseEmptyString() {
		JSONValue v = JSON.parse("\"\"");
		assertNotNull(v);
		assertTrue(v.isString());
		assertEquals("",v.asString().value);
	}
	
	@Test
	public void parseEmptyArray() {
		JSONValue v = JSON.parse("[]");
		assertNotNull(v);
		assertTrue(v.isArray());
		assertEquals(0,v.asArray().size());
	}
	
	@Test
	public void parseEmptyObject() {
		JSONValue v = JSON.parse("{}");
		assertNotNull(v);
		assertTrue(v.isObject());
		assertTrue(v.asObject().members().isEmpty());
	}
	
	@Test
	public void parseEmptyObjectFromStream() {
		StringReader r = new StringReader("{}");
		JSONStream s = new JSONStream(r);
		JSONValue v = s.next();
		assertNotNull(v);
		assertTrue(v.isObject());
		assertTrue(v.asObject().members().isEmpty());
		v = s.next();
		assertNull(v);
	}
	
	@Test
	public void parseEmptyArrayFromStream() {
		StringReader r = new StringReader("[]");
		JSONStream s = new JSONStream(r);
		JSONValue v = s.next();
		assertNotNull(v);
		assertTrue(v.isArray());
		assertEquals(0,v.asArray().size());
		v = s.next();
		assertNull(v);
	}
	
	@Test
	public void parseArray() {
		JSONValue v = JSON.parse(TEST_ARRAY);
		assertNotNull(v);
		String again = v.toString();
		assertEquals(TEST_ARRAY, again);
		
		assertTrue(v.isArray());
		JSONArray vo = v.asArray();
		int y = 1;
		for (JSONValue x : vo) {
			assertNotNull(x);
			assertTrue(x.isNumber());
			JSONNumber n = x.asNumber();
			assertEquals(y++, n.value.intValue());
		}
	}
	
	@Test
	public void parseString() {
		JSONValue v = JSON.parse("\"hello world\"");
		assertNotNull(v);
		assertTrue(v.isString());
		JSONString n = v.asString();
		
//		System.out.printf("%s%n",n.value);
		assertEquals("hello world",n.value);
	}
	
	@Test
	public void parseNumber() {
		JSONValue v = JSON.parse("-11.345E+2");
		assertNotNull(v);
		assertTrue(v.isNumber());
		JSONNumber n = v.asNumber();
		
//		System.out.printf("%s%n",n.value);
		double nn = n.value.doubleValue();
		assertEquals(-1134.5,nn,0.0001);
	}
	
	@Test
	public void parseBoolean() {
		JSONValue v = JSON.parse("true");
		assertNotNull(v);
		assertTrue(v.isBoolean());
		JSONBoolean n = v.asBoolean();
		assertEquals(true,n.value);
	}
	
	@Test
	public void parseNull() {
		JSONValue v = JSON.parse("null");
		assertNotNull(v);
		assertTrue(v.isNull());
	}
	
	@Test
	public void parseGraphStream() {
		
		String corpus = "    " + TEST_JSON_WHITESPACE + "    " + TEST_JSON_WHITESPACE +  "    ";
		
		StringReader r = new StringReader(corpus);
		JSONStream s = new JSONStream(r);
		List<JSONValue> values = new ArrayList<JSONValue>();
		for(JSONValue v = s.next(); v != null; v = s.next()) {
			values.add(v); 
		}
		assertEquals(2,values.size());
		
		for (JSONValue v : values) {
			String again = v.toString();
	//		System.out.println(TEST_JSON_WHITESPACE);
	//		System.out.println(again);
			assertEquals(TEST_JSON, again);
			
			assertTrue(v.isObject());
			JSONObject vo = v.asObject();
			String[] members = new String[]{"myBoolean","myText","myNull","myNumber","myArray"};
			for(String m : members) assertTrue(vo.contains(m));
		}
	}
	
	@Test
	public void arrayCardinality() {
		JSONArray a = createTestArray();
		
		int[] cardinals = JSONUtils.getCardinality(a);
		int[] expect = new int[]{1,2,4};
//		System.out.println(Arrays.toString(cardinals));
		
		assertTrue(Arrays.equals(expect, cardinals));
	}
	
	@Test
	public void arrayDimensions() {
		JSONArray a = createTestArray();
		int dimensions = JSONUtils.getDimensions(a);
		assertEquals(3,dimensions);
	}

	private JSONArray createTestArray() {
		JSONArray a = new JSONArray();
		JSONArray b = new JSONArray();
		JSONArray c1 = new JSONArray();
		JSONArray c2 = new JSONArray();
		
		c1.add(new JSONNumber(1));
		c1.add(new JSONNumber(2));
		c1.add(new JSONNumber(3));
		c1.add(new JSONNumber(4));
		
		c2.add(new JSONNumber(1));
		c2.add(new JSONNumber(2));
		c2.add(new JSONNumber(3));
		c2.add(new JSONNumber(4));
		
		b.add(c1);
		b.add(c2);
		
		a.add(b);
		return a;
	}
}
