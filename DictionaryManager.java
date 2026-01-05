import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DictionaryManager {
    private DLinkedList dictionary;

    public DictionaryManager() {
        dictionary = new DLinkedList();
    }

    public boolean addWord(String word, String englishMeaning, String arabicMeaning,
                           String example, String type) {
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
        Word updatedWord = new Word(englishWord, englishMeaning, arabicMeaning, example, type);
        return dictionary.updateWord(englishWord, updatedWord);
    }

    public boolean deleteWord(String englishWord) {
        return dictionary.deleteWord(englishWord);
    }

    public int loadFromFile(String filePath) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                Word word = Word.fromString(line);
                if (word != null && dictionary.addWord(word)) {
                    count++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading file: " + e.getMessage());
        }
        return count;
    }

    public int saveToFile(String filePath) {
        List<Word> allWords = dictionary.getAllWords();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            for (Word word : allWords) {
                writer.write(word.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            return -1;
        }
        return allWords.size();
    }

    public String translateEnglishToArabic(String text) {
        String[] words = text.split("\\s+");
        StringBuilder translation = new StringBuilder();

        for (String word : words) {
            Word dictWord = dictionary.searchEnglish(word.replaceAll("[^a-zA-Z]", ""));
            if (dictWord != null) {
                translation.append(dictWord.getArabicMeaning()).append(" ");
            } else {
                translation.append(word).append(" ");
            }
        }

        return translation.toString().trim();
    }

    public String translateArabicToEnglish(String text) {
        String[] words = text.split("\\s+");
        StringBuilder translation = new StringBuilder();

        for (String word : words) {
            Word dictWord = dictionary.searchArabic(word);
            if (dictWord != null) {
                translation.append(dictWord.getWord()).append(" ");
            } else {
                translation.append(word).append(" ");
            }
        }

        return translation.toString().trim();
    }

    public String generateEnglishSentence() {
        Word subject = dictionary.getRandomWordByType("Noun");
        Word verb = dictionary.getRandomWordByType("Verb");
        Word object = dictionary.getRandomWordByType("Noun");

        if (subject == null || verb == null || object == null) {
            return "Not enough words in dictionary to generate sentence.";
        }

        return subject.getWord() + " " + verb.getWord() + " " + object.getWord();
    }

    public String generateArabicSentence() {
        Word subject = dictionary.getRandomWordByType("Noun");
        Word verb = dictionary.getRandomWordByType("Verb");
        Word object = dictionary.getRandomWordByType("Noun");

        if (subject == null || verb == null || object == null) {
            return "لا توجد كلمات كافية في القاموس لتوليد جملة";
        }

        return verb.getArabicMeaning() + " " + subject.getArabicMeaning() + " " + object.getArabicMeaning();
    }

    public List<DLinkedList.LetterCount> getWordCountByLetter() {
        return dictionary.getWordCountByLetter();
    }

    public List<DLinkedList.TypeCount> getWordCountByType() {
        return dictionary.getWordCountByType();
    }

    public List<Word> getWordsByLetter(char letter) {
        return dictionary.getWordsByLetter(letter);
    }

    public List<Word> getAllWords() {
        return dictionary.getAllWords();
    }

    public List<DLinkedList.LetterHeight> getTreeHeights() {
        return dictionary.getTreeHeights();
    }

    public int getTotalWords() {
        return dictionary.getTotalWords();
    }

    public DLinkedList getDictionary() {
        return dictionary;
    }
}