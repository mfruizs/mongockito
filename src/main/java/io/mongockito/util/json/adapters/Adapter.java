package io.mongockito.util.json.adapters;

import java.lang.reflect.Type;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Adapter {
	Type typeClass;
	Object typeAdapter;
}
