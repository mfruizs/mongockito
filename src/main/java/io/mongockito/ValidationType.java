package io.mongockito;

import static io.mongockito.util.json.JsonTool.GSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;
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

	MAP_SIZE {
		@Override
		public void validate(final Document document, final Pair<?, ?> pair) {

			final Object fieldName = pair.getKey();
			final Map<?, ?> fieldMap = (Map<?, ?>) document.get(fieldName);
			final Integer expectedSize = (Integer) pair.getValue();

			assertEquals(expectedSize, fieldMap.size());
		}
	},

	JSON {
		@Override
		public void validate(final Document document, final Pair<?, ?> pair) {

			if (pair.getKey() instanceof Class) {
				final Class<?> clazz = (Class<?>) pair.getKey();
				final Object expectedObject = pair.getValue();
				final Object currentDocument = GSON.fromJson(document.toJson(), clazz);

				assertEquals(currentDocument, expectedObject);
			} else {
				fail();
			}
		}
	};

	public abstract void validate(Document document, Pair<?, ?> pair);

}
