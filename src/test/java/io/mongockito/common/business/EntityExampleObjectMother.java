package io.mongockito.common.business;

import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;

import io.mongockito.common.model.EntityExample;
import java.time.LocalDateTime;
import java.util.Map;
import org.bson.types.ObjectId;

public class EntityExampleObjectMother {

	public static final String ID_FIELD = new ObjectId().toHexString();
	public static final String ID_FILED_OTHER = "12345";
	public static final LocalDateTime DATE_NOW = LocalDateTime.now();

	public static final String MONTH_VALUE_01 = "01";
	public static final String MONTH_VALUE_02 = "02";

	public static EntityExample createSimpleEntityExample() {

		return EntityExample.builder()
			.id(ID_FIELD)
			.build();

	}

	public static EntityExample createEntityExample() {

		return EntityExample.builder()
			.id(ID_FIELD)
			.month(MONTH_VALUE_02)
			.locked(Boolean.TRUE)
			.creationUser("User_a")
			.creationTimestamp(DATE_NOW)
			.lastUpdateUser("User_b")
			.lastUpdateTimestamp(DATE_NOW.plusDays(INTEGER_ONE))
			.entityExampleMap(createFieldMap())
			.build();

	}

	public static EntityExample createEntityExampleWithoutMap() {

		return EntityExample.builder().id(ID_FIELD).month(MONTH_VALUE_02)
			.locked(Boolean.TRUE).creationUser("User_a").creationTimestamp(DATE_NOW).lastUpdateUser("User_b")
			.lastUpdateTimestamp(DATE_NOW.plusDays(INTEGER_ONE))
			.build();
	}

	public static Map<String, EntityExample> createFieldMap() {

		return Map.of(
			"A", EntityExample.builder().id(ID_FILED_OTHER).month(MONTH_VALUE_01)
				.locked(Boolean.FALSE).creationUser("User_1").creationTimestamp(DATE_NOW).lastUpdateUser("User_2")
				.lastUpdateTimestamp(DATE_NOW.plusDays(INTEGER_ONE))

				.build(),
			"B", EntityExample.builder().id(ID_FILED_OTHER).month(MONTH_VALUE_02)
				.locked(Boolean.FALSE).creationUser("User_3").creationTimestamp(DATE_NOW).lastUpdateUser("User_4")
				.lastUpdateTimestamp(DATE_NOW.plusDays(INTEGER_ONE))
				.build());
	}


}
