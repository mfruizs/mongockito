package io.mongockito.util.document;

import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import io.mongockito.common.business.EntityExampleObjectMother;
import io.mongockito.common.model.EntityExample;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentUtilityTest {

	private static final String FIELD = "field";
	private static final Map<String, EntityExample> map = EntityExampleObjectMother.createFieldMap();
	private static final List<EntityExample> list = EntityExampleObjectMother.createFieldList();

	@Test
	void should_recover_size_from_map(@Mock final Document document) {

		given(document.get(FIELD)).willReturn(map);

		final int resultSize = DocumentUtility.obtainCollectionLength(document, FIELD);

		assertEquals(map.size(), resultSize);
	}

	@Test
	void should_recover_size_from_list(@Mock final Document document) {

		given(document.get(FIELD)).willReturn(list);

		final int resultSize = DocumentUtility.obtainCollectionLength(document, FIELD);

		assertEquals(list.size(), resultSize);
	}

	@Test
	void should_return_size_zero_from_list(@Mock final Document document) {

		given(document.get(FIELD)).willReturn(null);

		final int resultSize = DocumentUtility.obtainCollectionLength(document, FIELD);

		assertEquals(INTEGER_ZERO, resultSize);
	}

	@Test
	void should_return_size_zero_from_other_type(@Mock final Document document) {

		given(document.get(FIELD)).willReturn("null");

		final int resultSize = DocumentUtility.obtainCollectionLength(document, FIELD);

		assertEquals(INTEGER_ZERO, resultSize);
	}

}
