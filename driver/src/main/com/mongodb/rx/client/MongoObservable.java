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

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Operations that can produce an Observable to iterate over the results with.
 *
 * @param <TResult> the result type
 * @since 1.0
 */
public interface MongoObservable<TResult> {

    /**
     * Returns an Observable for the operation
     *<p>
     * For more information on Observables see the
     * <a href="http://reactivex.io/documentation/observable.html">ReactiveX documentation</a>.
     *</p>
     *
     * @return the Observable for the operation
     */
    Observable<TResult> toObservable();

    /**
     * A convience method that subscribes to the Observable as provided by {@link #toObservable}.
     *
     * <p>
     * For more information on Subscriptions see the
     * <a href="http://reactivex.io/documentation/observable.html">ReactiveX documentation</a>.
     *</p>
     *
     * @param subscriber the Subscriber that will handle emissions and notifications from the Observable
     * @return a Subscription reference with which Subscribers that are Observers can
     *         unsubscribe from the Observable
     */
    Subscription subscribe(Subscriber<? super TResult> subscriber);
}
