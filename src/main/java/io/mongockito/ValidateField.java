package io.mongockito;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.tuple.Pair;

@Value
@Builder
public class ValidateField {

	ValidationType validationType;
	Pair<?, ?> field;

}
