package database;

import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PaymentControl {
    private MongoCollection<Document> propertiesCollection;
    private MongoCollection<Document> paymentDetails;
    private int period;
    private double cost;
    private Date currentPeriodStart;
    private Date currentDate;

    public PaymentControl(MongoCollection<Document> propCol,MongoCollection<Document> pd) {
        propertiesCollection = propCol;
        paymentDetails = pd;

        Document sysDetailsTemp = new Document("PERIOD",30).append("PERIODSTART", "2021-11-18").append("COST",(double)100.00);
        paymentDetails.insertOne(sysDetailsTemp);
        getDate();
        getDetails();

        System.out.println(currentPeriodStart);
        System.out.println(currentDate);

        long diff = currentDate.getTime() - currentPeriodStart.getTime();


        long days = diff / (24 * 60 * 60 * 1000);
        System.out.println(days + "days have passed");


        if(days > period){
            resetPayment();
            setPeriod(currentDate);//TODO fix this later it just sets to current day, works if it will be opened each day
        }

    }

    public boolean payForProperty(double amountPayed, ObjectId id){
        double outstandingFee = getOutstandingFee(id) - amountPayed;
        Bson temp = Updates.set("OUTSTANDINGFEE", outstandingFee);
        propertiesCollection.updateOne(eq("_id",id),temp);
        return true;
    }

    public double getOutstandingFee(ObjectId id){
        Document prop = propertiesCollection.find(eq("_id",id)).first();
        assert prop != null;
        return prop.getDouble("OUTSTANDINGFEE");
    }



    void resetPayment(){


        Bson payedForUpdate = Updates.set("PAYEDFOR", false);
        propertiesCollection.updateMany(eq("PAYEDFOR",true), payedForUpdate);
        Bson periodUpdate = Updates.set("OUTSTANDINGFEE", cost);

        propertiesCollection.updateMany(exists("OUTSTANDINGFEE"), periodUpdate);

    }


    void setPeriod(Date setTo){
        currentPeriodStart = setTo;
    }

    void getDate(){
        Calendar cal = Calendar.getInstance();

        currentDate = cal.getTime();

    }

    void getDetails(){
        Document periodLength = paymentDetails.find(type("PERIOD","int")).first();
        Document periodStart = paymentDetails.find(type("PERIODSTART","string")).first();
        Document costInfo = paymentDetails.find(type("COST","double")).first();
        assert periodLength != null;
        period = periodLength.getInteger("PERIOD");
        assert periodStart != null;

        String temp = periodStart.getString("PERIODSTART");
        SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            currentPeriodStart = sdf.parse(temp);
        }catch (ParseException e){
            System.err.println("date parse failed");
        }
        assert costInfo != null;
        cost = costInfo.getDouble("COST");


    }
}
