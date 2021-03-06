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

import com.mongodb.async.client.ListCollectionsIterable
import com.mongodb.rx.client.ObservableAdapter
import org.bson.Document
import rx.observers.TestSubscriber
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class ListCollectionsObservableImplSpecification extends Specification {

    def 'should call the underlying wrapped methods'() {
        given:
        def filter = new Document('field', 1)
        def subscriber = { new TestSubscriber() }
        def wrapped = Mock(ListCollectionsIterable)
        def observableAdapter = Mock(ObservableAdapter)
        def observable = new ListCollectionsObservableImpl(wrapped, observableAdapter)

        when: 'setting options'
        observable = observable
                .filter(filter)
                .maxTime(1, TimeUnit.SECONDS)

        then:
        1 * wrapped.filter(filter) >> wrapped
        1 * wrapped.maxTime(1, TimeUnit.SECONDS) >> wrapped

        when:
        observable.subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.batchCursor(_)
    }

}
