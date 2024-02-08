package io.mongockito;

import static io.mongockito.util.json.JsonTool.GSON;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import org.bson.Document;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public enum Operation {
	FIND {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final int calls) {

			verify(mongoTemplate, times(calls)).find(queryCaptor.capture(), eq(clazz));
			return queryCaptor.getValue().getQueryObject();
		}
	},

	FIND_ONE {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final int calls) {

			verify(mongoTemplate, times(calls)).findOne(queryCaptor.capture(), eq(clazz));
			return queryCaptor.getValue().getQueryObject();
		}
	},

	FIND_BY_ID {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final int calls) {

			verify(mongoTemplate, times(calls)).findById(stringCaptor.capture(), eq(clazz));
			return new Document(DEFAULT_KEY_ID, stringCaptor.getValue());
		}
	},

	FIND_AND_REMOVE {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final int calls) {

			verify(mongoTemplate, times(calls)).findAndRemove(queryCaptor.capture(), eq(clazz));
			return queryCaptor.getValue().getQueryObject();
		}
	},

	UPDATE_FIRST {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final int calls) {

			verify(mongoTemplate, times(calls)).updateFirst(queryCaptor.capture(), updateCaptor.capture(), eq(clazz));
			return recoverFieldsFromQueryAndUpdateCaptors();

		}
	},

	UPDATE_MULTI {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final int calls) {

			verify(mongoTemplate, times(calls)).updateMulti(queryCaptor.capture(), updateCaptor.capture(), eq(clazz));
			return recoverFieldsFromQueryAndUpdateCaptors();

		}
	},

	UPSERT {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final int calls) {

			verify(mongoTemplate, times(calls)).upsert(queryCaptor.capture(), updateCaptor.capture(), eq(clazz));
			return recoverFieldsFromQueryAndUpdateCaptors();

		}
	},

	SAVE {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final int calls) {

			final ArgumentCaptor<?> saveCaptor = ArgumentCaptor.forClass(clazz);
			verify(mongoTemplate, times(calls)).save(saveCaptor.capture());
			return Document.parse(GSON.toJson(saveCaptor.getValue()));
		}
	};

	private static Document recoverFieldsFromQueryAndUpdateCaptors() {

		final Document responseDoc = queryCaptor.getValue().getQueryObject();

		final Collection<?> updateValues = updateCaptor.getValue().getUpdateObject().values();
		updateValues.forEach(doc -> responseDoc.putAll((Document) doc));

		return responseDoc;
	}

	public static final String DEFAULT_KEY_ID = "_id"; // TODO configurable
	private static final ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
	private static final ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
	private static final ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);

	public abstract Document execute(MongoTemplate mongoTemplate, Class<?> clazz, int calls);


}
