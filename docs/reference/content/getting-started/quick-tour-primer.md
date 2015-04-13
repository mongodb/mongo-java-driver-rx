+++
date = "2015-03-17T15:36:56Z"
title = "Quick Tour Primer"
[menu.main]
  parent = "Getting Started"
  identifier = "Primer"
  weight = 20
  pre = "<i class='fa'></i>"
+++

# Quick Tour Primer

The following code snippets come from the `SubscriberHelpers.java` example code
that can be found with the [examples source]({{< srcref "examples/tour/src/main/tour/SubscriberHelpers.java">}}).

{{% note %}}
See the [installation guide]({{< relref "getting-started/installation-guide.md" >}})
for instructions on how to install the MongoDB RxJava Driver.
{{% /note %}}

## Reactive Extensions

This library provides support for [ReactiveX (Reactive Extensions)](http://reactivex.io/) by using the 
[RxJava](https://github.com/ReactiveX/RxJava) library.  All database calls return an `Observable` which an `Observer` can subscribe to. 
That `Observer` reacts to whatever item or sequence of items the `Observable` emits.  This pattern facilitates concurrent operations 
because it does not need to block while waiting for the `Observable` to emit objects, but instead it creates a sentry in the form of 
an `Observer` that stands ready to react appropriately at whatever future time the `Observable` does so.

For more information about Reactive Extensions and Observables go to: [http://reactivex.io](http://reactivex.io/).

## From Async Callbacks to Observables

The MongoDB RxJava Driver is built upon the MongoDB Async driver which is callback driven.
The API mirrors the Async driver API and any methods that cause network IO return either a `Observable<T>` or a `MongoObservable<T>` 
where `T` is the type of response for the operation.  
The exception to that rule is for methods in the async driver that return a `Void` value in the callback. 
As an `Observable<Void>` is generally considered bad practise, in these circumstances we
return a [`Observable<Success>`]({{< apiref "com/mongodb/reactivestreams/client/Success.html">}}) for the operation.

### MongoObservable

In RxJava `Observable` is not an interface, so where the MongoDB Async Driver API follows a fluent interface pattern we return a 
`MongoObservable<T>`.  The `MongoObservable<T>` mirrors the underlying fluent API and provides two extra methods:
 
1. `toObservable()` 

    Returns an `Observable<T>` instance for the operation.

2.
method to convert into an `Observable`.

{{% note %}}
All [`Observables`](http://www.reactive-streams.org/reactive-streams-1.0.0.RC4-javadoc/?org/reactivestreams/Publisher.html) returned 
from the API are cold, meaning that nothing happens until they are subscribed to. As such an observer is guaranteed to see the whole 
sequence from the beginning. So just creating an `Observable` won't cause any network IO, and it's not until `Subscriber.request()` is called 
that the driver executes the operation.

Publishers in this implementation are unicast. Each [`Subscription`](http://reactivex.io/RxJava/javadoc/rx/Subscription.html) 
to an `Observable` relates to a single MongoDB operation and it's ['Subscriber'](http://reactivex.io/RxJava/javadoc/rx/Subscriber.html)  
will receive it's own specific set of results. 
{{% /note %}}

## Subscribers used in the Quick Tour

For the Quick Tour we use RxJava's [TestSubscriber<T>](http://reactivex.io/RxJava/javadoc/rx/observers/TestSubscriber.html) and although 
this is an artificial scenario for reactive extensions we generally block on the results of one example before starting the next, so as to 
ensure the state of the database.  [`SubscriberHelpers.java`]({{< srcref "examples/tour/src/main/tour/SubscriberHelpers.java">}}) provides
two static helpers:

1.  printSubscriber

    Prints each value emitted by the `Observable`, along with an optional initial message.

2.  printDocumentSubscriber

    Prints the json version of each `Document` emitted but the `Observable`.


##  Blocking and non blocking examples

As the `TestSubscriber` contains a latch that is only released when the `onCompleted` method of the `Subscriber` is called, 
we can use that latch  to block on by calling the `subscriber.awaitTerminalEvent()` method.  Below are two examples using our auto-requesting `PrintDocumentSubscriber`.  
The first is non-blocking and the second blocks waiting for the `Publisher` to complete:

```java
// Create a publisher
Observable<Document> observable = collection.find().toObservable();

// Non blocking
observable.subscribe(printDocumentSubscriber());

Subscriber<Document> subscriber = printDocumentSubscriber();
observable.subscribe(subscriber);
subscriber.awaitTerminalEvent(); // Block for the publisher to complete
```
