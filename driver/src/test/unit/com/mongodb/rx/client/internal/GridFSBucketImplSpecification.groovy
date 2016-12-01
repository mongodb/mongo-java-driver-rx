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

import com.mongodb.ReadConcern
import com.mongodb.ReadPreference
import com.mongodb.WriteConcern
import com.mongodb.async.client.gridfs.GridFSBucket as WrappedGridFSBucket
import com.mongodb.async.client.gridfs.GridFSDownloadStream
import com.mongodb.async.client.gridfs.GridFSFindIterable
import com.mongodb.async.client.gridfs.GridFSUploadStream
import com.mongodb.client.gridfs.model.GridFSDownloadOptions
import com.mongodb.client.gridfs.model.GridFSUploadOptions
import com.mongodb.rx.client.ObservableAdapter
import com.mongodb.rx.client.gridfs.AsyncInputStream
import com.mongodb.rx.client.gridfs.AsyncOutputStream
import org.bson.BsonObjectId
import org.bson.Document
import rx.observers.TestSubscriber
import spock.lang.Specification

class GridFSBucketImplSpecification extends Specification {

    def subscriber = { new TestSubscriber() }

    def 'should call the underlying GridFSBucket when getting bucket meta data'() {
        given:
        def wrapped = Mock(WrappedGridFSBucket)
        def observableAdapter = Stub(ObservableAdapter)
        def bucket = new GridFSBucketImpl(wrapped, observableAdapter)

        when:
        bucket.getBucketName()

        then:
        1 * wrapped.getBucketName()

        when:
        bucket.getChunkSizeBytes()

        then:
        1 * wrapped.getChunkSizeBytes()

        then:
        bucket.getObservableAdapter() == observableAdapter

        when:
        bucket.getWriteConcern()

        then:
        1 * wrapped.getWriteConcern()

        when:
        bucket.getReadPreference()

        then:
        1 * wrapped.getReadPreference()

        when:
        bucket.getReadConcern()

        then:
        1 * wrapped.getReadConcern()
    }

    def 'should call the underlying GridFSBucket when adjusting settings'() {
        given:
        def chunkSizeBytes = 1
        def writeConcern = WriteConcern.MAJORITY
        def readPreference = ReadPreference.secondaryPreferred()
        def readConcern = ReadConcern.MAJORITY

        def wrapped = Mock(WrappedGridFSBucket)
        def observableAdapter = Stub(ObservableAdapter)
        def bucket = new GridFSBucketImpl(wrapped, observableAdapter)

        when:
        bucket.withChunkSizeBytes(chunkSizeBytes)

        then:
        1 * wrapped.withChunkSizeBytes(chunkSizeBytes) >> wrapped

        when:
        bucket.withWriteConcern(writeConcern)

        then:
        1 * wrapped.withWriteConcern(writeConcern) >> wrapped

        when:
        bucket.withReadPreference(readPreference)

        then:
        1 * wrapped.withReadPreference(readPreference) >> wrapped

        when:
        bucket.withReadConcern(readConcern)

        then:
        1 * wrapped.withReadConcern(readConcern) >> wrapped
    }

    def 'should call the wrapped openUploadStream'() {
        given:
        def filename = 'filename'
        def options = new GridFSUploadOptions()
        def fileId = new BsonObjectId()
        def uploadStream = Stub(GridFSUploadStream)
        def wrapped = Mock(WrappedGridFSBucket)
        def observableAdapter = Stub(ObservableAdapter)
        def bucket = new GridFSBucketImpl(wrapped, observableAdapter)

        when:
        bucket.openUploadStream(filename)

        then:
        1 * wrapped.openUploadStream(filename) >> uploadStream

        when:
        bucket.openUploadStream(filename, options)

        then:
        1 * wrapped.openUploadStream(filename, options) >> uploadStream

        when:
        bucket.openUploadStream(fileId, filename)

        then:
        1 * wrapped.openUploadStream(fileId, filename) >> uploadStream

        when:
        bucket.openUploadStream(fileId, filename, options)

        then:
        1 * wrapped.openUploadStream(fileId, filename, options) >> uploadStream
    }

    def 'should call the wrapped uploadFromStream'() {
        given:
        def filename = 'filename'
        def options = new GridFSUploadOptions()
        def fileId = new BsonObjectId()
        def source = Stub(AsyncInputStream)

        def wrapped = Mock(WrappedGridFSBucket)
        def observableAdapter = Mock(ObservableAdapter)
        def bucket = new GridFSBucketImpl(wrapped, observableAdapter)

        when:
        def observable = bucket.uploadFromStream(filename, source)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.uploadFromStream(filename, _, _)

        when:
        observable = bucket.uploadFromStream(filename, source, options)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.uploadFromStream(filename, _, options, _)

        when:
        observable = bucket.uploadFromStream(fileId, filename, source)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.uploadFromStream(fileId, filename, _, _)

        when:
        observable = bucket.uploadFromStream(fileId, filename, source, options)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.uploadFromStream(fileId, filename, _, options, _)
    }

    def 'should call the wrapped openDownloadStream'() {
        given:
        def filename = 'filename'
        def options = new GridFSDownloadOptions()
        def fileId = new BsonObjectId()
        def downloadStream = Stub(GridFSDownloadStream)
        def wrapped = Mock(WrappedGridFSBucket)
        def observableAdapter = Stub(ObservableAdapter)
        def bucket = new GridFSBucketImpl(wrapped, observableAdapter)

        when:
        bucket.openDownloadStream(fileId)

        then:
        1 * wrapped.openDownloadStream(fileId) >> downloadStream

        when:
        bucket.openDownloadStream(fileId.getValue())

        then:
        1 * wrapped.openDownloadStream(fileId.getValue()) >> downloadStream

        when:
        bucket.openDownloadStream(filename)

        then:
        1 * wrapped.openDownloadStream(filename) >> downloadStream

        when:
        bucket.openDownloadStream(filename, options)

        then:
        1 * wrapped.openDownloadStream(filename, options) >> downloadStream
    }

    def 'should call the wrapped downloadToStream'() {
        given:
        def filename = 'filename'
        def options = new GridFSDownloadOptions()
        def fileId = new BsonObjectId()
        def destination = Stub(AsyncOutputStream)

        def wrapped = Mock(WrappedGridFSBucket)
        def observableAdapter = Mock(ObservableAdapter)
        def bucket = new GridFSBucketImpl(wrapped, observableAdapter)

        when:
        def observable = bucket.downloadToStream(fileId, destination)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.downloadToStream(fileId, _, _)

        when:
        observable = bucket.downloadToStream(fileId.getValue(), destination)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.downloadToStream(fileId.getValue(), _, _)

        when:
        observable = bucket.downloadToStream(filename, destination)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.downloadToStream(filename, _, _)

        when:
        observable = bucket.downloadToStream(filename, destination, options)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.downloadToStream(filename, _, options, _)
    }

    def 'should call the underlying find method'() {
        given:
        def filter = new Document('filter', 2)
        def findIterable = Stub(GridFSFindIterable)
        def wrapped = Mock(WrappedGridFSBucket)
        def observableAdapter = Stub(ObservableAdapter)
        def bucket = new GridFSBucketImpl(wrapped, observableAdapter)

        when:
        bucket.find()

        then:
        1 * wrapped.find() >> findIterable

        when:
        bucket.find(filter)

        then:
        1 * wrapped.find(filter) >> findIterable
    }

    def 'should call the underlying delete method'() {
        given:
        def fileId = new BsonObjectId()
        def wrapped = Mock(WrappedGridFSBucket)
        def observableAdapter = Mock(ObservableAdapter)
        def bucket = new GridFSBucketImpl(wrapped, observableAdapter)

        when:
        def observable = bucket.delete(fileId)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        0 * wrapped.delete(_, _)

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.delete(fileId, _)

        when:
        observable = bucket.delete(fileId.getValue())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        0 * wrapped.delete(_, _)

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.delete(fileId.getValue(), _)
    }

    def 'should call the underlying rename method'() {
        given:
        def fileId = new BsonObjectId()
        def newFilename = 'newFilename'
        def wrapped = Mock(WrappedGridFSBucket)
        def observableAdapter = Mock(ObservableAdapter)
        def bucket = new GridFSBucketImpl(wrapped, observableAdapter)

        when:
        def observable = bucket.rename(fileId, newFilename)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.rename(fileId, newFilename, _)

        when:
        observable = bucket.rename(fileId.getValue(), newFilename)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.rename(fileId.getValue(), newFilename, _)
    }

    def 'should call the underlying drop method'() {
        given:
        def wrapped = Mock(WrappedGridFSBucket)
        def observableAdapter = Mock(ObservableAdapter)
        def bucket = new GridFSBucketImpl(wrapped, observableAdapter)

        when:
        def observable = bucket.drop()

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.drop(_)
    }

}
