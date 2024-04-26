package io.mongockito;

import static io.mongockito.Operation.SAVE;
import static io.mongockito.common.TestConstants.DEFAULT_KEY_ID;
import static io.mongockito.common.TestConstants.ENTITY_EXAMPLE_LIST;
import static io.mongockito.common.TestConstants.ENTITY_EXAMPLE_MAP;
import static io.mongockito.common.TestConstants.EXAMPLE_COLLECTION_NAME;
import static io.mongockito.common.TestConstants.FIELD_LAST_UPDATE_TIMESTAMP;
import static io.mongockito.common.TestConstants.FIELD_LOCKED;
import static io.mongockito.common.TestConstants.FIELD_MONTH;
import static io.mongockito.common.TestConstants.NULLABLE_VALUE_FIELD;
import static io.mongockito.common.TestConstants.OPERATION_FIND_BY_ID;
import static io.mongockito.common.business.EntityExampleObjectMother.DATE_NOW;
import static io.mongockito.common.business.EntityExampleObjectMother.ID_FIELD;
import static io.mongockito.common.business.EntityExampleObjectMother.MONTH_VALUE_01;
import static io.mongockito.common.business.EntityExampleObjectMother.createEntityExample;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_TWO;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.times;

import io.mongockito.Verify.OperationBuilder;
import io.mongockito.common.model.EntityExample;
import io.mongockito.model.ValidateField;
import io.mongockito.util.json.model.Adapter;
import io.mongockito.util.json.adapters.LocalDateTimeAdapter;
import io.mongockito.util.json.adapters.ObjectIdAdapter;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
class VerifyTest {

	@Spy
	private OperationBuilder operationBuilder;

	@Mock(name = "database")
	protected MongoTemplate mongoTemplate;

	@Test
	void should_create_a_build_object_with_correct_operation_and_class() {

		final Verify result = this.operationBuilder.addOperation(OPERATION_FIND_BY_ID)
			.addClass(EntityExample.class)
			.addValidation(ValidationType.EQUALS, DEFAULT_KEY_ID, ID_FIELD)
			.addValidation(ValidationType.EQUALS, FIELD_LOCKED, true)
			.addValidation(ValidationType.EQUALS, FIELD_MONTH, MONTH_VALUE_01)
			.validateNotNull(FIELD_LAST_UPDATE_TIMESTAMP)
			.build();

		assertEquals(OPERATION_FIND_BY_ID, result.getOperation(), "Error adding operation type");
		assertEquals(EntityExample.class, result.getClazz(), "Error adding class");

	}

	@Test
	void should_create_a_build_object_with_correct_operation_and_class_using_validate_equals_method() {

		final Verify result = this.operationBuilder.addOperation(OPERATION_FIND_BY_ID)
			.addClass(EntityExample.class)
			.validateEquals(DEFAULT_KEY_ID, ID_FIELD)
			.validateEquals(FIELD_LOCKED, true)
			.validateEquals(FIELD_MONTH, MONTH_VALUE_01)
			.validateNotNull(FIELD_LAST_UPDATE_TIMESTAMP)
			.build();

		assertEquals(OPERATION_FIND_BY_ID, result.getOperation(), "Error adding operation type");
		assertEquals(EntityExample.class, result.getClazz(), "Error adding class");

	}

	@Test
	void should_create_a_build_object_with_correct_validations() {

		final Verify result = this.operationBuilder.addOperation(OPERATION_FIND_BY_ID)
			.addClass(EntityExample.class)
			.addValidation(ValidationType.EQUALS, DEFAULT_KEY_ID, ID_FIELD)
			.addValidation(ValidationType.EQUALS, FIELD_LOCKED, true)
			.addValidation(ValidationType.EQUALS, FIELD_MONTH, MONTH_VALUE_01)
			.validateNotNull(FIELD_LAST_UPDATE_TIMESTAMP)
			.addValidation(ValidationType.EQUALS, FIELD_LAST_UPDATE_TIMESTAMP, DATE_NOW)
			.build();

		final List<ValidateField> validateFields = result.getFields();
		assertEquals(5, validateFields.size());

		final ValidateField firstValidation = validateFields.get(INTEGER_ZERO);
		assertEquals(ValidationType.EQUALS, firstValidation.getValidationType(), "Error adding validation type field");
		assertEquals(Pair.of(DEFAULT_KEY_ID, ID_FIELD), firstValidation.getField(), "Error adding pair of fields");

		final ValidateField secondValidation = validateFields.get(INTEGER_ONE);
		assertEquals(ValidationType.EQUALS, secondValidation.getValidationType(), "Error adding validation type field");
		assertEquals(Pair.of(FIELD_LOCKED, true), secondValidation.getField(), "Error adding pair of fields");

		final ValidateField thirdValidation = validateFields.get(INTEGER_TWO);
		assertEquals(ValidationType.EQUALS, thirdValidation.getValidationType(), "Error adding validation type field");
		assertEquals(Pair.of(FIELD_MONTH, MONTH_VALUE_01), thirdValidation.getField(), "Error adding pair of fields");

		final ValidateField fourthValidation = validateFields.get(3);
		assertEquals(ValidationType.NOT_NULL, fourthValidation.getValidationType(), "Error adding validation type field");
		assertEquals(Pair.of(FIELD_LAST_UPDATE_TIMESTAMP, null), fourthValidation.getField(), "Error adding pair of fields");

		final ValidateField fifthValidation = validateFields.get(4);
		assertEquals(ValidationType.EQUALS, fifthValidation.getValidationType(), "Error adding validation type field");
		assertEquals(Pair.of(FIELD_LAST_UPDATE_TIMESTAMP, DATE_NOW), fifthValidation.getField(), "Error adding pair of fields");

	}

	@Test
	void should_create_a_build_object_with_correct_number_of_invocations() {

		final VerificationMode verificationMode = times(INTEGER_TWO);

		final Verify result = this.operationBuilder.addOperation(OPERATION_FIND_BY_ID)
			.addClass(EntityExample.class)
			.addVerificationMode(verificationMode)
			.build();

		assertEquals(verificationMode, result.getVerificationMode(), "Error adding number of invocations");

	}

	@Test
	void should_execute_verify_method_using_find_by_id_correctly() {

		this.mongoTemplate.findById(ID_FIELD, EntityExample.class);

		this.operationBuilder.addOperation(OPERATION_FIND_BY_ID)
			.addClass(EntityExample.class)
			.addValidation(ValidationType.EQUALS, DEFAULT_KEY_ID, ID_FIELD)
			.addVerificationMode(times(INTEGER_ONE))
			.verify(this.mongoTemplate);

	}

	@Test
	void should_add_json_adapters_correctly() {

		final EntityExample entityExample = createEntityExample();
		final ObjectIdAdapter objectIdAdapter = new ObjectIdAdapter();
		final LocalDateTimeAdapter localDateTimeAdapter = new LocalDateTimeAdapter();

		this.mongoTemplate.save(entityExample);

		final Verify result = this.operationBuilder
			.addOperation(SAVE)
			.addClass(EntityExample.class)
			.allowSerializeNulls(false)
			.addAdapter(ObjectId.class, objectIdAdapter)
			.addAdapter(LocalDateTime.class, localDateTimeAdapter)
			.validateJson(entityExample)
			.build();

		assertFalse(result.isAllowNulls());

		final List<ValidateField> validateFields = result.getFields();
		assertEquals(INTEGER_ONE, validateFields.size());

		final ValidateField jsonValidation = validateFields.get(INTEGER_ZERO);
		assertEquals(ValidationType.JSON, jsonValidation.getValidationType(), "Error adding validation type field");
		assertEquals(Pair.of(entityExample, null), jsonValidation.getField(), "Error adding pair of fields");

		final List<Adapter> adapters = result.getAdapters();
		assertEquals(INTEGER_TWO, adapters.size());
		assertEquals(ObjectId.class, adapters.get(INTEGER_ZERO).getTypeClass());
		assertEquals(objectIdAdapter, adapters.get(INTEGER_ZERO).getTypeAdapter());
		assertEquals(LocalDateTime.class, adapters.get(INTEGER_ONE).getTypeClass());
		assertEquals(localDateTimeAdapter, adapters.get(INTEGER_ONE).getTypeAdapter());

	}

	@Test
	void should_verify_all_validation_type_correctly() {

		final EntityExample entityExample = createEntityExample();
		final ObjectIdAdapter objectIdAdapter = new ObjectIdAdapter();
		final LocalDateTimeAdapter localDateTimeAdapter = new LocalDateTimeAdapter();

		this.mongoTemplate.save(entityExample);

		Verify.builder()
			.addOperation(SAVE)
			.addClass(EntityExample.class)
			.allowSerializeNulls(true)
			.addAdapter(ObjectId.class, objectIdAdapter)
			.addAdapter(LocalDateTime.class, localDateTimeAdapter)
			.validateJson(entityExample)
			.validateJsonByKey(ENTITY_EXAMPLE_MAP, entityExample.getEntityExampleMap())
			.validateNull(NULLABLE_VALUE_FIELD)
			.validateNotNull(DEFAULT_KEY_ID)
			.validateEquals(DEFAULT_KEY_ID, entityExample.getId())
			.validateCollectionSize(ENTITY_EXAMPLE_LIST, entityExample.getEntityExampleList().size())
			.verify(this.mongoTemplate);
	}


	@Test
	void should_verify_all_validation_type_correctly_with_add_specified_collection_name() {

		final EntityExample entityExample = createEntityExample();
		final ObjectIdAdapter objectIdAdapter = new ObjectIdAdapter();
		final LocalDateTimeAdapter localDateTimeAdapter = new LocalDateTimeAdapter();

		this.mongoTemplate.save(entityExample, EXAMPLE_COLLECTION_NAME);

		Verify.builder()
			.addOperation(SAVE)
			.addClass(EntityExample.class)
			.addCollectionName(EXAMPLE_COLLECTION_NAME)
			.allowSerializeNulls(true)
			.addAdapter(ObjectId.class, objectIdAdapter)
			.addAdapter(LocalDateTime.class, localDateTimeAdapter)
			.validateJson(entityExample)
			.validateJsonByKey(ENTITY_EXAMPLE_MAP, entityExample.getEntityExampleMap())
			.validateNull(NULLABLE_VALUE_FIELD)
			.validateNotNull(DEFAULT_KEY_ID)
			.validateEquals(DEFAULT_KEY_ID, entityExample.getId())
			.validateCollectionSize(ENTITY_EXAMPLE_LIST, entityExample.getEntityExampleList().size())
			.verify(this.mongoTemplate);
	}


}
