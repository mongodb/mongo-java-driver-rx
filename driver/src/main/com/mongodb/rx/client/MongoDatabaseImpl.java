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

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.client.model.CreateCollectionOptions;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import rx.Observable;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.rx.client.ObservableHelper.voidToSuccessCallback;

class MongoDatabaseImpl implements MongoDatabase {

    private final com.mongodb.async.client.MongoDatabase wrapped;

    MongoDatabaseImpl(final com.mongodb.async.client.MongoDatabase wrapped) {
        this.wrapped = notNull("wrapped", wrapped);
    }

    @Override
    public String getName() {
        return wrapped.getName();
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
    public MongoDatabase withCodecRegistry(final CodecRegistry codecRegistry) {
        return new MongoDatabaseImpl(wrapped.withCodecRegistry(codecRegistry));
    }

    @Override
    public MongoDatabase withReadPreference(final ReadPreference readPreference) {
        return new MongoDatabaseImpl(wrapped.withReadPreference(readPreference));
    }

    @Override
    public MongoDatabase withWriteConcern(final WriteConcern writeConcern) {
        return new MongoDatabaseImpl(wrapped.withWriteConcern(writeConcern));
    }

    @Override
    public MongoCollection<Document> getCollection(final String collectionName) {
        return getCollection(collectionName, Document.class);
    }

    @Override
    public <TDocument> MongoCollection<TDocument> getCollection(final String collectionName, final Class<TDocument> clazz) {
        return new MongoCollectionImpl<TDocument>(wrapped.getCollection(collectionName, clazz));
    }

    @Override
    public Observable<Document> runCommand(final Bson command) {
        return runCommand(command, Document.class);
    }

    @Override
    public Observable<Document> runCommand(final Bson command, final ReadPreference readPreference) {
        return runCommand(command, readPreference, Document.class);
    }

    @Override
    public <TResult> Observable<TResult> runCommand(final Bson command, final Class<TResult> clazz) {
        return Observable.create(new SingleResultOnSubscribeAdapter<TResult>() {
            @Override
            void execute(final SingleResultCallback<TResult> callback) {
                wrapped.runCommand(command, clazz, callback);
            }
        });
    }

    @Override
    public <TResult> Observable<TResult> runCommand(final Bson command, final ReadPreference readPreference,
                                                       final Class<TResult> clazz) {
        return Observable.create(new SingleResultOnSubscribeAdapter<TResult>() {
            @Override
            void execute(final SingleResultCallback<TResult> callback) {
                wrapped.runCommand(command, readPreference, clazz, callback);
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
    public Observable<String> listCollectionNames() {
        return MongoIterableObservable.create(wrapped.listCollectionNames());
    }

    @Override
    public ListCollectionsObservable<Document> listCollections() {
        return listCollections(Document.class);
    }

    @Override
    public <C> ListCollectionsObservable<C> listCollections(final Class<C> clazz) {
        return new ListCollectionsObservableImpl<C>(wrapped.listCollections(clazz));
    }

    @Override
    public Observable<Success> createCollection(final String collectionName) {
        return createCollection(collectionName, new CreateCollectionOptions());
    }

    @Override
    public Observable<Success> createCollection(final String collectionName, final CreateCollectionOptions options) {
        return Observable.create(new SingleResultOnSubscribeAdapter<Success>() {
            @Override
            void execute(final SingleResultCallback<Success> callback) {
                wrapped.createCollection(collectionName, options, voidToSuccessCallback(callback));
            }
        });
    }
}
