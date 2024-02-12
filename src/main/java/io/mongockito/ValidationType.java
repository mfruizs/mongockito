package io.mongockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;

public enum ValidationType {

	EQUALS {
		@Override
		public void validate(final Document document, final Pair<?, ?> pair) {

			final Object currentValue = pair.getKey();
			final Object expectedValue = pair.getValue();

			assertEquals(String.valueOf(expectedValue), String.valueOf(document.get(currentValue)));
		}
	},

	NOT_NULL {
		@Override
		public void validate(final Document document, final Pair<?, ?> pair) {

			final Object currentValue = pair.getKey();
			assertNotNull(document.get(currentValue));
		}
	},

	NULL {
		@Override
		public void validate(final Document document, final Pair<?, ?> pair) {

			final Object currentValue = pair.getKey();
			assertNull(document.get(currentValue));
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
	};


	public abstract void validate(Document document, Pair<?, ?> pair);

}
