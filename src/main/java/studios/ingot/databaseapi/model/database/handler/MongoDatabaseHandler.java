package studios.ingot.databaseapi.model.database.handler;


import studios.ingot.databaseapi.interfaces.IDatabaseHandler;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

@Getter @Setter
public class MongoDatabaseHandler implements IDatabaseHandler<Document> {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private ExecutorService executorService;

    public MongoDatabaseHandler(String user, String password, String host, int port, String database) {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://" + user + ":" + password + "@" + host + ":" + port + "/?authSource=admin"));
        mongoDatabase = mongoClient.getDatabase(database);
    }

    @Override
    public <K> void insertModel(K... query) {
        executorService.execute(() -> {
            mongoDatabase.getCollection((String) query[0]).insertOne((Document) query[1]);
        });
    }

    @Override
    public <K> Document getAsyncModel(K... query) {
        try {
            return executorService.submit(() -> mongoDatabase.getCollection((String) query[0]).find((Bson) query[1]).first()).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public <K> List<Document> getAsyncModels(K... query) {
        try {
            return executorService.submit(() -> {
                List<Document> list = new ArrayList<>();
                for (Document document : mongoDatabase.getCollection((String) query[0]).find()) {
                    list.add(document);
                }
                return list;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public <K> void updateModel(K... query) {
        executorService.execute(() -> {
            mongoDatabase.getCollection((String) query[0]).updateOne((Bson) query[1], new BasicDBObject("$set", query[2]));
        });
    }


}