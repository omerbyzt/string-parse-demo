import Entity.Cast;
import Entity.Movie;
import Entity.WatchList;
import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Form1 {
    private JPanel jPanel1;
    private JTextField textField1;
    private JButton button1;
    private JList list1;
    private JLabel labelResult;
    private JList list2;
    private JList list3;
    private JList list4;
    private JList list5;
    private JButton btnAddWatchList;
    Cast postCast = new Cast();
    ArrayList<Cast> Casts = new ArrayList();
    Movie mv;
    ArrayList<Movie> movieWatchList = new ArrayList<>();
    WatchList watchList = new WatchList();
    Gson gson = new Gson();

    public void fillWatchList(){
        DefaultListModel dfmMovieWatchList = new DefaultListModel();
        int counter = 0 ;
        for (Movie m : movieWatchList){
            dfmMovieWatchList.add(counter,m);
            counter++;
        }

        list2.setModel(dfmMovieWatchList);
    }
    public Form1() {

        btnAddWatchList.setEnabled(false);
        String isFull = readFile("D:\\watchList.txt");

        if (isFull.length()>1){
            watchList = gson.fromJson(readFile("D:\\watchList.txt"),WatchList.class);
            movieWatchList = watchList.getWatchList();

            fillWatchList();
        }

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstURL = "https://www.imdb.com/find?q=" + textField1.getText();
                String result = getUrl(firstURL);

                String startKey = "<table class=\"findList\">";
                String endKey = "</table>";

                int startIndex = result.indexOf(startKey)+startKey.length();
                int endIndex = result.indexOf(endKey);

                result = result.substring(startIndex,endIndex);

                DefaultListModel<Movie> movieList = new DefaultListModel<Movie>();

                startKey = "<td class=\"result_text\"> <a href=\"";
                endKey = "\" >";

                startIndex = result.indexOf(startKey) + startKey.length();
                endIndex = result.indexOf(endKey, startIndex);

                while(startIndex > -1) {
                    Movie mv = new Movie();

                    //Url
                    mv.setUrl(result.substring(startIndex, endIndex));

                    //Name
                    startIndex = endIndex + endKey.length();
                    endKey = "</a> ";
                    endIndex = result.indexOf(endKey, startIndex);
                    mv.setName(result.substring(startIndex, endIndex));

                    //Year
                    startIndex = endIndex + endKey.length();
                    endKey = ")";
                    endIndex = result.indexOf(endKey, startIndex);
                    mv.setYear(result.substring(startIndex, endIndex)+")");

                    startKey = "<td class=\"result_text\"> <a href=\"";
                    endKey = "\" >";

                    startIndex = result.indexOf(startKey,endIndex);

                    if(startIndex > -1){
                        startIndex = result.indexOf(startKey,endIndex) + startKey.length();
                        endIndex = result.indexOf(endKey, startIndex);
                    }

                    movieList.addElement(mv);
                }
                list1.setModel(movieList);

            }
        });
        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                mv = (Movie) list1.getSelectedValue();
                Casts.clear();

                btnAddWatchList.setEnabled(true);

                String castURL = null;
                ArrayList<String> castNames = new ArrayList<>();

                //Film Page Result
                String result = getUrl("https://www.imdb.com" + mv.getUrl());

                //Film Description
                String startKey = "<div class=\"summary_text\">";
                String endKey = "</div>";

                int startIndex = result.indexOf(startKey) + startKey.length();
                int endIndex = result.indexOf(endKey, startIndex);

                mv.setDescription(result.substring(startIndex, endIndex));
                labelResult.setText(mv.getDescription());

                //Directors Result
                startKey = "<h4 class=\"inline\">Director";
                endKey = " </div>";
                startIndex = result.indexOf(startKey) + startKey.length();
                endIndex = result.indexOf(endKey, startIndex);

                String directorResult = result.substring(startIndex, endIndex);

                //Directors CastLURL
                startKey = "<a href=\"";
                endKey = "\">";
                startIndex = directorResult.indexOf(startKey) + startKey.length();
                endIndex = directorResult.indexOf(endKey, startIndex);
                String controlString = directorResult.substring(startIndex, endIndex);

                if (controlString.substring(0, 2).equals("/n")) {
                    while (startIndex > -1) {
                        Cast tempCast = new Cast();

                        castURL = directorResult.substring(startIndex, endIndex);

                        startKey = "\">";
                        endKey = "</a>";
                        startIndex = directorResult.indexOf(startKey, endIndex) + startKey.length();
                        endIndex = directorResult.indexOf(endKey, startIndex);

                        String directorName = directorResult.substring(startIndex, endIndex);

                        if (!directorName.contains("more")) {
                            if (!castNames.contains(directorName)) {
                                tempCast.setCastName(directorName);
                                castNames.add(directorName);
                                tempCast.setCastURL(castURL);
                                tempCast.setCastRole(Cast.Role.Director);
                                Casts.add(tempCast);
                            } else {
                                for (Cast c : Casts) {
                                    if (c.getCastName().equals(directorName)) {
                                        tempCast = c;
                                        tempCast.setCastRole(Cast.Role.Director);
                                        c = tempCast;
                                    }
                                }
                            }

                        }
                        startKey = "<a href=\"";
                        endKey = "\">";

                        startIndex = directorResult.indexOf(startKey, endIndex);

                        if (startIndex > -1) {
                            startIndex = directorResult.indexOf(startKey, endIndex) + startKey.length();
                            endIndex = directorResult.indexOf(endKey, startIndex);
                        }
                    }
                }
                mv.setCasts(Casts);
                DefaultListModel dfmDirector = new DefaultListModel();

                int count = 0;
                int count2 = 0;
                for (Cast c : Casts) {
                    if (c.getCastRole().contains(Cast.Role.Director)) {
                        dfmDirector.add(count, Casts.get(count2));
                        count++;
                    }
                    count2++;
                }
               // list2.setModel(dfmDirector);

                //Writers
                startKey = "<h4 class=\"inline\">Writer";
                endKey = "    </div";
                startIndex = result.indexOf(startKey) + startKey.length();
                endIndex = result.indexOf(endKey, startIndex);

                String writerResult = result.substring(startIndex, endIndex);

                //Writers
                startKey = "<a href=\"";
                endKey = "\">";
                startIndex = writerResult.indexOf(startKey) + startKey.length();
                endIndex = writerResult.indexOf(endKey, startIndex);

                controlString = writerResult.substring(startIndex, endIndex);
                if (controlString.substring(0, 2).equals("/n")){
                    while (startIndex > -1) {
                        Cast tempCast = new Cast();

                        castURL = writerResult.substring(startIndex, endIndex);

                        startKey = "\">";
                        endKey = "</a>";
                        startIndex = writerResult.indexOf(startKey, endIndex) + startKey.length();
                        endIndex = writerResult.indexOf(endKey, startIndex);

                        String writerName = writerResult.substring(startIndex, endIndex);
                        if (!writerName.contains("more")) {
                            if (!castNames.contains(writerName)) {
                                tempCast.setCastName(writerName);
                                castNames.add(writerName);
                                tempCast.setCastURL(castURL);
                                tempCast.setCastRole(Cast.Role.Writer);
                                Casts.add(tempCast);
                            } else {
                                for (Cast c : Casts) {
                                    if (c.getCastName().equals(writerName)) {
                                        tempCast = c;
                                        tempCast.setCastRole(Cast.Role.Writer);
                                        c = tempCast;
                                    }
                                }
                            }
                        }
                        startKey = "<a href=\"";
                        endKey = "\">";

                        startIndex = writerResult.indexOf(startKey, endIndex);

                        if (startIndex > -1) {
                            startIndex = writerResult.indexOf(startKey, endIndex) + startKey.length();
                            endIndex = writerResult.indexOf(endKey, startIndex);
                        }
                    }
                }
                DefaultListModel dfmWriter = new DefaultListModel();
                mv.setCasts(Casts);
                count =0 ;
                count2 = 0;
                for (Cast c : Casts){
                    if (c.getCastRole().contains(Cast.Role.Writer)) {
                        dfmWriter.add(count, Casts.get(count2));
                        count++;
                    }
                    count2++;
                }
                list3.setModel(dfmWriter);

                //Stars
                startKey = "<h4 class=\"inline\">Star";
                endKey = "</div>";
                startIndex = result.indexOf(startKey)+startKey.length();
                endIndex = result.indexOf(endKey,startIndex);

                String starResult = result.substring(startIndex,endIndex);

                //Stars
                startKey = "<a href=\"";
                endKey = "\">";
                startIndex = starResult.indexOf(startKey)+startKey.length();
                endIndex = starResult.indexOf(endKey,startIndex);

                while(startIndex > -1) {
                    Cast tempCast = new Cast();

                    castURL = starResult.substring(startIndex, endIndex);

                    startKey = "\">";
                    endKey = "</a>";
                    startIndex = starResult.indexOf(startKey, endIndex) + startKey.length();
                    endIndex = starResult.indexOf(endKey, startIndex);

                    String starName = starResult.substring(startIndex, endIndex);

                    if (!starName.equals("See full cast & crew")){
                        if (!castNames.contains(starName)) {
                            tempCast.setCastName(starName);
                            castNames.add(starName);
                            tempCast.setCastURL(castURL);
                            tempCast.setCastRole(Cast.Role.Star);
                            Casts.add(tempCast);
                        } else {
                            for (Cast c : Casts) {
                                if (c.getCastName().equals(starName)) {
                                    tempCast = c;
                                    tempCast.setCastRole(Cast.Role.Star);
                                    c = tempCast;
                                }
                            }
                        }
                    }
                    startKey = "<a href=\"";
                    endKey = "\">";

                    startIndex = starResult.indexOf(startKey,endIndex);

                    if (startIndex > -1){
                        startIndex = starResult.indexOf(startKey,endIndex)+startKey.length();
                        endIndex = starResult.indexOf(endKey,startIndex);
                    }

                }
                DefaultListModel dfmStar = new DefaultListModel();
                mv.setCasts(Casts);
                count =0 ;
                count2 = 0;
                for (Cast c : Casts){
                    if (c.getCastRole().contains(Cast.Role.Star)) {
                        dfmStar.add(count, Casts.get(count2));
                        count++;
                    }
                    count2++;
                }
                list4.setModel(dfmStar);

                DefaultListModel dfmFinal = new DefaultListModel();
                int count3 = 0;
                for (Cast cast : Casts){
                    dfmFinal.add(count3,cast);
                    count3++;
                }
                list5.setModel(dfmFinal);
            }
        });
        list5.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                Cast gönderilenCast = new Cast();
                gönderilenCast = (Cast)list5.getSelectedValue();
                try {
                    NewForm nf = new NewForm(gönderilenCast);
                } catch (MalformedURLException | InterruptedException malformedURLException) {
                    malformedURLException.printStackTrace();
                }

            }
        });
        btnAddWatchList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                movieWatchList.add(mv);

                watchList.setWatchList(movieWatchList);
                fillWatchList();

                String temp = gson.toJson(watchList, WatchList.class);

                try {
                    writeFile(temp);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                btnAddWatchList.setEnabled(false);
            }
        });
        list2.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Movie deleteMovie = (Movie) list2.getSelectedValue();
                movieWatchList.remove(deleteMovie);

                watchList.setWatchList(movieWatchList);

                fillWatchList();

                String temp = gson.toJson(watchList, WatchList.class);

                try {
                    writeFile(temp);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                btnAddWatchList.setEnabled(false);

            }
        });
    }
    public void writeFile(String watchList)throws IOException {
        File file = new File("D:\\watchList.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(watchList);
        writer.close();
    }

    public String readFile(String path){
        String data = "";
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data += myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return data;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Form1");
        frame.setContentPane(new Form1().jPanel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        frame.setVisible(true);
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


