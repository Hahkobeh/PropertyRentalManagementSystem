package database.users;

public class Manager extends RegisteredUser{
    public Manager(String email, String password) {
        super(email, password, 3);
    }
}
