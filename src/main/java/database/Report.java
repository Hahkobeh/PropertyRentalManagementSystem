package database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;


public class Report {
    //all the info
    MongoCollection<Document> propertiesCollection;
    int numberOfPropertiesTotal = 0;

    Report(MongoCollection<Document> propCol){
        propertiesCollection = propCol;

        MongoCursor<Document> cursor = propertiesCollection.find(eq("STATUS",0)).iterator();
        while(cursor.hasNext()){
            numberOfPropertiesTotal++;
            cursor.next();
        }
        System.out.println(numberOfPropertiesTotal);

    }

}
