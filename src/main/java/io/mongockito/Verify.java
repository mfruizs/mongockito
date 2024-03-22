package io.mongockito;

import static lombok.AccessLevel.PRIVATE;

import io.mongockito.util.json.JsonTool;
import io.mongockito.util.json.adapters.Adapter;
import java.lang.reflect.Type;
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
	List<Adapter> adapters;
	boolean allowNulls;

	Verify(final OperationBuilder builder) {

		this.operation = builder.operation;
		this.clazz = builder.clazz;
		this.fields = builder.fields;
		this.calls = builder.calls;
		this.adapters = builder.adapters;
		this.allowNulls = builder.allowNulls;
	}

	public static OperationBuilder builder() {

		return new OperationBuilder();
	}


	public static class OperationBuilder {

		Operation operation;
		Class<?> clazz;
		List<ValidateField> fields;
		private int calls = 1;
		List<Adapter> adapters;
		boolean allowNulls = true;

		OperationBuilder() {

		}

		public Verify build() {

			return new Verify(this);
		}

		public OperationBuilder addOperation(final Operation operation) {

			this.operation = operation;
			return this;
		}

		public OperationBuilder addClass(final Class<?> clazz) {

			this.clazz = clazz;
			return this;
		}

		public OperationBuilder addNumberOfInvocations(final int calls) {

			this.calls = calls;
			return this;
		}

		public <K, V> OperationBuilder addValidation(final ValidationType validationType,
													 final K fieldName,
													 final V expectedValue) {

			return this.addValidation(validationType, Pair.of(fieldName, expectedValue));
		}

		public <K> OperationBuilder addValidation(final ValidationType type, final K fieldName) {

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

		public OperationBuilder allowSerializeNulls(boolean allow) {

			this.allowNulls = allow;
			return this;
		}

		public OperationBuilder addAdapter(final Type type, final Object typeAdapter) {

			this.adapters = Optional.ofNullable(this.adapters).orElseGet(ArrayList::new);
			this.adapters.add(Adapter.builder().type(type).typeAdapter(typeAdapter).build());
			return this;
		}

		public void verify(final MongoTemplate mongoTemplate) {

			JsonTool.addAdapters(this.adapters);
			JsonTool.allowSerializeNulls(allowNulls);

			final Document document = this.operation.execute(mongoTemplate, this.clazz, this.calls);

			this.fields.forEach(field -> field.getValidationType().validate(document, field.getField()));
		}

	}
}




