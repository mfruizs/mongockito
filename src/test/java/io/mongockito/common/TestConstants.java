package io.mongockito.common;

import io.mongockito.Operation;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestConstants {

	public static final Operation OPERATION_FIND_BY_ID = Operation.FIND_BY_ID;
	public static final String DEFAULT_KEY_ID = "_id";
	public static final String FIELD_LOCKED = "locked";
	public static final String FIELD_MONTH = "month";
	public static final String FIELD_LAST_UPDATE_TIMESTAMP = "lastUpdateTimestamp";
	public static final String ENTITY_EXAMPLE_MAP = "entityExampleMap";
	public static final String ENTITY_EXAMPLE_LIST = "entityExampleList";
	public static final String NULLABLE_VALUE_FIELD = "nullableValue";
	public static final String EXAMPLE_COLLECTION_NAME = "example";

}
