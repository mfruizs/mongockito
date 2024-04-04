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

	public static final Adapter DEFAULT_OBJECT_ID_ADAPTER = Adapter.builder()
		.typeClass(ObjectId.class)
		.typeAdapter(new ObjectIdAdapter())
		.build();

	public static final Adapter DEFAULT_LOCAL_DATE_TIME_ADAPTER = Adapter.builder()
		.typeClass(LocalDateTime.class)
		.typeAdapter(new LocalDateTimeAdapter())
		.build();

	private final List<Adapter> adapters = new ArrayList<>();
	private boolean allowNulls = true;

	public static Gson gsonBuilder() {
		return createGsonBuilder().create();
	}

	public static void addAdapters(final List<Adapter> newAdapters) {
		if (newAdapters != null) {
			adapters.clear();
			adapters.addAll(newAdapters);
		}
	}

	public static void allowSerializeNulls(final boolean allow) {
		allowNulls = allow;
	}

	public static void reset() {
		adapters.clear();
		allowNulls = true;
	}

	private static GsonBuilder createGsonBuilder() {

		final GsonBuilder gsonBuilder = new GsonBuilder().setFieldNamingStrategy(JsonTool::obtainFieldNaming);

		addAdaptersToBuilder(gsonBuilder);

		if (allowNulls) {
			gsonBuilder.serializeNulls();
		}

		return gsonBuilder;
	}

	private static void addAdaptersToBuilder(final GsonBuilder gsonBuilder) {

		final List<Adapter> defaultOrAdapters = obtainJsonAdapters();

		defaultOrAdapters.forEach(adapter -> addAdapterToBuilder(gsonBuilder, adapter));
	}

	private static List<Adapter> obtainJsonAdapters() {

		return adapters.isEmpty()
			? List.of( DEFAULT_OBJECT_ID_ADAPTER, DEFAULT_LOCAL_DATE_TIME_ADAPTER)
			: adapters;
	}

	private static void addAdapterToBuilder(final GsonBuilder gsonBuilder, final Adapter adapter) {

		gsonBuilder.registerTypeAdapter(adapter.getTypeClass(), adapter.getTypeAdapter());
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

}
