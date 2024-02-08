package io.mongockito.util.json;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mongockito.util.json.adapters.LocalDateTimeAdapter;
import io.mongockito.util.json.adapters.ObjectIdAdapter;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@UtilityClass
public class JsonTool {

	public static final Gson GSON = new GsonBuilder()
		.setFieldNamingStrategy(JsonTool::obtainFieldNaming)
		.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
		.registerTypeAdapter(ObjectId.class, new ObjectIdAdapter())
		.serializeNulls() // TODO configurable
		.create();

	private static String obtainFieldNaming(final java.lang.reflect.Field f) {

		final Field fieldAnnotation = f.getAnnotation(Field.class);
		if (fieldAnnotation != null && fieldAnnotation.targetType().equals(FieldType.IMPLICIT)) {
			return isEmpty(fieldAnnotation.value())
				? fieldAnnotation.name()
				: fieldAnnotation.value();
		}
		return f.getName();
	}

}
