package smap_f18_24.smap_fridge.ModelClasses;

import java.util.List;

public class User {
    String Name;
    String Email;
    List<String> ConnectedFridges;


    //Constructors
    public User()
    {
        //Empty constructor for use with firebase (i think..)
    }

    public User(String name, String email, List<String> connectedFridges) {
        Name = name;
        Email = email;
        ConnectedFridges = connectedFridges;
    }



    //getters and setters
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public List<String> getConnectedFridges() {
        return ConnectedFridges;
    }

    public void setConnectedFridges(List<String> connectedFridges) {
        ConnectedFridges = connectedFridges;
    }



    //Methods
    public void LeaveFridge(String ID)
    {
        //TODO
        //Remove own ID from fridges list of connected users

        //Remove fridge ID from list of connected fridges.
        ConnectedFridges.remove(ID);
    }

    public void CreateNewFridge(String name)
    {
        //TODO
        //Get ID from Database.
        //Create new Fridge object.
        //Upload to database
    }


    public void AddConnectedFridge(String ID)
    {
        ConnectedFridges.add(ID);
    }
}
