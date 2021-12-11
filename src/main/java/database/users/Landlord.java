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

    public void addProp(Property prop){
        properties.add(prop);
    }

    public double getFee(){
        double fee = 0;
        for(Property prop: properties){
            fee += prop.getOutstandingFee();
        }
        return fee;
    }
    public void displayProps(){
        for(Property prop: properties){
            prop.setPayedFor(true);
            prop.setStatus(1);
        }
    }

    public ArrayList<Property> getProperties() {
        return properties;
    }

    public void payFee(double amount){
        double currFee = getFee();
        double portionFee = amount / properties.size();
        for(Property prop: properties){
            prop.setOutstandingFee(prop.getOutstandingFee() - portionFee);
        }
    }

}
