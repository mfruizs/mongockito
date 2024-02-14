package io.mongockito;

import static io.mongockito.common.EntityExampleObjectMother.ID_FIELD;
import static io.mongockito.common.TestConstants.DATE_NOW;
import static io.mongockito.common.TestConstants.DEFAULT_KEY_ID;
import static io.mongockito.common.TestConstants.FIELD_LOCKED;
import static io.mongockito.common.TestConstants.FIELD_MONTH;
import static io.mongockito.common.TestConstants.MONTH_VALUE;
import static io.mongockito.util.json.JsonTool.GSON;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import io.mongockito.common.EntityExample;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Slf4j
@ExtendWith(MockitoExtension.class)
class OperationTest {

	@Mock(name = "database")
	protected MongoTemplate mongoTemplate;

	@Test
	void should_execute_find_operation_correctly() {

		Query query = new Query();
		query.addCriteria(where(DEFAULT_KEY_ID).is(ID_FIELD));
		mongoTemplate.find(query, EntityExample.class);

		Document document = Operation.FIND.execute(mongoTemplate, EntityExample.class, INTEGER_ONE);
		EntityExample entity = GSON.fromJson(document.toJson(), EntityExample.class);

		assertEquals(ID_FIELD, entity.getId());
	}

	@Test
	void should_execute_find_one_operation_correctly() {

		Query query = new Query();
		query.addCriteria(where(DEFAULT_KEY_ID).is(ID_FIELD));
		mongoTemplate.findOne(query, EntityExample.class);

		Document document = Operation.FIND_ONE.execute(mongoTemplate, EntityExample.class, INTEGER_ONE);
		EntityExample entity = GSON.fromJson(document.toJson(), EntityExample.class);

		assertEquals(ID_FIELD, entity.getId());

	}

	@Test
	void should_execute_find_by_id_operation_correctly() {

		mongoTemplate.findById(ID_FIELD, EntityExample.class);

		Document document = Operation.FIND_BY_ID.execute(mongoTemplate, EntityExample.class, INTEGER_ONE);
		EntityExample entity = GSON.fromJson(document.toJson(), EntityExample.class);

		assertEquals(ID_FIELD, entity.getId());

	}

	@Test
	void should_execute_find_and_Remove_operation_correctly() {

		Query query = new Query();
		query.addCriteria(where(DEFAULT_KEY_ID).is(ID_FIELD));

		mongoTemplate.findAndRemove(query, EntityExample.class);

		Document document = Operation.FIND_AND_REMOVE.execute(mongoTemplate, EntityExample.class, INTEGER_ONE);
		EntityExample entity = GSON.fromJson(document.toJson(), EntityExample.class);

		assertEquals(ID_FIELD, entity.getId());

	}

	@Test
	void should_execute_update_first_operation_correctly() {

		Query query = new Query();
		query.addCriteria(where(DEFAULT_KEY_ID).is(ID_FIELD));

		final Update update = new Update();
		update.set(DEFAULT_KEY_ID, ID_FIELD);
		update.set(FIELD_LOCKED, Boolean.TRUE);
		mongoTemplate.updateFirst(query, update, EntityExample.class);

		Document document = Operation.UPDATE_FIRST.execute(mongoTemplate, EntityExample.class, INTEGER_ONE);
		EntityExample entity = GSON.fromJson(document.toJson(), EntityExample.class);

		assertEquals(ID_FIELD, entity.getId());
		assertEquals(Boolean.TRUE, entity.isLocked());

	}

	@Test
	void should_execute_update_multi_operation_correctly() {

		Query query = new Query();
		query.addCriteria(where(DEFAULT_KEY_ID).is(ID_FIELD));

		final Update update = new Update();
		update.set(DEFAULT_KEY_ID, ID_FIELD);
		update.set(FIELD_LOCKED, Boolean.TRUE);
		mongoTemplate.updateMulti(query, update, EntityExample.class);

		Document document = Operation.UPDATE_MULTI.execute(mongoTemplate, EntityExample.class, INTEGER_ONE);
		EntityExample entity = GSON.fromJson(document.toJson(), EntityExample.class);

		assertEquals(ID_FIELD, entity.getId());
		assertEquals(Boolean.TRUE, entity.isLocked());

	}

	@Test
	void should_execute_upsert_operation_correctly() {

		Query query = new Query();
		query.addCriteria(where(DEFAULT_KEY_ID).is(ID_FIELD));

		final Update update = new Update();
		update.set(DEFAULT_KEY_ID, ID_FIELD);
		update.set(FIELD_LOCKED, Boolean.TRUE);
		update.set(FIELD_MONTH, MONTH_VALUE);
		mongoTemplate.upsert(query, update, EntityExample.class);

		Document document = Operation.UPSERT.execute(mongoTemplate, EntityExample.class, INTEGER_ONE);
		EntityExample entity = GSON.fromJson(document.toJson(), EntityExample.class);

		assertEquals(ID_FIELD, entity.getId());
		assertEquals(Boolean.TRUE, entity.isLocked());
		assertEquals(MONTH_VALUE, entity.getMonth());

	}

	@Test
	void should_execute_save_operation_correctly() {

		EntityExample entityExample = EntityExample.builder()
			.id(ID_FIELD)
			.locked(Boolean.FALSE)
			.month(MONTH_VALUE)
			.lastUpdateTimestamp(DATE_NOW)
			.build();

		mongoTemplate.save(entityExample);

		Document document = Operation.SAVE.execute(mongoTemplate, EntityExample.class, INTEGER_ONE);
		EntityExample entity = GSON.fromJson(document.toJson(), EntityExample.class);

		assertEquals(entityExample, entity);
	}

}
