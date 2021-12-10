package database.users;

import database.Property;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public class Landlord extends RegisteredUser{
    ArrayList<Property> properties;
    public Landlord(String email, String password, ArrayList<Property> properties) {
        super(email, password, 2);
        this.properties = properties;
    }


}
