package database.users;

public class RegisteredRenter extends RegisteredUser{

    public RegisteredRenter(String email, String password) {
        super(email, password, 1);
    }
}
