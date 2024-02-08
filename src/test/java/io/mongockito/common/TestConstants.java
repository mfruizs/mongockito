package io.mongockito.common;

import io.mongockito.Operation;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import org.bson.types.ObjectId;

@UtilityClass
public class TestConstants {

	public static final Operation OPERATION_FIND_BY_ID = Operation.FIND_BY_ID;
	public static final String DEFAULT_KEY_ID = "_id";
	public static final String ID_FIELD = new ObjectId().toHexString();
	public static final String FIELD_LOCKED = "locked";
	public static final String FIELD_MONTH = "month";
	public static final String MONTH_VALUE = "01";
	public static final String FIELD_LAST_UPDATE_TIMESTAMP = "lastUpdateTimestamp";
	public static final LocalDateTime DATE_NOW = LocalDateTime.now();
	public static final String ENTITY_EXAMPLE_MAP = "entityExampleMap";

}
