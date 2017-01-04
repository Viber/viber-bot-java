# Viber Java Bot API
Use this library to develop a bot for Viber platform.
The library is available on [GitHub](https://github.com/Viber/viber-bot-java) as well as [maven central](http://central.maven.org/maven2/com/viber/viber-bot/).

## License
This library is released under the terms of the Apache 2.0 license. See [License](https://github.com/Viber/viber-bot-java/blob/master/LICENSE.md) for more information.

## Library Prerequisites
* Java >= 8
* Get your Viber Public Account authentication token. Your token is generated and provided to you during the Public Account creation process. As a Public Account admin, you can always find the account token in the "edit info" page.
* SSL Certification - You'll need a trusted (ca.pem) certificate, not self-signed. You can find one at [Let's Encrypt](https://letsencrypt.org/) or buy one.

## Installation
This library is released on [maven central](http://central.maven.org/maven2/com/viber/viber-bot/).

### Gradle

```
compile group: 'com.viber', name: 'viber-bot', version: '1.0.5'
```

### Maven

```
<dependency>
    <groupId>com.viber</groupId>
    <artifactId>viber-bot</artifactId>
    <version>1.0.5</version>
</dependency>
```

## Documentation
### JavaDocs
All public APIs are documented with JavaDocs which can be found in the [GitHub repository](http://htmlpreview.github.io/?https://github.com/Viber/viber-bot-java/blob/master/docs/index.html).

### Sample projects
We've created two sample projects to help you get started.
- [Spring-Boot Sample](https://github.com/Viber/viber-bot-java/tree/master/spring-boot-sample) using [spring-boot-starter-web](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-starters/spring-boot-starter-web) package,
- and [NanoHTTPd Sample](https://github.com/Viber/viber-bot-java/tree/master/nano-httpd-sample/) with a tiny embeddable http server, called [NanoHTTPd](https://github.com/NanoHttpd/nanohttpd).

### A simple overview

```java
public void botExample() {
    ViberBot bot = new ViberBot(new BotProfile("SampleBot", "http://viber.com/avatar.jpg"), "YOUR_AUTH_TOKEN_HERE");
    bot.onMessageReceived((event, message, response) -> response.send(message));

    // somewhere else in your web server of choice:
    bot.incoming(Request.fromJsonString("..."));
}
```

You can chose to use any web server or framework you like. All you need to do is call the API with -

```java
bot.incoming(Request.fromJsonString("...")); // or
bot.incoming(Request.fromInputStream(inputStream));
```

### Should I be concerned with synchronizing my web server threads? Is this library thread-safe?
The Viber bot library is *thread-safe* and highly concurrent. You do not have to worry about synchronizing anything.

All calls to `ViberBot#incoming()` go through a `BlockingQueue`, and ordering is retained.
All I/O calls are directly executed on the same thread they were initially called on.

### Can I make I/O calls asynchronous and still retain thread-safety for my bot?
Yes. You can pass a system property to control the I/O thread pool:

`com.viber.bot.executor.strategy=[DIRECT|THREAD]` (default is DIRECT)

`com.viber.bot.executor.threads=N` (default is `getRuntime().availableProcessors()`)

* Note: This will not change the way you use the library. You still don't have to synchronize anything.

### Do you supply a basic router for text messages?
Well funny you ask. Yes we do. But a word of warning - messages sent to your router callback will also be emitted to the `ViberBot#onMessageReceived` event.

```java
public void botTextRouterExample() {
    bot.onTextMessage("(hi|hello)", (event, message, response) -> response.send("Hi " + event.getSender().getName()));
}
```

## Community
Join the conversation on **[Gitter] (https://gitter.im/viber/bot-java)**.
