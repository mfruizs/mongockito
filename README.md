# Mongockito
Java library for unit testing of NoSQL mongodb

### Description
Currently, there is no library that allows to build a local bd in the H2 style of mongodb.

Starting from the premise in which the database is correctly configured, using the mockito library and based on the ArgumentCaptor, 
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

## Examples:

* The following example will validate MongoDB save operation of a collection item defined by identifier, 
and will check that the attached fields have been sent to the DB as intended.
```java
    public static final String ID_FIELD = new ObjectId().toHexString();

    Verify.builder()
        .addOperation(Operation.SAVE)
        .addClass(EntityExample.class)
        .addValidation(ValidationType.EQUALS, "_Id", ID_FIELD)
        .addValidation(ValidationType.EQUALS, "boolean_field", true)
        .addValidation(ValidationType.EQUALS, "String_field," "name")
        .addValidation(ValidationType.NOT_NULL, "another_field")
        .verify();

```