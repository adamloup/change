# Change

A Java library to resolve changes (additions, alterations, deletions) from some state to another state. It began as a
need to persist changes made to JPA entities outside of the Persistence Context. For example, a desktop application
where a collection entities (or some projection) is altered and then passed back to the persistence context.   