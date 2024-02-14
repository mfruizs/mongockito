package io.mongockito.common;

import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;

import java.time.LocalDateTime;
import java.util.Map;
import org.bson.types.ObjectId;

public class EntityExampleObjectMother {

	public static final String ID_FIELD = new ObjectId().toHexString();
	public static final LocalDateTime LOCAL_DATE_TIME_NOW = LocalDateTime.now();

	public static EntityExample createSimpleEntityExample() {

		return EntityExample.builder()
			.id(ID_FIELD)
			.build();

	}

	public static EntityExample createEntityExample() {

		return EntityExample.builder()
			.id(ID_FIELD)
			.month("02")
			.locked(Boolean.TRUE)
			.creationUser("User_a")
			.creationTimestamp(LOCAL_DATE_TIME_NOW)
			.lastUpdateUser("User_b")
			.lastUpdateTimestamp(LOCAL_DATE_TIME_NOW.plusDays(INTEGER_ONE))
			.entityExampleMap(createFieldMap())
			.build();

	}

	public static EntityExample createEntityExampleWithoutMap() {

		return EntityExample.builder()
			.id(ID_FIELD)
			.month("02")
			.locked(Boolean.TRUE)
			.creationUser("User_a")
			.creationTimestamp(LOCAL_DATE_TIME_NOW)
			.lastUpdateUser("User_b")
			.lastUpdateTimestamp(LOCAL_DATE_TIME_NOW.plusDays(INTEGER_ONE))
			.build();
	}

	public static Map<String, EntityExample> createFieldMap() {
		return Map.of(
			"A", EntityExample.builder()
				.id("12345")
				.month("01")
				.locked(Boolean.FALSE)
				.creationUser("User_1")
				.creationTimestamp(LOCAL_DATE_TIME_NOW)
				.lastUpdateUser("User_2")
				.lastUpdateTimestamp(LOCAL_DATE_TIME_NOW.plusDays(INTEGER_ONE))

				.build(),
			"B", EntityExample.builder()
				.id("12345")
				.month("02")
				.locked(Boolean.FALSE)
				.creationUser("User_3")
				.creationTimestamp(LOCAL_DATE_TIME_NOW)
				.lastUpdateUser("User_4")
				.lastUpdateTimestamp(LOCAL_DATE_TIME_NOW.plusDays(INTEGER_ONE))
				.build());
	}


}
