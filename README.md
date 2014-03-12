RTB simple project using Akka
=================

## Quickstart
Just execute

```
mvn jetty:run
```

 and go to `http://localhost:9090/place-bid/simple-ad`

You will see bid result for your ad request.

You can change number of emulated bidders by providing parameter `-Dbidders`, by default there is 5 bidders:

```
mvn jetty:run -Dbidders = 10
```

To run  performance tests, stop any currently running jetty and execute:

```
mvn -Pembedded-jetty verify
```

This will:

* start an embedded jetty server wit a small webapp,
* run jmeter tests (just some http requests) against this webserver and
* create some nice graphs of the result (you will find them in `../target` and `../target/jmeter/results`).

Number of cuncurent threads are controlled by property `performancetest.threadCount`

Number of request, each thread(user) will execute  are controlled by property `performancetest.loopCount`

For example, to run 4 total requests in 2 threads execute:

```
mvn -Pembedded-jetty verify -Dperformancetest.threadCount=2 -Dperformancetest.loopCount=2
```

## JMeter GUI

To start the JMeter GUI, use the `jmeter:gui` goal. The tests are located in `/src/test/jmeter`. If you start the tests, make sure that the example webapp is running. You can start the webapp explicitly with `jetty:run`.

## JMeter Headless

To just execute the jmeter-tests from commandline (without gui, without embedded webapp, without graph-generation), use the `jmeter:jmeter` goal.
The results of the test-run can be found in `/target/jmeter/results`. If you want graph-generation, run `mvn verify` (without the "local" profile).



Prerequisites
-------------
* JDK 7.x
* Maven 3.x
* 

Credits
-------------

https://github.com/mlex/jmeter-maven-example

https://github.com/pofallon/jersey2-akka-java
