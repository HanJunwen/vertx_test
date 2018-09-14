package utils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.junit.Test;

import java.util.List;

public class Dbutils {


    @Test
    public void mongoConn() {
        JsonObject config = new JsonObject();
        config.put("connection_string", "mongodb://127.0.0.1:27017");
        config.put("db_name", "test");

        JsonObject data = new JsonObject().put("test", "test");
        MongoClient client = MongoClient.createShared(Vertx.vertx(), config);

        client.getCollections(res -> {
            try {
                if (res.succeeded()) {
                    List<String> collections = res.result();
                    for (int i = 0; i < collections.size(); i++) {
                        System.out.println(collections.get(i));
                    }
                } else {
                    res.cause().printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        });
    }
}
