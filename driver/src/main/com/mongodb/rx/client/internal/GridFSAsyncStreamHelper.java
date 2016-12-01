/*
 * Copyright 2016 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.rx.client.internal;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.Observables;
import com.mongodb.rx.client.ObservableAdapter;
import com.mongodb.rx.client.Success;
import com.mongodb.rx.client.gridfs.AsyncInputStream;
import com.mongodb.rx.client.gridfs.AsyncOutputStream;
import rx.Observable;
import rx.Subscriber;

import java.nio.ByteBuffer;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.rx.client.internal.ObservableHelper.voidToSuccessCallback;

/**
 * Internal GridFS AsyncStream Helper
 *
 * <p>This should not be considered a part of the public API.</p>
 */
public final class GridFSAsyncStreamHelper {

    /**
     * Converts the callback AsyncInputStream to an Observable AsyncInputStream
     *
     * <p>This should not be considered a part of the public API.</p>
     * @param wrapper the callback AsyncInputStream
     * @param observableAdapter the ObservableAdapter
     * @return the Observable AsyncInputStream
     */
    public static AsyncInputStream toAsyncInputStream(final com.mongodb.async.client.gridfs.AsyncInputStream wrapper,
                                                      final ObservableAdapter observableAdapter) {
        notNull("wrapper", wrapper);
        notNull("observableAdapter", observableAdapter);
        return new AsyncInputStream() {
            @Override
            public Observable<Integer> read(final ByteBuffer dst) {
                return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Integer>>() {
                    @Override
                    public void apply(final SingleResultCallback<Integer> callback) {
                        wrapper.read(dst, callback);
                    }
                }), observableAdapter);
            }

            @Override
            public Observable<Success> close() {
                return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Success>>() {
                    @Override
                    public void apply(final SingleResultCallback<Success> callback) {
                        wrapper.close(voidToSuccessCallback(callback));
                    }
                }), observableAdapter);
            }
        };
    }

    /**
     * Converts the callback AsyncOutputStream to an Observable AsyncOutputStream
     *
     * <p>This should not be considered a part of the public API.</p>
     * @param wrapper the callback AsyncOutputStream
     * @param observableAdapter the ObservableAdapter
     * @return the Observable AsyncOutputStream
     */
    public static AsyncOutputStream toAsyncOutputStream(final com.mongodb.async.client.gridfs.AsyncOutputStream wrapper,
                                                        final ObservableAdapter observableAdapter) {
        notNull("wrapper", wrapper);
        notNull("observableAdapter", observableAdapter);
        return new AsyncOutputStream() {
            @Override
            public Observable<Integer> write(final ByteBuffer src) {
                return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Integer>>() {
                    @Override
                    public void apply(final SingleResultCallback<Integer> callback) {
                        wrapper.write(src, callback);
                    }
                }), observableAdapter);
            }

            @Override
            public Observable<Success> close() {
                return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Success>>() {
                    @Override
                    public void apply(final SingleResultCallback<Success> callback) {
                        wrapper.close(voidToSuccessCallback(callback));
                    }
                }), observableAdapter);
            }
        };
    }

    static com.mongodb.async.client.gridfs.AsyncInputStream toCallbackAsyncInputStream(final AsyncInputStream wrapped) {
        notNull("wrapped", wrapped);
        return new com.mongodb.async.client.gridfs.AsyncInputStream() {

            @Override
            public void read(final ByteBuffer dst, final SingleResultCallback<Integer> callback) {
                wrapped.read(dst).asObservable().subscribe(new Subscriber<Integer>() {
                    private Integer result = null;

                    @Override
                    public void onError(final Throwable e) {
                        callback.onResult(null, e);
                    }

                    @Override
                    public void onNext(final Integer integer) {
                        result = integer;
                    }

                    @Override
                    public void onCompleted() {
                        callback.onResult(result, null);
                    }
                });

            }

            @Override
            public void close(final SingleResultCallback<Void> callback) {
                wrapped.close().subscribe(new Subscriber<Success>() {

                    @Override
                    public void onNext(final Success success) {
                    }

                    @Override
                    public void onError(final Throwable t) {
                        callback.onResult(null, t);
                    }

                    @Override
                    public void onCompleted() {
                        callback.onResult(null, null);
                    }
                });
            }
        };
    }

    static com.mongodb.async.client.gridfs.AsyncOutputStream toCallbackAsyncOutputStream(final AsyncOutputStream wrapped) {
        notNull("wrapped", wrapped);
        return new com.mongodb.async.client.gridfs.AsyncOutputStream() {

            @Override
            public void write(final ByteBuffer src, final SingleResultCallback<Integer> callback) {
                wrapped.write(src).subscribe(new Subscriber<Integer>() {
                    private Integer result = null;

                    @Override
                    public void onNext(final Integer integer) {
                        result = integer;
                    }

                    @Override
                    public void onError(final Throwable t) {
                        callback.onResult(null, t);
                    }

                    @Override
                    public void onCompleted() {
                        callback.onResult(result, null);
                    }
                });
            }

            @Override
            public void close(final SingleResultCallback<Void> callback) {
                wrapped.close().subscribe(new Subscriber<Success>() {

                    @Override
                    public void onNext(final Success success) {
                    }

                    @Override
                    public void onError(final Throwable t) {
                        callback.onResult(null, t);
                    }

                    @Override
                    public void onCompleted() {
                        callback.onResult(null, null);
                    }
                });
            }
        };
    }

    private GridFSAsyncStreamHelper() {
    }
}
