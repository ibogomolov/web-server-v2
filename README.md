WebServer v2
============

The 2nd implementation of the multi-threaded (e.g. file-based) web server with thread-pooling in Java.

## Building

This repository uses Gradle for all the tasks. There is no need to install it thanks to the wrapper.

- Build the project.
```shell
./gradlew build
```

## Running locally

1. Prepare a directory with some files to access. Copy its absolute path. A path relative to the application's root directory works too.


2. Start the server using your preferred port and root configuration params.
```shell
./gradlew run --args='-port 80 -root /Users/ibogomolov/workspace/dev-root'
```

3. Access files via localhost in the browser or another tool.

> Make sure to use http protocol, https is not supported. 