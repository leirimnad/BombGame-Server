package ua.leirimnad.bombgameserver.words;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class WordManager {
    private final Set<String> words;

    public WordManager() {
        words = new HashSet<>();

        System.out.println("Reading words...");
        try {
            URL resource = getClass().getClassLoader().getResource("words.txt");
            if (resource == null)
                throw new FileNotFoundException();
            File file = new File(resource.toURI());
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("windows-1251"));
            BufferedReader reader = new BufferedReader(isr);

            while (true) {
                String data = reader.readLine();
                if (data == null) break;
                words.add(data);
            }

            reader.close();

        } catch (FileNotFoundException | URISyntaxException e) {
            System.out.println("File words.txt wasn't found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished reading words!");

    }

    public boolean matches(String word, String syllable){
        return word.contains(syllable) & words.contains(word);
    }

}
