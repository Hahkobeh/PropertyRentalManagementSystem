package database.users;

import org.bson.types.ObjectId;

import java.util.ArrayList;

public class Landlord extends RegisteredUser{
    ArrayList<ObjectId> propertyIDs;
    public Landlord(String email, String password, ArrayList<ObjectId> propertyIDs) {
        super(email, password, 2);
        this.propertyIDs = propertyIDs;
    }


}
