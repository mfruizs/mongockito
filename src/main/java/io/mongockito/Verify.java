package io.mongockito;

import static lombok.AccessLevel.PRIVATE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

@Value
@RequiredArgsConstructor(access = PRIVATE)
public class Verify {

	Operation operation;
	Class<?> clazz;
	List<ValidateField> fields;
	int calls;

	Verify(OperationBuilder builder) {

		this.operation = builder.operation;
		this.clazz = builder.clazz;
		this.fields = builder.fields;
		this.calls = builder.calls;
	}

	public static OperationBuilder builder() {

		return new OperationBuilder();
	}


	public static class OperationBuilder {

		Operation operation;
		Class<?> clazz;
		List<ValidateField> fields;
		private int calls = 1;

		OperationBuilder() {

		}

		public Verify build() {

			return new Verify(this);
		}

		public OperationBuilder addOperation(Operation operation) {

			this.operation = operation;
			return this;
		}

		public OperationBuilder addClass(Class<?> clazz) {

			this.clazz = clazz;
			return this;
		}

		public OperationBuilder addNumberOfInvocations(int calls) {

			this.calls = calls;
			return this;
		}

		public <K, V> OperationBuilder addValidation(ValidationType validationType, K fieldName, V expectedValue) {

			return addValidation(validationType, Pair.of(fieldName, expectedValue));
		}

		public <K> OperationBuilder addValidation(ValidationType type, K fieldName) {

			return addValidation(type, Pair.of(fieldName, null));
		}

		private <K, V> OperationBuilder addValidation(ValidationType validationType, Pair<K, V> field) {

			fields = Optional.ofNullable(fields).orElseGet(ArrayList::new);
			fields.add(ValidateField.builder()
						   .validationType(validationType)
						   .field(field)
						   .build());
			return this;
		}

		public void verify(final MongoTemplate mongoTemplate) {

			final Document document = operation.execute(mongoTemplate, clazz, calls);

			fields.forEach(field -> field.getValidationType().validate(document, field.getField()));
		}

	}
}




