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

import com.mongodb.async.client.ListCollectionsIterable
import com.mongodb.async.client.MongoIterable
import spock.lang.Specification

class ListCollectionsObservableSpecification extends Specification {

    def 'should have the same methods as the wrapped ListCollectionsIterable'() {
        given:
        def wrapped = (ListCollectionsIterable.methods*.name - MongoIterable.methods*.name).sort()
        def local = (ListCollectionsObservable.methods*.name - MongoObservable.methods*.name - 'batchSize').sort()
        expect:
        wrapped == local
    }

}
