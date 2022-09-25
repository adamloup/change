# Change

A Java library to resolve changes (additions, alterations, deletions) from some state to another state. It began as a
need to persist changes made to JPA entities outside the Persistence Context. For example, a desktop application
where a collection entities (or some projection) is altered and then passed back to the persistence context.   

## Usage

```java
        
        List<String> original = List.of("five", "one", "three");

        List<String> incoming = List.of("one", "two", "three", "four", "five", "six", "seven");

        
        ChangeResolver.Changes<String, String> changes = ChangeResolver.<String>simple()
                .resolve(original, incoming);
```