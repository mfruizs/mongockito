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

## Examples:

> To see a complete example, follow this [documentation](./EXAMPLE.md)

* The following example will validate MongoDB save operation of a collection item defined by identifier, 
and will check that the attached fields have been sent to the DB as intended.

```java
    public static final String ID_FIELD = new ObjectId().toHexString();

    Verify.builder()
        .addOperation( Operation.SAVE )
        .addClass( EntityExample.class )
        .addValidation( ValidationType.EQUALS,   "_Id", ID_FIELD )
        .addValidation( ValidationType.EQUALS,   "boolean_field", true )
        .addValidation( ValidationType.EQUALS,   "string_field", "name" )
        .addValidation( ValidationType.NULL,     "one_field" )
        .addValidation( ValidationType.NOT_NULL, "another_field" )
        .verify( <mongoTemplate> );

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
    Verify.builder()
        .addOperation( Operation.SAVE )
        .addClass( EntityExample.class )
        .addValidation( ValidationType.JSON, EntityExample.class,  entityExample )
        .addVerificationMode(times(INTEGER_ONE))
        .verify( <mongoTemplate> );
	
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
    Verify.builder()
        .addOperation( Operation.SAVE )
        .addClass( EntityExample.class )
        .validateJson(entityExample)
        .validateJsonByKey(ENTITY_EXAMPLE_MAP, entityExample.getEntityExampleMap())
        .validateNull(NULLABLE_VALUE_FIELD)
        .validateNotNull(DEFAULT_KEY_ID)
        .validateEquals(DEFAULT_KEY_ID, entityExample.getId())
        .validateCollectionSize(ENTITY_EXAMPLE_LIST, entityExample.getEntityExampleList().size())
        .verify( <mongoTemplate> );
	
```