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
import com.mongodb.async.AsyncBatchCursor
import com.mongodb.async.client.MongoIterable
import org.bson.Document
import rx.observers.TestSubscriber
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class MongoIterableObservableSpecification extends Specification {

    def 'should be cold and not execute the batchCursor until subscribed is called'() {
        given:
        def mongoIterable = Mock(MongoIterable)
        def subscriber = new TestSubscriber()

        when:
        def observable = MongoIterableObservable.create(mongoIterable)

        then:
        0 * mongoIterable.batchCursor(_)

        when:
        observable.subscribe(subscriber)

        then:
        1 * mongoIterable.batchCursor(_)
        1 * mongoIterable.batchSize(Integer.MAX_VALUE)  // By default will subscriber to Long.MAX_VALUE but batchSize is an integer
    }

    def 'should set batchSize to 2 when requesting 1'() {
        given:
        def mongoIterable = Mock(MongoIterable)
        def subscriber = new TestSubscriber()
        subscriber.requestMore(1)

        when:
        def observable = MongoIterableObservable.create(mongoIterable)

        then:
        0 * mongoIterable.batchCursor(_)

        when:
        observable.subscribe(subscriber)

        then:
        1 * mongoIterable.batchCursor(_)
        1 * mongoIterable.batchSize(2)  // Ensures the cursor doesn't close
    }

    def 'should set batchSize to the amount requested when less than Integer.MAX_VALUE'() {
        given:
        def mongoIterable = Mock(MongoIterable)
        def subscriber = new TestSubscriber()
        subscriber.requestMore(1000)

        when:
        def observable = MongoIterableObservable.create(mongoIterable)

        then:
        0 * mongoIterable.batchCursor(_)

        when:
        observable.subscribe(subscriber)

        then:
        1 * mongoIterable.batchCursor(_)
        1 * mongoIterable.batchSize(1000)
    }

    def 'should set batchSize to Integer.MAX_VALUE when more is requested'() {
        given:
        def mongoIterable = Mock(MongoIterable)
        def subscriber = new TestSubscriber()
        subscriber.requestMore(Integer.MAX_VALUE.longValue() + 1L)

        when:
        def observable = MongoIterableObservable.create(mongoIterable)

        then:
        0 * mongoIterable.batchCursor(_)

        when:
        observable.subscribe(subscriber)

        then:
        1 * mongoIterable.batchCursor(_)
        1 * mongoIterable.batchSize(Integer.MAX_VALUE)
    }

    def 'should handle exceptions when getting the batchCursor'() {
        given:
        def subscriber = new TestSubscriber()
        def mongoIterable = Stub(MongoIterable) {
            batchCursor(_) >> { args -> args[0].onResult(null, new MongoException('failure')) }
        }

        when:
        MongoIterableObservable.create(mongoIterable).subscribe(subscriber)
        subscriber.requestMore(1)
        subscriber.awaitTerminalEvent();
        subscriber.assertTerminalEvent();

        then:
        subscriber.getOnErrorEvents().size() == 1
        subscriber.getOnErrorEvents().head() instanceof MongoException
    }

    def 'should handle null returns when getting the batchCursor'() {
        given:
        def subscriber = new TestSubscriber()
        def mongoIterable = Stub(MongoIterable) {
            batchCursor(_) >> { args -> args[0].onResult(null, null) }
        }

        when:
        MongoIterableObservable.create(mongoIterable).subscribe(subscriber)
        subscriber.requestMore(1)
        subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS)

        then:
        subscriber.assertTerminalEvent()

    }

    def 'should call next on the batchCursor'() {
        given:
        def batchSize = 10
        def batches = 10
        def cannedResults = (1..(batchSize * batches)).collect({ new Document('_id', it) })
        def cursorResults = cannedResults.collate(batchSize)
        def cursor = {
            Stub(AsyncBatchCursor) {
                next(_) >> {
                    it[0].onResult(cursorResults.remove(0), null)
                }
                isClosed() >> { cursorResults.isEmpty() }
            }
        }
        def subscriber = new TestSubscriber()
        def mongoIterable = Stub(MongoIterable) {
            batchCursor(_) >> { args -> args[0].onResult(cursor(), null) }
        }

        when:
        subscriber.requestMore(10)  // Request first batch
        MongoIterableObservable.create(mongoIterable).subscribe(subscriber)

        then:
        subscriber.assertReceivedOnNext(cannedResults[0..9])

        when:
        subscriber.requestMore(15) // Request across batches

        then:
        subscriber.assertReceivedOnNext(cannedResults[0..24])

        when:
        subscriber.requestMore(55) // Request across batches

        then:
        subscriber.assertReceivedOnNext(cannedResults[0..79])

        when:
        subscriber.requestMore(99)  // Request more than is left

        then:
        subscriber.assertReceivedOnNext(cannedResults)
        subscriber.assertTerminalEvent()
    }

    def 'should close the cursor when the subscriber unsubscribed'() {
        given:
        def isClosed = false;
        def subscriber = new TestSubscriber()
        subscriber.requestMore(1)  // Request first batch
        def mongoIterable = Stub(MongoIterable) {
            batchCursor(_) >> { args ->
                args[0].onResult(Stub(AsyncBatchCursor) {
                    close() >> { isClosed = true }
                }, null)
            }
        }

        when:
        MongoIterableObservable.create(mongoIterable).subscribe(subscriber)
        subscriber.unsubscribe()

        then:
        !isClosed

        when:
        subscriber.requestMore(1)

        then:
        isClosed
    }
}
