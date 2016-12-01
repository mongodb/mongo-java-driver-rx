/*
 * Copyright 2016 MongoDB, Inc.
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

import com.mongodb.async.client.gridfs.GridFSDownloadStream as WrappedGridFSDownloadStream
import com.mongodb.rx.client.ObservableAdapter
import rx.observers.TestSubscriber
import spock.lang.Specification

import java.nio.ByteBuffer

class GridFSDownloadStreamImplSpecification extends Specification {

    def subscriber = { new TestSubscriber() }

    def 'should call the underlying getGridFSFile'() {
        given:
        def wrapped = Mock(WrappedGridFSDownloadStream)
        def observableAdapter = Mock(ObservableAdapter)
        def downloadStream = new GridFSDownloadStreamImpl(wrapped, observableAdapter)

        when:
        def observable = downloadStream.getGridFSFile()

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.getGridFSFile(_)
    }

    def 'should call the underlying batchSize'() {
        given:
        def wrapped = Mock(WrappedGridFSDownloadStream)
        def observableAdapter = Stub(ObservableAdapter)
        def downloadStream = new GridFSDownloadStreamImpl(wrapped, observableAdapter)

        when:
        downloadStream.batchSize(10)

        then:
        1 * wrapped.batchSize(10)
    }

    def 'should call the underlying read'() {
        given:
        def wrapped = Mock(WrappedGridFSDownloadStream)
        def observableAdapter = Mock(ObservableAdapter)
        def downloadStream = new GridFSDownloadStreamImpl(wrapped, observableAdapter)

        when:
        def observable = downloadStream.read(ByteBuffer.allocate(2))

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.read(ByteBuffer.allocate(2), _)
    }

    def 'should call the underlying close'() {
        given:
        def wrapped = Mock(WrappedGridFSDownloadStream)
        def observableAdapter = Mock(ObservableAdapter)
        def downloadStream = new GridFSDownloadStreamImpl(wrapped, observableAdapter)

        when:
        def observable = downloadStream.close()

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.close(_)
    }

}
