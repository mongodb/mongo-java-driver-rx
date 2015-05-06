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

import com.mongodb.MongoException;
import com.mongodb.async.AsyncBatchCursor;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoIterable;
import rx.Observable;
import rx.Producer;
import rx.Subscriber;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class MongoIterableObservable {

    static <TResult> Observable<TResult> create(final MongoIterable<TResult> mongoIterable) {
        return Observable.create(new Observable.OnSubscribe<TResult>() {
                    @Override
                    public void call(final Subscriber<? super TResult> subscriber) {
                        subscriber.onStart();
                        subscriber.setProducer(new BatchCursorProducer<TResult>(mongoIterable, subscriber));
                    }
                });
    }

    static final class BatchCursorProducer<TResult> implements Producer {
        private final MongoIterable<TResult> mongoIterable;
        private final Subscriber<? super TResult> subscriber;

        private final Lock lock = new ReentrantLock(false);

        private boolean requestedBatchCursor;
        private boolean isReading;
        private boolean isProcessing;
        private boolean cursorCompleted;

        private long wanted = 0;
        private volatile AsyncBatchCursor<TResult> batchCursor;
        private final ConcurrentLinkedQueue<TResult> resultsQueue = new ConcurrentLinkedQueue<TResult>();

        public BatchCursorProducer(final MongoIterable<TResult> mongoIterable, final Subscriber<? super TResult> subscriber) {
            this.subscriber = subscriber;
            this.mongoIterable = mongoIterable;
        }

        @Override
        public void request(final long n) {
            lock.lock();
            boolean mustGetCursor = false;
            try {
                wanted += n;
                if (!requestedBatchCursor) {
                    requestedBatchCursor = true;
                    mustGetCursor = true;
                }
            } finally {
                lock.unlock();
            }

            if (mustGetCursor) {
                getBatchCursor();
            } else {
                processResultsQueue();
            }
        }

        private void processResultsQueue() {
            lock.lock();
            boolean mustProcess = false;
            try {
                if (!isProcessing && isSubscribed()) {
                    isProcessing = true;
                    mustProcess = true;
                }
            } finally {
                lock.unlock();
            }

            if (mustProcess) {
                boolean getNextBatch = false;

                long processedCount = 0;
                boolean completed = false;
                while (true) {
                    long localWanted = 0;
                    lock.lock();
                    try {
                        wanted -= processedCount;
                        if (resultsQueue.isEmpty()) {
                            completed = cursorCompleted;
                            getNextBatch = wanted > 0;
                            isProcessing = false;
                            break;
                        } else if (wanted == 0) {
                            isProcessing = false;
                            break;
                        }
                        localWanted = wanted;
                    } finally {
                        lock.unlock();
                    }

                    while (localWanted > 0) {
                        TResult item = resultsQueue.poll();
                        if (item == null) {
                            break;
                        } else {
                            onNext(item);
                            localWanted -= 1;
                            processedCount += 1;
                        }
                    }
                }

                if (completed) {
                    onCompleted();
                } else if (getNextBatch) {
                    getNextBatch();
                }
            }
        }

        private void getNextBatch() {
            lock.lock();
            boolean mustRead = false;
            try {
                if (!isReading && isSubscribed() && batchCursor != null) {
                    isReading = true;
                    mustRead = true;
                }
            } finally {
                lock.unlock();
            }

            if (mustRead) {
                batchCursor.setBatchSize(getBatchSize());
                batchCursor.next(new SingleResultCallback<List<TResult>>() {
                    @Override
                    public void onResult(final List<TResult> result, final Throwable t) {
                        lock.lock();
                        try {
                            isReading = false;
                            if (t == null && result == null) {
                                cursorCompleted = true;
                            }
                        } finally {
                            lock.unlock();
                        }

                        if (t != null) {
                            onError(t);
                        } else {
                            if (result != null) {
                                resultsQueue.addAll(result);
                            }
                            processResultsQueue();
                        }
                    }
                });
            }
        }

        private void onError(final Throwable t) {
            if (isSubscribed()) {
                subscriber.onError(t);
            }
        }

        private void onNext(final TResult next) {
            if (isSubscribed()) {
                subscriber.onNext(next);
            }
        }

        private void onCompleted() {
            if (isSubscribed()) {
                subscriber.onCompleted();
            }
        }

        private boolean isSubscribed() {
            boolean subscribed = !subscriber.isUnsubscribed();
            if (!subscribed && batchCursor != null) {
                batchCursor.close();
            }
            return subscribed;
        }

        private void getBatchCursor() {
            mongoIterable.batchSize(getBatchSize());
            mongoIterable.batchCursor(new SingleResultCallback<AsyncBatchCursor<TResult>>() {
                @Override
                public void onResult(final AsyncBatchCursor<TResult> result, final Throwable t) {
                    if (t != null) {
                        onError(t);
                    } else if (result != null) {
                        batchCursor = result;
                        getNextBatch();
                    } else {
                        onError(new MongoException("Unexpected error, no AsyncBatchCursor returned from the MongoIterable."));
                    }
                }
            });
        }

        /**
         * Returns the batchSize to be used with the cursor.
         *
         * <p>Anything less than 2 would close the cursor so that is the minimum batchSize and `Integer.MAX_VALUE` is the maximum
         * batchSize.</p>
         *
         * @return the batchSize to use
         */
        private int getBatchSize() {
            long requested = wanted;
            if (requested <= 1) {
                return 2;
            } else if (requested < Integer.MAX_VALUE) {
                return (int) requested;
            } else {
                return Integer.MAX_VALUE;
            }
        }
    }

    private MongoIterableObservable(){
    }

}
