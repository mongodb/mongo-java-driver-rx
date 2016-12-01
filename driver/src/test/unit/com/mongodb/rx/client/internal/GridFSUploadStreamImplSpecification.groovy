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

import com.mongodb.async.client.gridfs.GridFSUploadStream as WrappedGridFSUploadStream
import com.mongodb.rx.client.ObservableAdapter
import org.bson.BsonObjectId
import rx.observers.TestSubscriber
import spock.lang.Specification

import java.nio.ByteBuffer

class GridFSUploadStreamImplSpecification extends Specification {

    def subscriber = { new TestSubscriber() }
    def fileId = new BsonObjectId()
    def content = 'file content ' as byte[]

    def 'should call the underlying getId'() {
        when:
        def wrapped = Mock(WrappedGridFSUploadStream) {
            1 * getId() >> { fileId }
        }
        def observableAdapter = Stub(ObservableAdapter)
        def uploadStream = new GridFSUploadStreamImpl(wrapped, observableAdapter)

        then:
        uploadStream.getId() == fileId
    }

    def 'should call the underlying getObjectId'() {
        when:
        def wrapped = Mock(WrappedGridFSUploadStream) {
            1 * getObjectId() >> { fileId.getValue() }
        }
        def observableAdapter = Stub(ObservableAdapter)
        def uploadStream = new GridFSUploadStreamImpl(wrapped, observableAdapter)

        then:
        uploadStream.getObjectId() == fileId.getValue()
    }

    def 'should call the underlying write'() {
        given:
        def wrapped = Mock(WrappedGridFSUploadStream)
        def observableAdapter = Mock(ObservableAdapter)
        def uploadStream = new GridFSUploadStreamImpl(wrapped, observableAdapter)

        when:
        def observer = uploadStream.write(ByteBuffer.wrap(content))

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observer.subscribe(subscriber())

        then:
        1 * wrapped.write(ByteBuffer.wrap(content), _)
    }

    def 'should call the underlying abort'() {
        given:
        def wrapped = Mock(WrappedGridFSUploadStream)
        def observableAdapter = Mock(ObservableAdapter)
        def uploadStream = new GridFSUploadStreamImpl(wrapped, observableAdapter)

        when:
        def observer = uploadStream.abort()

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observer.subscribe(subscriber())

        then:
        1 * wrapped.abort(_)
    }

    def 'should call the underlying close'() {
        given:
        def wrapped = Mock(WrappedGridFSUploadStream)
        def observableAdapter = Mock(ObservableAdapter)
        def uploadStream = new GridFSUploadStreamImpl(wrapped, observableAdapter)

        when:
        def observer = uploadStream.close()

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observer.subscribe(subscriber())

        then:
        1 * wrapped.close(_)
    }

}
