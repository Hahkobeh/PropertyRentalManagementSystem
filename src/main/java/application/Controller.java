package application;

import database.DatabaseModel;
import database.Property;
import database.users.Landlord;
import org.bson.Document;
import org.bson.types.ObjectId;
import presentation.View;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Controller {
    DatabaseModel model;
    View view;
    Controller(DatabaseModel model, View view){
        this.model = model;
        this.view = view;
        addButtonListeners();



        //view.getSearchButton().addActionListener(new SearchButton());

    }

    private void addButtonListeners(){
        view.showPropsFirstTimeR(model.getAll());
        view.getRegisterNewUserButton().addActionListener(new RegisterButton());
        view.getLoginButton().addActionListener(new LoginButton());
        view.getTheReportButton().addActionListener( new GetReportButton());
        view.getChangefeesbutton().addActionListener( new changeFeeButton());
        view.getNewPropSubmitButton().addActionListener(new AddProp());
        view.getPayFeesLandlord().addActionListener(new getFee());
        view.getDepositButton().addActionListener(new payFee());
        view.getEnterHomeButton().addActionListener(new enterButton());
        view.getEditListingsLandlord().addActionListener(new getLLListing());
        view.getEditListingsManagerButton().addActionListener(new getMListing());
        view.getSearchButtonSubmit().addActionListener(new SearchButton());
    }

    public class getMListing implements ActionListener{
        public void actionPerformed(ActionEvent e){

            view.showPropsM(model.getAll());
        }
    }

    public class editLLProp implements ActionListener{
        public void actionPerformed(ActionEvent e){
            int temp = view.getEditLL();
            Landlord ll = (Landlord) model.getUser();
            //ll.getProperties().get(temp) = new Property();
        }
    }


    public class getLLListing implements ActionListener{
        public void actionPerformed(ActionEvent e){
            Landlord ll = (Landlord) model.getUser();
            view.showLLProps(model.search(new Document("LANDLORD",ll.getEmail())));
        }
    }


    public class enterButton implements ActionListener{
        public void actionPerformed(ActionEvent e){
            System.out.println("WAS HERE");
            model.unregistedLogin();
            view.showPropsR(model.getAll());
        }
    }

    public class AddProp implements ActionListener{
        public void actionPerformed(ActionEvent e){
            model.addProperty(view.getQuadrant(), view.getNumBedroom(), view.getNumBathroom(),view.getFurnished(),view.getPropType(),view.getPrice());
            view.propAdded();
        }
    }

    //get the outstanding fee


    //pay the fee for a house
    public class getFee implements ActionListener{
        public void actionPerformed(ActionEvent e){
            Landlord ll = (Landlord) model.getUser();
            view.setBalanceFieldText(ll.getFee());
        }
    }

    public class payFee implements ActionListener{
        public void actionPerformed(ActionEvent e){
            Landlord ll = (Landlord) model.getUser();
            ll.payFee(view.getDepositField());
            if(ll.getFee() == 0){
                model.payForProps(ll.getEmail());
            }
            view.setBalanceFieldText(ll.getFee());
        }
    }



    //admin edit fee / period
    public class changeFeeButton implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(view.getManagerFee() != 0){
                model.getPc().editFee(view.getManagerFee());
            }
            if(view.getPeriod() != 0){
                model.getPc().editFee(view.getPeriod());
            }
            view.feePeriodChanged();
        }
    }

    public class GetReportButton implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            StringBuilder temp = model.makeReport();
            System.out.println("hello");
            view.setSummaryText(temp.toString());

        }
    }


    public class SearchButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO getters for search criteria

            //EXAMPLE
            List<Document> criterias = new ArrayList<>();
            if(view.getSearchNumbBath()){
                criterias.add(new Document("NUMBATHROOMS",view.getNumbBathroomField2()));
            }
            if(view.getSearchNumbBed()){
                criterias.add(new Document("NUMBEDROOMS",view.getNumbBedSpinner2()));
            }
            if(view.getSearchPrice()){
                criterias.add(new Document("PRICE",view.getPriceField2()));
            }
            if(view.getSearchQuad()){
                criterias.add(new Document("QUADRANT",view.getQuadrantInputBox2()));
            }
            if(view.getSearchPropType()){
                criterias.add(new Document("PROPERTYTYPE",view.getPropTypeNewPropField2()));
            }
            if(view.getSearchFurn()){
                criterias.add(new Document("FURNISHED",view.getFurnishedBox2()));
            }

            if(criterias.isEmpty()){
                view.showPropsR(model.getAll());
                return;
            }
            Document criteria = new Document("$and", criterias);
            //SEARCH DATABASE
            ArrayList<Property> props = model.search(criteria);

            view.showPropsR(props);




        }
    }
    //for registration of an account
    public class RegisterButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            view.showPropsR(model.getAll());

            int accessLevel = view.getComboRegisterBox();
            String email = view.getEmailRegisterField();
            String password = view.getPasswordRegisterField();
            //System.out.println(accessLevel +" "+ email +" "+ password);
            if(model.addUser(accessLevel,email,password)){
                view.successfulLogin(model.getAccountType());

            }else{
                view.throwError("That email is already tied to an account!");
            }


        }
    }



    //for logging in
    public class LoginButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            view.showPropsR(model.getAll());
            System.out.println("Login attempted");
            String username = view.getUsernameField();
            String password = view.getPasswordField();

            System.out.println(username+" "+password);
            if(model.login(username,password)){
                view.successfulLogin(model.getAccountType());
            }else{
                view.failedLogin();
                view.throwError("Login was unsuccessful!");
            }
        }
    }
}
