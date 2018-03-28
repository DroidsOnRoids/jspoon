[![Maven Central](https://maven-badges.herokuapp.com/maven-central/pl.droidsonroids/jspoon/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/pl.droidsonroids/jspoon)
[![Javadocs](https://javadoc.io/badge/pl.droidsonroids/jspoon.svg?color=blue)](https://javadoc.io/doc/pl.droidsonroids/jspoon)

# jspoon
jspoon is a Java library that provides parsing HTML into Java objects basing on CSS selectors. It uses [jsoup][jsoup] underneath as a HTML parser.

## Installation
Insert the following dependency into your project's `build.gradle` file:
```gradle
dependencies {
    compile 'pl.droidsonroids:jspoon:1.3.0'
}
```
## Usage
jspoon works on any class with a default constructor. To make it work you need to annotate fields with `@Selector` annotation and set a CSS selector as the annotation's value:
```java
class Page {
    @Selector("#title") String title;
    @Selector("li.a") List<Integer> intList;
    @Selector(value = "#image1", attr = "src") String imageSource;
}
```
Then you can create a `HtmlAdapter` and use it to build objects:
```java
String htmlContent = "<div>" 
    + "<p id='title'>Title</p>" 
    + "<ul>"
    + "<li class='a'>1</li>"
    + "<li>2</li>"
    + "<li class='a'>3</li>"
    + "</ul>"
    + "<img id='image1' src='image.bmp' />"
    + "</div>";

Jspoon jspoon = Jspoon.create();
HtmlAdapter<Page> htmlAdapter = jspoon.adapter(Page.class);

Page page = htmlAdapter.fromHtml(htmlContent);
//title = "Title"; intList = [1, 3]; imageSource = "image.bmp"
```
It looks for the first occurrence in HTML and sets its value to a field.

### Supported types
`@Selector` can be applied to any field of the following types (or their primitive equivalents):
* `String`
* `Boolean`
* `Integer`
* `Long`
* `Float`
* `Double`
* `Date`
* `BigDecimal`
* Jsoup's `Element`
* Any class with  default constructor
* `List` (or its superclass/superinterface) of supported type

It can also be used with a class, then you don't need to annotate every field inside it.

### Attributes
By default, the HTML's `textContent` value is used on Strings, Dates and numbers. It is possible to use an attribute by setting an `attr` parameter in the `@Selector` annotation. You can also use `"html"` (or `"innerHtml"`) and `"outerHtml"` as `attr`'s value.

### Formatting and regex
Regex can be set up by passing `regex` parameter to `@Selector` annotation. Example:
```java
class Page {
    @Selector(value = "#numbers", regex = "([a-z]+),") String matchedNumber;
}
```
Date format can be set up by passing `value` parameter to `@Format` annotation. Example:
```java
class Page {
    @Format(value = "HH:mm:ss dd.MM.yyyy")
    @Selector(value = "#date") Date date;
}
```
```java
String htmlContent = "<span id='date'>13:30:12 14.07.2017</span>"
    + "<span id='numbers'>ONE, TwO, three,</span>";
Jspoon jspoon = Jspoon.create();
HtmlAdapter<Page> htmlAdapter = jspoon.adapter(Page.class);
Page page = htmlAdapter.fromHtml(htmlContent);//date = Jul 14, 2017 13:30:12; matchedNumber = "three";
```

Java's `Locale` is used for parsing Floats, Doubles and Dates. You can override it by setting `languageTag` @Format parameter:
```java
@Format(languageTag = "pl")
@Selector(value = "div > p > span") Double pi; //3,14 will be parsed 
```
If jspoon doesn't find a HTML element it wont't set field's value unless you set the `defValue` parameter:
```java
@Selector(value = "div > p > span", defValue = "NO_TEXT") String text;
```

### Retrofit
Retrofit converter is available [here][retrofit-converter].

### Other libraries/inspirations
* [jsoup][jsoup] - all HTML parsing in jspoon is made by this library
* [webGrude][webGrude] - when I had an idea I found this library. It was the biggest inspiration and I used some ideas from it
* [Moshi][Moshi] - I wanted to make jspoon work with HTML the same way as Moshi works with JSON. I adapted caching mechanism (fields and adapters) from it.
* [jsoup-annotations][jsoup-annotations] - similar to jspoon

[//]: #
   [jsoup]: <https://jsoup.org/>
   [webGrude]: <https://github.com/beothorn/webGrude>
   [Moshi]: <https://github.com/square/moshi>
   [jsoup-annotations]: <https://github.com/fcannizzaro/jsoup-annotations>
   [retrofit-converter]: <https://github.com/DroidsOnRoids/jspoon/tree/master/retrofit-converter-jspoon>
