package io.mongockito.common.model;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Value
@Builder(toBuilder = true)
@Document("example")
public class EntityExample {

	@Id
	@Field(name = "_id")
	String id;

	int amount;

	Integer nullableValue;

	boolean locked;

	String month;

	String creationUser;

	LocalDateTime creationTimestamp;

	String lastUpdateUser;

	LocalDateTime lastUpdateTimestamp;

	Map<String, EntityExample> entityExampleMap;

}
