package smap_f18_24.smap_fridge.ModelClasses;

public class List_ID {

    private String ID;

    public List_ID(){
        //For firebase, perhaps?
    }

    public List_ID(String ID){
        setID(ID);
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
