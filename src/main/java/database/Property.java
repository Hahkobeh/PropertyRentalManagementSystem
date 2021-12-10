package database;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Property {
    private ObjectId propertyID;
    private int status;// 0 not shown, 1 payed for (shown)
    private String landlord;
    private boolean payedFor;
    private double outstandingFee;
    private String quadrant;
    private int numBedrooms;
    private int numBathrooms;
    private boolean furnished;
    private String propertyType;
    private double price;


    //create a new property to add
    public Property(ObjectId propertyID, String landlord,String quadrant, int numBedrooms, int numBathrooms, boolean furnished, String propertyType, double price) {
        this.quadrant = quadrant;
        this.propertyID = propertyID;
        this.landlord = landlord;
        this.numBedrooms = numBedrooms;
        this.numBathrooms = numBathrooms;
        this.furnished = furnished;
        this.propertyType = propertyType;
        this.price = price;
        status = 0;
        payedFor = false;
        outstandingFee = 100; // should change how this works maybe stored in database
    }

    public Property(Document prop) {
        this.propertyID = prop.getObjectId("_id");
        this.status = prop.getInteger("STATUS");
        this.landlord = prop.getString("LANDLORD");
        this.payedFor = prop.getBoolean("PAYEDFOR");
        this.outstandingFee = prop.getDouble("OUTSTANDINGFEE");
        this.numBedrooms = prop.getInteger("NUMBEDROOMS");
        this.numBathrooms = prop.getInteger("NUMBATHROOMS");
        this.furnished = prop.getBoolean("FURNISHED");
        this.propertyType = prop.getString("PROPERTYTYPE");
        this.price = prop.getDouble("PRICE");
    }

    public int getNumBedrooms() {
        return numBedrooms;
    }

    public void setNumBedrooms(int numBedrooms) {
        this.numBedrooms = numBedrooms;
    }

    public int getNumBathrooms() {
        return numBathrooms;
    }

    public void setNumBathrooms(int numBathrooms) {
        this.numBathrooms = numBathrooms;
    }

    public boolean isFurnished() {
        return furnished;
    }

    public void setFurnished(boolean furnished) {
        this.furnished = furnished;
    }

    public ObjectId getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(ObjectId propertyID) {
        this.propertyID = propertyID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLandlord() {
        return landlord;
    }

    public void setLandlord(String landlord) {
        this.landlord = landlord;
    }

    public boolean isPayedFor() {
        return payedFor;
    }

    public void setPayedFor(boolean payedFor) {
        this.payedFor = payedFor;
    }

    public double getOutstandingFee() {
        return outstandingFee;
    }

    public void setOutstandingFee(double outstandingFee) {
        this.outstandingFee = outstandingFee;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getQuadrant() {
        return quadrant;
    }

    public void setQuadrant(String quadrant) {
        this.quadrant = quadrant;
    }
}
