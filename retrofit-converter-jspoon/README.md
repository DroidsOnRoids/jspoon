[![Maven Central](https://maven-badges.herokuapp.com/maven-central/pl.droidsonroids.retrofit2/converter-jspoon/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/pl.droidsonroids/jspoon)
[![Javadocs](https://javadoc.io/badge/pl.droidsonroids/jspoon.svg?color=blue)](https://javadoc.io/doc/pl.droidsonroids.retrofit2/converter-jspoon)

jspoon Retrofit Converter
===============

A Retrofit `Converter` which uses [jspoon](https://github.com/DroidsOnRoids/jspoon) for parsing body from HTML.

A default `Jspoon` instance will be created or one can be configured and passed to
`JspoonConverterFactory.create()` to further control the conversion.


Installation
--------
Insert the following dependency to `build.gradle` file of your project:
```gradle
dependencies {
    compile 'pl.droidsonroids.retrofit2:converter-jspoon:1.3.0'
}
```
Example
--------
[Java/Kotlin + RxJava2](https://github.com/DroidsOnRoids/jspoon/tree/master/advanced-example)
