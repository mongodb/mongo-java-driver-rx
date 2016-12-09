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

package com.mongodb.rx.client;

import rx.Observable;

/**
 * An Observable Adapter that will apply an adaption to all Observables returned by the driver.
 *
 * <p>By default this is a noop but it maybe useful if wanting to switch schedulers by combining with
 *  <a href="http://reactivex.io/documentation/operators/observeon.html">ObserveOn</a>.
 * </p>
 *
 * @since 1.2
 */
public interface ObservableAdapter {
    /**
     * Performs any adapations to the underlying observable
     *
     * @param observable the Observable to adapt
     * @param <T> the type of the items emitted by the Observable
     * @return an adapted Observable
     */
   <T> Observable<T> adapt(Observable<T> observable);
}
