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

import com.mongodb.CursorType;
import com.mongodb.async.SingleResultCallback;
import org.bson.conversions.Bson;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

import java.util.concurrent.TimeUnit;

import static com.mongodb.assertions.Assertions.notNull;

class FindObservableImpl<TResult> implements FindObservable<TResult> {

    private final com.mongodb.async.client.FindIterable<TResult> wrapped;

    FindObservableImpl(final com.mongodb.async.client.FindIterable<TResult> wrapped) {
        this.wrapped = notNull("wrapped", wrapped);
    }

    @Override
    public Observable<TResult> first() {
        return Observable.create(new SingleResultOnSubscribeAdapter<TResult>() {
            @Override
            void execute(final SingleResultCallback<TResult> callback) {
                wrapped.first(callback);
            }
        });
    }

    @Override
    public FindObservable<TResult> filter(final Bson filter) {
        wrapped.filter(filter);
        return this;
    }

    @Override
    public FindObservable<TResult> limit(final int limit) {
        wrapped.limit(limit);
        return this;
    }

    @Override
    public FindObservable<TResult> skip(final int skip) {
        wrapped.skip(skip);
        return this;
    }

    @Override
    public FindObservable<TResult> maxTime(final long maxTime, final TimeUnit timeUnit) {
        wrapped.maxTime(maxTime, timeUnit);
        return this;
    }

    @Override
    public FindObservable<TResult> modifiers(final Bson modifiers) {
        wrapped.modifiers(modifiers);
        return this;
    }

    @Override
    public FindObservable<TResult> projection(final Bson projection) {
        wrapped.projection(projection);
        return this;
    }

    @Override
    public FindObservable<TResult> sort(final Bson sort) {
        wrapped.sort(sort);
        return this;
    }

    @Override
    public FindObservable<TResult> noCursorTimeout(final boolean noCursorTimeout) {
        wrapped.noCursorTimeout(noCursorTimeout);
        return this;
    }

    @Override
    public FindObservable<TResult> oplogReplay(final boolean oplogReplay) {
        wrapped.oplogReplay(oplogReplay);
        return this;
    }

    @Override
    public FindObservable<TResult> partial(final boolean partial) {
        wrapped.partial(partial);
        return this;
    }

    @Override
    public FindObservable<TResult> cursorType(final CursorType cursorType) {
        wrapped.cursorType(cursorType);
        return this;
    }

    @Override
    public Observable<TResult> toObservable() {
        return MongoIterableObservable.create(wrapped);
    }

    @Override
    public Subscription subscribe(final Subscriber<? super TResult> subscriber) {
        return toObservable().subscribe(subscriber);
    }
}
