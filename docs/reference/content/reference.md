+++
date = "2015-03-18T16:56:14Z"
title = "Reference"
[menu.main]
  weight = 50
  pre = "<i class='fa fa-book'></i>"
+++

## Reference

The MongoDB RxJava Driver is built upon the [`MongoDB Async Driver`](http://mongodb.github.io/mongo-java-driver/3.1/driver-async/)
for detailed reference information see the [official documentation](http://mongodb.github.io/mongo-java-driver/3.1/driver-async/reference).

### MongoObservable

In RxJava `Observable` is not an interface, so where the MongoDB Async Driver API follows a fluent interface pattern we return a 
`MongoObservable<T>`.  The `MongoObservable<T>` mirrors the underlying fluent API and provides two extra methods:
 
1. `toObservable()` 

    Returns an `Observable<T>` instance for the operation.

2. `subscribe(Subscriber<? super TResult> subscriber)`

    Subscribes to the `Observable`.

### ObservableAdapter

Driver version 1.2.0 introduced the [`ObservableAdapter`]({{ apiref "com/mongodb/rx/client/ObservableAdapter" }}) interface. 
This provides a simple way to adapt all `Observable`s returned by the driver.  On such use case might be to use a different `Scheduler` 
after returning the results from MongoDB therefore freeing up the connection thread. `ObservableAdapter`s can be applied at the 
following levels:

  * [`MongoClients`]({{ apiref "com/mongodb/rx/client/MongoClients"}})
  * [`MongoDatabase`]({{ apiref "com/mongodb/rx/client/MongoDatabase.html#withObservableAdapter-com.mongodb.rx.client.ObservableAdapter-"}})
  * [`MongoCollection`]({{ apiref "com/mongodb/rx/client/MongoCollection.html#withObservableAdapter-com.mongodb.rx.client.ObservableAdapter-"}})

An example of an `ObservableAdapter` is as follows:

```java

import rx.Schedulers;

MongoCollection adaptedCollection = collection.withObservableAdapter(new ObservableAdapter() {
    @Override
    public <T> Observable<T> adapt(final Observable<T> observable) {
        return observable.observeOn(Schedulers.newThread());
    }
});

```

Any computations on `Observables` returned by the `adaptedCollection` will use a new thread, rather than blocking the MongoDB Connection thread.
      
