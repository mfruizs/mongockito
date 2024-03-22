# Complete Example

> This is a complete example where we are validating `save` and `findOne` operations

## Entity class

* Entity with all the fields that represent the `MyCollection` collection.

```java
@Entity
@Value
@Document("MyCollection")
@Builder(toBuilder = true)
public class MyEntity {

    @Id
    @Field(name = "_id")
    String id;
    
    @Field("fooCode")
    int code;
    
    @Field("desc")
    String description;
    
    boolean active;

}

```
## Repository class

* All allowed operations that are implemented for this database `MyMongoDB` with the `MyCollection` collection.

```java

import org.springframework.stereotype.Repository;

@Repository
public class MongoRepository {

    private static  final String MONGO_DATABASE_NAME = "MyMongoDB";
    private final MongoTemplate mongoTemplate;
    
    // We have defined several Databases with their own collections
    public MongoRepository(@Qualifier(MONGO_DATABASE_NAME) final MongoTemplate mongoTemplate) {
        
        this.webappMongoTemplate = mongoTemplate;
    }


    public Optional<MyEntity> findById(final String id) {

        final Query query = new Query();

        query.addCriteria(where("_id").is(id));

        return Optional.ofNullable(this.mongoTemplate.findOne(query, MyEntity.class));
    }

    public MyEntity saveEntity(final MyEntity myEntity) {

        return this.mongoTemplate.save(myEntity);
    }

}
```


## Test class

* All tests necessary to validate these operations.

```java
import io.mongockito.ValidationType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@ExtendWith(MockitoExtension.class)
class MongoRepositoryTest {

    private static final String DEFAULT_KEY_ID = "_id";
    private static final String ID_FIELD = new ObjectId().toHexString();
    
    @Mock(name = "database")
    private MongoTemplate mongoTemplate;
    
    @InjectMocks
    private MongoRepository sut;
    
    @Test
    void should_find_item_by_code_in_my_collection_correctly() {
    
        this.sut.findById( ID_FIELD );
    
        Verify.builder()
            .addMongoOperation( Operation.FIND_ONE )
            .addClass( MyEntity.class )
            .addValidation( ValidationType.EQUALS, DEFAULT_KEY_ID, ID_FIELD )
            .verify( this.mongoTemplate );
    
    }

    @Test
    void should_throw_error_on_find_item_by_code_in_my_collection() {
    
        this.sut.findByFooCode( null );
        
        assertThatThrownBy(() -> Verify.builder()
            .addMongoOperation( Operation.FIND_ONE )
            .addClass( MyEntity.class )
            .addValidation( ValidationType.NOT_NULL, DEFAULT_KEY_ID )
            .verify( this.mongoTemplate ))
        .isInstanceOf(AssertionError.class);
    
    }
    
    @Test
    void should_save_item_in_my_collection_correctly() {
    
        MyEntity myEntity = MyEntity.builder()
            .id( ID_FIELD )
            .fooCode( "bar" )
            .description( "lore ipsum" )
            .active( true )
            .build();
    
        this.sut.save( myEntity );
    
        Verify.builder()
            .addMongoOperation(Operation.SAVE)
            .addClass( MyEntity.class )
            .addValidation( ValidationType.JSON, myEntity )
            .addValidation( ValidationType.EQUALS, "active", true )
            .verify( this.mongoTemplate );
    
    }

    @Test
    void should_validate_field_one_by_one_on_save_operation_correctly() {
    
        MyEntity myEntity = MyEntity.builder()
        .id( ID_FIELD )
        .fooCode( "bar" )
        .description( "lore ipsum" )
        .active( true )
        .build();
        
        this.sut.save( myEntity );
        
        Verify.builder()
            .addMongoOperation(Operation.SAVE)
            .addClass( MyEntity.class )
            .addValidation( ValidationType.EQUALS, "fooCode", "bar" )
            .addValidation( ValidationType.NOT_NULL, "desc" )
            .addValidation( ValidationType.EQUALS, "active", true )
        .verify( this.mongoTemplate );
    
    }


    @Test
    void should_throw_error_on_save_item_in_my_collection() {
    
        MyEntity myEntity = MyEntity.builder()
            .id( ID_FIELD )
            .fooCode( "bar" )
            .description( "lore ipsum" )
            .active( true )
            .build();
        
        this.sut.save(myEntity);
        
        assertThatThrownBy(() -> Verify.builder()
            .addMongoOperation(Operation.SAVE)
            .addClass( MyEntity.class )
            .addValidation( ValidationType.EQUALS, "active", false )
            .verify( this.mongoTemplate ))
        .isInstanceOf(AssertionError.class);

    }

}

```

## Creating an adapter method

* With **JSON** validation maybe you need to implement your own adapters using 'addAdapter' method

```java 
    @Test
    void should_save_item_in_my_collection_correctly() {
    
        MyEntity myEntity = MyEntity.builder()
            .id( ID_FIELD )
            .fooCode( "bar" )
            .description( "lore ipsum" )
            .active( true )
            .build();
    
        this.sut.save( myEntity );
    
        Verify.builder()
            .addMongoOperation(Operation.SAVE)
            .addClass( MyEntity.class )
            .addValidation( ValidationType.JSON, myEntity )
            .addAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .verify( this.mongoTemplate );
    
    }
```

* **LocalDateTimeAdapter** definition:
```java
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

	// Here, we are defining the iso format for date objects
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
```