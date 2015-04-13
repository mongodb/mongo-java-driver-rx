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

import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import com.mongodb.rx.client.Success;
import org.bson.Document;
import org.bson.conversions.Bson;
import rx.observers.TestSubscriber;

import static com.mongodb.client.model.Filters.text;
import static java.util.Arrays.asList;
import static tour.SubscriberHelpers.printDocumentSubscriber;
import static tour.SubscriberHelpers.printSubscriber;

/**
 * The QuickTourAdmin code example see: https://mongodb.github.io/mongo-java-driver/3.0/getting-started
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class QuickTourAdmin {
    /**
     * Run this main method to see the output of this quick example.
     *
     * @param args takes an optional single argument for the connection string
     * @throws Throwable if an operation fails
     */
    public static void main(final String[] args) throws Throwable {
        MongoClient mongoClient;

        if (args.length == 0) {
            // connect to the local database server
            mongoClient = MongoClients.create();
        } else {
            mongoClient = MongoClients.create(args[0]);
        }

        // get handle to "mydb" database
        MongoDatabase database = mongoClient.getDatabase("mydb");


        // get a handle to the "test" collection
        MongoCollection<Document> collection = database.getCollection("test");
        TestSubscriber subscriber = new TestSubscriber<Success>();
        collection.drop().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // getting a list of databases
        mongoClient.listDatabaseNames().subscribe(printSubscriber("Database Names: "));

        // drop a database
        mongoClient.getDatabase("databaseToBeDropped").drop().toBlocking().single();

        // create a collection
        database.createCollection("cappedCollection", new CreateCollectionOptions().capped(true).sizeInBytes(0x100000))
                    .subscribe(printSubscriber("Collection Created! "));


        database.listCollectionNames().subscribe(printSubscriber("Collection Names: "));

        // drop a collection:
        collection.drop().toBlocking().single();

        // create an ascending index on the "i" field
        collection.createIndex(new Document("i", 1)).subscribe(printSubscriber("Created an index named: "));

        // list the indexes on the collection
        collection.listIndexes().subscribe(printDocumentSubscriber());


        // create a text index on the "content" field
        subscriber = printSubscriber("Created an index named: ");
        collection.createIndex(new Document("content", "text")).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        subscriber = new TestSubscriber();
        collection.insertMany(asList(new Document("_id", 0).append("content", "textual content"),
                new Document("_id", 1).append("content", "additional content"),
                new Document("_id", 2).append("content", "irrelevant content"))).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Find using the text index
        subscriber = printSubscriber("Text search matches: ");
        collection.count(text("textual content -irrelevant")).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Find using the $language operator
        subscriber = printSubscriber("Text search matches (english): ");
        Bson textSearch = text("textual content -irrelevant", "english");
        collection.count(textSearch).subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        // Find the highest scoring match
        System.out.print("Highest scoring document: ");
        Document projection = new Document("score", new Document("$meta", "textScore"));
        collection.find(textSearch).projection(projection).first().subscribe(printDocumentSubscriber());


        // Run a command
        database.runCommand(new Document("buildInfo", 1)).subscribe(printDocumentSubscriber());

        // release resources
        subscriber = new TestSubscriber();
        database.drop().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        mongoClient.close();
    }

    private QuickTourAdmin() {
    }
}
