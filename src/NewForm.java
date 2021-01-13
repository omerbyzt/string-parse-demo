import Entity.Cast;
import Entity.Movie;
import jdk.swing.interop.SwingInterOpUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NewForm extends JFrame{
    public JPanel panel1;
    private JLabel lblCastName;
    private JLabel labelDescription;
    private JLabel labelBirthDate;
    private JLabel labelImage;
    private JButton nextPhotoButton;
    private JLabel labelPhotos;
    private JButton prevPhotoButton;
    private JList listCastMovies;
    Cast postedCast = new Cast();
    ArrayList<String> castPhotosUrl = new ArrayList<>();
    int photoCount = 0;
    ArrayList<Movie> castMovieList = new ArrayList<>();

    public NewForm(Cast cast) throws MalformedURLException, InterruptedException {
        JFrame frame = new JFrame();
        add(panel1);
        setSize(400,800);
        setVisible(true);
        postedCast = cast;

        //Cast Result String
        lblCastName.setText(postedCast.getCastName());
        String castUrl = "https://www.imdb.com/"+postedCast.getCastURL();

        String castResult = getUrl(castUrl);

        //Description and Birthdate
        String startKey = "\"description\":";
        String endKey = "</script>";
        int startIndex = castResult.indexOf(startKey)+startKey.length();
        int endIndex = castResult.indexOf(endKey,startIndex);

        String castDescription = castResult.substring(startIndex,endIndex);
        String castBirthDate = "-";

        if (castDescription.contains("\"birthDate\":")){
            endKey = "\",";
            endIndex = castResult.indexOf(endKey,startIndex);

            castDescription = castResult.substring(startIndex,endIndex);

            startKey ="\"birthDate\": \"";
            endKey = "\"";

            startIndex = castResult.indexOf(startKey)+startKey.length();
            endIndex = castResult.indexOf(endKey,startIndex);

            castBirthDate = castResult.substring(startIndex,endIndex);
        }

        //labelDescription.setText(castDescription);
        labelBirthDate.setText(castBirthDate);

        //Cast Image URL
        startKey = "name-poster";
        endKey= "</a>";
        startIndex = castResult.indexOf(startKey)+startKey.length();
        endIndex = castResult.indexOf(endKey,startIndex);

        String castImageResult = castResult.substring(startIndex,endIndex);

        startKey = "src=\"";
        endKey = "\" />";

        startIndex = castImageResult.indexOf(startKey)+startKey.length();
        endIndex = castImageResult.indexOf(endKey,startIndex);

        String castImageUrl = castImageResult.substring(startIndex,endIndex);

        //Cast Picture
        URL imageUrl= new URL(castImageUrl);

        ImageIcon icon = new ImageIcon(imageUrl);
        labelImage.setIcon(icon);

        //Cast Film Photos

        startKey = "<div class=\"mediastrip\"> ";
        endKey = "</div>";

        startIndex = castResult.indexOf(startKey)+startKey.length();
        endIndex = castResult.indexOf(endKey,startIndex);

        String castPhotosResult = castResult.substring(startIndex,endIndex);

        startKey = "loadlate=\"";
        endKey = "\" /></a>";
        //startKey = "src=\"";
        //endKey = "\" class";

        startIndex = castPhotosResult.indexOf(startKey)+startKey.length();
        endIndex = castPhotosResult.indexOf(endKey,startIndex);

        while(startIndex > -1){
            castPhotosUrl.add(castPhotosResult.substring(startIndex,endIndex));

            startKey = "loadlate=\"";
            endKey = "\" /></a>";
            //startKey = "src=\"";
            //endKey = "\" class";

            startIndex = castPhotosResult.indexOf(startKey,endIndex);

            if(startIndex > -1){
                startIndex = castPhotosResult.indexOf(startKey,endIndex)+startKey.length();
                endIndex = castPhotosResult.indexOf(endKey,startIndex);
            }
        }

        if(postedCast.getCastRole().contains(Cast.Role.Star)){
            startKey = "<a name=\"actor\">Actor</a>";
            endKey = "<div id=\"filmo";

            startIndex = castResult.indexOf(startKey);
            endIndex = castResult.indexOf(endKey,startIndex);

            String castMoviesResult = castResult.substring(startIndex,endIndex);

            startKey = "<b><a href=\"";
            endKey = "\">";
            startIndex = castMoviesResult.indexOf(startKey)+startKey.length();
            endIndex = castMoviesResult.indexOf(endKey,startIndex);

            while(startIndex > -1){
                Movie tempMv = new Movie();

                String movieUrl = castMoviesResult.substring(startIndex,endIndex);

                startIndex=endIndex+endKey.length();
                endKey = "</a>";
                endIndex = castMoviesResult.indexOf(endKey,startIndex);

                String movieName = castMoviesResult.substring(startIndex,endIndex);

                tempMv.setUrl(movieUrl);
                tempMv.setName(movieName);
                castMovieList.add(tempMv);

                startKey = "<b><a href=\"";
                endKey = "\">";
                startIndex = castMoviesResult.indexOf(startKey,endIndex);

                if (startIndex > -1){
                    startIndex = castMoviesResult.indexOf(startKey,endIndex)+startKey.length();
                    endIndex = castMoviesResult.indexOf(endKey,startIndex);
                }

                System.out.println(tempMv.getName());
            }
        }

        DefaultListModel castMovieListDfm = new DefaultListModel();
        int count = 0;
        for (Movie mv : castMovieList){
            castMovieListDfm.add(count,castMovieList.get(count));
            count++;
        }
        listCastMovies.setModel(castMovieListDfm);

        postedCast.setCastMovies(castMovieList);


        URL castPhotoUrl = new URL(castPhotosUrl.get(photoCount));
        photoCount++;
        ImageIcon castPhotoIcon = new ImageIcon(castPhotoUrl);
        labelPhotos.setIcon(castPhotoIcon);
        nextPhotoButton.setPreferredSize(new Dimension(150, 40));
        prevPhotoButton.setPreferredSize(new Dimension(150, 40));
        prevPhotoButton.setVisible(false);

        nextPhotoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                URL castPhotoUrl = null;
                try {
                    if(photoCount < castPhotosUrl.size()-1) {
                        castPhotoUrl = new URL(castPhotosUrl.get(photoCount));
                        photoCount++;
                    }
                    else if(photoCount == castPhotosUrl.size()-1){
                        castPhotoUrl = new URL(castPhotosUrl.get(photoCount));
                        nextPhotoButton.setVisible(false);
                    }
                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                }
                ImageIcon castPhotoIcon = new ImageIcon(castPhotoUrl);
                labelPhotos.setIcon(castPhotoIcon);
                prevPhotoButton.setVisible(true);
            }
        });
        prevPhotoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                URL castPhotoUrl = null;
                photoCount--;
                try {
                    if(photoCount > 0) {
                        castPhotoUrl = new URL(castPhotosUrl.get(photoCount));
                    }
                    else if(photoCount == 0){
                        castPhotoUrl = new URL(castPhotosUrl.get(photoCount));
                        prevPhotoButton.setVisible(false);
                    }
                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                }
                ImageIcon castPhotoIcon = new ImageIcon(castPhotoUrl);
                labelPhotos.setIcon(castPhotoIcon);
                nextPhotoButton.setVisible(true);
            }
        });
    }
    private String getUrl(String _url) {

        HttpURLConnection con = null;
        StringBuffer content = new StringBuffer();

        try {
            URL url = new URL(_url);

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }

        return content.toString();
    }

}
