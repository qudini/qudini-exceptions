# qudini-exceptions

[![CircleCI](https://circleci.com/gh/qudini/qudini-exceptions.svg?style=svg)](https://circleci.com/gh/qudini/qudini-exceptions)

Utilities for exception handling and general case common exception types.

Provides exceptions such as:

* `ExhaustedConditionException`
* `IllegalDataModelException`
* `InvalidCodePathException`
* ...and more.

Report errors but continue without throwing an exception. This is designed for
events whose breakages should be logged, but should not break execution flow.
```java
Exceptions.reportQuietly(
        Arrays.asList(
                new NewRelicReporter(),
                (message, exception) -> myOwnHandler(exception)
        ),
        () -> {
            doSomethingThatMayCrash1();
            doSomethingThatMayCrash2();
            return successfulResult;
        }
);
```

Report errors and then continue throwing the exception. This is designed for
exceptions that we want explicitly to be logged to services like NewRelic.
```java
Exceptions.reportAndRethrow(
        Arrays.asList(
                new NewRelicReporter(),
                (message, exception) -> myOwnHandler(exception)
        ),
        () -> {
            doSomethingThatMayCrash1();
            doSomethingThatMayCrash2();
            return successfulResult;
        }
);
```