import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dictionary {
    private DLinkedList dictionary;
    private Random random;

    public Dictionary() {
        dictionary = new DLinkedList();
        random = new Random();
    }

    public boolean addWord(String word, String englishMeaning,
                           String arabicMeaning, String example, String type) {
        Word newWord = new Word(word, englishMeaning, arabicMeaning, example, type);
        return dictionary.addWord(newWord);
    }

    public Word searchEnglish(String englishWord) {
        return dictionary.searchEnglish(englishWord);
    }

    public Word searchArabic(String arabicWord) {
        return dictionary.searchArabic(arabicWord);
    }

    public boolean updateWord(String englishWord, String englishMeaning,
                              String arabicMeaning, String example, String type) {
        Word updatedWord = new Word(englishWord, englishMeaning,
                arabicMeaning, example, type);
        return dictionary.updateWord(englishWord, updatedWord);
    }

    public boolean deleteWord(String englishWord) {
        return dictionary.deleteWord(englishWord);
    }

    public int loadFromFile(String filename) {
        int count = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;

            while ((line = reader.readLine()) != null) {
                Word word = Word.fromString(line);
                if (word != null && dictionary.addWord(word)) {
                    count++;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
        return count;
    }

    public int saveToFile(String filename) {
        List<Word> allWords = dictionary.getAllWords();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

            for (Word word : allWords) {
                writer.write(word.toString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
            return -1;
        }
        return allWords.size();
    }

    public String translateEnglishToArabic(String text) {
        String[] words = text.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z]", "");
            Word found = dictionary.searchEnglish(cleanWord);

            if (found != null) {
                result.append(found.getArabicMeaning()).append(" ");
            } else {
                result.append(word).append(" ");
            }
        }
        return result.toString().trim();
    }

    public String translateArabicToEnglish(String text) {
        String[] words = text.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            Word found = dictionary.searchArabic(word);
            if (found != null) {
                result.append(found.getWord()).append(" ");
            } else {
                result.append(word).append(" ");
            }
        }
        return result.toString().trim();
    }

    private Word getRandomWordByType(String type) {
        List<Word> allWords = dictionary.getAllWords();
        List<Word> wordsOfType = new ArrayList<>();

        for (Word word : allWords) {
            if (word.getType().equalsIgnoreCase(type)) {
                wordsOfType.add(word);
            }
        }

        if (!wordsOfType.isEmpty()) {
            int index = random.nextInt(wordsOfType.size());
            return wordsOfType.get(index);
        }
        return null;
    }

    public String generateEnglishSentence() {
        Word noun1 = getRandomWordByType("Noun");
        Word verb = getRandomWordByType("Verb");
        Word noun2 = getRandomWordByType("Noun");

        if (noun1 == null || verb == null || noun2 == null) {
            return "Not enough words to generate sentence";
        }

        return noun1.getWord() + " " + verb.getWord() + " " + noun2.getWord();
    }

    public String generateArabicSentence() {
        Word noun1 = getRandomWordByType("Noun");
        Word verb = getRandomWordByType("Verb");
        Word noun2 = getRandomWordByType("Noun");

        if (noun1 == null || verb == null || noun2 == null) {
            return "لا توجد كلمات كافية لتوليد جملة";
        }

        return verb.getArabicMeaning() + " " +
                noun1.getArabicMeaning() + " " +
                noun2.getArabicMeaning();
    }

    public List<DLinkedList.LetterCount> getLetterCounts() {
        return dictionary.getLetterCounts();
    }

    public List<DLinkedList.TypeCount> getTypeCounts() {
        return dictionary.getTypeCounts();
    }

    public List<DLinkedList.LetterHeight> getTreeHeights() {
        return dictionary.getTreeHeights();
    }

    public List<Word> getWordsByLetter(char letter) {
        return dictionary.getWordsByLetter(letter);
    }

    public List<Word> getAllWords() {
        return dictionary.getAllWords();
    }

    public int getTotalWords() {
        return dictionary.getTotalWords();
    }
}