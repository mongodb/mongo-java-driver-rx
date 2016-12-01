/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.rx.client.internal

import com.mongodb.MongoException
import com.mongodb.async.client.Observable
import com.mongodb.async.client.Observer
import com.mongodb.async.client.Subscription
import com.mongodb.rx.client.ObservableAdapter
import rx.Subscriber
import rx.observers.TestSubscriber
import rx.schedulers.TestScheduler
import spock.lang.Specification

class RxObservablesSpecification extends Specification {

    def observableAdapter = new ObservableHelper.NoopObservableAdapter()

    def 'should be cold and nothing should happen until request is called'() {
        given:
        def subscriber = new TestSubscriber()
        def requested = false;

        when:
        def observable = RxObservables.create(new Observable() {
            @Override
            void subscribe(final Observer observer) {
                observer.onSubscribe(new Subscription() {
                    @Override
                    void request(final long n) {
                        requested = true
                        observer.onComplete()
                    }

                    @Override
                    void unsubscribe() {
                    }

                    @Override
                    boolean isUnsubscribed() {
                        false
                    }
                })
            }
        }, observableAdapter)

        then:
        !requested

        when:
        observable.subscribe(subscriber)

        then:
        requested
        subscriber.assertNoErrors()
        subscriber.assertTerminalEvent()
    }

    def 'should pass Observer.onNext values to Subscriber.onNext'() {
        given:
        def subscriber = new TestSubscriber()

        when:
        RxObservables.create(new Observable() {
            @Override
            void subscribe(final Observer observer) {
                observer.onSubscribe(new Subscription() {
                    @Override
                    void request(final long n) {
                        (1..n).each{
                            observer.onNext(it.intValue())
                        }
                    }

                    @Override
                    void unsubscribe() {
                    }

                    @Override
                    boolean isUnsubscribed() {
                        false
                    }
                })
            }
        }, observableAdapter).subscribe(subscriber)
        subscriber.requestMore(3)

        then:
        subscriber.assertReceivedOnNext([1, 2, 3])
    }

    def 'should pass Observer.onError values to Subscriber.onError'() {
        given:
        def errored = false
        def subscriber = new TestSubscriber(new rx.Observer(){
            @Override
            void onCompleted() {
            }

            @Override
            void onError(final Throwable e) {
                errored = true
            }

            @Override
            void onNext(final Object o) {
            }
        })

        when:
        RxObservables.create(new Observable() {
            @Override
            void subscribe(final Observer observer) {
                observer.onSubscribe(new Subscription() {
                    @Override
                    void request(final long n) {
                        observer.onError(new MongoException('failed'))
                    }

                    @Override
                    void unsubscribe() {
                    }

                    @Override
                    boolean isUnsubscribed() {
                        false
                    }
                })
            }
        }, observableAdapter).subscribe(subscriber)
        subscriber.requestMore(1)

        then:
        errored
        subscriber.assertTerminalEvent()
    }

    def 'should only call Subscriber.onStart() once'() {
        given:
        def onStartCount = 0

        when:
        RxObservables.create(new Observable() {
            @Override
            void subscribe(final Observer observer) {
                observer.onSubscribe(new Subscription() {
                    @Override
                    void request(final long n) {
                        observer.onComplete()
                    }

                    @Override
                    void unsubscribe() {
                    }

                    @Override
                    boolean isUnsubscribed() {
                        false
                    }
                })
            }
        }, observableAdapter).subscribe(new Subscriber() {
            @Override
            void onStart() {
                onStartCount += 1
                request(1)
            }

            @Override
            void onCompleted() {
            }

            @Override
            void onError(final Throwable e) {
            }

            @Override
            void onNext(final Object o) {
            }
        })

        then:
        onStartCount == 1
    }

    def 'should trigger Subscriber.onComplete when Observer.onComplete is called'() {
        given:
        def subscriber = new TestSubscriber()

        when:
        RxObservables.create(new Observable() {
            @Override
            void subscribe(final Observer observer) {
                observer.onSubscribe(new Subscription() {
                    @Override
                    void request(final long n) {
                        observer.onComplete()
                    }

                    @Override
                    void unsubscribe() {
                    }

                    @Override
                    boolean isUnsubscribed() {
                        false
                    }
                })
            }
        }, observableAdapter).subscribe(subscriber)
        subscriber.requestMore(1)

        then:
        subscriber.assertNoErrors()
        subscriber.assertTerminalEvent()
    }

    def 'should unsubscribe from the observer if rx subscription is cancelled'() {
        given:
        def unsubscribed = false
        def subscriber = new TestSubscriber()
        def subscription = new Subscription() {
            @Override
            void request(final long n) {
            }

            @Override
            void unsubscribe() {
                unsubscribed = true
            }

            @Override
            boolean isUnsubscribed() {
                unsubscribed
            }
        }

        when:
        RxObservables.create(new Observable() {
            @Override
            void subscribe(final Observer observer) {
                observer.onSubscribe(subscription)
            }
        }, observableAdapter).subscribe(subscriber)

        then:
        !unsubscribed

        when:
        subscriber.unsubscribe()

        then:
        unsubscribed
    }

    def 'should trigger onError if request is less than 1'() {
        given:
        def subscriber = new TestSubscriber()

        when:
        RxObservables.create(new Observable() {
            @Override
            void subscribe(final Observer observer) {
                observer.onSubscribe(new Subscription() {
                    @Override
                    void request(final long n) {
                    }

                    @Override
                    void unsubscribe() {
                    }

                    @Override
                    boolean isUnsubscribed() {
                        false
                    }
                })
            }
        }, observableAdapter).subscribe(subscriber)
        subscriber.requestMore(-1)

        then:
        thrown(IllegalArgumentException)
    }

    def 'should use the passed ObserverAdapter to adapt the resulting Observer'() {
        given:
        def scheduler = new TestScheduler()
        def observer = RxObservables.create(new Observable() {
            @Override
            void subscribe(final Observer observer) {
                observer.onSubscribe(new Subscription() {
                    @Override
                    void request(final long n) {
                        (1..n).each {  observer.onNext('onNext called') }
                    }

                    @Override
                    void unsubscribe() {
                    }

                    @Override
                    boolean isUnsubscribed() {
                        false
                    }
                })
            }
        }, new ObservableAdapter() {
            @Override
            def <T> rx.Observable<T> adapt(final rx.Observable<T> observable) {
                observable.observeOn(scheduler);
            }
        })
        def subscriber = new TestSubscriber(0)

        when:
        observer.subscribe(subscriber)
        subscriber.requestMore(3)

        then:
        subscriber.assertReceivedOnNext([])

        when:
        scheduler.triggerActions()

        then:
        subscriber.assertReceivedOnNext(['onNext called', 'onNext called', 'onNext called'])
    }
}
