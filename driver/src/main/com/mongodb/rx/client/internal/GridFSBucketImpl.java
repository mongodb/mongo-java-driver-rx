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
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.Observables;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.rx.client.ObservableAdapter;
import com.mongodb.rx.client.Success;
import com.mongodb.rx.client.gridfs.AsyncInputStream;
import com.mongodb.rx.client.gridfs.AsyncOutputStream;
import com.mongodb.rx.client.gridfs.GridFSBucket;
import com.mongodb.rx.client.gridfs.GridFSDownloadStream;
import com.mongodb.rx.client.gridfs.GridFSFindObservable;
import com.mongodb.rx.client.gridfs.GridFSUploadStream;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import rx.Observable;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.rx.client.internal.GridFSAsyncStreamHelper.toCallbackAsyncInputStream;
import static com.mongodb.rx.client.internal.GridFSAsyncStreamHelper.toCallbackAsyncOutputStream;
import static com.mongodb.rx.client.internal.ObservableHelper.voidToSuccessCallback;

/**
 * The internal GridFSBucket implementation.
 *
 * <p>This should not be considered a part of the public API.</p>
 */
public final class GridFSBucketImpl implements GridFSBucket {

    private final com.mongodb.async.client.gridfs.GridFSBucket wrapped;
    private final ObservableAdapter observableAdapter;

    /**
     * The GridFSBucket constructor
     *
     * <p>This should not be considered a part of the public API.</p>
     * @param wrapped the GridFSBucket
     * @param observableAdapter the ObservableAdapter
     */
    public GridFSBucketImpl(final com.mongodb.async.client.gridfs.GridFSBucket wrapped, final ObservableAdapter observableAdapter) {
        this.wrapped = notNull("GridFSBucket", wrapped);
        this.observableAdapter = notNull("observableAdapter", observableAdapter);
    }

    @Override
    public String getBucketName() {
        return wrapped.getBucketName();
    }

    @Override
    public int getChunkSizeBytes() {
        return wrapped.getChunkSizeBytes();
    }

    @Override
    public ObservableAdapter getObservableAdapter() {
        return observableAdapter;
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
    public GridFSBucket withChunkSizeBytes(final int chunkSizeBytes) {
        return new GridFSBucketImpl(wrapped.withChunkSizeBytes(chunkSizeBytes), observableAdapter);
    }

    @Override
    public GridFSBucket withReadPreference(final ReadPreference readPreference) {
        return new GridFSBucketImpl(wrapped.withReadPreference(readPreference), observableAdapter);
    }

    @Override
    public GridFSBucket withWriteConcern(final WriteConcern writeConcern) {
        return new GridFSBucketImpl(wrapped.withWriteConcern(writeConcern), observableAdapter);
    }

    @Override
    public GridFSBucket withReadConcern(final ReadConcern readConcern) {
        return new GridFSBucketImpl(wrapped.withReadConcern(readConcern), observableAdapter);
    }

    @Override
    public GridFSUploadStream openUploadStream(final String filename) {
        return new GridFSUploadStreamImpl(wrapped.openUploadStream(filename), observableAdapter);
    }

    @Override
    public GridFSUploadStream openUploadStream(final String filename, final GridFSUploadOptions options) {
        return new GridFSUploadStreamImpl(wrapped.openUploadStream(filename, options), observableAdapter);
    }

    @Override
    public GridFSUploadStream openUploadStream(final BsonValue id, final String filename) {
        return new GridFSUploadStreamImpl(wrapped.openUploadStream(id, filename), observableAdapter);
    }

    @Override
    public GridFSUploadStream openUploadStream(final BsonValue id, final String filename, final GridFSUploadOptions options) {
        return new GridFSUploadStreamImpl(wrapped.openUploadStream(id, filename, options), observableAdapter);
    }

    @Override
    public Observable<ObjectId> uploadFromStream(final String filename, final AsyncInputStream source) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<ObjectId>>() {
            @Override
            public void apply(final SingleResultCallback<ObjectId> callback) {
                wrapped.uploadFromStream(filename, toCallbackAsyncInputStream(source), callback);
            }
        }), observableAdapter);
    }

    @Override
    public Observable<ObjectId> uploadFromStream(final String filename, final AsyncInputStream source, final GridFSUploadOptions options) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<ObjectId>>() {
            @Override
            public void apply(final SingleResultCallback<ObjectId> callback) {
                wrapped.uploadFromStream(filename, toCallbackAsyncInputStream(source), options, callback);
            }
        }), observableAdapter);
    }

    @Override
    public Observable<Success> uploadFromStream(final BsonValue id, final String filename, final AsyncInputStream source) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.uploadFromStream(id, filename, toCallbackAsyncInputStream(source), voidToSuccessCallback(callback));
            }
        }), observableAdapter);
    }

    @Override
    public Observable<Success> uploadFromStream(final BsonValue id, final String filename, final AsyncInputStream source,
                                               final GridFSUploadOptions options) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.uploadFromStream(id, filename, toCallbackAsyncInputStream(source), options, voidToSuccessCallback(callback));
            }
        }), observableAdapter);
    }

    @Override
    public GridFSDownloadStream openDownloadStream(final ObjectId id) {
        return new GridFSDownloadStreamImpl(wrapped.openDownloadStream(id), observableAdapter);
    }

    @Override
    public Observable<Long> downloadToStream(final ObjectId id, final AsyncOutputStream destination) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Long>>() {
            @Override
            public void apply(final SingleResultCallback<Long> callback) {
                wrapped.downloadToStream(id, toCallbackAsyncOutputStream(destination), callback);
            }
        }), observableAdapter);
    }

    @Override
    public GridFSDownloadStream openDownloadStream(final BsonValue id) {
        return new GridFSDownloadStreamImpl(wrapped.openDownloadStream(id), observableAdapter);
    }

    @Override
    public Observable<Long> downloadToStream(final BsonValue id, final AsyncOutputStream destination) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Long>>() {
            @Override
            public void apply(final SingleResultCallback<Long> callback) {
                wrapped.downloadToStream(id, toCallbackAsyncOutputStream(destination), callback);
            }
        }), observableAdapter);
    }

    @Override
    public GridFSDownloadStream openDownloadStream(final String filename) {
        return new GridFSDownloadStreamImpl(wrapped.openDownloadStream(filename), observableAdapter);
    }

    @Override
    public GridFSDownloadStream openDownloadStream(final String filename, final GridFSDownloadOptions options) {
        return new GridFSDownloadStreamImpl(wrapped.openDownloadStream(filename, options), observableAdapter);
    }

    @Override
    public Observable<Long> downloadToStream(final String filename, final AsyncOutputStream destination) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Long>>() {
            @Override
            public void apply(final SingleResultCallback<Long> callback) {
                wrapped.downloadToStream(filename, toCallbackAsyncOutputStream(destination), callback);
            }
        }), observableAdapter);
    }

    @Override
    public Observable<Long> downloadToStream(final String filename, final AsyncOutputStream destination,
                                            final GridFSDownloadOptions options) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Long>>() {
            @Override
            public void apply(final SingleResultCallback<Long> callback) {
                wrapped.downloadToStream(filename, toCallbackAsyncOutputStream(destination), options, callback);
            }
        }), observableAdapter);
    }

    @Override
    public GridFSFindObservable find() {
        return new GridFSFindObservableImpl(wrapped.find(), observableAdapter);
    }

    @Override
    public GridFSFindObservable find(final Bson filter) {
        return new GridFSFindObservableImpl(wrapped.find(filter), observableAdapter);
    }

    @Override
    public Observable<Success> delete(final ObjectId id) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.delete(id, voidToSuccessCallback(callback));
            }
        }), observableAdapter);
    }

    @Override
    public Observable<Success> delete(final BsonValue id) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.delete(id, voidToSuccessCallback(callback));
            }
        }), observableAdapter);
    }

    @Override
    public Observable<Success> rename(final ObjectId id, final String newFilename) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.rename(id, newFilename, voidToSuccessCallback(callback));
            }
        }), observableAdapter);
    }

    @Override
    public Observable<Success> rename(final BsonValue id, final String newFilename) {
        return RxObservables.create(Observables.observe(new Block<SingleResultCallback<Success>>() {
            @Override
            public void apply(final SingleResultCallback<Success> callback) {
                wrapped.rename(id, newFilename, voidToSuccessCallback(callback));
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

}
