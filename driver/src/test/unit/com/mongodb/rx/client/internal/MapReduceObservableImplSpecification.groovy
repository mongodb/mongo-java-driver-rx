/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.rx.client.internal

import com.mongodb.async.client.MapReduceIterable
import com.mongodb.client.model.Collation
import com.mongodb.client.model.MapReduceAction
import com.mongodb.rx.client.ObservableAdapter
import org.bson.Document
import rx.observers.TestSubscriber
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class MapReduceObservableImplSpecification extends Specification {

    def 'should call the underlying wrapped methods'() {
        given:
        def filter = new Document('field', 1)
        def scope = new Document('scope', 1)
        def sort = new Document('sort', 1)
        def collation = Collation.builder().locale('en').build()
        def subscriber = { new TestSubscriber() }
        def wrapped = Mock(MapReduceIterable)
        def observableAdapter = Mock(ObservableAdapter)
        def observable = new MapReduceObservableImpl(wrapped, observableAdapter)

        when: 'setting options'
        observable = observable
                .action(MapReduceAction.MERGE)
                .bypassDocumentValidation(true)
                .collation(collation)
                .collectionName('coll')
                .databaseName('db')
                .filter(filter)
                .finalizeFunction('finalize')
                .jsMode(true)
                .limit(999)
                .maxTime(1, TimeUnit.SECONDS)
                .nonAtomic(true)
                .scope(scope)
                .sharded(true)
                .sort(sort)
                .verbose(false)

        then:
        1 * wrapped.action(MapReduceAction.MERGE) >> wrapped
        1 * wrapped.bypassDocumentValidation(true) >> wrapped
        1 * wrapped.collation(collation) >> wrapped
        1 * wrapped.collectionName('coll') >> wrapped
        1 * wrapped.databaseName('db') >> wrapped
        1 * wrapped.filter(filter) >> wrapped
        1 * wrapped.finalizeFunction('finalize') >> wrapped
        1 * wrapped.jsMode(true) >> wrapped
        1 * wrapped.limit(999) >> wrapped
        1 * wrapped.maxTime(1, TimeUnit.SECONDS) >> wrapped
        1 * wrapped.nonAtomic(true) >> wrapped
        1 * wrapped.scope(scope) >> wrapped
        1 * wrapped.sharded(true) >> wrapped
        1 * wrapped.sort(sort) >> wrapped
        1 * wrapped.verbose(false) >> wrapped

        when:
        observable.subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.batchCursor(_)

        when: 'calling toCollection'
        observable = observable.toCollection()

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.toCollection(_)
    }

}
