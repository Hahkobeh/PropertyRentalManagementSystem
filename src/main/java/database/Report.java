package database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;


public class Report {
    //all the info
    MongoCollection<Document> propertiesCollection;
    int numberOfPropertiesTotal = 0;
    StringBuilder theReport = new StringBuilder();


    Report(MongoCollection<Document> propCol){
        propertiesCollection = propCol;
        int totalNumberOfHouses = (int) propertiesCollection.countDocuments();

        theReport.append( "The total number of houses listed is: " + totalNumberOfHouses + "\n") ;

        MongoCursor<Document> cur = propertiesCollection.find(eq("STATUS",0)).iterator();
        int totalNumberofRentedHouses =  0;
        while(cur.hasNext()) {
            totalNumberofRentedHouses++;
            cur.next();
        }
        theReport.append("The total number of inactive houses is: " + totalNumberofRentedHouses + "\n");

        cur = propertiesCollection.find(eq("STATUS",1)).iterator();
        int totalNumberofActiveHouses =  0;
        while(cur.hasNext()) {
            totalNumberofActiveHouses++;
            cur.next();
        }
        theReport.append("The total number of active houses is: " + totalNumberofActiveHouses + "\n\n");

        cur = propertiesCollection.find(eq("STATUS",0)).iterator();
        System.out.println("The house(s) that were rented is/are:");
        while(cur.hasNext()){
            Document stuff = cur.next();
            theReport.append( "House id: " + stuff.get("_id") + " Landord is: " + stuff.get("LANDLORD")+ "  and Quadrant is: "+ stuff.get("QUADRANT") + "\n");
        }
        System.out.println(theReport.toString());

    }

    public StringBuilder getTheReport() {
        return theReport;
    }
}