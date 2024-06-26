package io.mongockito;

import static io.mongockito.util.json.JsonTool.gsonBuilder;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import org.bson.Document;
import org.mockito.ArgumentCaptor;
import org.mockito.verification.VerificationMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public enum Operation {
	FIND {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final VerificationMode verificationMode) {

			verify(mongoTemplate, verificationMode).find(queryCaptor.capture(), eq(clazz));
			return queryCaptor.getValue().getQueryObject();
		}

		@Override
		public Document execute(final MongoTemplate mongoTemplate,
								final Class<?> clazz,
								final VerificationMode verificationMode,
								final String collectionName) {

			verify(mongoTemplate, verificationMode).find(queryCaptor.capture(), eq(clazz), eq(collectionName));
			return queryCaptor.getValue().getQueryObject();
		}
	},

	FIND_ONE {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final VerificationMode verificationMode) {

			verify(mongoTemplate, verificationMode).findOne(queryCaptor.capture(), eq(clazz));
			return queryCaptor.getValue().getQueryObject();
		}

		@Override
		public Document execute(final MongoTemplate mongoTemplate,
								final Class<?> clazz,
								final VerificationMode verificationMode,
								final String collectionName) {

			verify(mongoTemplate, verificationMode).findOne(queryCaptor.capture(), eq(clazz), eq(collectionName));
			return queryCaptor.getValue().getQueryObject();
		}
	},

	FIND_BY_ID {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final VerificationMode verificationMode) {

			verify(mongoTemplate, verificationMode).findById(stringCaptor.capture(), eq(clazz));
			return new Document(DEFAULT_KEY_ID, stringCaptor.getValue());
		}

		@Override
		public Document execute(final MongoTemplate mongoTemplate,
								final Class<?> clazz,
								final VerificationMode verificationMode,
								final String collectionName) {

			verify(mongoTemplate, verificationMode).findById(stringCaptor.capture(), eq(clazz), eq(collectionName));
			return new Document(DEFAULT_KEY_ID, stringCaptor.getValue());
		}
	},

	FIND_AND_REMOVE {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final VerificationMode verificationMode) {

			verify(mongoTemplate, verificationMode).findAndRemove(queryCaptor.capture(), eq(clazz));
			return queryCaptor.getValue().getQueryObject();
		}

		@Override
		public Document execute(final MongoTemplate mongoTemplate,
								final Class<?> clazz,
								final VerificationMode verificationMode,
								final String collectionName) {

			verify(mongoTemplate, verificationMode).findAndRemove(queryCaptor.capture(), eq(clazz), eq(collectionName));
			return queryCaptor.getValue().getQueryObject();
		}
	},

	UPDATE_FIRST {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final VerificationMode verificationMode) {

			verify(mongoTemplate, verificationMode).updateFirst(queryCaptor.capture(), updateCaptor.capture(), eq(clazz));
			return recoverFieldsFromQueryAndUpdateCaptors();

		}

		@Override
		public Document execute(final MongoTemplate mongoTemplate,
								final Class<?> clazz,
								final VerificationMode verificationMode,
								final String collectionName) {

			verify(mongoTemplate, verificationMode).updateFirst(queryCaptor.capture(),
																updateCaptor.capture(),
																eq(clazz),
																eq(collectionName));
			return recoverFieldsFromQueryAndUpdateCaptors();
		}
	},

	UPDATE_MULTI {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final VerificationMode verificationMode) {

			verify(mongoTemplate, verificationMode).updateMulti(queryCaptor.capture(), updateCaptor.capture(), eq(clazz));
			return recoverFieldsFromQueryAndUpdateCaptors();

		}

		@Override
		public Document execute(final MongoTemplate mongoTemplate,
								final Class<?> clazz,
								final VerificationMode verificationMode,
								final String collectionName) {

			verify(mongoTemplate, verificationMode).updateMulti(queryCaptor.capture(),
																updateCaptor.capture(),
																eq(clazz),
																eq(collectionName));
			return recoverFieldsFromQueryAndUpdateCaptors();
		}
	},

	UPSERT {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final VerificationMode verificationMode) {

			verify(mongoTemplate, verificationMode).upsert(queryCaptor.capture(), updateCaptor.capture(), eq(clazz));
			return recoverFieldsFromQueryAndUpdateCaptors();

		}

		@Override
		public Document execute(final MongoTemplate mongoTemplate,
								final Class<?> clazz,
								final VerificationMode verificationMode,
								final String collectionName) {

			verify(mongoTemplate, verificationMode).upsert(queryCaptor.capture(),
														   updateCaptor.capture(),
														   eq(clazz),
														   eq(collectionName));
			return recoverFieldsFromQueryAndUpdateCaptors();
		}
	},

	SAVE {
		@Override
		public Document execute(final MongoTemplate mongoTemplate, final Class<?> clazz, final VerificationMode verificationMode) {

			final ArgumentCaptor<?> saveCaptor = ArgumentCaptor.forClass(clazz);
			verify(mongoTemplate, verificationMode).save(saveCaptor.capture());
			return Document.parse(gsonBuilder().toJson(saveCaptor.getValue()));
		}

		@Override
		public Document execute(MongoTemplate mongoTemplate,
								Class<?> clazz,
								VerificationMode verificationMode,
								String collectionName) {

			final ArgumentCaptor<?> saveCaptor = ArgumentCaptor.forClass(clazz);
			verify(mongoTemplate, verificationMode).save(saveCaptor.capture(), eq(collectionName));
			return Document.parse(gsonBuilder().toJson(saveCaptor.getValue()));
		}
	};

	private static Document recoverFieldsFromQueryAndUpdateCaptors() {

		final Document responseDoc = queryCaptor.getValue().getQueryObject();

		final Collection<?> updateValues = updateCaptor.getValue().getUpdateObject().values();
		updateValues.forEach(doc -> responseDoc.putAll((Document) doc));

		return responseDoc;
	}

	public static final String DEFAULT_KEY_ID = "_id";
	private static final ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
	private static final ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
	private static final ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);

	public abstract Document execute(MongoTemplate mongoTemplate, Class<?> clazz, VerificationMode verificationMode);

	public abstract Document execute(MongoTemplate mongoTemplate,
									 Class<?> clazz,
									 VerificationMode verificationMode,
									 String collectionName);

}
