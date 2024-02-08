package io.mongockito.util.json.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	@Override
	public void write(final JsonWriter out, final LocalDateTime value) throws IOException {

		if (Objects.isNull(value)) {
			out.nullValue();
		} else {
			out.value(formatter.format(value));
		}
	}

	@Override
	public LocalDateTime read(final JsonReader in) throws IOException {

		if (in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		} else {
			final String dateString = in.nextString();
			return LocalDateTime.parse(dateString, formatter);
		}
	}
}
