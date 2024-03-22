package io.mongockito.util.json;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mongockito.util.json.adapters.Adapter;
import io.mongockito.util.json.adapters.LocalDateTimeAdapter;
import io.mongockito.util.json.adapters.ObjectIdAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@UtilityClass
public class JsonTool {

	private final List<Adapter> adapters = new ArrayList<>();
	private boolean allowNulls = true;

	public static Gson gsonBuilder() {

		return createGsonBuilder().create();
	}

	private static GsonBuilder createGsonBuilder() {

		final GsonBuilder gsonBuilder = new GsonBuilder().setFieldNamingStrategy(JsonTool::obtainFieldNaming);

		addAdaptersOnBuilder(gsonBuilder);

		if (allowNulls) {
			gsonBuilder.serializeNulls();
		}

		return gsonBuilder;
	}

	public static void addAdapters(final List<Adapter> newAdapters) {
		if (newAdapters != null) {
			adapters.clear();
			adapters.addAll(newAdapters);
		}
	}

	public static void clearAdapters() {

		adapters.clear();
	}

	private static void addAdaptersOnBuilder(final GsonBuilder gsonBuilder) {

		if (adapters.isEmpty()) {
			gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
			gsonBuilder.registerTypeAdapter(ObjectId.class, new ObjectIdAdapter());
		} else {
			adapters.forEach(adapter -> addAdapterToBuilder(gsonBuilder, adapter));
		}
	}

	private static void addAdapterToBuilder(final GsonBuilder gsonBuilder, final Adapter adapter) {

		gsonBuilder.registerTypeAdapter(adapter.getType(), adapter.getTypeAdapter());
	}

	private static String obtainFieldNaming(final java.lang.reflect.Field f) {

		final Field fieldAnnotation = f.getAnnotation(Field.class);
		if (fieldAnnotation != null && FieldType.IMPLICIT.equals(fieldAnnotation.targetType())) {
			return isEmpty(fieldAnnotation.value())
				? fieldAnnotation.name()
				: fieldAnnotation.value();
		}
		return f.getName();
	}

	public static void allowSerializeNulls(boolean allow) {

		allowNulls = allow;
	}


}
