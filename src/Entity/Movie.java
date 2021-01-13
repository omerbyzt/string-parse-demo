package Entity;

import java.util.ArrayList;

public class Movie {

    private String Id;
    private String Name;
    private String Year;
    private String Url;
    private String Description;
    private ArrayList<Cast> Casts;

    @Override
    public String toString() {
        return getName() + " " +getYear();
    }

    public void setCasts(ArrayList<Cast> casts) {
        Casts = casts;
    }

    public ArrayList<Cast> getCasts() {
        return Casts;
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getYear() {
        return Year;
    }

    public String getUrl() {
        return Url;
    }

    public String getDescription() {
        return Description;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setYear(String year) {
        Year = year;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public void setDescription(String description) {
        Description = description;
    }


}
