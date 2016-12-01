/*
 * Copyright 2014 MongoDB, Inc.
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
import com.mongodb.async.client.ListCollectionsIterable
import com.mongodb.async.client.MongoCollection as WrappedMongoCollection
import com.mongodb.async.client.MongoDatabase as WrappedMongoDatabase
import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.CreateViewOptions
import com.mongodb.rx.client.ObservableAdapter
import org.bson.BsonDocument
import org.bson.Document
import org.bson.codecs.configuration.CodecRegistry
import rx.observers.TestSubscriber
import spock.lang.Specification

import static com.mongodb.rx.client.CustomMatchers.isTheSameAs
import static spock.util.matcher.HamcrestSupport.expect

class MongoDatabaseImplSpecification extends Specification {

    def subscriber = { new TestSubscriber() }
    def wrapped = Mock(WrappedMongoDatabase)
    def observableAdapter = Mock(ObservableAdapter)
    def mongoDatabase = new MongoDatabaseImpl(wrapped, observableAdapter)

    def 'should return the a collection'() {
        given:
        def wrappedCollection = Stub(WrappedMongoCollection)

        when:
        def collection = mongoDatabase.getCollection('collectionName')

        then:
        1 * wrapped.getCollection('collectionName', Document) >> wrappedCollection

        then:
        expect collection, isTheSameAs(new MongoCollectionImpl(wrappedCollection, observableAdapter))
    }

    def 'should call the underlying getName'() {
        when:
        mongoDatabase.getName()

        then:
        1 * wrapped.getName()
    }

    def 'should call the underlying getCodecRegistry'() {
        when:
        mongoDatabase.getCodecRegistry()

        then:
        1 * wrapped.getCodecRegistry()
    }

    def 'should return the observableAdapter when calling getObservableAdapter'() {
        expect:
        mongoDatabase.getObservableAdapter() == observableAdapter
    }

    def 'should call the underlying getReadPreference'() {
        when:
        mongoDatabase.getReadPreference()

        then:
        1 * wrapped.getReadPreference()

    }

    def 'should call the underlying getWriteConcern'() {
        when:
        mongoDatabase.getWriteConcern()

        then:
        1 * wrapped.getWriteConcern()
    }

    def 'should call the underlying getReadConcern'() {
        when:
        mongoDatabase.getReadConcern()

        then:
        1 * wrapped.getReadConcern()
    }

    def 'should call the underlying withCodecRegistry'() {
        given:
        def codecRegistry = Stub(CodecRegistry)

        when:
        def result = mongoDatabase.withCodecRegistry(codecRegistry)

        then:
        1 * wrapped.withCodecRegistry(codecRegistry) >> wrapped

        then:
        expect result, isTheSameAs(new MongoDatabaseImpl(wrapped, observableAdapter))
    }

    def 'should call the underlying withReadPreference'() {
        given:
        def readPreference = Stub(ReadPreference)
        when:
        def result = mongoDatabase.withReadPreference(readPreference)

        then:
        1 * wrapped.withReadPreference(readPreference) >> wrapped

        then:
        expect result, isTheSameAs(new MongoDatabaseImpl(wrapped, observableAdapter))
    }

    def 'should call the underlying withWriteConcern'() {
        given:
        def writeConcern = Stub(WriteConcern)

        when:
        def result = mongoDatabase.withWriteConcern(writeConcern)

        then:
        1 * wrapped.withWriteConcern(writeConcern) >> wrapped

        then:
        expect result, isTheSameAs(new MongoDatabaseImpl(wrapped, observableAdapter))
    }


    def 'should call the underlying withReadConcern'() {
        given:
        def readConcern = ReadConcern.DEFAULT

        when:
        def result = mongoDatabase.withReadConcern(readConcern)

        then:
        1 * wrapped.withReadConcern(readConcern) >> wrapped

        then:
        expect result, isTheSameAs(new MongoDatabaseImpl(wrapped, observableAdapter))
    }

    def 'should call the underlying runCommand when writing'() {
        when:
        def observable = mongoDatabase.runCommand(new Document())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.runCommand(new Document(), Document, _)

        when:
        observable = mongoDatabase.runCommand(new BsonDocument(), BsonDocument)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.runCommand(new BsonDocument(), BsonDocument, _)
    }
    def 'should call the underlying runCommand for read operations'() {
        given:
        def readPreference = Stub(ReadPreference)

        when:
        def observable = mongoDatabase.runCommand(new Document(), readPreference)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.runCommand(new Document(), readPreference, Document, _)

        when:
        observable = mongoDatabase.runCommand(new BsonDocument(), readPreference, BsonDocument)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.runCommand(new BsonDocument(), readPreference, BsonDocument, _)
    }

    def 'should call the underlying drop'() {
        when:
        def observable = mongoDatabase.drop()

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.drop(_)
    }

    def 'should call the underlying listCollectionNames'() {
        when:
        mongoDatabase.listCollectionNames().subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.listCollectionNames()

    }
    def 'should call the underlying listCollections'() {
        given:
        def wrappedResult = Stub(ListCollectionsIterable)

        when:
        def observable = mongoDatabase.listCollections()

        then:
        1 * wrapped.listCollections(Document) >> wrappedResult

        then:
        expect observable, isTheSameAs(new ListCollectionsObservableImpl(wrappedResult, observableAdapter))

        when:
        observable = mongoDatabase.listCollections(BsonDocument)

        then:
        1 * wrapped.listCollections(BsonDocument) >> wrappedResult

        then:
        expect observable, isTheSameAs(new ListCollectionsObservableImpl(wrappedResult, observableAdapter))
    }

    def 'should call the underlying createCollection'() {
        given:
        def createCollectionOptions = Stub(CreateCollectionOptions)

        when:
        def observable = mongoDatabase.createCollection('collectionName')

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.createCollection('collectionName', _, _)

        when:
        observable = mongoDatabase.createCollection('collectionName', createCollectionOptions)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.createCollection('collectionName', createCollectionOptions, _)
    }

    def 'should call the underlying createView'() {
        given:
        def viewName = 'view1'
        def viewOn = 'col1'
        def pipeline = [new Document('$match', new Document('x', true))];
        def createViewOptions = Stub(CreateViewOptions)

        when:
        def observable = mongoDatabase.createView(viewName, viewOn, pipeline)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.createView(viewName, viewOn, pipeline, _)

        when:
        observable = mongoDatabase.createView(viewName, viewOn, pipeline, createViewOptions)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.createView(viewName, viewOn, pipeline, createViewOptions, _)
    }
}
