# Chair Company Shared Messaging Library

This library is meant to be used in order a) reduce boiler plate in our services and b) enforce that are services are communicated using the same 'language' and 'grammar'.

> In general, shared libraries in microservices which do something like this are fine, but under no circuimstances should there be shared business logic

### Usage

This lib is intended to be used as part of your 'local maven' repository. To use it, add the following two items to your `build.gradle`:

* Within your `repositories` block: `mavenLocal()`
* Within your `dependencies` block: `implementation 'event.club:chair-messages:0.1.0-SNAPSHOT'`

### Building and publishing

If this is your first time running this application, the first time you want to use the library, or you've just made changes... you must do the following:

* `./gradlew clean build` -> should build a .jar in `chair-messages-lib/build/libs`
* `./gradlew pTML` -> short for "publishToMavenLocal", will put the jar in your local maven repository `~/.m2`

At this point, your service could should be able to resolve the lib and make use of it.