package utils;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/data?characterEncoding=UTF-8", "root", "root");
    }

    public static Boolean add(ArrayList<String> list) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        if (connection == null){
            System.out.println("数据库连接失败！");
            return false;
        }else {
            System.out.println("数据库连接成功！");
            String sql = "insert  into  room_id(code) values (?)";
            PreparedStatement pst = connection.prepareStatement(sql);
            connection.setAutoCommit(false);
            System.out.println("数据大小："+list.size());
            for (int i = 0; i < list.size(); i++) {
                pst.setString(1,list.get(i));
                pst.addBatch();
            }

            int[] a=pst.executeBatch();
            connection.commit();
            System.out.println("成功添加数据条数："+a.length);
            pst.close();
            connection.close();
            return  true;
        }
    }

    public static void saveToMongo(String dataSource,ArrayList list){
        try {
            MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
            MongoDatabase mongoDatabase = mongoClient.getDatabase("data");
            System.out.println("数据库连接成功！！");
            MongoCollection<DBObject> collection = mongoDatabase.getCollection(dataSource, DBObject.class);
            List<DBObject> ds = new ArrayList<DBObject>();
            for (int i = 0; i < list.size(); i++) {
                DBObject d = (DBObject) list.get(i);
                ds.add(d);
            }

            collection.insertMany(ds);
            System.out.println("文档插入成功");
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }

    }

    @Test
    public void test2(){
        try{

            String json = " {" +
                    " 'school_code' : '111111', " +
                    " 'school_name' : '汉东政法大学', " +
                    " 'teacher_idcard' : '0000001', " +
                    " 'teacher_name' : '高育良' " +
                    " } ";

            MongoClient mongoClient = new MongoClient("localhost", 27017);

            MongoDatabase database = mongoClient.getDatabase("data");

            MongoCollection<DBObject> collection = database.getCollection("code", DBObject.class);

            DBObject bson = (DBObject) JSON.parse(json);
            System.out.println(JSON.parse(json).toString());

            collection.insertOne(bson);
        }catch (Exception  e){
            e.printStackTrace();
        }finally {

        }



    }

    @Test
    public void testMongo(){
    try {
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        MongoDatabase mongoDatabase = mongoClient.getDatabase("test");
        System.out.println("Connect to database successfully");
        MongoCollection<Document> collection = mongoDatabase.getCollection("test");
        Document document = new Document("title", "MongoDB").
                append("description", "database").
                append("likes", 100).
                append("by", "Fly");
        List<Document> documents = new ArrayList<Document>();
        documents.add(document);
        collection.insertMany(documents);
        System.out.println("文档插入成功");
    }catch (Exception e){

    }finally {

    }
    }
}
