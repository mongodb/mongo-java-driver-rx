/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.rx.client;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.Observables;
import com.mongodb.client.model.MapReduceAction;
import org.bson.conversions.Bson;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

import java.util.concurrent.TimeUnit;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.rx.client.ObservableHelper.voidToSuccessCallback;

class MapReduceObservableImpl<TResult> implements MapReduceObservable<TResult> {

    private final com.mongodb.async.client.MapReduceIterable<TResult> wrapped;
    private final ObservableAdapter observableAdapter;

    MapReduceObservableImpl(final com.mongodb.async.client.MapReduceIterable<TResult> wrapped, final ObservableAdapter observableAdapter) {
        this.wrapped = notNull("wrapped", wrapped);
        this.observableAdapter = notNull("observableAdapter", observableAdapter);
    }

    @Override
    public MapReduceObservable<TResult> collectionName(final String collectionName) {
        wrapped.collectionName(collectionName);
        return this;
    }

    @Override
    public MapReduceObservable<TResult> finalizeFunction(final String finalizeFunction) {
        wrapped.finalizeFunction(finalizeFunction);
        return this;
    }

    @Override
    public MapReduceObservable<TResult> scope(final Bson scope) {
        wrapped.scope(scope);
        return this;
    }

    @Override
    public MapReduceObservable<TResult> sort(final Bson sort) {
        wrapped.sort(sort);
        return this;
    }

    @Override
    public MapReduceObservable<TResult> filter(final Bson filter) {
        wrapped.filter(filter);
        return this;
    }

    @Override
    public MapReduceObservable<TResult> limit(final int limit) {
        wrapped.limit(limit);
        return this;
    }

    @Override
    public MapReduceObservable<TResult> jsMode(final boolean jsMode) {
        wrapped.jsMode(jsMode);
        return this;
    }

    @Override
    public MapReduceObservable<TResult> verbose(final boolean verbose) {
        wrapped.verbose(verbose);
        return this;
    }

    @Override
    public MapReduceObservable<TResult> maxTime(final long maxTime, final TimeUnit timeUnit) {
        wrapped.maxTime(maxTime, timeUnit);
        return this;
    }

    @Override
    public MapReduceObservable<TResult> action(final MapReduceAction action) {
        wrapped.action(action);
        return this;
    }

    @Override
    public MapReduceObservable<TResult> databaseName(final String databaseName) {
        wrapped.databaseName(databaseName);
        return this;
    }

    @Override
    public MapReduceObservable<TResult> sharded(final boolean sharded) {
        wrapped.sharded(sharded);
        return this;
    }

    @Override
    public MapReduceObservable<TResult> nonAtomic(final boolean nonAtomic) {
        wrapped.nonAtomic(nonAtomic);
        return this;
    }

    @Override
    public MapReduceObservable<TResult> bypassDocumentValidation(final Boolean bypassDocumentValidation) {
        wrapped.bypassDocumentValidation(bypassDocumentValidation);
        return this;
    }

    @Override
    public Observable<Success> toCollection() {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.toCollection(voidToSuccessCallback(callback));
            }
        }), observableAdapter);
    }

    @Override
    public Observable<TResult> toObservable() {
        return RxObservables.create(Observables.observe(wrapped), observableAdapter);
    }

    @Override
    public Subscription subscribe(final Subscriber<? super TResult> subscriber) {
        return toObservable().subscribe(subscriber);
    }
}
