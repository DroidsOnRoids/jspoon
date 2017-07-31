jspoon Retrofit Converter
===============

A Retrofit `Converter` which uses jspoon for parsing body from HTML.

A default `Jspoon` instance will be created or one can be configured and passed to
`JspoonConverterFactory.create()` to further control the convertion.


Installation
--------
Insert the following dependency to `build.gradle` file of your project:
```gradle
dependencies {
    compile 'pl.droidsonroids.retrofit2:converter-jspoon:1.0.0'
}
```
Example
--------
[Kotlin + RxJava2](https://github.com/DroidsOnRoids/jspoon/tree/master/advanced-example)
