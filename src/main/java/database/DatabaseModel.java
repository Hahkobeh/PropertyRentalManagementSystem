package database;

import com.mongodb.MongoException;
import com.mongodb.client.*;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;


import database.users.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseModel {
    private User user;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> propertiesCollection;
    private MongoCollection<Document> emailCollection;
    private MongoCollection<Document> paymentDetails;
    PaymentControl pc;


    public DatabaseModel(){
        //


        connectToDatabase();



        //System.out.println(test.getAccessLevel());
    }

    void connectToDatabase(){
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);

        String uri = "mongodb+srv://jacob_artuso:123pass123@prms.vrama.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
        try {
            mongoClient = MongoClients.create(uri);
            database = mongoClient.getDatabase("PRMS");
            propertiesCollection = database.getCollection("properties");
            userCollection = database.getCollection("users");
            paymentDetails = database.getCollection("paymentdetails");
            emailCollection = database.getCollection("emails");



            //addProperty("NW",2,4,false,"condo",20000);
            //addProperty("SE",2,4,false,"condo",20000);


            //addUser(1,"jacob@gmail.com","password12");

            //addUser(2,"jeffery@gmail.com","pass12");
            //addUser(3,"stuart@gmail.com","managerpass");





            pc = new PaymentControl(propertiesCollection,paymentDetails);


        }catch (MongoException me){
            System.err.println("database error");
        }


    }
    boolean hasAccess(int requiredAccess){
        return requiredAccess == user.getAccessLevel();
    }


    public Report makeReport(){
        return new Report(propertiesCollection);
    }

    public PaymentControl getPc() {
        return pc;
    }

    <T> void editProperty(String toEdit, T setTo){
        switch (toEdit){
            case "propertyID":

        }
    }

    void addProperty(String quadrant, int numBedrooms, int numBathrooms, boolean furnished, String propertyType, double price) {
        if(!hasAccess(2)){
            System.err.println("WRONG ACCESS TO ADD PROPERTY");
            System.exit(-1);
        }
        RegisteredUser temp = (RegisteredUser) user;

        propertiesCollection.insertOne(convertProperty(new Property(new ObjectId(),quadrant, temp.getEmail(), numBedrooms, numBathrooms, furnished, propertyType, price)));
    }

    public static Document convertProperty(Property prop){
        return new Document("_id", new ObjectId()).append("STATUS", prop.getStatus()).append("LANDLORD",prop.getLandlord()).append("QUADRANT",prop.getQuadrant()).append("PAYEDFOR", prop.isPayedFor()).append("OUTSTANDINGFEE", prop.getOutstandingFee()).append("NUMBEDROOMS", prop.getNumBedrooms()).append("NUMBATHROOMS", prop.getNumBathrooms()).append("FURNISHED",prop.isFurnished()).append("PROPERTYTPYE", prop.getPropertyType()).append("PRICE", prop.getPrice());

    }

    public boolean login(String email, String password){
        //verify with database
        Document query = new Document("$and", Arrays.asList(
                new Document("EMAIL", email),
                new Document("PASSWORD", password)
        ));
        Document user = userCollection.find(query).first();


        if (user != null) {
            switch ((int) user.get("TYPE")) {
                case 1: {
                    this.user = new RegisteredRenter((String) user.get("EMAIL"), (String) user.get("PASSWORD"));
                    return true;
                }
                case 2: {
                    MongoCursor<Document> cursor = propertiesCollection.find(eq("LANDLORD",(String) user.get("EMAIL"))).iterator();
                    ArrayList<ObjectId> propIds = new ArrayList<>();
                    while(cursor.hasNext()) {
                        propIds.add((ObjectId) cursor.next().get("_id"));
                    }
                    this.user = new Landlord((String) user.get("EMAIL"), (String) user.get("PASSWORD"),propIds);
                    return true;
                }
                case 3: {
                    this.user = new Manager((String) user.get("EMAIL"), (String) user.get("PASSWORD"));
                    return true;
                }
            }
        }
        return false;
    }



    boolean userExists(String email){
        Document query = new Document("EMAIL", email);
        return userCollection.find(query).first() != null;
    }

    public void unregistedLogin(){
        user = new RegularRenter();
    }

    public ArrayList<Property> search(Document criteria){
        ArrayList<Property> props = new ArrayList<>();
        MongoCursor<Document> cursor = propertiesCollection.find(criteria).iterator();
        while(cursor.hasNext()){
            props.add(new Property(cursor.next()));
        }
        return props;
    }

    public boolean addUser(int accessLevel, String email, String password){
        if(userExists(email)){
            return false;
        }
        switch (accessLevel){
            case 0:
                return false;
            case 1:
                user = new RegisteredRenter(email,password);
                break;
            case 2:
                user = new Landlord(email,password,new ArrayList<>());
                break;
            case 3:
                user = new Manager(email,password);
        }
        userCollection.insertOne(convertUser((RegisteredUser) user));
        return true;
    }

    public static Document convertUser(RegisteredUser user){
        return new Document("_id", new ObjectId()).append("TYPE",user.getAccessLevel()).append("EMAIL", user.getEmail()).append("PASSWORD",user.getPassword());
    }


}
