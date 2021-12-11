package application;
import database.*;
import presentation.View;

import java.awt.*;

public class PRMS {
    public static void main(String [] args){

        DatabaseModel model = new DatabaseModel();

        View view = new View();

        Controller controller = new Controller(model, view);

        view.setVisible(true);
    }
}
