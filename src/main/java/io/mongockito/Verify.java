package io.mongockito;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

import io.mongockito.model.ValidateField;
import io.mongockito.util.json.JsonTool;
import io.mongockito.util.json.model.Adapter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.mockito.verification.VerificationMode;
import org.springframework.data.mongodb.core.MongoTemplate;

@Value
@RequiredArgsConstructor(access = PRIVATE)
public class Verify {

	private static final String MANDATORY_FIELD_NAME = "mandatory field: FieldName";
	private static final String MANDATORY_OPERATION = "mandatory field: Operation";
	private static final String MANDATORY_VALIDATION_TYPE = "mandatory field: Validation Type";
	private static final String MANDATORY_CLASS = "mandatory field: Class";
	private static final String MANDATORY_TYPE_ADAPTER = "mandatory field: Type Adapter";
	private static final String MANDATORY_ADAPTER_CLASS = "mandatory field: Adapter Class";
	private static final String MANDATORY_MONGO_TEMPLATE = "mandatory field: Mongo Template";
	private static final String MANDATORY_EXPECTED_SIZE = "mandatory field: Map Size";
	private static final String MANDATORY_FIELD_EXPECTED_VALUE = "mandatory field: expectedValue";
	private static final String MANDATORY_FIELD_VERIFICATION_MODE = "mandatory field: Verification Mode";

	Operation operation;
	Class<?> clazz;
	List<ValidateField> fields;
	List<Adapter> adapters;
	boolean allowNulls;
	VerificationMode verificationMode;

	Verify(final OperationBuilder builder) {

		this.operation = builder.operation;
		this.clazz = builder.clazz;
		this.fields = builder.fields;
		this.adapters = builder.adapters;
		this.allowNulls = builder.allowNulls;
		this.verificationMode = builder.verificationMode;
	}

	public static OperationBuilder builder() {

		return new OperationBuilder();
	}


	public static class OperationBuilder {

		Operation operation;
		Class<?> clazz;
		List<ValidateField> fields;
		List<Adapter> adapters;
		boolean allowNulls = true;
		private VerificationMode verificationMode = times(1);

		OperationBuilder() {

		}

		public Verify build() {

			return new Verify(this);
		}

		public OperationBuilder addOperation(final Operation operation) {

			assertNotNull(operation, MANDATORY_OPERATION);

			this.operation = operation;
			return this;
		}

		public OperationBuilder addClass(final Class<?> clazz) {

			assertNotNull(clazz, MANDATORY_CLASS);

			this.clazz = clazz;
			return this;
		}

		public OperationBuilder addVerificationMode(final VerificationMode verificationMode) {

			assertNotNull(verificationMode, MANDATORY_FIELD_VERIFICATION_MODE);

			this.verificationMode = verificationMode;
			return this;
		}

		public <K, V> OperationBuilder validateEquals(final K fieldName,
													 final V expectedValue) {

			assertNotNull(fieldName, MANDATORY_FIELD_NAME);

			return this.addValidation(ValidationType.EQUALS, Pair.of(fieldName, expectedValue));
		}

		public <K> OperationBuilder validateNull(final K fieldName) {

			assertNotNull(fieldName, MANDATORY_FIELD_NAME);

			return this.addValidation(ValidationType.NULL, fieldName);
		}

		public <K> OperationBuilder validateNotNull(final K fieldName) {

			assertNotNull(fieldName, MANDATORY_FIELD_NAME);

			return this.addValidation(ValidationType.NOT_NULL, fieldName);
		}

		public <K, V> OperationBuilder validateCollectionSize(final K fieldName,
															  final V expectedSize) {

			assertNotNull(fieldName, MANDATORY_FIELD_NAME);
			assertNotNull(expectedSize, MANDATORY_EXPECTED_SIZE);

			return this.addValidation(ValidationType.COLLECTION_SIZE, Pair.of(fieldName, expectedSize));
		}

		public <K> OperationBuilder validateJson(final K expectedValue) {

			assertNotNull(expectedValue, MANDATORY_FIELD_EXPECTED_VALUE);

			return this.addValidation(ValidationType.JSON, expectedValue);
		}

		public <K, V> OperationBuilder validateJsonByKey(final K fieldName,
													  final V expectedValue) {
			assertNotNull(fieldName, MANDATORY_FIELD_NAME);
			assertNotNull(expectedValue, MANDATORY_FIELD_EXPECTED_VALUE);

			return this.addValidation(ValidationType.JSON_BY_KEY, Pair.of(fieldName, expectedValue));
		}

		public <K, V> OperationBuilder addValidation(final ValidationType validationType,
													 final K fieldName,
													 final V expectedValue) {

			assertNotNull(validationType, MANDATORY_VALIDATION_TYPE);
			assertNotNull(fieldName, MANDATORY_FIELD_NAME);

			return this.addValidation(validationType, Pair.of(fieldName, expectedValue));
		}

		private <K> OperationBuilder addValidation(final ValidationType type, final K fieldName) {

			assertNotNull(type, MANDATORY_VALIDATION_TYPE);
			assertNotNull(fieldName, MANDATORY_FIELD_NAME);

			return this.addValidation(type, Pair.of(fieldName, null));
		}

		private <K, V> OperationBuilder addValidation(final ValidationType validationType, final Pair<K, V> field) {

			this.fields = Optional.ofNullable(this.fields).orElseGet(ArrayList::new);
			this.fields.add(ValidateField.builder()
								.validationType(validationType)
								.field(field)
								.build());
			return this;
		}

		public OperationBuilder allowSerializeNulls(final boolean allow) {

			this.allowNulls = allow;
			return this;
		}

		public OperationBuilder addAdapter(final Type typeClass, final Object typeAdapter) {

			assertNotNull(typeClass, MANDATORY_ADAPTER_CLASS);
			assertNotNull(typeAdapter, MANDATORY_TYPE_ADAPTER);

			this.adapters = Optional.ofNullable(this.adapters).orElseGet(ArrayList::new);
			this.adapters.add(Adapter.builder().typeClass(typeClass).typeAdapter(typeAdapter).build());
			return this;
		}

		public void verify(final MongoTemplate mongoTemplate) {

			this.validateAndCompleteBuilder(mongoTemplate);

			JsonTool.addAdapters(this.adapters);
			JsonTool.allowSerializeNulls(this.allowNulls);

			final Document document = this.operation.execute(mongoTemplate, this.clazz, this.verificationMode);

			this.fields.forEach(field -> field.getValidationType().validate(document, field.getField()));

			JsonTool.reset();
		}

		private void validateAndCompleteBuilder(final MongoTemplate mongoTemplate) {

			assertNotNull(mongoTemplate, MANDATORY_MONGO_TEMPLATE);
			assertNotNull(this.operation, MANDATORY_OPERATION);
			assertNotNull(this.clazz, MANDATORY_CLASS);

			if (this.verificationMode == null) {
				this.verificationMode = times(INTEGER_ONE);
			}

			if (this.fields == null) {
				this.fields = Collections.emptyList();
			}
		}
	}
}




