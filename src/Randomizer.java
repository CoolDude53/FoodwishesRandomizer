package src;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.jsoup.Jsoup;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

public class Randomizer
{
    public static void main(String args[]) throws IOException
    {
        File file = new File(System.getProperty("user.home") + "/index.json");
        ConcurrentHashMap<String, String> recipes;
        Type mapType = new TypeToken<ConcurrentHashMap<String, String>>()
        {
        }.getType();

        if (JOptionPane.showOptionDialog(null, "Click a button to find a random recipe from Chef John!", "FoodwishesRandomizer", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Update website index.", "Find a random recipe."}, null) == 1)
        {
            if (file.exists())
            {
                JsonReader jsonReader = new JsonReader(new FileReader(file));
                recipes = new Gson().fromJson(jsonReader, mapType);
            }
            else
            {
                recipes = new ConcurrentHashMap<>();
                HTMLScanner htmlScanner;
                String url = "http://foodwishes.blogspot.com/";
                int year = 2007;
                int month = 1;

                while (year < 2020)
                {
                    while (month < 12)
                    {
                        String monthURL = "/" + (month < 10 ? "0" + month : month) + "/";

                        try
                        {
                            htmlScanner = new HTMLScanner(url + year + monthURL);
                        } catch (IOException e)
                        {
                            break;
                        }

                        while (htmlScanner.hasNextLink())
                        {
                            String nextLink = htmlScanner.nextLink();

                            if (nextLink.startsWith(url + year + monthURL) && !recipes.containsKey(nextLink))
                            {
                                new Thread(() -> {
                                    try
                                    {
                                        recipes.put(nextLink, Jsoup.connect(nextLink).get().getElementsByTag("h3").text());
                                    } catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }).run();
                            }
                        }

                        month++;
                    }

                    year++;
                    month = 1;
                }

                String json = new Gson().toJson(recipes);
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(json);
                fileWriter.flush();
                fileWriter.close();
            }

            String recipeURL = (String) recipes.keySet().toArray()[new SecureRandom().nextInt(recipes.size())];

            JEditorPane editorPane = new JEditorPane("text/html", "<html><center><a href=\"" + recipeURL + "\">" + recipes.get(recipeURL) + "</a></center></html>");

            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

            editorPane.addHyperlinkListener(e -> {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED) && desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
                {
                    try
                    {
                        desktop.browse(new URI(recipeURL));
                    } catch (IOException | URISyntaxException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            });

            editorPane.setEditable(false);
            editorPane.setBackground(new JLabel().getBackground());

            int reRandomize = JOptionPane.showOptionDialog(null, editorPane, "Recipe", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Another Recipe?"}, null);

            while (reRandomize == 0)
            {
                String newRecipeURL = (String) recipes.keySet().toArray()[new SecureRandom().nextInt(recipes.size())];

                JEditorPane newEditorPane = new JEditorPane("text/html", "<html><center><a href=\"" + newRecipeURL + "\">" + recipes.get(newRecipeURL) + "</a></center></html>");

                Desktop newDesktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

                newEditorPane.addHyperlinkListener(e -> {
                    if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED) && newDesktop != null && newDesktop.isSupported(Desktop.Action.BROWSE))
                    {
                        try
                        {
                            newDesktop.browse(new URI(newRecipeURL));
                        } catch (IOException | URISyntaxException e1)
                        {
                            e1.printStackTrace();
                        }
                    }
                });

                newEditorPane.setEditable(false);
                newEditorPane.setBackground(new JLabel().getBackground());

                reRandomize = JOptionPane.showOptionDialog(null, newEditorPane, "Recipe", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Find another recipe?"}, null);
            }
        }
        else
        {
            recipes = new ConcurrentHashMap<>();
            HTMLScanner htmlScanner;
            String url = "http://foodwishes.blogspot.com/";
            int year = 2007;
            int month = 1;

            while (year < 2020)
            {
                while (month < 12)
                {
                    String monthURL = "/" + (month < 10 ? "0" + month : month) + "/";

                    try
                    {
                        htmlScanner = new HTMLScanner(url + year + monthURL);
                    } catch (IOException e)
                    {
                        break;
                    }

                    while (htmlScanner.hasNextLink())
                    {
                        String nextLink = htmlScanner.nextLink();

                        if (nextLink.startsWith(url + year + monthURL) && !recipes.containsKey(nextLink))
                        {
                            new Thread(() -> {
                                try
                                {
                                    recipes.put(nextLink, Jsoup.connect(nextLink).get().getElementsByTag("h3").text());
                                } catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }).run();
                        }
                    }

                    month++;
                }

                year++;
                month = 1;
            }

            String json = new Gson().toJson(recipes);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
        }
    }
}
