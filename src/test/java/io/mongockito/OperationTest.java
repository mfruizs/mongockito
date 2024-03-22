package io.mongockito;

import static io.mongockito.common.EntityExampleObjectMother.ID_FIELD;
import static io.mongockito.common.EntityExampleObjectMother.MONTH_VALUE_01;
import static io.mongockito.common.TestConstants.DEFAULT_KEY_ID;
import static io.mongockito.common.TestConstants.FIELD_LOCKED;
import static io.mongockito.common.TestConstants.FIELD_MONTH;
import static io.mongockito.util.json.JsonTool.gsonBuilder;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import io.mongockito.common.EntityExample;
import io.mongockito.common.EntityExampleObjectMother;
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

		final Query query = new Query();
		query.addCriteria(where(DEFAULT_KEY_ID).is(ID_FIELD));
		this.mongoTemplate.find(query, EntityExample.class);

		final Document document = Operation.FIND.execute(this.mongoTemplate, EntityExample.class, INTEGER_ONE);
		final EntityExample entity = gsonBuilder().fromJson(document.toJson(), EntityExample.class);

		assertEquals(ID_FIELD, entity.getId());
	}

	@Test
	void should_execute_find_one_operation_correctly() {

		final Query query = new Query();
		query.addCriteria(where(DEFAULT_KEY_ID).is(ID_FIELD));
		this.mongoTemplate.findOne(query, EntityExample.class);

		final Document document = Operation.FIND_ONE.execute(this.mongoTemplate, EntityExample.class, INTEGER_ONE);
		final EntityExample entity = gsonBuilder().fromJson(document.toJson(), EntityExample.class);

		assertEquals(ID_FIELD, entity.getId());

	}

	@Test
	void should_execute_find_by_id_operation_correctly() {

		this.mongoTemplate.findById(ID_FIELD, EntityExample.class);

		final Document document = Operation.FIND_BY_ID.execute(this.mongoTemplate, EntityExample.class, INTEGER_ONE);
		final EntityExample entity = gsonBuilder().fromJson(document.toJson(), EntityExample.class);

		assertEquals(ID_FIELD, entity.getId());

	}

	@Test
	void should_execute_find_and_Remove_operation_correctly() {

		final Query query = new Query();
		query.addCriteria(where(DEFAULT_KEY_ID).is(ID_FIELD));

		this.mongoTemplate.findAndRemove(query, EntityExample.class);

		final Document document = Operation.FIND_AND_REMOVE.execute(this.mongoTemplate, EntityExample.class, INTEGER_ONE);
		final EntityExample entity = gsonBuilder().fromJson(document.toJson(), EntityExample.class);

		assertEquals(ID_FIELD, entity.getId());

	}

	@Test
	void should_execute_update_first_operation_correctly() {

		final Query query = new Query();
		query.addCriteria(where(DEFAULT_KEY_ID).is(ID_FIELD));

		final Update update = new Update();
		update.set(DEFAULT_KEY_ID, ID_FIELD);
		update.set(FIELD_LOCKED, Boolean.TRUE);
		this.mongoTemplate.updateFirst(query, update, EntityExample.class);

		final Document document = Operation.UPDATE_FIRST.execute(this.mongoTemplate, EntityExample.class, INTEGER_ONE);
		final EntityExample entity = gsonBuilder().fromJson(document.toJson(), EntityExample.class);

		assertEquals(ID_FIELD, entity.getId());
		assertEquals(Boolean.TRUE, entity.isLocked());

	}

	@Test
	void should_execute_update_multi_operation_correctly() {

		final Query query = new Query();
		query.addCriteria(where(DEFAULT_KEY_ID).is(ID_FIELD));

		final Update update = new Update();
		update.set(DEFAULT_KEY_ID, ID_FIELD);
		update.set(FIELD_LOCKED, Boolean.TRUE);
		this.mongoTemplate.updateMulti(query, update, EntityExample.class);

		final Document document = Operation.UPDATE_MULTI.execute(this.mongoTemplate, EntityExample.class, INTEGER_ONE);
		final EntityExample entity = gsonBuilder().fromJson(document.toJson(), EntityExample.class);

		assertEquals(ID_FIELD, entity.getId());
		assertEquals(Boolean.TRUE, entity.isLocked());

	}

	@Test
	void should_execute_upsert_operation_correctly() {

		final Query query = new Query();
		query.addCriteria(where(DEFAULT_KEY_ID).is(ID_FIELD));

		final Update update = new Update();
		update.set(DEFAULT_KEY_ID, ID_FIELD);
		update.set(FIELD_LOCKED, Boolean.TRUE);
		update.set(FIELD_MONTH, MONTH_VALUE_01);
		this.mongoTemplate.upsert(query, update, EntityExample.class);

		final Document document = Operation.UPSERT.execute(this.mongoTemplate, EntityExample.class, INTEGER_ONE);
		final EntityExample entity = gsonBuilder().fromJson(document.toJson(), EntityExample.class);

		assertEquals(ID_FIELD, entity.getId());
		assertEquals(Boolean.TRUE, entity.isLocked());
		assertEquals(MONTH_VALUE_01, entity.getMonth());

	}

	@Test
	void should_execute_save_operation_correctly() {

		final EntityExample entityExample = EntityExampleObjectMother.createEntityExampleWithoutMap();

		this.mongoTemplate.save(entityExample);

		final Document document = Operation.SAVE.execute(this.mongoTemplate, EntityExample.class, INTEGER_ONE);
		final EntityExample entity = gsonBuilder().fromJson(document.toJson(), EntityExample.class);

		assertEquals(entityExample, entity);
	}

}
