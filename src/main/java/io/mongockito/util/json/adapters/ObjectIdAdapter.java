package io.mongockito.util.json.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Objects;
import org.bson.types.ObjectId;

public class ObjectIdAdapter extends TypeAdapter<ObjectId> {

	@Override
	public void write(final JsonWriter out, final ObjectId value) throws IOException {

		if (Objects.isNull(value)) {
			out.nullValue();
		} else {
			out.value(value.toString());
		}

	}

	@Override
	public ObjectId read(final JsonReader in) throws IOException {

		throw new IOException("Need to work!");
	}
}
