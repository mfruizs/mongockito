package io.mongockito;

import static io.mongockito.common.EntityExampleObjectMother.ID_FIELD;
import static io.mongockito.common.TestConstants.DEFAULT_KEY_ID;
import static io.mongockito.common.TestConstants.ENTITY_EXAMPLE_MAP;
import static io.mongockito.util.json.JsonTool.GSON;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import io.mongockito.common.EntityExample;
import io.mongockito.common.EntityExampleObjectMother;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidationTypeTest {

	@Test
	void should_validate_equals_operation_correctly() {

		EntityExample entityExample = EntityExampleObjectMother.createSimpleEntityExample();

		Document doc = Document.parse(GSON.toJson(entityExample));

		ValidationType.EQUALS.validate(doc, Pair.of(DEFAULT_KEY_ID, ID_FIELD));
	}

	@Test
	void should_throw_error_on_validating_equals_operation() {

		EntityExample entityExample = EntityExampleObjectMother.createSimpleEntityExample();

		Document doc = Document.parse(GSON.toJson(entityExample));

		assertThatThrownBy(() -> ValidationType.EQUALS.validate(doc, Pair.of(DEFAULT_KEY_ID, "12345")))
			.isInstanceOf(AssertionError.class);
	}

	@Test
	void should_validate_not_null_operation_correctly() {

		EntityExample entityExample = EntityExampleObjectMother.createSimpleEntityExample();

		Document doc = Document.parse(GSON.toJson(entityExample));

		ValidationType.NOT_NULL.validate(doc, Pair.of(DEFAULT_KEY_ID, ID_FIELD));
	}

	@Test
	void should_throw_error_on_validating_not_null_operation() {

		EntityExample entityExample = EntityExample.builder().build();

		Document doc = Document.parse(GSON.toJson(entityExample));

		assertThatThrownBy(() -> ValidationType.NOT_NULL.validate(doc, Pair.of(DEFAULT_KEY_ID, ID_FIELD)))
			.isInstanceOf(AssertionError.class);
	}

	@Test
	void should_validate_is_null_operation_correctly() {

		EntityExample entityExample = EntityExample.builder().build();

		Document doc = Document.parse(GSON.toJson(entityExample));

		ValidationType.NULL.validate(doc, Pair.of(DEFAULT_KEY_ID, ID_FIELD));
	}

	@Test
	void should_throw_error_on_validating_null_operation() {

		EntityExample entityExample = EntityExampleObjectMother.createSimpleEntityExample();

		Document doc = Document.parse(GSON.toJson(entityExample));

		assertThatThrownBy(() -> ValidationType.NULL.validate(doc, Pair.of(DEFAULT_KEY_ID, ID_FIELD)))
			.isInstanceOf(AssertionError.class);
	}

	@Test
	void should_validate_map_size_operation_correctly() {

		EntityExample entityExample = EntityExampleObjectMother.createEntityExample();
		Map<String, EntityExample> entityExampleFieldMap = entityExample.getEntityExampleMap();

		Document doc = Document.parse(GSON.toJson(entityExample));

		ValidationType.MAP_SIZE.validate(doc, Pair.of(ENTITY_EXAMPLE_MAP, entityExampleFieldMap.size()));
	}

	@Test
	void should_throw_error_on_validating_map_size_operation() {

		EntityExample entityExample = EntityExampleObjectMother.createEntityExample();

		Document doc = Document.parse(GSON.toJson(entityExample));

		assertThatThrownBy(() -> ValidationType.MAP_SIZE.validate(doc, Pair.of(ENTITY_EXAMPLE_MAP, INTEGER_ONE)))
			.isInstanceOf(AssertionError.class);
	}

	@Test
	void should_validate_complete_json() {

		EntityExample entityExample = EntityExampleObjectMother.createEntityExample();

		Document doc = Document.parse(GSON.toJson(entityExample));

		ValidationType.JSON.validate(doc, Pair.of(EntityExample.class, entityExample));
	}

	@Test
	void should_fail_on_validate_complete_json() {

		EntityExample entityExample = EntityExampleObjectMother.createEntityExample();
		EntityExample simpleEntityExample = EntityExampleObjectMother.createSimpleEntityExample();

		Document doc = Document.parse(GSON.toJson(entityExample));


		assertThatThrownBy(() -> ValidationType.JSON.validate(doc, Pair.of(EntityExample.class, simpleEntityExample)))
			.isInstanceOf(AssertionError.class);
	}

}
