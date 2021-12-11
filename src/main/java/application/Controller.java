package application;

import database.DatabaseModel;
import database.Property;
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

        view.getRegisterNewUserButton().addActionListener(new RegisterButton());
        view.getLoginButton().addActionListener(new LoginButton());
        view.getTheReportButton().addActionListener( new GetReportButton());
        view.getChangefeesbutton().addActionListener( new changeFeeButton());

        //view.getSearchButton().addActionListener(new SearchButton());

    }
    //get the outstanding fee


    //pay the fee for a house

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
            criterias.add(new Document("NUMBEDROOMS",3));
            criterias.add(new Document("FURNISHED",true));
            Document criteria = new Document("$and", criterias);
            //SEARCH DATABASE
            ArrayList<Property> props = model.search(criteria);
            //TODO display properties in view




        }
    }
    //for registration of an account
    public class RegisterButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

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
