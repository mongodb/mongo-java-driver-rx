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
