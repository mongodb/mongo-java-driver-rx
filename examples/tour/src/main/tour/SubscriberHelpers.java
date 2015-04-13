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

package tour;

import org.bson.Document;
import rx.Observer;
import rx.observers.TestSubscriber;

/**
 *  Subscriber helper implementations for the Quick Tour.
 */
public final class SubscriberHelpers {

    /**
     * A Subscriber that prints each result onNext
     *
     * @param <T> The TestSubscriber result type
     * @return the subscriber
     */
    public static <T> TestSubscriber<T> printSubscriber() {
        return printSubscriber(null);
    }

    /**
     * A Subscriber that prints each result onNext
     *
     * @param <T> The TestSubscriber result type
     * @param onStartMessage the initial start message printed on return of the first item.
     * @return the subscriber
     */
    public static <T> TestSubscriber<T> printSubscriber(final String onStartMessage) {
        return new TestSubscriber<T>(new Observer<T>() {
            private boolean first = true;

            @Override
            public void onCompleted() {
                System.out.println();
            }

            @Override
            public void onError(final Throwable t) {
                System.out.println("The Observer errored: " + t.getMessage());
            }

            @Override
            public void onNext(final T t) {
                if (first && onStartMessage != null) {
                    System.out.print(onStartMessage);
                    first = false;
                }
                System.out.print(t + " ");
            }
        });
    }

    /**
     * A Subscriber that prints the json version of each document
     *
     * @return the subscriber
     */
    public static TestSubscriber<Document> printDocumentSubscriber() {
        return new TestSubscriber<Document>(new Observer<Document>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(final Throwable t) {
                System.out.println("The Observer errored: " + t.getMessage());
            }

            @Override
            public void onNext(final Document document) {
                System.out.println(document.toJson());
            }
        });
    }

    private SubscriberHelpers() {
    }
}
