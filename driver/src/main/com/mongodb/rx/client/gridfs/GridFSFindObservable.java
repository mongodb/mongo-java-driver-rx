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

package com.mongodb.rx.client.gridfs;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Collation;
import com.mongodb.rx.client.MongoObservable;
import org.bson.conversions.Bson;
import rx.Observable;

import java.util.concurrent.TimeUnit;

/**
 * Iterable for the GridFS Files Collection.
 *
 * @since 1.3
 */
public interface GridFSFindObservable extends MongoObservable<GridFSFile> {

    /**
     * Helper to return an observable limited first from the query.
     *
     * @return an observable with a single element
     */
    Observable<GridFSFile> first();

    /**
     * Sets the query filter to apply to the query.
     * <p>
     * Below is an example of filtering against the filename and some nested metadata that can also be stored along with the file data:
     * <pre>
     *  {@code
     *      Filters.and(Filters.eq("filename", "mongodb.png"), Filters.eq("metadata.contentType", "image/png"));
     *  }
     *  </pre>
     *
     * @param filter the filter, which may be null.
     * @return this
     * @mongodb.driver.manual reference/method/db.collection.find/ Filter
     * @see com.mongodb.client.model.Filters
     */
    GridFSFindObservable filter(Bson filter);

    /**
     * Sets the limit to apply.
     *
     * @param limit the limit, which may be null
     * @return this
     * @mongodb.driver.manual reference/method/cursor.limit/#cursor.limit Limit
     */
    GridFSFindObservable limit(int limit);

    /**
     * Sets the number of documents to skip.
     *
     * @param skip the number of documents to skip
     * @return this
     * @mongodb.driver.manual reference/method/cursor.skip/#cursor.skip Skip
     */
    GridFSFindObservable skip(int skip);

    /**
     * Sets the sort criteria to apply to the query.
     *
     * @param sort the sort criteria, which may be null.
     * @return this
     * @mongodb.driver.manual reference/method/cursor.sort/ Sort
     */
    GridFSFindObservable sort(Bson sort);

    /**
     * The server normally times out idle cursors after an inactivity period (10 minutes)
     * to prevent excess memory use. Set this option to prevent that.
     *
     * @param noCursorTimeout true if cursor timeout is disabled
     * @return this
     */
    GridFSFindObservable noCursorTimeout(boolean noCursorTimeout);

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param maxTime  the max time
     * @param timeUnit the time unit, which may not be null
     * @return this
     * @mongodb.driver.manual reference/method/cursor.maxTimeMS/#cursor.maxTimeMS Max Time
     */
    GridFSFindObservable maxTime(long maxTime, TimeUnit timeUnit);

    /**
     * Sets the collation options
     *
     * <p>A null value represents the server default.</p>
     * @param collation the collation options to use
     * @return this
     * @since 1.3
     * @mongodb.server.release 3.4
     */
    GridFSFindObservable collation(Collation collation);
}
