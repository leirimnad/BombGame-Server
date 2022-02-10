package ua.leirimnad.bombgameserver.words;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ua.leirimnad.bombgameserver.networking.server_queries.ServerActionQuery;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class WordManager {
    private final Set<String> words;
    private final Map<String, Float> syllables_2, syllables_3, syllables_4;

    public WordManager() {

        // init words
        List<String> tempWords = new ArrayList<>();

        try {
            URL resource = getClass().getClassLoader().getResource("words.txt");
            if (resource == null)
                throw new FileNotFoundException();
            File file = new File(resource.toURI());
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);

            while (true) {
                String data = reader.readLine();
                if (data == null) break;
                tempWords.add(data);
            }

            reader.close();

        } catch (FileNotFoundException | URISyntaxException e) {
            System.out.println("File words.txt wasn't found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.words = new HashSet<>(tempWords);
        tempWords.clear();

        // init syllables
        syllables_2 = new TreeMap<>();
        syllables_3 = new TreeMap<>();
        syllables_4 = new TreeMap<>();
        parseSyllables(syllables_2, "syllables_2_perc.txt");
        parseSyllables(syllables_3, "syllables_3_perc.txt");
        parseSyllables(syllables_4, "syllables_4_perc.txt");

    }

    private void parseSyllables(Map<String, Float> map, String filename){
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
            if (is == null)
                throw new FileNotFoundException();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);

            while (true) {
                String data = reader.readLine();
                if (data == null) break;
                String syl = data.substring(0, data.indexOf(' '));
                float com = Float.parseFloat(data.substring(data.indexOf(' ')+1));
                map.put(syl, com);
            }

            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("File "+filename+" wasn't found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean matches(String word, String syllable){
        return word.contains(syllable) & words.contains(word);
    }

    public String getSyllable(int len, float minComplexity, float maxComplexity){
        if (len < 2 || len > 4) return null;
        Map<String, Float> syllables;
        switch (len){
            case 2:
                syllables = syllables_2;
                break;
            case 3:
                syllables = syllables_3;
                break;
            default:
                syllables = syllables_4;
                break;
        }

        List<String> applicable = syllables.entrySet().stream()
                .filter(entry -> (entry.getValue() >= minComplexity && entry.getValue() <= maxComplexity))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return applicable.get(new SecureRandom().nextInt(applicable.size()));
    }

    public String getSyllable(float minComplexity, float maxComplexity){
        Random random = new Random();
        int r = random.nextInt(100);
        if (r < 45)
            return getSyllable(2, minComplexity, maxComplexity);
        if (r < 80)
            return getSyllable(3, minComplexity, maxComplexity);
        return getSyllable(4, minComplexity, maxComplexity);
    }

    public String getSyllable(float maxComplexity){
        return this.getSyllable(0, maxComplexity);
    }

    public void processGetSyllable(WebSocketSession session, float minComplexity, float maxComplexity) {
        // this is a testing-only method and should be deleted
        try {
            session.sendMessage(new TextMessage(getSyllable(minComplexity, maxComplexity)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
