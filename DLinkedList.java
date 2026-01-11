import java.util.ArrayList;
import java.util.List;

public class DLinkedList {
    private DNode head;
    private int totalWords;

    public DLinkedList() {
        head = new DNode(' ');
        head.setNext(head);
        head.setPrev(head);
        totalWords = 0;

        for (char c = 'A'; c <= 'Z'; c++) {
            DNode newNode = new DNode(c);
            addNode(newNode);
        }
    }

    private void addNode(DNode newNode) {
        DNode last = head.getPrev();
        last.setNext(newNode);
        newNode.setPrev(last);
        newNode.setNext(head);
        head.setPrev(newNode);
    }

    public DNode getNodeByLetter(char letter) {
        DNode current = head.getNext();
        while (current != head) {
            if (current.getLetter() == Character.toUpperCase(letter)) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }

    // Add word - FIXED
    public boolean addWord(Word word) {
        if (word == null || word.getWord().isEmpty()) {
            return false;
        }

        char firstLetter = Character.toUpperCase(word.getWord().charAt(0));
        if (firstLetter < 'A' || firstLetter > 'Z') {
            return false;
        }

        // Check if word already exists - FIXED
        Word existing = searchEnglish(word.getWord());
        if (existing != null) {
            return false;
        }

        DNode node = getNodeByLetter(firstLetter);
        if (node != null) {
            node.getTree().insert(word);
            totalWords++;
            return true;
        }

        return false;
    }

    // Search by English
    public Word searchEnglish(String englishWord) {
        if (englishWord == null || englishWord.isEmpty()) {
            return null;
        }

        char firstLetter = Character.toUpperCase(englishWord.charAt(0));
        DNode node = getNodeByLetter(firstLetter);
        if (node != null) {

            Word tempWord = new Word(englishWord, "", "", "", "");
            Word found = node.getTree().search(tempWord);
            return found;
        }
        return null;
    }

    // Search by Arabic
    public Word searchArabic(String arabicMeaning) {
        DNode current = head.getNext();
        while (current != head) {
            Word result = current.getTree().searchByArabic(arabicMeaning);
            if (result != null) {
                return result;
            }
            current = current.getNext();
        }
        return null;
    }

    // Update word
    public boolean updateWord(String englishWord, Word newWord) {
        // First delete the old word
        if (deleteWord(englishWord)) {
            // Then add the new word
            return addWord(newWord);
        }
        return false;
    }

    // Delete word
    public boolean deleteWord(String englishWord) {
        if (englishWord == null || englishWord.isEmpty()) {
            return false;
        }

        char firstLetter = Character.toUpperCase(englishWord.charAt(0));
        DNode node = getNodeByLetter(firstLetter);
        if (node != null) {

            Word tempWord = new Word(englishWord, "", "", "", "");
            Word wordToDelete = node.getTree().search(tempWord);

            if (wordToDelete != null) {
                node.getTree().delete(wordToDelete);
                totalWords--;
                return true;
            }
        }
        return false;
    }

    public List<Word> getAllWords() {
        List<Word> allWords = new ArrayList<>();
        DNode current = head.getNext();

        while (current != head) {
            allWords.addAll(current.getTree().getInOrder());
            current = current.getNext();
        }
        return allWords;
    }

    public List<Word> getWordsByLetter(char letter) {
        DNode node = getNodeByLetter(letter);
        if (node != null) {
            return node.getTree().getInOrder();
        }
        return new ArrayList<>();
    }

    public int getTotalWords() {
        return totalWords;
    }

    public List<LetterCount> getLetterCounts() {
        List<LetterCount> counts = new ArrayList<>();
        DNode current = head.getNext();

        while (current != head) {
            counts.add(new LetterCount(current.getLetter(),
                    current.getTree().getSize()));
            current = current.getNext();
        }
        return counts;
    }

    public List<TypeCount> getTypeCounts() {
        List<TypeCount> counts = new ArrayList<>();
        List<Word> allWords = getAllWords();

        for (Word word : allWords) {
            String type = word.getType();
            boolean found = false;

            for (TypeCount tc : counts) {
                if (tc.getType().equals(type)) {
                    tc.count++;
                    found = true;
                    break;
                }
            }

            if (!found) {
                counts.add(new TypeCount(type, 1));
            }
        }
        return counts;
    }

    public List<LetterHeight> getTreeHeights() {
        List<LetterHeight> heights = new ArrayList<>();
        DNode current = head.getNext();

        while (current != head) {
            heights.add(new LetterHeight(current.getLetter(),
                    current.getTree().getTreeHeight()));
            current = current.getNext();
        }
        return heights;
    }

    public static class LetterCount {
        public char letter;
        public int count;

        public LetterCount(char letter, int count) {
            this.letter = letter;
            this.count = count;
        }

        public char getLetter() { return letter; }
        public int getCount() { return count; }
    }

    public static class TypeCount {
        public String type;
        public int count;

        public TypeCount(String type, int count) {
            this.type = type;
            this.count = count;
        }

        public String getType() { return type; }
        public int getCount() { return count; }
    }

    public static class LetterHeight {
        public char letter;
        public int height;

        public LetterHeight(char letter, int height) {
            this.letter = letter;
            this.height = height;
        }

        public char getLetter() { return letter; }
        public int getHeight() { return height; }
    }
}