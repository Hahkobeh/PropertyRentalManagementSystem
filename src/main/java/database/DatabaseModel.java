package database;

import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;


import database.users.*;
import org.bson.conversions.Bson;
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
            pc = new PaymentControl(propertiesCollection,paymentDetails);


        }catch (MongoException me){
            System.err.println("database error");
        }


    }

    public void payForProps(String ll){
        Bson temp = Updates.set("PAYEDFOR", true);
        Bson temp2 = Updates.set("STATUS", 1);
        propertiesCollection.updateMany(eq("LANDLORD",ll), temp);
        propertiesCollection.updateMany(eq("LANDLORD",ll), temp2);
    }

    public User getUser() {
        return user;
    }

    public String getAccountType(){
        switch (user.getAccessLevel()){
            case 1:
                return "Renter";
            case 2:
                return "Landlord";
            case 3:
                return "Manager";
            default:
                return "unregistered";
        }

    }


    boolean hasAccess(int requiredAccess){
        return requiredAccess == user.getAccessLevel();
    }


    public StringBuilder makeReport(){
        return new Report(propertiesCollection).getTheReport();
    }

    public PaymentControl getPc() {
        return pc;
    }

    <T> void editProperty(String toEdit, T setTo){
        switch (toEdit){
            case "propertyID":

        }
    }

    public void addProperty(String quadrant, int numBedrooms, int numBathrooms, boolean furnished, String propertyType, double price) {
        if(!hasAccess(2)){
            System.err.println("WRONG ACCESS TO ADD PROPERTY");
            System.exit(-1);
        }
        RegisteredUser temp = (RegisteredUser) user;

        Property proptemp = new Property(new ObjectId(), temp.getEmail(),quadrant, numBedrooms, numBathrooms, furnished, propertyType, price);
        Landlord lluser = (Landlord) user;
        lluser.addProp(proptemp);
        propertiesCollection.insertOne(convertProperty(proptemp));
    }

    public static Document convertProperty(Property prop){
        return new Document("_id", new ObjectId()).append("STATUS", prop.getStatus()).append("LANDLORD",prop.getLandlord()).append("QUADRANT",prop.getQuadrant()).append("PAYEDFOR", prop.isPayedFor()).append("OUTSTANDINGFEE", prop.getOutstandingFee()).append("NUMBEDROOMS", prop.getNumBedrooms()).append("NUMBATHROOMS", prop.getNumBathrooms()).append("FURNISHED",prop.isFurnished()).append("PROPERTYTYPE", prop.getPropertyType()).append("PRICE", prop.getPrice());

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

                    ArrayList<Property> props = new ArrayList<>();
                    while(cursor.hasNext()) {
                        props.add(new Property(cursor.next()));
                    }
                    this.user = new Landlord((String) user.get("EMAIL"), (String) user.get("PASSWORD"),props);
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
    public ArrayList<Property> getAll(){
        ArrayList<Property> props = new ArrayList<>();
        MongoCursor<Document> cursor = propertiesCollection.find().iterator();
        while(cursor.hasNext()){
            props.add(new Property(cursor.next()));
        }
        return props;
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
