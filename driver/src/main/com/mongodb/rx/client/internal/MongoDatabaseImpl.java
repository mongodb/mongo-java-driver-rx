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

package com.mongodb.rx.client.internal;

import com.mongodb.Block;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.Observables;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.CreateViewOptions;
import com.mongodb.rx.client.ListCollectionsObservable;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import com.mongodb.rx.client.ObservableAdapter;
import com.mongodb.rx.client.Success;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import rx.Observable;

import java.util.List;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.rx.client.internal.ObservableHelper.voidToSuccessCallback;

/**
 * The internal MongoDatabase implementation.
 *
 * <p>This should not be considered a part of the public API.</p>
 */
public final class MongoDatabaseImpl implements MongoDatabase {

    private final com.mongodb.async.client.MongoDatabase wrapped;
    private final ObservableAdapter observableAdapter;

    MongoDatabaseImpl(final com.mongodb.async.client.MongoDatabase wrapped, final ObservableAdapter observableAdapter) {
        this.wrapped = notNull("wrapped", wrapped);
        this.observableAdapter = notNull("observableAdapter", observableAdapter);
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public ObservableAdapter getObservableAdapter() {
        return observableAdapter;
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
    public ReadConcern getReadConcern() {
        return wrapped.getReadConcern();
    }

    @Override
    public MongoDatabase withObservableAdapter(final ObservableAdapter observableAdapter) {
        return new MongoDatabaseImpl(wrapped, observableAdapter);
    }

    @Override
    public MongoDatabase withCodecRegistry(final CodecRegistry codecRegistry) {
        return new MongoDatabaseImpl(wrapped.withCodecRegistry(codecRegistry), observableAdapter);
    }

    @Override
    public MongoDatabase withReadPreference(final ReadPreference readPreference) {
        return new MongoDatabaseImpl(wrapped.withReadPreference(readPreference), observableAdapter);
    }

    @Override
    public MongoDatabase withWriteConcern(final WriteConcern writeConcern) {
        return new MongoDatabaseImpl(wrapped.withWriteConcern(writeConcern), observableAdapter);
    }

    @Override
    public MongoDatabase withReadConcern(final ReadConcern readConcern) {
        return new MongoDatabaseImpl(wrapped.withReadConcern(readConcern), observableAdapter);
    }

    @Override
    public MongoCollection<Document> getCollection(final String collectionName) {
        return getCollection(collectionName, Document.class);
    }

    @Override
    public <TDocument> MongoCollection<TDocument> getCollection(final String collectionName, final Class<TDocument> clazz) {
        return new MongoCollectionImpl<TDocument>(wrapped.getCollection(collectionName, clazz), observableAdapter);
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
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<TResult>>() {
            @Override
            public void apply(final SingleResultCallback<TResult> callback) {
                wrapped.runCommand(command, clazz, callback);
            }
        }), observableAdapter);
    }

    @Override
    public <TResult> Observable<TResult> runCommand(final Bson command, final ReadPreference readPreference,
                                                       final Class<TResult> clazz) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<TResult>>() {
            @Override
            public void apply(final SingleResultCallback<TResult> callback) {
                wrapped.runCommand(command, readPreference, clazz, callback);
            }
        }), observableAdapter);
    }

    @Override
    public Observable<Success> drop() {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.drop(voidToSuccessCallback(callback));
            }
        }), observableAdapter);
    }

    @Override
    public Observable<String> listCollectionNames() {
        return RxObservables.create(Observables.observe(wrapped.listCollectionNames()), observableAdapter);
    }

    @Override
    public ListCollectionsObservable<Document> listCollections() {
        return listCollections(Document.class);
    }

    @Override
    public <C> ListCollectionsObservable<C> listCollections(final Class<C> clazz) {
        return new ListCollectionsObservableImpl<C>(wrapped.listCollections(clazz), observableAdapter);
    }

    @Override
    public Observable<Success> createCollection(final String collectionName) {
        return createCollection(collectionName, new CreateCollectionOptions());
    }

    @Override
    public Observable<Success> createCollection(final String collectionName, final CreateCollectionOptions options) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.createCollection(collectionName, options, voidToSuccessCallback(callback));
            }
        }), observableAdapter);
    }

    @Override
    public Observable<Success> createView(final String viewName, final String viewOn, final List<? extends Bson> pipeline) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.createView(viewName, viewOn, pipeline, voidToSuccessCallback(callback));
            }
        }), observableAdapter);
    }

    @Override
    public Observable<Success> createView(final String viewName, final String viewOn, final List<? extends Bson> pipeline,
                                          final CreateViewOptions createViewOptions) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.createView(viewName, viewOn, pipeline, createViewOptions, voidToSuccessCallback(callback));
            }
        }), observableAdapter);
    }

    /**
     * Gets the wrapped MongoDatabase
     *
     * <p>This should not be considered a part of the public API.</p>
     * @return wrapped MongoDatabase
     */
    public com.mongodb.async.client.MongoDatabase getWrapped() {
        return wrapped;
    }
}
