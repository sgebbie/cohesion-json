# Cohesion Framework - JSON Library

This library provides a standalone Java JSON implementation.

One of the key features it to support streaming of JSON values. This means that
the parser will emit JSON values when they are available, but that the sender
can simply send well formed JSON values back-to-back.

## Build

This library has been pulled out of the non-public Cohesion framework so the
build is still, unfortunately, a little untidy.

```bash
$ gradle
$ ls -al build/libs/cohesion-json-deploy.jar
```
