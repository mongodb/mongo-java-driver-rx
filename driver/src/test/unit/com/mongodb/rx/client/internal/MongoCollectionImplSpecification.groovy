/*
 * Copyright 2014-2015 MongoDB, Inc.
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

import com.mongodb.MongoNamespace
import com.mongodb.ReadConcern
import com.mongodb.ReadPreference
import com.mongodb.WriteConcern
import com.mongodb.async.client.AggregateIterable
import com.mongodb.async.client.DistinctIterable
import com.mongodb.async.client.FindIterable
import com.mongodb.async.client.ListIndexesIterable
import com.mongodb.async.client.MapReduceIterable
import com.mongodb.async.client.MongoCollection as WrappedMongoCollection
import com.mongodb.client.model.BulkWriteOptions
import com.mongodb.client.model.CountOptions
import com.mongodb.client.model.FindOneAndDeleteOptions
import com.mongodb.client.model.FindOneAndReplaceOptions
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.IndexModel
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.InsertManyOptions
import com.mongodb.client.model.InsertOneModel
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.model.RenameCollectionOptions
import com.mongodb.client.model.UpdateOptions
import com.mongodb.rx.client.ObservableAdapter
import org.bson.BsonDocument
import org.bson.Document
import org.bson.codecs.configuration.CodecRegistry
import rx.observers.TestSubscriber
import spock.lang.Specification

import static com.mongodb.rx.client.CustomMatchers.isTheSameAs
import static spock.util.matcher.HamcrestSupport.expect

class MongoCollectionImplSpecification extends Specification {

    def subscriber = { new TestSubscriber() }
    def wrapped = Mock(WrappedMongoCollection)
    def observableAdapter = Mock(ObservableAdapter)
    def mongoCollection = new MongoCollectionImpl(wrapped, observableAdapter)
    def filter = new Document('_id', 1)

    def 'should use the underlying getNamespace'() {
        when:
        mongoCollection.getNamespace()

        then:
        1 * wrapped.getNamespace()
    }

    def 'should use the underlying getDocumentClass'() {
        when:
        mongoCollection.getDocumentClass()

        then:
        1 * wrapped.getDocumentClass()
    }

    def 'should call the underlying getCodecRegistry'() {
        when:
        mongoCollection.getCodecRegistry()

        then:
        1 * wrapped.getCodecRegistry()
    }

    def 'should call the underlying getReadPreference'() {
        when:
        mongoCollection.getReadPreference()

        then:
        1 * wrapped.getReadPreference()
    }

    def 'should call the underlying getWriteConcern'() {
        when:
        mongoCollection.getWriteConcern()

        then:
        1 * wrapped.getWriteConcern()
    }

    def 'should call the underlying getReadConcern'() {
        when:
        mongoCollection.getReadConcern()

        then:
        1 * wrapped.getReadConcern()
    }

    def 'should use the underlying withDocumentClass'() {
        when:
        def result = mongoCollection.withDocumentClass(BsonDocument)

        then:
        1 * wrapped.withDocumentClass(BsonDocument) >> wrapped
        expect result, isTheSameAs(new MongoCollectionImpl(wrapped, observableAdapter))
    }

    def 'should call the underlying withCodecRegistry'() {
        given:
        def codecRegistry = Stub(CodecRegistry)

        when:
        def result = mongoCollection.withCodecRegistry(codecRegistry)

        then:
        1 * wrapped.withCodecRegistry(codecRegistry) >> wrapped
        expect result, isTheSameAs(new MongoCollectionImpl(wrapped, observableAdapter))
    }

    def 'should call the underlying withReadPreference'() {
        given:
        def readPreference = Stub(ReadPreference)

        when:
        def result = mongoCollection.withReadPreference(readPreference)

        then:
        1 * wrapped.withReadPreference(readPreference) >> wrapped
        expect result, isTheSameAs(new MongoCollectionImpl(wrapped, observableAdapter))
    }

    def 'should call the underlying withWriteConcern'() {
        given:
        def writeConcern = Stub(WriteConcern)

        when:
        def result = mongoCollection.withWriteConcern(writeConcern)

        then:
        1 * wrapped.withWriteConcern(writeConcern) >> wrapped
        expect result, isTheSameAs(new MongoCollectionImpl(wrapped, observableAdapter))
    }

    def 'should call the underlying withReadConcern'() {
        given:
        def readConcern = ReadConcern.MAJORITY

        when:
        def result = mongoCollection.withReadConcern(readConcern)

        then:
        1 * wrapped.withReadConcern(readConcern) >> wrapped
        expect result, isTheSameAs(new MongoCollectionImpl(wrapped, observableAdapter))
    }

    def 'should use the underlying count'() {
        given:
        def options = new CountOptions()

        when:
        def observer = mongoCollection.count()

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observer.subscribe(subscriber())

        then:
        1 * wrapped.count(_, _, _)

        when:
        mongoCollection.count(filter).subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.count(filter, _, _)

        when:
        mongoCollection.count(filter, options).subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.count(filter, options, _)
    }


    def 'should create DistinctObservable correctly'() {
        given:
        def wrappedIterable = Stub(DistinctIterable)

        when:
        def observable = mongoCollection.distinct('field', String)

        then:
        1 * wrapped.distinct('field', String) >> wrappedIterable
        expect observable, isTheSameAs(new DistinctObservableImpl(wrappedIterable, observableAdapter))
    }

    def 'should create FindObservable correctly'() {
        given:
        def wrappedIterable = Stub(FindIterable)

        when:
        def observable = mongoCollection.find()

        then:
        1 * wrapped.getDocumentClass() >> Document
        1 * wrapped.find(new BsonDocument(), Document) >> wrappedIterable
        expect observable, isTheSameAs(new FindObservableImpl(wrappedIterable, observableAdapter))

        when:
        observable = mongoCollection.find(BsonDocument)

        then:
        1 * wrapped.find(new BsonDocument(), BsonDocument) >> wrappedIterable
        expect observable, isTheSameAs(new FindObservableImpl(wrappedIterable, observableAdapter))

        when:
        observable = mongoCollection.find(new Document())

        then:
        1 * wrapped.getDocumentClass() >> Document
        1 * wrapped.find(new Document(), Document) >> wrappedIterable
        expect observable, isTheSameAs(new FindObservableImpl(wrappedIterable, observableAdapter))

        when:
        observable = mongoCollection.find(new Document(), BsonDocument)

        then:
        1 * wrapped.find(new Document(), BsonDocument) >> wrappedIterable
        expect observable, isTheSameAs(new FindObservableImpl(wrappedIterable, observableAdapter))
    }

    def 'should use AggregateObservable correctly'() {
        given:
        def wrappedIterable = Stub(AggregateIterable)
        def pipeline = [new Document('$match', 1)]

        when:
        def observable = mongoCollection.aggregate(pipeline)

        then:
        1 * wrapped.aggregate(pipeline, Document) >> wrappedIterable
        expect observable, isTheSameAs(new AggregateObservableImpl(wrappedIterable, observableAdapter))

        when:
        observable = mongoCollection.aggregate(pipeline, BsonDocument)

        then:
        1 * wrapped.aggregate(pipeline, BsonDocument) >> wrappedIterable
        expect observable, isTheSameAs(new AggregateObservableImpl(wrappedIterable, observableAdapter))
    }

    def 'should create MapReduceObservable correctly'() {
        given:
        def wrappedIterable = Stub(MapReduceIterable)

        when:
        def observable = mongoCollection.mapReduce('map', 'reduce')

        then:
        1 * wrapped.mapReduce('map', 'reduce', Document) >> wrappedIterable
        expect observable, isTheSameAs(new MapReduceObservableImpl(wrappedIterable, observableAdapter))

        when:
        observable = mongoCollection.mapReduce('map', 'reduce', BsonDocument)

        then:
        1 * wrapped.mapReduce('map', 'reduce', BsonDocument) >> wrappedIterable
        expect observable, isTheSameAs(new MapReduceObservableImpl(wrappedIterable, observableAdapter))
    }


    def 'should use the underlying bulkWrite'() {
        def bulkOperation = [new InsertOneModel<Document>(new Document('_id', 10))]
        def options = new BulkWriteOptions()

        when:
        def observer = mongoCollection.bulkWrite(bulkOperation)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observer.subscribe(subscriber())

        then:
        1 * wrapped.bulkWrite(bulkOperation, _, _)

        when:
        mongoCollection.bulkWrite(bulkOperation, options).subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.bulkWrite(bulkOperation, options, _)
    }

    def 'should use the underlying insertOne'() {
        given:
        def insert = new Document('_id', 1)
        def options = new InsertOneOptions()

        when:
        def observer = mongoCollection.insertOne(insert)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observer.subscribe(subscriber())

        then:
        1 * wrapped.insertOne(insert, _)

        when:
        mongoCollection.insertOne(insert, options).subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.insertOne(insert, options, _)
    }

    def 'should use the underlying insertMany'() {
        given:
        def inserts = [new Document('_id', 1)]
        def options = new InsertManyOptions()

        when:
        def observable = mongoCollection.insertMany(inserts)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.insertMany(inserts, _, _)

        when:
        mongoCollection.insertMany(inserts, options).subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.insertMany(inserts, options, _)
    }

    def 'should use the underlying deleteOne'() {
        when:
        def observable = mongoCollection.deleteOne(filter)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.deleteOne(filter, _)
    }

    def 'should use the underlying deleteMany'() {
        when:
        def observable = mongoCollection.deleteMany(filter)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.deleteMany(filter, _)
    }

    def 'should use the underlying replaceOne'() {
        given:
        def replacement = new Document('new', 1)
        def options = new UpdateOptions()

        when:
        def observable = mongoCollection.replaceOne(filter, replacement)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.replaceOne(filter, replacement, _, _)

        when:
        mongoCollection.replaceOne(filter, replacement, options).subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.replaceOne(filter, replacement, options, _)
    }


    def 'should use the underlying updateOne'() {
        given:
        def update = new Document('new', 1)
        def options = new UpdateOptions()

        when:
        def observable = mongoCollection.updateOne(filter, update)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.updateOne(filter, update, _, _)

        when:
        mongoCollection.updateOne(filter, update, options).subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.updateOne(filter, update, options, _)
    }

    def 'should use the underlying updateMany'() {
        given:
        def update = new Document('new', 1)
        def options = new UpdateOptions()

        when:
        def observable = mongoCollection.updateMany(filter, update)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.updateMany(filter, update, _, _)

        when:
        mongoCollection.updateMany(filter, update, options).subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.updateMany(filter, update, options, _)
    }

    def 'should use the underlying findOneAndDelete'() {
        given:
        def options = new FindOneAndDeleteOptions()

        when:
        def observable = mongoCollection.findOneAndDelete(filter)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.findOneAndDelete(filter, _, _)

        when:
        mongoCollection.findOneAndDelete(filter, options).subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.findOneAndDelete(filter, options, _)
    }

    def 'should use the underlying findOneAndReplace'() {
        given:
        def replacement = new Document('new', 1)
        def options = new FindOneAndReplaceOptions()

        when:
        def observable = mongoCollection.findOneAndReplace(filter, replacement)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.findOneAndReplace(filter, replacement, _, _)

        when:
        mongoCollection.findOneAndReplace(filter, replacement, options).subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.findOneAndReplace(filter, replacement, options, _)
    }

    def 'should use the underlying findOneAndUpdate'() {
        given:
        def update = new Document('new', 1)
        def options = new FindOneAndUpdateOptions()

        when:
        def observable = mongoCollection.findOneAndUpdate(filter, update)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.findOneAndUpdate(filter, update, _, _)

        when:
        mongoCollection.findOneAndUpdate(filter, update, options).subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.findOneAndUpdate(filter, update, options, _)
    }

    def 'should use the underlying drop'() {
        when:
        def observable = mongoCollection.drop()

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.drop(_)
    }

    def 'should use the underlying createIndex'() {
        given:
        def index = new Document('index', 1)
        def options = new IndexOptions()

        when:
        def observable = mongoCollection.createIndex(index)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.createIndex(index, _, _)

        when:
        mongoCollection.createIndex(index, options).subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.createIndex(index, options, _)
    }

    def 'should use the underlying createIndexes'() {
        given:
        def indexes = [new IndexModel(new Document('index', 1))]

        when:
        def observable = mongoCollection.createIndexes(indexes)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.createIndexes(indexes, _)
    }

    def 'should use the underlying listIndexes'() {
        def wrappedIterable = Stub(ListIndexesIterable)

        when:
        def observable = mongoCollection.listIndexes()

        then:
        1 * wrapped.listIndexes(Document) >> wrappedIterable
        expect observable, isTheSameAs(new ListIndexesObservableImpl(wrappedIterable, observableAdapter))

        when:
        mongoCollection.listIndexes(BsonDocument)

        then:
        1 * wrapped.listIndexes(BsonDocument) >> wrappedIterable
        expect observable, isTheSameAs(new ListIndexesObservableImpl(wrappedIterable, observableAdapter))
    }

    def 'should use the underlying dropIndex'() {
        given:
        def index = 'index'

        when:
        def observable = mongoCollection.dropIndex(index)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.dropIndex(index, _)

        when:
        index = new Document('index', 1)
        mongoCollection.dropIndex(index).subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.dropIndex(_, _)

        when:
        mongoCollection.dropIndexes().subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.dropIndex('*', _)
    }

    def 'should use the underlying renameCollection'() {
        given:
        def nameCollectionNamespace = new MongoNamespace('db', 'coll')
        def options = new RenameCollectionOptions()

        when:
        def observable = mongoCollection.renameCollection(nameCollectionNamespace)

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }

        when:
        observable.subscribe(subscriber())

        then:
        1 * wrapped.renameCollection(nameCollectionNamespace, _, _)

        when:
        mongoCollection.renameCollection(nameCollectionNamespace, options).subscribe(subscriber())

        then:
        1 * observableAdapter.adapt(_) >> { args -> args[0] }
        1 * wrapped.renameCollection(nameCollectionNamespace, options, _)
    }

}
