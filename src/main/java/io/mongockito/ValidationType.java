package io.mongockito;

import static io.mongockito.util.json.JsonTool.gsonBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import io.mongockito.util.document.DocumentUtility;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;

public enum ValidationType {

	EQUALS {
		@Override
		public void validate(final Document document, final Pair<?, ?> pair) {

			final Object fieldName = pair.getKey();
			final Object expectedValue = pair.getValue();

			assertEquals(String.valueOf(expectedValue), String.valueOf(document.get(fieldName)));
		}
	},

	NOT_NULL {
		@Override
		public void validate(final Document document, final Pair<?, ?> pair) {

			final Object fieldName = pair.getKey();
			assertNotNull(document.get(fieldName));
		}
	},

	NULL {
		@Override
		public void validate(final Document document, final Pair<?, ?> pair) {

			final Object fieldName = pair.getKey();
			assertNull(document.get(fieldName));
		}
	},

	COLLECTION_SIZE {
		@Override
		public void validate(final Document document, final Pair<?, ?> pair) {

			final Object fieldName = pair.getKey();
			final Integer expectedSize = (Integer) pair.getValue();

			final int currentSize = DocumentUtility.obtainCollectionLength(document, fieldName);
			assertEquals(expectedSize, currentSize);
		}
	},

	JSON {
		@Override
		public void validate(final Document document, final Pair<?, ?> pair) {

			if (pair.getKey() == null) {
				fail("Mandatory object on first pair parameter");
				return;
			}

			final Class<?> clazz = pair.getKey().getClass();
			final Object expectedObject = pair.getKey();
			final Object currentDocument = gsonBuilder().fromJson(document.toJson(), clazz);

			assertEquals(currentDocument, expectedObject);
		}

	},

	JSON_BY_KEY {
		@Override
		public void validate(final Document document, final Pair<?, ?> pair) {

			final Object fieldName = pair.getKey();
			final Object expectedValue = pair.getValue();

			if (fieldName == null || expectedValue == null) {
				fail("Mandatory fieldName or ClassType on pair");
				return;
			}

			final Document currentDocument = (Document) document.get(fieldName);
			final String currentValue = this.obtainFormattedCurrentValue(currentDocument);
			final String expectedItem = this.obtainFormattedExpectedValue(expectedValue);
			assertEquals(expectedItem, currentValue);
		}

		private String obtainFormattedCurrentValue(final Document currentDocument) {

			return currentDocument.toJson().replaceAll("\\s+", "");
		}

		private String obtainFormattedExpectedValue(final Object expectedValue) {

			return gsonBuilder().toJson(expectedValue)
				.replaceAll("^\"|\"$", "")
				.replace("\\\"", "\"");
		}

	};

	public abstract void validate(Document document, Pair<?, ?> pair);

}
