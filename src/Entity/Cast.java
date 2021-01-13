package Entity;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Cast {
    public enum Role {
        Director,Writer,Star,Creator
    }
    String CastId;
    String CastName;
    String CastURL;
    ArrayList<Role> CastRole = new ArrayList<Role>();
    ArrayList<Movie> CastMovies = new ArrayList<Movie>();

    @Override
    public String toString() {
        return CastName + " " +CastRole;
    }

    public void setCastRole(ArrayList<Role> castRole) {
        CastRole = castRole;
    }

    public ArrayList<Movie> getCastMovies() {
        return CastMovies;
    }

    public void setCastMovies(ArrayList<Movie> castMovies) {
        CastMovies = castMovies;
    }

    public ArrayList<Role> getCastRole(){
        return CastRole;
    }

    public void setCastRole(Role role){
        CastRole.add(role);
    }

    public void setCastId(String castId) {
        CastId = castId;
    }

    public void setCastName(String castName) {
        CastName = castName;
    }

    public void setCastURL(String castURL) {
        CastURL = castURL;
    }

    public String getCastId() {
        return CastId;
    }

    public String getCastName() {
        return CastName;
    }

    public String getCastURL() {
        return CastURL;
    }
}
