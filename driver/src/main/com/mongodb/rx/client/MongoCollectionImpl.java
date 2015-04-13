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
import com.mongodb.async.SingleResultCallback;
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
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import rx.Observable;

import java.util.List;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.rx.client.ObservableHelper.voidToSuccessCallback;

class MongoCollectionImpl<TDocument> implements MongoCollection<TDocument> {

    private final com.mongodb.async.client.MongoCollection<TDocument> wrapped;

    MongoCollectionImpl(final com.mongodb.async.client.MongoCollection<TDocument> wrapped) {
        this.wrapped = notNull("wrapped", wrapped);
    }

    @Override
    public MongoNamespace getNamespace() {
        return wrapped.getNamespace();
    }

    @Override
    public Class<TDocument> getDocumentClass() {
        return wrapped.getDocumentClass();
    }

    @Override
    public CodecRegistry getCodecRegistry() {
        return wrapped.getCodecRegistry();
    }

    @Override
    public ReadPreference getReadPreference() {
        return wrapped.getReadPreference();
    }

    @Override
    public WriteConcern getWriteConcern() {
        return wrapped.getWriteConcern();
    }

    @Override
    public <NewTDocument> MongoCollection<NewTDocument> withDocumentClass(final Class<NewTDocument> clazz) {
        return new MongoCollectionImpl<NewTDocument>(wrapped.withDocumentClass(clazz));
    }

    @Override
    public MongoCollection<TDocument> withCodecRegistry(final CodecRegistry codecRegistry) {
        return new MongoCollectionImpl<TDocument>(wrapped.withCodecRegistry(codecRegistry));
    }

    @Override
    public MongoCollection<TDocument> withReadPreference(final ReadPreference readPreference) {
        return new MongoCollectionImpl<TDocument>(wrapped.withReadPreference(readPreference));
    }

    @Override
    public MongoCollection<TDocument> withWriteConcern(final WriteConcern writeConcern) {
        return new MongoCollectionImpl<TDocument>(wrapped.withWriteConcern(writeConcern));
    }

    @Override
    public Observable<Long> count() {
        return count(new BsonDocument(), new CountOptions());
    }

    @Override
    public Observable<Long> count(final Bson filter) {
        return count(filter, new CountOptions());
    }

    @Override
    public Observable<Long> count(final Bson filter, final CountOptions options) {
        return Observable.create(new SingleResultOnSubscribeAdapter<Long>() {
            @Override
            void execute(final SingleResultCallback<Long> callback) {
                wrapped.count(filter, options, callback);
            }
        });
    }

    @Override
    public <TResult> DistinctObservable<TResult> distinct(final String fieldName, final Class<TResult> clazz) {
        return new DistinctObservableImpl<TResult>(wrapped.distinct(fieldName, clazz));
    }

    @Override
    public FindObservable<TDocument> find() {
        return find(new BsonDocument(), getDocumentClass());
    }

    @Override
    public <TResult> FindObservable<TResult> find(final Class<TResult> clazz) {
        return find(new BsonDocument(), clazz);
    }

    @Override
    public FindObservable<TDocument> find(final Bson filter) {
        return find(filter, getDocumentClass());
    }

    @Override
    public <TResult> FindObservable<TResult> find(final Bson filter, final Class<TResult> clazz) {
        return new FindObservableImpl<TResult>(wrapped.find(filter, clazz));
    }

    @Override
    public AggregateObservable<Document> aggregate(final List<? extends Bson> pipeline) {
        return aggregate(pipeline, Document.class);
    }

    @Override
    public <TResult> AggregateObservable<TResult> aggregate(final List<? extends Bson> pipeline, final Class<TResult> clazz) {
        return new AggregateObservableImpl<TResult>(wrapped.aggregate(pipeline, clazz));
    }

    @Override
    public MapReduceObservable<Document> mapReduce(final String mapFunction, final String reduceFunction) {
        return mapReduce(mapFunction, reduceFunction, Document.class);
    }

    @Override
    public <TResult> MapReduceObservable<TResult> mapReduce(final String mapFunction, final String reduceFunction,
                                                           final Class<TResult> clazz) {
        return new MapReduceObservableImpl<TResult>(wrapped.mapReduce(mapFunction, reduceFunction, clazz));
    }

    @Override
    public Observable<BulkWriteResult> bulkWrite(final List<? extends WriteModel<? extends TDocument>> requests) {
        return bulkWrite(requests, new BulkWriteOptions());
    }

    @Override
    public Observable<BulkWriteResult> bulkWrite(final List<? extends WriteModel<? extends TDocument>> requests,
                                                final BulkWriteOptions options) {
        return Observable.create(new SingleResultOnSubscribeAdapter<BulkWriteResult>() {
            @Override
            void execute(final SingleResultCallback<BulkWriteResult> callback) {
                wrapped.bulkWrite(requests, options, callback);
            }
        });
    }

    @Override
    public Observable<Success> insertOne(final TDocument document) {
        return Observable.create(new SingleResultOnSubscribeAdapter<Success>() {
            @Override
            void execute(final SingleResultCallback<Success> callback) {
                wrapped.insertOne(document, voidToSuccessCallback(callback));
            }
        });
    }

    @Override
    public Observable<Success> insertMany(final List<? extends TDocument> documents) {
        return insertMany(documents, new InsertManyOptions());
    }

    @Override
    public Observable<Success> insertMany(final List<? extends TDocument> documents, final InsertManyOptions options) {
        return Observable.create(new SingleResultOnSubscribeAdapter<Success>() {
            @Override
            void execute(final SingleResultCallback<Success> callback) {
                wrapped.insertMany(documents, options, voidToSuccessCallback(callback));
            }
        });
    }

    @Override
    public Observable<DeleteResult> deleteOne(final Bson filter) {
        return Observable.create(new SingleResultOnSubscribeAdapter<DeleteResult>() {
            @Override
            void execute(final SingleResultCallback<DeleteResult> callback) {
                wrapped.deleteOne(filter, callback);
            }
        });
    }

    @Override
    public Observable<DeleteResult> deleteMany(final Bson filter) {
        return Observable.create(new SingleResultOnSubscribeAdapter<DeleteResult>() {
            @Override
            void execute(final SingleResultCallback<DeleteResult> callback) {
                wrapped.deleteMany(filter, callback);
            }
        });
    }

    @Override
    public Observable<UpdateResult> replaceOne(final Bson filter, final TDocument replacement) {
        return replaceOne(filter, replacement, new UpdateOptions());
    }

    @Override
    public Observable<UpdateResult> replaceOne(final Bson filter, final TDocument replacement, final UpdateOptions options) {
        return Observable.create(new SingleResultOnSubscribeAdapter<UpdateResult>() {
            @Override
            void execute(final SingleResultCallback<UpdateResult> callback) {
                wrapped.replaceOne(filter, replacement, options, callback);
            }
        });
    }

    @Override
    public Observable<UpdateResult> updateOne(final Bson filter, final Bson update) {
        return updateOne(filter, update, new UpdateOptions());
    }

    @Override
    public Observable<UpdateResult> updateOne(final Bson filter, final Bson update, final UpdateOptions options) {
        return Observable.create(new SingleResultOnSubscribeAdapter<UpdateResult>() {
            @Override
            void execute(final SingleResultCallback<UpdateResult> callback) {
                wrapped.updateOne(filter, update, options, callback);
            }
        });
    }

    @Override
    public Observable<UpdateResult> updateMany(final Bson filter, final Bson update) {
        return updateMany(filter, update, new UpdateOptions());
    }

    @Override
    public Observable<UpdateResult> updateMany(final Bson filter, final Bson update, final UpdateOptions options) {
        return Observable.create(new SingleResultOnSubscribeAdapter<UpdateResult>() {
            @Override
            void execute(final SingleResultCallback<UpdateResult> callback) {
                wrapped.updateMany(filter, update, options, callback);
            }
        });
    }

    @Override
    public Observable<TDocument> findOneAndDelete(final Bson filter) {
        return findOneAndDelete(filter, new FindOneAndDeleteOptions());
    }

    @Override
    public Observable<TDocument> findOneAndDelete(final Bson filter, final FindOneAndDeleteOptions options) {
        return Observable.create(new SingleResultOnSubscribeAdapter<TDocument>() {
            @Override
            void execute(final SingleResultCallback<TDocument> callback) {
                wrapped.findOneAndDelete(filter, options, callback);
            }
        });
    }

    @Override
    public Observable<TDocument> findOneAndReplace(final Bson filter, final TDocument replacement) {
        return findOneAndReplace(filter, replacement, new FindOneAndReplaceOptions());
    }

    @Override
    public Observable<TDocument> findOneAndReplace(final Bson filter, final TDocument replacement, final FindOneAndReplaceOptions options) {
        return Observable.create(new SingleResultOnSubscribeAdapter<TDocument>() {
            @Override
            void execute(final SingleResultCallback<TDocument> callback) {
                wrapped.findOneAndReplace(filter, replacement, options, callback);
            }
        });
    }

    @Override
    public Observable<TDocument> findOneAndUpdate(final Bson filter, final Bson update) {
        return findOneAndUpdate(filter, update, new FindOneAndUpdateOptions());
    }

    @Override
    public Observable<TDocument> findOneAndUpdate(final Bson filter, final Bson update, final FindOneAndUpdateOptions options) {
        return Observable.create(new SingleResultOnSubscribeAdapter<TDocument>() {
            @Override
            void execute(final SingleResultCallback<TDocument> callback) {
                wrapped.findOneAndUpdate(filter, update, options, callback);
            }
        });
    }

    @Override
    public Observable<Success> drop() {
        return Observable.create(new SingleResultOnSubscribeAdapter<Success>() {
            @Override
            void execute(final SingleResultCallback<Success> callback) {
                wrapped.drop(voidToSuccessCallback(callback));
            }
        });
    }

    @Override
    public Observable<String> createIndex(final Bson key) {
        return createIndex(key, new IndexOptions());
    }

    @Override
    public Observable<String> createIndex(final Bson key, final IndexOptions options) {
        return Observable.create(new SingleResultOnSubscribeAdapter<String>() {
            @Override
            void execute(final SingleResultCallback<String> callback) {
                wrapped.createIndex(key, options, callback);
            }
        });
    }

    @Override
    public Observable<String> createIndexes(final List<IndexModel> indexes) {
        return Observable.create(new SingleResultListOnSubscribeAdapter<String>() {
            @Override
            void execute(final SingleResultCallback<List<String>> callback) {
                wrapped.createIndexes(indexes, callback);
            }
        });
    }

    @Override
    public ListIndexesObservable<Document> listIndexes() {
        return listIndexes(Document.class);
    }

    @Override
    public <TResult> ListIndexesObservable<TResult> listIndexes(final Class<TResult> clazz) {
        return new ListIndexesObservableImpl<TResult>(wrapped.listIndexes(clazz));
    }

    @Override
    public Observable<Success> dropIndex(final String indexName) {
        return Observable.create(new SingleResultOnSubscribeAdapter<Success>() {
            @Override
            void execute(final SingleResultCallback<Success> callback) {
                wrapped.dropIndex(indexName, voidToSuccessCallback(callback));
            }
        });
    }

    @Override
    public Observable<Success> dropIndex(final Bson keys) {
        return Observable.create(new SingleResultOnSubscribeAdapter<Success>() {
            @Override
            void execute(final SingleResultCallback<Success> callback) {
                wrapped.dropIndex(keys, voidToSuccessCallback(callback));
            }
        });
    }

    @Override
    public Observable<Success> dropIndexes() {
        return dropIndex("*");
    }

    @Override
    public Observable<Success> renameCollection(final MongoNamespace newCollectionNamespace) {
        return renameCollection(newCollectionNamespace, new RenameCollectionOptions());
    }

    @Override
    public Observable<Success> renameCollection(final MongoNamespace newCollectionNamespace, final RenameCollectionOptions options) {
        return Observable.create(new SingleResultOnSubscribeAdapter<Success>() {
            @Override
            void execute(final SingleResultCallback<Success> callback) {
                wrapped.renameCollection(newCollectionNamespace, options, voidToSuccessCallback(callback));
            }
        });
    }

}
