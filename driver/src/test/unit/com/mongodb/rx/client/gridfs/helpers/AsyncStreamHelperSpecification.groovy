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

package com.mongodb.rx.client.gridfs.helpers

import com.mongodb.MongoGridFSException
import com.mongodb.async.client.gridfs.helpers.AsyncStreamHelper as WrappedAsyncStreamHelper
import rx.observers.TestSubscriber
import spock.lang.Specification

import java.nio.ByteBuffer

class AsyncStreamHelperSpecification extends Specification {

    def 'should have the same methods as the wrapped AsyncStreamHelper'() {
        given:
        def wrapped = WrappedAsyncStreamHelper.methods*.name.sort().toSet()
        def local = AsyncStreamHelper.methods*.name.sort().toSet()

        expect:
        wrapped == local
    }

    def 'should call the underlying InputStream methods'() {
        given:
        def subscriber = new TestSubscriber()
        def inputStream = Mock(InputStream)
        def byteBuffer = ByteBuffer.allocate(10)
        def asyncInputStream = AsyncStreamHelper.toAsyncInputStream(inputStream)

        when:
        asyncInputStream.read(byteBuffer).subscribe(subscriber)

        then:
        1 * inputStream.read(_)
        subscriber.assertTerminalEvent()

        when:
        subscriber = new TestSubscriber()
        asyncInputStream.close().subscribe(subscriber)

        then:
        1 * inputStream.close()
        subscriber.assertTerminalEvent()
    }

    def 'should call the underlying OutputStream methods'() {
        given:
        def subscriber = new TestSubscriber()
        def outputStream = Mock(OutputStream)
        def byteBuffer = ByteBuffer.wrap(new byte[10])
        def asyncOutputStream = AsyncStreamHelper.toAsyncOutputStream(outputStream)

        when:
        asyncOutputStream.write(byteBuffer).subscribe(subscriber)

        then:
        1 * outputStream.write(_)
        subscriber.assertTerminalEvent()

        when:
        subscriber = new TestSubscriber()
        asyncOutputStream.close().subscribe(subscriber)

        then:
        1 * outputStream.close()
        subscriber.assertTerminalEvent()
    }

    def 'should handle underlying InputStream errors'() {
        def inputStream = Mock(InputStream)

        when:
        def subscriber = new TestSubscriber<Integer>()
        AsyncStreamHelper.toAsyncInputStream(inputStream).read(ByteBuffer.allocate(1024)).subscribe(subscriber)

        then:
        1 *  inputStream.read(_) >> { throw new IOException('Read failed') }

        then:
        subscriber.assertError(MongoGridFSException)

        when:
        subscriber = new TestSubscriber<Integer>()
        AsyncStreamHelper.toAsyncInputStream(inputStream).close().subscribe(subscriber)

        then:
        1 *  inputStream.close() >> { throw new IOException('Closed failed') }

        then:
        subscriber.assertError(MongoGridFSException)
    }

    def 'should handle underlying OutputStream errors'() {
        def outputStream = Mock(OutputStream)

        when:
        def subscriber = new TestSubscriber<Integer>()
        AsyncStreamHelper.toAsyncOutputStream(outputStream).write(ByteBuffer.allocate(1024)).subscribe(subscriber)

        then:
        1 *  outputStream.write(_) >> { throw new IOException('Read failed') }

        then:
        subscriber.assertError(MongoGridFSException)

        when:
        subscriber = new TestSubscriber<Integer>()
        AsyncStreamHelper.toAsyncOutputStream(outputStream).close().subscribe(subscriber)

        then:
        1 *  outputStream.close() >> { throw new IOException('Closed failed') }

        then:
        subscriber.assertError(MongoGridFSException)
    }

}
