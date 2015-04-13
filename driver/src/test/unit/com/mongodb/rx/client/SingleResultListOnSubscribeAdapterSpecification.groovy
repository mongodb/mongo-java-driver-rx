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

package com.mongodb.rx.client

import com.mongodb.MongoException
import com.mongodb.async.SingleResultCallback
import rx.Observable
import rx.observers.TestSubscriber
import spock.lang.Specification

class SingleResultListOnSubscribeAdapterSpecification extends Specification {

    def 'should not execute until subscribed'() {
        given:
        def called = false;
        def subscriber = new TestSubscriber()

        when:
        def observable = Observable.create(new SingleResultListOnSubscribeAdapter() {
            @Override
            void execute(final SingleResultCallback callback) {
                called = true
                callback.onResult([Success.SUCCESS], null);
            }
        })

        then:
        !called
        !subscriber.isUnsubscribed()

        when:
        observable.subscribe(subscriber)

        then:
        called
        subscriber.assertTerminalEvent()
    }

    def 'should return the callback result'() {
        given:
        def subscriber = new TestSubscriber()
        def observable = Observable.create(new SingleResultListOnSubscribeAdapter() {
            @Override
            void execute(final SingleResultCallback callback) {
                callback.onResult(['callback', 'result'], null);
            }
        })

        when:
        observable.subscribe(subscriber)

        then:
        subscriber.assertTerminalEvent()
        subscriber.assertReceivedOnNext(['callback', 'result'])
    }

    def 'should return handle and surface errors result'() {
        given:
        def error = new MongoException('failure')
        def subscriber = new TestSubscriber()
        def observable = Observable.create(new SingleResultListOnSubscribeAdapter() {
            @Override
            void execute(final SingleResultCallback callback) {
                callback.onResult(null, error);
            }
        })

        when:
        observable.subscribe(subscriber)

        then:
        subscriber.assertTerminalEvent()
        subscriber.getOnErrorEvents().head() == error
    }
}
