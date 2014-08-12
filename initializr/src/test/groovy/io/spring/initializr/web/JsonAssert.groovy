package io.spring.initializr.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType

import static org.junit.Assert.*

/**
 *
 * @author Stephane Nicoll
 */
class JsonAssert {

	private final JsonNode content;

	JsonAssert(String jsonContent) {
		try {
			this.content = new ObjectMapper().readTree(jsonContent);
		}
		catch (IOException e) {
			throw new IllegalStateException("Failed to parse '" + jsonContent + "'", e);
		}
	}

	JsonAssert(JsonNode node) {
		this.content = node;
	}

	void assertSize(int size) {
		assertEquals size, content.size()
	}

	void assertField(String name, String value) {
		JsonNode field = this.content.get(name);
		assertEquals(JsonNodeType.STRING, field.getNodeType());
		assertEquals(value, field.textValue());
	}

	void hasNoField(String... fieldNames) {
		for (String fieldName : fieldNames) {
			assertNull("Field '" + fieldName + "' is not expected on " + this.content, this.content.get(fieldName));
		}
	}

	public void assertArraySize(int size) {
		assertEquals("Not an array '" + this.content + "'", JsonNodeType.ARRAY, this.content.getNodeType());
		assertEquals("wrong number of elements for '" + this.content + "'", size, this.content.size());
	}

	void assertRootSize(String name, int count) {
		assertEquals("Wrong number of '" + name + "'", count, getMandatory(name).size());
	}

	JsonAssert getChild(String name) {
		return new JsonAssert(getMandatory(name));
	}

	JsonAssert getElement(int index) {
		return new JsonAssert(this.content.get(index));
	}

	private JsonNode getMandatory(String name) {
		JsonNode node = this.content.get(name);
		assertNotNull("Field with name '" + name + "' does not exist", node);
		return node;
	}
}
