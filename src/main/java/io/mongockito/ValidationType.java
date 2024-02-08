package io.mongockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;

public enum ValidationType {

	EQUALS {
		@Override
		public void validate(final Document document, final Pair<?, ?> pair) {

			final Object actual = pair.getKey();
			final Object expected = pair.getValue();

			assertEquals(String.valueOf(expected), String.valueOf(document.get(actual)));
		}
	},

	NOT_NULL {
		@Override
		public void validate(final Document document, final Pair<?, ?> pair) {

			final Object key = pair.getKey();
			assertNotNull(document.get(key));
		}
	},

	MAP_SIZE {
		@Override
		public void validate(final Document document, final Pair<?, ?> pair) {

			final Object key = pair.getKey();
			final Map<?, ?> collection = (Map<?, ?>) document.get(key);
			final Integer expected = (Integer) pair.getValue();

			assertEquals(expected, collection.size());
		}
	};


	public abstract void validate(Document document, Pair<?, ?> pair);

}
