package io.mongockito.util.document;

import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import org.bson.Document;

@UtilityClass
public class DocumentUtility {

	public static int obtainCollectionLength(final Document completeDocument, final Object fieldName) {

		final Object document = completeDocument.get(fieldName);

		if (Objects.isNull(document)) {
			return INTEGER_ZERO;
		}

		if (document instanceof final Collection<?> collection) {
			return collection.size();
		} else if (document instanceof final Map<?, ?> map) {
			return map.size();
		}

		return INTEGER_ZERO;
	}

}
