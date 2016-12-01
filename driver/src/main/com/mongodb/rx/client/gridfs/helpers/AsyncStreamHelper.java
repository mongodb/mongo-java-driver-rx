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

package com.mongodb.rx.client.gridfs.helpers;

import com.mongodb.rx.client.ObservableAdapter;
import com.mongodb.rx.client.gridfs.AsyncInputStream;
import com.mongodb.rx.client.gridfs.AsyncOutputStream;
import com.mongodb.rx.client.internal.GridFSAsyncStreamHelper;
import com.mongodb.rx.client.internal.ObservableHelper;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import static com.mongodb.assertions.Assertions.notNull;

/**
 * A general helper class that creates {@link AsyncInputStream} or {@link AsyncOutputStream} instances.
 *
 * Provides support for:
 * <ul>
 *     <li>{@code byte[]} - Converts byte arrays into Async Streams</li>
 *     <li>{@link ByteBuffer} - Converts ByteBuffers into Async Streams</li>
 *     <li>{@link InputStream} - Converts InputStreams into Async Streams (Note: InputStream implementations are blocking)</li>
 *     <li>{@link OutputStream} - Converts OutputStreams into Async Streams (Note: OutputStream implementations are blocking)</li>
 * </ul>
 *
 * @since 1.3
 */
public final class AsyncStreamHelper {

    private static final ObservableHelper.NoopObservableAdapter NOOP_OBSERVABLE_ADAPTER = new ObservableHelper.NoopObservableAdapter();

    /**
     * Converts a {@code byte[]} into a {@link AsyncInputStream}
     *
     * @param srcBytes the data source
     * @return the AsyncInputStream
     */
    public static AsyncInputStream toAsyncInputStream(final byte[] srcBytes) {
        return toAsyncInputStream(srcBytes, NOOP_OBSERVABLE_ADAPTER);
    }

    /**
     * Converts a {@code byte[]} into a {@link AsyncInputStream}
     *
     * @param srcBytes the data source
     * @param observableAdapter the ObservableAdapter
     * @return the AsyncInputStream
     */
    public static AsyncInputStream toAsyncInputStream(final byte[] srcBytes, final ObservableAdapter observableAdapter) {
        notNull("srcBytes", srcBytes);
        notNull("observableAdapter", observableAdapter);
        return GridFSAsyncStreamHelper.toAsyncInputStream(
                com.mongodb.async.client.gridfs.helpers.AsyncStreamHelper.toAsyncInputStream(srcBytes),
                observableAdapter);
    }


    /**
     * Converts a {@code byte[]} into a {@link AsyncOutputStream}
     *
     * @param dstBytes the data destination
     * @return the AsyncOutputStream
     */
    public static AsyncOutputStream toAsyncOutputStream(final byte[] dstBytes) {
        return toAsyncOutputStream(dstBytes, NOOP_OBSERVABLE_ADAPTER);
    }

    /**
     * Converts a {@code byte[]} into a {@link AsyncOutputStream}
     *
     * @param dstBytes the data destination
     * @param observableAdapter the ObservableAdapter
     * @return the AsyncOutputStream
     */
    public static AsyncOutputStream toAsyncOutputStream(final byte[] dstBytes, final ObservableAdapter observableAdapter) {
        notNull("dstBytes", dstBytes);
        notNull("observableAdapter", observableAdapter);
        return GridFSAsyncStreamHelper.toAsyncOutputStream(com.mongodb.async.client.gridfs.helpers.AsyncStreamHelper
                .toAsyncOutputStream(dstBytes), observableAdapter);
    }

    /**
     * Converts a {@link ByteBuffer} into a {@link AsyncInputStream}
     *
     * @param srcByteBuffer the data source
     * @return the AsyncInputStream
     */
    public static AsyncInputStream toAsyncInputStream(final ByteBuffer srcByteBuffer) {
        return toAsyncInputStream(srcByteBuffer, NOOP_OBSERVABLE_ADAPTER);
    }

    /**
     * Converts a {@link ByteBuffer} into a {@link AsyncInputStream}
     *
     * @param srcByteBuffer the data source
     * @param observableAdapter the ObservableAdapter
     * @return the AsyncInputStream
     */
    public static AsyncInputStream toAsyncInputStream(final ByteBuffer srcByteBuffer, final ObservableAdapter observableAdapter) {
        notNull("srcByteBuffer", srcByteBuffer);
        notNull("observableAdapter", observableAdapter);
        return GridFSAsyncStreamHelper.toAsyncInputStream(
                com.mongodb.async.client.gridfs.helpers.AsyncStreamHelper.toAsyncInputStream(srcByteBuffer),
                observableAdapter);
    }

    /**
     * Converts a {@link ByteBuffer} into a {@link AsyncOutputStream}
     *
     * @param dstByteBuffer the data destination
     * @return the AsyncOutputStream
     */
    public static AsyncOutputStream toAsyncOutputStream(final ByteBuffer dstByteBuffer) {
        return toAsyncOutputStream(dstByteBuffer, NOOP_OBSERVABLE_ADAPTER);
    }

    /**
     * Converts a {@link ByteBuffer} into a {@link AsyncOutputStream}
     *
     * @param dstByteBuffer the data destination
     * @param observableAdapter the ObservableAdapter
     * @return the AsyncOutputStream
     */
    public static AsyncOutputStream toAsyncOutputStream(final ByteBuffer dstByteBuffer, final ObservableAdapter observableAdapter) {
        notNull("dstByteBuffer", dstByteBuffer);
        notNull("observableAdapter", observableAdapter);
        return GridFSAsyncStreamHelper.toAsyncOutputStream(
                com.mongodb.async.client.gridfs.helpers.AsyncStreamHelper.toAsyncOutputStream(dstByteBuffer),
                observableAdapter);
    }

    /**
     * Converts a {@link InputStream} into a {@link AsyncInputStream}
     *
     * @param inputStream the InputStream
     * @return the AsyncInputStream
     */
    public static AsyncInputStream toAsyncInputStream(final InputStream inputStream) {
        return toAsyncInputStream(inputStream, NOOP_OBSERVABLE_ADAPTER);
    }


    /**
     * Converts a {@link InputStream} into a {@link AsyncInputStream}
     *
     * @param inputStream the InputStream
     * @param observableAdapter the ObservableAdapter
     * @return the AsyncInputStream
     */
    public static AsyncInputStream toAsyncInputStream(final InputStream inputStream, final ObservableAdapter observableAdapter) {
        notNull("inputStream", inputStream);
        notNull("observableAdapter", observableAdapter);
        return GridFSAsyncStreamHelper.toAsyncInputStream(
                com.mongodb.async.client.gridfs.helpers.AsyncStreamHelper.toAsyncInputStream(inputStream),
                observableAdapter);
    }

    /**
     * Converts a {@link OutputStream} into a {@link AsyncOutputStream}
     *
     * @param outputStream the OutputStream
     * @return the AsyncOutputStream
     */
    public static AsyncOutputStream toAsyncOutputStream(final OutputStream outputStream) {
        return toAsyncOutputStream(outputStream, NOOP_OBSERVABLE_ADAPTER);
    }

    /**
     * Converts a {@link OutputStream} into a {@link AsyncOutputStream}
     *
     * @param outputStream the OutputStream
     * @param observableAdapter the ObservableAdapter
     * @return the AsyncOutputStream
     */
    public static AsyncOutputStream toAsyncOutputStream(final OutputStream outputStream, final ObservableAdapter observableAdapter) {
        notNull("outputStream", outputStream);
        notNull("observableAdapter", observableAdapter);
        return GridFSAsyncStreamHelper.toAsyncOutputStream(
                com.mongodb.async.client.gridfs.helpers.AsyncStreamHelper.toAsyncOutputStream(outputStream),
                observableAdapter);
    }

    private AsyncStreamHelper() {
    }
}
