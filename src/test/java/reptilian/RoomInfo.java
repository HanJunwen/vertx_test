package reptilian;


import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/*
广州 自如 APP 房源信息
 */
public class RoomInfo {

    public static void main(String[] args) throws InterruptedException, SQLException, ClassNotFoundException {
        ArrayList<String> idList = new ArrayList<String>();
        ArrayList<DBObject> dataList = new ArrayList<DBObject>();

        CountDownLatch countDownLatch = new CountDownLatch(413);
        WebClient client = WebClient.create(Vertx.vertx());
        for (int m = 1; m < 414; m++) {
            client.get("m.ziroom.com", "/v7/room/list.json")
                    .addQueryParam("city_code", "440100")
                    .addQueryParam("page", String.valueOf(m))
                    .as(BodyCodec.jsonObject())
                    .send(ar -> {
                        try {
                            if (ar.succeeded()) {
                                HttpResponse<JsonObject> response = ar.result();
                                JsonObject body = response.body();//响应的JSON结果
                                JsonObject rooms = body.getJsonObject("data");
                                String bodyData = body.toString();
                                dataList.add((DBObject) JSON.parse(bodyData));
                                JsonArray rooms1 = rooms.getJsonArray("rooms");
                                ArrayList a = (ArrayList) rooms1.getList();
                                for (int i = 0; i < a.size(); i++) {
//                            System.out.println(a.get(i));
                                    Map<String, String> map = (Map<String, String>) a.get(i);
                                    String id = map.get("id");
//                                System.out.println(id);
                                    idList.add(id);
                                }
//                                System.out.println(countDownLatch.getCount());
                            } else {
                                System.out.println("请求失败！！");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            countDownLatch.countDown();
                        }
                    });
        }
        countDownLatch.await();
        System.out.println("请求完成！！");
//        System.out.println(idList.size());
//       Utils.add(idList);     //存到数据库中
//        Utils.saveToMongo("code", dataList);

        CountDownLatch c = new CountDownLatch(idList.size());
        ArrayList<DBObject> infoList = new ArrayList<DBObject>();
        for (int i = 0; i < idList.size(); i++) {
            client.get("m.ziroom.com", "/v7/room/detail.json")
                    .addQueryParam("city_code", "440100")
                    .addQueryParam("id", idList.get(i))//61335509    idList.get(i)
                    .as(BodyCodec.jsonObject())
                    .send(ar -> {
                        try {
                            if (ar.succeeded()) {
                                HttpResponse<JsonObject> response = ar.result();
                                JsonObject body = response.body();//响应的JSON结果
                                String bodyData = body.toString();
                                infoList.add((DBObject) JSON.parse(bodyData));
                                JsonObject data = body.getJsonObject("data");

                                Map<String, Object> map = data.getMap();
                                ArrayList item = (ArrayList) map.get("config");
//                                LinkedHashMap linkedHashMap = (LinkedHashMap) item.get(0);
//                                System.out.println(linkedHashMap.get("name"));
                            } else {
                                System.out.println("请求失败！！");
                            }
                        } catch (Exception e) {
                            System.out.println("请求异常！！！");
                            e.printStackTrace();
                        } finally {
                            c.countDown();
                            System.out.println(c.getCount());
                        }
                    });
        }
        c.await();
//        Utils.saveToMongo("roomInfo", infoList);
        System.out.println("===========完成=============");

    }

}
