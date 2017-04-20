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

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.client.MongoDriverInformation;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import com.mongodb.rx.client.internal.MongoClientImpl;
import org.bson.codecs.configuration.CodecRegistry;

import static com.mongodb.rx.client.internal.ObservableHelper.NoopObservableAdapter;

/**
 * A factory for MongoClient instances.
 *
 */
public final class MongoClients {

    /**
     * Creates a new client with the default connection string "mongodb://localhost".
     *
     * @return the client
     */
    public static MongoClient create() {
        return create(new ConnectionString("mongodb://localhost"));
    }

    /**
     * Create a new client with the given client settings.
     *
     * @param settings the settings
     * @return the client
     */
    public static MongoClient create(final MongoClientSettings settings) {
        return create(settings, new NoopObservableAdapter());
    }

    /**
     * Create a new client with the given client settings.
     *
     * @param settings the settings
     * @param observableAdapter the {@link ObservableAdapter} to adapt all {@code Observables}
     * @return the client
     * @since 1.2
     */
    public static MongoClient create(final MongoClientSettings settings, final ObservableAdapter observableAdapter) {
        return create(settings, observableAdapter, null);
    }

    /**
     * Create a new client with the given connection string.
     *
     * @param connectionString the connection
     * @return the client
     */
    public static MongoClient create(final String connectionString) {
        return create(new ConnectionString(connectionString));
    }

    /**
     * Create a new client with the given connection string.
     *
     * @param connectionString the connection
     * @param observableAdapter the {@link ObservableAdapter} to adapt all {@code Observables}
     * @return the client
     * @since 1.2
     */
    public static MongoClient create(final String connectionString, final ObservableAdapter observableAdapter) {
        return create(new ConnectionString(connectionString), observableAdapter);
    }

    /**
     * Create a new client with the given connection string.
     *
     * @param connectionString the settings
     * @return the client
     */
    public static MongoClient create(final ConnectionString connectionString) {
        return create(connectionString, new NoopObservableAdapter());
    }

    /**
     * Create a new client with the given connection string.
     *
     * @param connectionString the settings
     * @param observableAdapter the {@link ObservableAdapter} to adapt all {@code Observables}.
     * @return the client
     * @since 1.2
     */
    public static MongoClient create(final ConnectionString connectionString, final ObservableAdapter observableAdapter) {
        return create(connectionString, observableAdapter, null);
    }

    /**
     * Create a new client with the given connection string.
     *
     * <p>Note: Intended for driver and library authors to associate extra driver metadata with the connections.</p>
     *
     * @param connectionString the settings
     * @param observableAdapter the {@link ObservableAdapter} to adapt all {@code Observables}.
     * @param mongoDriverInformation any driver information to associate with the MongoClient
     * @return the client
     * @since 1.3
     */
    public static MongoClient create(final ConnectionString connectionString, final ObservableAdapter observableAdapter,
                                     final MongoDriverInformation mongoDriverInformation) {
        return new MongoClientImpl(com.mongodb.async.client.MongoClients.create(connectionString,
                getMongoDriverInformation(mongoDriverInformation)), observableAdapter);
    }

    /**
     * Creates a new client with the given client settings.
     *
     * <p>Note: Intended for driver and library authors to associate extra driver metadata with the connections.</p>
     *
     * @param settings the settings
     * @param observableAdapter the {@link ObservableAdapter} to adapt all {@code Observables}.
     * @param mongoDriverInformation any driver information to associate with the MongoClient
     * @return the client
     * @since 1.3
     */
    public static MongoClient create(final MongoClientSettings settings, final ObservableAdapter observableAdapter,
                                     final MongoDriverInformation mongoDriverInformation) {
        return new MongoClientImpl(com.mongodb.async.client.MongoClients.create(settings,
                getMongoDriverInformation(mongoDriverInformation)), observableAdapter);
    }

    /**
     * Gets the default codec registry.
     *
     * @return the default codec registry
     * @see MongoClientSettings#getCodecRegistry()
     * @since 1.4
     */
    public static CodecRegistry getDefaultCodecRegistry() {
        return com.mongodb.async.client.MongoClients.getDefaultCodecRegistry();
    }

    private static MongoDriverInformation getMongoDriverInformation(final MongoDriverInformation mongoDriverInformation) {
        if (mongoDriverInformation == null) {
            return DEFAULT_DRIVER_INFORMATION;
        } else {
            return MongoDriverInformation.builder(mongoDriverInformation)
                    .driverName(DRIVER_NAME)
                    .driverVersion(DRIVER_VERSION).build();
        }
    }

    private static final String DRIVER_NAME = "mongo-java-driver-rx";
    private static final String DRIVER_VERSION = getDriverVersion();
    private static final MongoDriverInformation DEFAULT_DRIVER_INFORMATION = MongoDriverInformation.builder().driverName(DRIVER_NAME)
            .driverVersion(DRIVER_VERSION).build();

    private static String getDriverVersion() {
        String driverVersion = "unknown";

        try {
            CodeSource codeSource = MongoClients.class.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                String path = codeSource.getLocation().getPath();
                URL jarUrl = path.endsWith(".jar") ? new URL("jar:file:" + path + "!/") : null;
                if (jarUrl != null) {
                    JarURLConnection jarURLConnection = (JarURLConnection) jarUrl.openConnection();
                    Manifest manifest = jarURLConnection.getManifest();
                    String version = (String) manifest.getMainAttributes().get(new Attributes.Name("Build-Version"));
                    if (version != null) {
                        driverVersion = version;
                    }
                }
            }
        } catch (SecurityException e) {
            // do nothing
        } catch (IOException e) {
            // do nothing
        }
        return driverVersion;
    }

    private MongoClients() {
    }
}
