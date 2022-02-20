package ua.leirimnad.bombgameserver.words;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class WordManager {
    private final Set<String> words;
    private final Map<String, Float> popular_words;
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

            // TODO все слова будут храниться в оперативной памяти?
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
        parseStringFloatFile(syllables_2, "syllables_2_perc.txt");
        parseStringFloatFile(syllables_3, "syllables_3_perc.txt");
        parseStringFloatFile(syllables_4, "syllables_4_perc.txt");

        popular_words = new TreeMap<>();
        parseStringFloatFile(popular_words, "popular_words.txt");

    }

    private void parseStringFloatFile(Map<String, Float> map, String filename){
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
        return word.toUpperCase(Locale.ROOT).contains(syllable.toUpperCase(Locale.ROOT))
                        && words.contains(word.toLowerCase(Locale.ROOT));
    }

    public String getSyllable(int len, float minComplexity, float maxComplexity){
        if (len < 2 || len > 4) return null;
        Map<String, Float> syllables = switch (len) {
            case 2 -> syllables_2;
            case 3 -> syllables_3;
            default -> syllables_4;
        };

        List<String> applicable = syllables.entrySet().stream()
                .filter(entry -> (entry.getValue() >= minComplexity && entry.getValue() <= maxComplexity))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (applicable.size() == 0) {
            System.out.println("! For some reason no applicable syllables found. Min complexity: "
                    +minComplexity+", max: "+maxComplexity+". Returning random syllable.");
            return (String) syllables.keySet().toArray()[new SecureRandom().nextInt(syllables.size())];
        }

        return applicable.get(new SecureRandom().nextInt(applicable.size())).toUpperCase(Locale.ROOT);
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

    public float getComplexity(String syllable){
        syllable = syllable.toLowerCase(Locale.ROOT);
        if (syllable.length() == 2)
            return Optional.ofNullable(syllables_2.get(syllable)).orElse(-1.0f);
        if (syllable.length() == 3)
            return Optional.ofNullable(syllables_3.get(syllable)).orElse(-1.0f);
        if (syllable.length() == 4)
            return Optional.ofNullable(syllables_4.get(syllable)).orElse(-1.0f);
        return -1.0f;
    }

    public String getPossibleWord(String syllable){
        List<Map.Entry<String, Float>> applicable = popular_words.entrySet().stream().filter((entry) ->
                entry.getKey().toLowerCase(Locale.ROOT)
                        .contains(syllable.toLowerCase(Locale.ROOT))
        ).sorted((e1, e2) -> (int) Math.signum(e1.getValue() - e2.getValue())).toList();

        for (int i = 1; i < applicable.size(); i++) {
            applicable.get(i).setValue(applicable.get(i).getValue()+applicable.get(i).getValue());
        }

        float total_weight = applicable.get(applicable.size()-1).getValue();
        float random_weight = new SecureRandom().nextFloat(total_weight);
        return applicable.stream()
                .filter(e->e.getValue() >= random_weight)
                .findFirst()
                .orElse(new AbstractMap.SimpleEntry<>("None", 0.0f))
                .getKey();
    }
}
