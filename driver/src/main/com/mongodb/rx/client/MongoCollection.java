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

package com.mongodb.rx.client;

import com.mongodb.MongoNamespace;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.annotations.ThreadSafe;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.RenameCollectionOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import rx.Observable;

import java.util.List;

/**
 * The MongoCollection interface.
 *
 * <p>Note: Additions to this interface will not be considered to break binary compatibility.</p>
 *
 * @param <TDocument> The type that this collection will encode documents from and decode documents to.
 * @since 1.0
 */
@ThreadSafe
public interface MongoCollection<TDocument> {

    /**
     * Gets the namespace of this collection.
     *
     * @return the namespace
     */
    MongoNamespace getNamespace();


    /**
     * Get the class of documents stored in this collection.
     *
     * @return the class
     */
    Class<TDocument> getDocumentClass();

    /**
     * Get the codec registry for the MongoCollection.
     *
     * @return the {@link org.bson.codecs.configuration.CodecRegistry}
     */
    CodecRegistry getCodecRegistry();

    /**
     * Get the read preference for the MongoCollection.
     *
     * @return the {@link com.mongodb.ReadPreference}
     */
    ReadPreference getReadPreference();

    /**
     * Get the write concern for the MongoCollection.
     *
     * @return the {@link com.mongodb.WriteConcern}
     */
    WriteConcern getWriteConcern();

    /**
     * Create a new MongoCollection instance with a different default class to cast any documents returned from the database into..
     *
     * @param clazz          the default class to cast any documents returned from the database into.
     * @param <NewTDocument> The type that the new collection will encode documents from and decode documents to
     * @return a new MongoCollection instance with the different default class
     */
    <NewTDocument> MongoCollection<NewTDocument> withDocumentClass(Class<NewTDocument> clazz);

    /**
     * Create a new MongoCollection instance with a different codec registry.
     *
     * @param codecRegistry the new {@link org.bson.codecs.configuration.CodecRegistry} for the collection
     * @return a new MongoCollection instance with the different codec registry
     */
    MongoCollection<TDocument> withCodecRegistry(CodecRegistry codecRegistry);

    /**
     * Create a new MongoCollection instance with a different read preference.
     *
     * @param readPreference the new {@link com.mongodb.ReadPreference} for the collection
     * @return a new MongoCollection instance with the different readPreference
     */
    MongoCollection<TDocument> withReadPreference(ReadPreference readPreference);

    /**
     * Create a new MongoCollection instance with a different write concern.
     *
     * @param writeConcern the new {@link com.mongodb.WriteConcern} for the collection
     * @return a new MongoCollection instance with the different writeConcern
     */
    MongoCollection<TDocument> withWriteConcern(WriteConcern writeConcern);

    /**
     * Counts the number of documents in the collection.
     *
     * @return an Observable with a single element indicating the number of documents
     */
    Observable<Long> count();

    /**
     * Counts the number of documents in the collection according to the given options.
     *
     * @param filter the query filter
     * @return an Observable with a single element indicating the number of documents
     */
    Observable<Long> count(Bson filter);

    /**
     * Counts the number of documents in the collection according to the given options.
     *
     * @param filter  the query filter
     * @param options the options describing the count
     * @return an Observable with a single element indicating the number of documents
     */
    Observable<Long> count(Bson filter, CountOptions options);

    /**
     * Gets the distinct values of the specified field name.
     *
     * @param fieldName   the field name
     * @param resultClass the default class to cast any distinct items into.
     * @param <TResult>   the target type of the iterable.
     * @return an Observable emitting the sequence of distinct values
     * @mongodb.driver.manual reference/command/distinct/ Distinct
     */
    <TResult> DistinctObservable<TResult> distinct(String fieldName, Class<TResult> resultClass);

    /**
     * Gets the distinct values of the specified field name.
     *
     * @param fieldName   the field name
     * @param filter      the query filter
     * @param resultClass the default class to cast any distinct items into.
     * @param <TResult>   the target type of the iterable.
     * @return an iterable of distinct values
     * @mongodb.driver.manual reference/command/distinct/ Distinct
     */
    <TResult> DistinctObservable<TResult> distinct(String fieldName, Bson filter, Class<TResult> resultClass);

    /**
     * Finds all documents in the collection.
     *
     * @return the fluent find interface
     * @mongodb.driver.manual tutorial/query-documents/ Find
     */
    FindObservable<TDocument> find();

    /**
     * Finds all documents in the collection.
     *
     * @param clazz     the class to decode each document into
     * @param <TResult> the target document type of the iterable.
     * @return the fluent find interface
     * @mongodb.driver.manual tutorial/query-documents/ Find
     */
    <TResult> FindObservable<TResult> find(Class<TResult> clazz);

    /**
     * Finds all documents in the collection.
     *
     * @param filter the query filter
     * @return the fluent find interface
     * @mongodb.driver.manual tutorial/query-documents/ Find
     */
    FindObservable<TDocument> find(Bson filter);

    /**
     * Finds all documents in the collection.
     *
     * @param filter    the query filter
     * @param clazz     the class to decode each document into
     * @param <TResult> the target document type of the iterable.
     * @return the fluent find interface
     * @mongodb.driver.manual tutorial/query-documents/ Find
     */
    <TResult> FindObservable<TResult> find(Bson filter, Class<TResult> clazz);

    /**
     * Aggregates documents according to the specified aggregation pipeline.
     *
     * @param pipeline the aggregate pipeline
     * @return an Observable containing the result of the aggregation operation
     * @mongodb.driver.manual aggregation/ Aggregation
     */
    AggregateObservable<Document> aggregate(List<? extends Bson> pipeline);

    /**
     * Aggregates documents according to the specified aggregation pipeline.
     *
     * @param pipeline  the aggregate pipeline
     * @param clazz     the class to decode each document into
     * @param <TResult> the target document type of the iterable.
     * @return an Observable containing the result of the aggregation operation
     * @mongodb.driver.manual aggregation/ Aggregation
     */
    <TResult> AggregateObservable<TResult> aggregate(List<? extends Bson> pipeline, Class<TResult> clazz);


    /**
     * Aggregates documents according to the specified map-reduce function.
     *
     * @param mapFunction    A JavaScript function that associates or "maps" a value with a key and emits the key and value pair.
     * @param reduceFunction A JavaScript function that "reduces" to a single object all the values associated with a particular key.
     * @return an Observable  containing the result of the map-reduce operation
     * @mongodb.driver.manual reference/command/mapReduce/ map-reduce
     */
    MapReduceObservable<Document> mapReduce(String mapFunction, String reduceFunction);

    /**
     * Aggregates documents according to the specified map-reduce function.
     *
     * @param mapFunction    A JavaScript function that associates or "maps" a value with a key and emits the key and value pair.
     * @param reduceFunction A JavaScript function that "reduces" to a single object all the values associated with a particular key.
     * @param clazz          the class to decode each resulting document into.
     * @param <TResult>      the target document type of the iterable.
     * @return an Observable containing the result of the map-reduce operation
     * @mongodb.driver.manual reference/command/mapReduce/ map-reduce
     */
    <TResult> MapReduceObservable<TResult> mapReduce(String mapFunction, String reduceFunction, Class<TResult> clazz);

    /**
     * Executes a mix of inserts, updates, replaces, and deletes.
     *
     * @param requests the writes to execute
     * @return an Observable with a single element the BulkWriteResult
     */
    Observable<BulkWriteResult> bulkWrite(List<? extends WriteModel<? extends TDocument>> requests);

    /**
     * Executes a mix of inserts, updates, replaces, and deletes.
     *
     * @param requests the writes to execute
     * @param options  the options to apply to the bulk write operation
     * @return an Observable with a single element the BulkWriteResult
     */
    Observable<BulkWriteResult> bulkWrite(List<? extends WriteModel<? extends TDocument>> requests, BulkWriteOptions options);

    /**
     * Inserts the provided document. If the document is missing an identifier, the driver should generate one.
     *
     * @param document the document to insert
     * @return an Observable with a single element indicating when the operation has completed or with either a
     * com.mongodb.DuplicateKeyException or com.mongodb.MongoException
     */
    Observable<Success> insertOne(TDocument document);

    /**
     * Inserts a batch of documents. The preferred way to perform bulk inserts is to use the BulkWrite API. However, when talking with a
     * server &lt; 2.6, using this method will be faster due to constraints in the bulk API related to error handling.
     *
     * @param documents the documents to insert
     * @return an Observable with a single element indicating when the operation has completed or with either a
     * com.mongodb.DuplicateKeyException or com.mongodb.MongoException
     */
    Observable<Success> insertMany(List<? extends TDocument> documents);

    /**
     * Inserts a batch of documents. The preferred way to perform bulk inserts is to use the BulkWrite API. However, when talking with a
     * server &lt; 2.6, using this method will be faster due to constraints in the bulk API related to error handling.
     *
     * @param documents the documents to insert
     * @param options   the options to apply to the operation
     * @return an Observable with a single element indicating when the operation has completed or with either a
     * com.mongodb.DuplicateKeyException or com.mongodb.MongoException
     */
    Observable<Success> insertMany(List<? extends TDocument> documents, InsertManyOptions options);

    /**
     * Removes at most one document from the collection that matches the given filter.  If no documents match, the collection is not
     * modified.
     *
     * @param filter the query filter to apply the the delete operation
     * @return an Observable with a single element the DeleteResult or with an com.mongodb.MongoException
     */
    Observable<DeleteResult> deleteOne(Bson filter);

    /**
     * Removes all documents from the collection that match the given query filter.  If no documents match, the collection is not modified.
     *
     * @param filter the query filter to apply the the delete operation
     * @return an Observable with a single element the DeleteResult or with an com.mongodb.MongoException
     */
    Observable<DeleteResult> deleteMany(Bson filter);

    /**
     * Replace a document in the collection according to the specified arguments.
     *
     * @param filter      the query filter to apply the the replace operation
     * @param replacement the replacement document
     * @return an Observable with a single element the UpdateResult
     * @mongodb.driver.manual tutorial/modify-documents/#replace-the-document Replace
     */
    Observable<UpdateResult> replaceOne(Bson filter, TDocument replacement);

    /**
     * Replace a document in the collection according to the specified arguments.
     *
     * @param filter      the query filter to apply the the replace operation
     * @param replacement the replacement document
     * @param options     the options to apply to the replace operation
     * @return an Observable with a single element the UpdateResult
     * @mongodb.driver.manual tutorial/modify-documents/#replace-the-document Replace
     */
    Observable<UpdateResult> replaceOne(Bson filter, TDocument replacement, UpdateOptions options);

    /**
     * Update a single document in the collection according to the specified arguments.
     *
     * @param filter a document describing the query filter, which may not be null.
     * @param update a document describing the update, which may not be null. The update to apply must include only update operators.
     * @return an Observable with a single element the UpdateResult
     * @mongodb.driver.manual tutorial/modify-documents/ Updates
     * @mongodb.driver.manual reference/operator/update/ Update Operators
     */
    Observable<UpdateResult> updateOne(Bson filter, Bson update);

    /**
     * Update a single document in the collection according to the specified arguments.
     *
     * @param filter  a document describing the query filter, which may not be null.
     * @param update  a document describing the update, which may not be null. The update to apply must include only update operators.
     * @param options the options to apply to the update operation
     * @return an Observable with a single element the UpdateResult
     * @mongodb.driver.manual tutorial/modify-documents/ Updates
     * @mongodb.driver.manual reference/operator/update/ Update Operators
     */
    Observable<UpdateResult> updateOne(Bson filter, Bson update, UpdateOptions options);

    /**
     * Update a single document in the collection according to the specified arguments.
     *
     * @param filter a document describing the query filter, which may not be null.
     * @param update a document describing the update, which may not be null. The update to apply must include only update operators.
     * @return an Observable with a single element the UpdateResult
     * @mongodb.driver.manual tutorial/modify-documents/ Updates
     * @mongodb.driver.manual reference/operator/update/ Update Operators
     */
    Observable<UpdateResult> updateMany(Bson filter, Bson update);

    /**
     * Update a single document in the collection according to the specified arguments.
     *
     * @param filter  a document describing the query filter, which may not be null.
     * @param update  a document describing the update, which may not be null. The update to apply must include only update operators.
     * @param options the options to apply to the update operation
     * @return an Observable with a single element the UpdateResult
     * @mongodb.driver.manual tutorial/modify-documents/ Updates
     * @mongodb.driver.manual reference/operator/update/ Update Operators
     */
    Observable<UpdateResult> updateMany(Bson filter, Bson update, UpdateOptions options);

    /**
     * Atomically find a document and remove it.
     *
     * @param filter the query filter to find the document with
     * @return an Observable with a single element the document that was removed. If no documents matched the query filter, then the
     * observer will complete without emitting any items
     */
    Observable<TDocument> findOneAndDelete(Bson filter);

    /**
     * Atomically find a document and remove it.
     *
     * @param filter  the query filter to find the document with
     * @param options the options to apply to the operation
     * @return an Observable with a single element the document that was removed.  If no documents matched the query filter, then the
     * observer will complete without emitting any items
     */
    Observable<TDocument> findOneAndDelete(Bson filter, FindOneAndDeleteOptions options);

    /**
     * Atomically find a document and replace it.
     *
     * @param filter      the query filter to apply the the replace operation
     * @param replacement the replacement document
     * @return an Observable with a single element the document that was replaced.  Depending on the value of the {@code returnOriginal}
     * property, this will either be the document as it was before the update or as it is after the update.  If no documents matched the
     * query filter, then the observer will complete without emitting any items
     */
    Observable<TDocument> findOneAndReplace(Bson filter, TDocument replacement);

    /**
     * Atomically find a document and replace it.
     *
     * @param filter      the query filter to apply the the replace operation
     * @param replacement the replacement document
     * @param options     the options to apply to the operation
     * @return an Observable with a single element the document that was replaced.  Depending on the value of the {@code returnOriginal}
     * property, this will either be the document as it was before the update or as it is after the update.
     * If no documents matched the query filter, then the observer will complete without emitting any items
     */
    Observable<TDocument> findOneAndReplace(Bson filter, TDocument replacement, FindOneAndReplaceOptions options);

    /**
     * Atomically find a document and update it.
     *
     * @param filter a document describing the query filter, which may not be null.
     * @param update a document describing the update, which may not be null. The update to apply must include only update operators.
     * @return an Observable with a single element the document that was updated before the update was applied.
     * If no documents matched the query filter, then the observer will complete without emitting any items
     */
    Observable<TDocument> findOneAndUpdate(Bson filter, Bson update);

    /**
     * Atomically find a document and update it.
     *
     * @param filter  a document describing the query filter, which may not be null.
     * @param update  a document describing the update, which may not be null. The update to apply must include only update operators.
     * @param options the options to apply to the operation
     * @return an Observable with a single element the document that was updated.  Depending on the value of the {@code returnOriginal}
     * property, this will either be the document as it was before the update or as it is after the update.
     * If no documents matched the query filter, then the observer will complete without emitting any items
     */
    Observable<TDocument> findOneAndUpdate(Bson filter, Bson update, FindOneAndUpdateOptions options);

    /**
     * Drops this collection from the Database.
     *
     * @return an Observable with a single element indicating when the operation has completed
     * @mongodb.driver.manual reference/command/drop/ Drop Collection
     */
    Observable<Success> drop();

    /**
     * Creates an index.
     *
     * @param key an object describing the index key(s), which may not be null.
     * @return an Observable with a single element indicating when the operation has completed
     * @mongodb.driver.manual reference/method/db.collection.ensureIndex Ensure Index
     */
    Observable<String> createIndex(Bson key);

    /**
     * Creates an index.
     *
     * @param key     an object describing the index key(s), which may not be null.
     * @param options the options for the index
     * @return an Observable with a single element indicating when the operation has completed
     * @mongodb.driver.manual reference/method/db.collection.ensureIndex Ensure Index
     */
    Observable<String> createIndex(Bson key, IndexOptions options);


    /**
     * Create multiple indexes.
     *
     * @param indexes the list of indexes
     * @return an Observable with a single element indicating when the operation has completed
     * @mongodb.driver.manual reference/command/createIndexes Create indexes
     * @mongodb.server.release 2.6
     */
    Observable<String> createIndexes(List<IndexModel> indexes);

    /**
     * Get all the indexes in this collection.
     *
     * @return the fluent list indexes interface
     * @mongodb.driver.manual reference/command/listIndexes/ listIndexes
     */
    ListIndexesObservable<Document> listIndexes();

    /**
     * Get all the indexes in this collection.
     *
     * @param clazz     the class to decode each document into
     * @param <TResult> the target document type of the iterable.
     * @return the fluent list indexes interface
     * @mongodb.driver.manual reference/command/listIndexes/ listIndexes
     */
    <TResult> ListIndexesObservable<TResult> listIndexes(Class<TResult> clazz);

    /**
     * Drops the given index.
     *
     * @param indexName the name of the index to remove
     * @return an Observable with a single element indicating when the operation has completed
     * @mongodb.driver.manual reference/command/dropIndexes/ Drop Indexes
     */
    Observable<Success> dropIndex(String indexName);

    /**
     * Drops the index given the keys used to create it.
     *
     * @param keys the keys of the index to remove
     * @return an Observable with a single element indicating when the operation has completed
     * @mongodb.driver.manual reference/command/dropIndexes/ Drop indexes
     */
    Observable<Success> dropIndex(Bson keys);

    /**
     * Drop all the indexes on this collection, except for the default on _id.
     *
     * @return an Observable with a single element indicating when the operation has completed
     * @mongodb.driver.manual reference/command/dropIndexes/ Drop Indexes
     */
    Observable<Success> dropIndexes();

    /**
     * Rename the collection with oldCollectionName to the newCollectionName.
     *
     * @param newCollectionNamespace the namespace the collection will be renamed to
     * @return an Observable with a single element indicating when the operation has completed
     * @mongodb.driver.manual reference/commands/renameCollection Rename collection
     */
    Observable<Success> renameCollection(MongoNamespace newCollectionNamespace);

    /**
     * Rename the collection with oldCollectionName to the newCollectionName.
     *
     * @param newCollectionNamespace the name the collection will be renamed to
     * @param options                the options for renaming a collection
     * @return an Observable with a single element indicating when the operation has completed
     * @mongodb.driver.manual reference/commands/renameCollection Rename collection
     */
    Observable<Success> renameCollection(MongoNamespace newCollectionNamespace, RenameCollectionOptions options);

}
