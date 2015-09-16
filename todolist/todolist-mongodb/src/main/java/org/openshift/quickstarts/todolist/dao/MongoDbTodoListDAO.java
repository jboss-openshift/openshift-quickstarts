package org.openshift.quickstarts.todolist.dao;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.openshift.quickstarts.todolist.model.TodoEntry;
import org.openshift.quickstarts.todolist.util.DbServicePrefixMappingParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class MongoDbTodoListDAO implements TodoListDAO {

    public static final String DB_SERVICE_PREFIX_MAPPING = "DB_SERVICE_PREFIX_MAPPING";

    private DbServicePrefixMappingParser dbServicePrefixMappingParser = new DbServicePrefixMappingParser();

    private TodoMapper mapper = new TodoMapper();

    @Override
    public void save(TodoEntry entry) {
        getMongoCollection().insertOne(mapper.toDocument(entry));
    }

    @Override
    public List<TodoEntry> list() {
        List<TodoEntry> list = new ArrayList<TodoEntry>();
        FindIterable<Document> documents = getMongoCollection().find();
        for (Document document : documents) {
            list.add(mapper.fromDocument(document));
        }
        return list;
    }



    private MongoCollection<Document> getMongoCollection() {
        return getMongoCollection(getMongoDatabase(getMongoClient()));
    }

    private MongoClient getMongoClient() {
        List<DbServicePrefixMappingParser.DbServicePrefixMapping> mappings = dbServicePrefixMappingParser.parseDbServicePrefixMappingEnvVar(System.getenv(DB_SERVICE_PREFIX_MAPPING));
        for (DbServicePrefixMappingParser.DbServicePrefixMapping mapping : mappings) {
            if ("MONGODB".equals(mapping.getDatabaseType().toUpperCase())) {
                String hostname = System.getenv(mapping.getServiceName() + "_SERVICE_HOST");
                String port = System.getenv(mapping.getServiceName() + "_SERVICE_PORT");
                String database = System.getenv(mapping.getEnvPrefix() + "_DATABASE");
                String username = System.getenv(mapping.getEnvPrefix() + "_USERNAME");
                String password = System.getenv(mapping.getEnvPrefix() + "_PASSWORD");
                MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
                return new MongoClient(new ServerAddress(hostname, Integer.parseInt(port)), Collections.singletonList(credential));
            }
        }
        throw new IllegalStateException("No MongoDB mapping in " + DB_SERVICE_PREFIX_MAPPING);
    }

    private MongoDatabase getMongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(System.getenv("DB_DATABASE"));
    }

    private MongoCollection<Document> getMongoCollection(MongoDatabase mongoDatabase) {
        return mongoDatabase.getCollection("TodoList");
    }


}
