package edu.virginia.sde.reviews;

import java.sql.SQLException;


//Has the basic logic for the login screen. Singleton because it only needs to be created once.
public final class LoginService {

    private static LoginService instance;
    private final DatabaseDriver databaseDriver;

    private LoginService(){
        this.databaseDriver = Config.getInstance().db;
    }

    public static LoginService getInstance(){
        if (instance == null){
            return new LoginService();
        }
        return instance;
    }


    //Check if a username and password match a user in the database
    public boolean isCorrectLoginInfo(String username, String password){
        try {
            return databaseDriver.checkUserExist(username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
            
        }
    }

    public boolean usernameExists(String username){
        try {
            return (!databaseDriver.isUsernameAvailable(username));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean addUser(String username, String password) {
        
        try {
            if (password.length() < 8 || username == null){
                return false;
            }

            if (databaseDriver.isUsernameAvailable(username)){
                databaseDriver.addUser(username, password);
                return true;
            }
            return false;

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        
    }

}