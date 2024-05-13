# Mongockito
Java library for Mongodb repository unit testing.

### Description
Currently, there is no library that allows you to build an H2-style mongodb in memory.

Starting from the premise in which the database is correctly configured, using the mockito library and based on 
[ArgumentCaptor](https://www.javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/ArgumentCaptor.html), 
we can validate that the fields sent to mongodb are the expected ones.

## Prerequisites

* **Java:** 17 
* **Spring-boot based:** 3.2.2
> **NOTE:** There are a compile error with Java 21, but it's compatible with java 21 projects.

## Installation

```xml
    <dependency>
        <groupId>io.mongockito</groupId>
        <artifactId>mongockito</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>
```

### Releases:

* [Mongockito release packages](https://github.com/mfruizs/mongockito/packages)

| Release      | Date     | Information   |
|:-------------|----------|---------------|
| 1.0-SNAPSHOT | 2-may-24 | First release |

## Validations

### Operation
| Operation       |                                                                                                                              Spring Documentation                                                                                                                               |
|:----------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| FIND            |                                      [find](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoTemplate.html#find(org.springframework.data.mongodb.core.query.Query,java.lang.Class))                                       |
| FIND_ONE        |                                   [findOne](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoTemplate.html#findOne(org.springframework.data.mongodb.core.query.Query,java.lang.Class))                                    |
| FIND_BY_ID      |                                                   [findById](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoTemplate.html#findById(java.lang.Object,java.lang.Class))                                                   |
| FIND_AND_REMOVE |                             [findAndRemoce](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoTemplate.html#findAndRemove(org.springframework.data.mongodb.core.query.Query,java.lang.Class))                              |
| UPDATE_FIRST    | [updateFirst](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoTemplate.html#updateFirst(org.springframework.data.mongodb.core.query.Query,org.springframework.data.mongodb.core.query.UpdateDefinition,java.lang.Class)) |
| UPDATE_MULTI    | [updateMulti](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoTemplate.html#updateMulti(org.springframework.data.mongodb.core.query.Query,org.springframework.data.mongodb.core.query.UpdateDefinition,java.lang.Class)) |
| UPSERT          |      [upsert](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoTemplate.html#upsert(org.springframework.data.mongodb.core.query.Query,org.springframework.data.mongodb.core.query.UpdateDefinition,java.lang.Class))      |
| SAVE            |                                                                      [save](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoTemplate.html#save(T))                                                                       |

### ValidationType

| Validation      | Description                                                                                 |
|:----------------|:--------------------------------------------------------------------------------------------|
| EQUALS          | Validates that the value of a field is as expected                                          |
| NOT_NULL        | Validates that the field is not null                                                        |
| NULL            | Validates that the field is null                                                            |
| COLLECTION_SIZE | Validates that collection has expected number of elements                                   |
| JSON            | Validates that the json to be inserted in the collection is equal to the input object       |
| JSON_BY_KEY     | Validate that a part of json to be inserted in the collection is equal to comparable object |

### Alternative validations 
* There are the following types of validations

|    Simplified Method    | ValidationType  |
|:-----------------------:|:---------------:|
|     validatesEquals     |     EQUALS      |
|    validatesNotNull     |    NOT_NULL     |
|      validatesNull      |      NULL       |
| validatesCollectionSize | COLLECTION_SIZE |
|      validatesJson      |      JSON       |
|   validatesJsonByKey    |   JSON_BY_KEY   |

> NOTE: all this functions can be replaced by one single function with parameters, 
> see [VerifyTest](./src/test/java/io/mongockito/VerifyTest.java) class for more examples

```java
    // use this
    .validates(ValidationType.NULL, NULLABLE_FIELD_NAME)
    // instead of him own method
    .validatesNull(NULLABLE_FIELD_NAME)
```


### Other configuration attributes

* **addVerificationMode:** 
  * Allows verifying that certain behavior happened at least once / exact number of times / never. E.g: ([MockitoDoc](https://www.javadoc.io/doc/org.mockito/mockito-core/2.2.6/org/mockito/verification/VerificationMode.html))
* **fromCollection:**
  * Optional parameter used to indicate the mongo collection name as an attribute instead of being embedded inside the entity bean using the @Document annotation.
* **addAdapter:**
  * With this operation we can add gson TypeAdapter class ([Gson](https://www.javadoc.io/doc/com.google.code.gson/gson/2.8.1/com/google/gson/TypeAdapter.html))
* **allowSerializeNulls:**
  * This parameter will indicate if the data used in the mongo operation aggregates the entity null fields


## Examples:

> To see a complete example, follow this [documentation](./EXAMPLE.md)

* The following example will validate MongoDB save operation of a collection item defined by identifier, 
and will check that the attached fields have been sent to the DB as intended.

```java
    public static final String ID_FIELD = new ObjectId().toHexString();

    Verify.that()
        .thisOperation( Operation.SAVE )
        .ofClass( EntityExample.class )
        .validatesEquals( "_Id", ID_FIELD )
        .validatesEquals( "boolean_field", true )
        .validatesEquals( "string_field", "name" )
        .validatesNull( "one_field" )
        .validatesNotNull( "another_field" )
        .run( <mongoTemplate> );

```

* This is an example to validate no field is missing on save operation

```java
    public static final String ID_FIELD = new ObjectId().toHexString();
    public static final LocalDateTime LOCAL_DATE_TIME_NOW = LocalDateTime.now();

    // Item sent on save operation and want to check no field is missing
    final EntityExample entityExample = EntityExample.builder()
        .id( ID_FIELD )
        .month( "02" )
        .locked( Boolean.TRUE )
        .creationUser( "User_a" )
        .creationTimestamp( LOCAL_DATE_TIME_NOW )
        .lastUpdateUser( "User_b" )
        .lastUpdateTimestamp( LOCAL_DATE_TIME_NOW.plusDays( INTEGER_ONE ) )
        .entityExampleMap( createFieldMap() )
        .build();
	
    // Validation
    Verify.that()
        .thisOperation( Operation.SAVE )
        .ofClass( EntityExample.class )
        .validatesJson( EntityExample.class,  entityExample )
        .addVerificationMode(times(INTEGER_ONE))
        .run( <mongoTemplate> );
	
```

* In this example we validate with specific methods for the save operation

```java
    public static final String ID_FIELD = new ObjectId().toHexString();
    public static final LocalDateTime LOCAL_DATE_TIME_NOW = LocalDateTime.now();

    // Item sent on save operation and want to check no field is missing
    final EntityExample entityExample = EntityExample.builder()
        .id( ID_FIELD )
        .month( "02" )
        .locked( Boolean.TRUE )
        .creationUser( "User_a" )
        .creationTimestamp( LOCAL_DATE_TIME_NOW )
        .lastUpdateUser( "User_b" )
        .lastUpdateTimestamp( LOCAL_DATE_TIME_NOW.plusDays( INTEGER_ONE ) )
        .entityExampleMap( createFieldMap() )
        .build();
	
    // Validation
    Verify.that()
        .thisOperation( Operation.SAVE )
        .ofClass( EntityExample.class )
        .fromCollection( "document_collection_example_name" ) // instead of use @Document in Entity Bean
        .validatesJson(entityExample)
        .validatesJsonByKey(ENTITY_EXAMPLE_MAP, entityExample.getEntityExampleMap())
        .validatesNull(NULLABLE_VALUE_FIELD)
        .validatesNotNull(DEFAULT_KEY_ID)
        .validatesEquals(DEFAULT_KEY_ID, entityExample.getId())
        .validatesCollectionSize(ENTITY_EXAMPLE_LIST, entityExample.getEntityExampleList().size())
        .run( <mongoTemplate> );
	
```