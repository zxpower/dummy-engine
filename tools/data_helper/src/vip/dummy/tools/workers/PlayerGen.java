package vip.dummy.tools.workers;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import vip.dummy.tools.model.Player;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by strawmanbobi
 * 2018-04-07
 *
 * player generator
 */
public class PlayerGen {
    private String dbHost;
    private String dbUser;
    private String dbPassword;
    private int playerCount;
    private int instanceCount;

    public PlayerGen(String dbHost, String dbUser, String dbPassword, int playerCount, int instanceCount) {
        this.dbHost = dbHost;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.playerCount = playerCount;
        this.instanceCount = instanceCount;
    }

    private Block<Document> printBlock = new Block<Document>() {
        @Override
        public void apply(final Document document) {
            System.out.println(document.toJson());
        }
    };

    public boolean generatePlayers() {
        MongoDatabase database;
        MongoCollection<Document> collection;
        try {
            String connectionString = "mongodb://" + dbUser + ":" + dbPassword + "@" +
                    dbHost + ":27017/?authSource=dummy_game";
            System.out.println(connectionString);
            MongoClientURI clientURI = new MongoClientURI(connectionString);
            MongoClient mongoClient = new MongoClient(clientURI);
            database = mongoClient.getDatabase("dummy_game");
            collection = database.getCollection("player");

            // clean virtual players
            System.out.println("deleting virtual players ...");
            collection.deleteMany(eq("role", 2));
            System.out.println("virtual players deleted");

            // insert virtual players
            long basePhoneNumber = 13700000000L;
            List<Document> documents = new ArrayList<Document>();
            for (int i = 0; i < this.playerCount; i++) {
                long phoneNumber = basePhoneNumber + i;
                Player player = new Player();
                player.setName("测试" + i);
                player.setStudentName("学生" + i);
                player.setPhoneNumber(Long.toString(phoneNumber));
                player.setVerificationCode("");
                player.setMail(player.getPhoneNumber() + "@dummy.vip");
                player.setEducation("2");
                player.setProfession("计算机科学与技术");
                player.setUniversity("大米大学");
                player.setGraduateDate("2018-04-07");
                player.setPassword("e10adc3949ba59abbe56e057f20f883e");
                player.setPasswordPlain("123456");
                player.setRole(2);
                player.setStatus(1);
                player.setMailStatus(1);
                player.setInstance((int)(phoneNumber % this.instanceCount));

                Document playerDoc = new Document("name", player.getName())
                        .append("studentName", player.getStudentName())
                        .append("phoneNumber", player.getPhoneNumber())
                        .append("verificationCode", player.getVerificationCode())
                        .append("mail", player.getMail())
                        .append("education", player.getEducation())
                        .append("profession", player.getProfession())
                        .append("university", player.getUniversity())
                        .append("graduateDate", player.getGraduateDate())
                        .append("password", player.getPassword())
                        .append("passwordPlain", player.getPasswordPlain())
                        .append("role", player.getRole())
                        .append("status", player.getStatus())
                        .append("mailStatus", player.getMailStatus())
                        .append("instance", player.getInstance());

                System.out.println("player " + player.getName() + ", " + player.getPhoneNumber() +
                        ", " + player.getInstance() + " has been added");
                documents.add(playerDoc);
            }
            System.out.println("inserting virtual players ...");
            collection.insertMany(documents);
            System.out.println(documents.size() + " virtual players has been inserted");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
