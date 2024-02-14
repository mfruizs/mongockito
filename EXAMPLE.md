# Complete Example

> This is a complete example where we are validating `save` and `findOne` operations

## Entity class

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
    
        this.sut.findByFooCode( ID_FIELD );
    
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
            .addValidation( ValidationType.JSON, MyEntity.class, myEntity )
            .addValidation( ValidationType.EQUALS, "actiue", true )
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