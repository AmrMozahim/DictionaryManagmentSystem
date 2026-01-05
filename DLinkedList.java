import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
            insertAtEnd(newNode);
        }
    }

    private void insertAtEnd(DNode newNode) {
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

    public boolean addWord(Word word) {
        if (word == null || word.getWord().isEmpty()) {
            return false;
        }

        char firstLetter = Character.toUpperCase(word.getWord().charAt(0));
        if (firstLetter < 'A' || firstLetter > 'Z') {
            return false;
        }

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

    public Word searchEnglish(String englishWord) {
        if (englishWord == null || englishWord.isEmpty()) {
            return null;
        }

        char firstLetter = Character.toUpperCase(englishWord.charAt(0));
        DNode node = getNodeByLetter(firstLetter);
        if (node != null) {
            Word searchWord = new Word(englishWord, "", "", "", "");
            return node.getTree().search(searchWord);
        }

        return null;
    }

    // البحث بالعربي بدون Map
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

    public boolean updateWord(String englishWord, Word newWord) {
        if (deleteWord(englishWord)) {
            return addWord(newWord);
        }
        return false;
    }

    public boolean deleteWord(String englishWord) {
        if (englishWord == null || englishWord.isEmpty()) {
            return false;
        }

        char firstLetter = Character.toUpperCase(englishWord.charAt(0));
        DNode node = getNodeByLetter(firstLetter);
        if (node != null) {
            Word searchWord = new Word(englishWord, "", "", "", "");
            Word wordToDelete = node.getTree().search(searchWord);

            if (wordToDelete != null) {
                node.getTree().delete(wordToDelete);
                totalWords--;
                return true;
            }
        }

        return false;
    }

    public List<Word> getWordsByLetter(char letter) {
        DNode node = getNodeByLetter(letter);
        if (node != null) {
            return node.getTree().inOrderTraversal();
        }
        return new ArrayList<>();
    }

    public List<Word> getAllWords() {
        List<Word> allWords = new ArrayList<>();
        DNode current = head.getNext();

        while (current != head) {
            allWords.addAll(current.getTree().inOrderTraversal());
            current = current.getNext();
        }

        return allWords;
    }

    // الحصول على عدد الكلمات لكل حرف بدون Map
    public List<LetterCount> getWordCountByLetter() {
        List<LetterCount> counts = new ArrayList<>();
        DNode current = head.getNext();

        while (current != head) {
            counts.add(new LetterCount(current.getLetter(), current.getTree().getWordCount()));
            current = current.getNext();
        }

        return counts;
    }

    // الحصول على عدد الكلمات لكل نوع بدون Map
    public List<TypeCount> getWordCountByType() {
        List<TypeCount> counts = new ArrayList<>();
        List<Word> allWords = getAllWords();

        for (Word word : allWords) {
            String type = word.getType();
            boolean found = false;

            for (TypeCount tc : counts) {
                if (tc.getType().equals(type)) {
                    tc.incrementCount();
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

    // الحصول على ارتفاع الأشجار بدون Map
    public List<LetterHeight> getTreeHeights() {
        List<LetterHeight> heights = new ArrayList<>();
        DNode current = head.getNext();

        while (current != head) {
            heights.add(new LetterHeight(current.getLetter(), current.getTree().getTreeHeight()));
            current = current.getNext();
        }

        return heights;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public Word getRandomWordByType(String type) {
        List<Word> wordsOfType = new ArrayList<>();
        DNode current = head.getNext();

        while (current != head) {
            wordsOfType.addAll(current.getTree().getWordsByType(type));
            current = current.getNext();
        }

        if (!wordsOfType.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(wordsOfType.size());
            return wordsOfType.get(randomIndex);
        }

        return null;
    }

    // كلاسات مساعدة بدلاً من Map
    public static class LetterCount {
        private char letter;
        private int count;

        public LetterCount(char letter, int count) {
            this.letter = letter;
            this.count = count;
        }

        public char getLetter() { return letter; }
        public int getCount() { return count; }
    }

    public static class TypeCount {
        private String type;
        private int count;

        public TypeCount(String type, int count) {
            this.type = type;
            this.count = count;
        }

        public String getType() { return type; }
        public int getCount() { return count; }
        public void incrementCount() { this.count++; }
    }

    public static class LetterHeight {
        private char letter;
        private int height;

        public LetterHeight(char letter, int height) {
            this.letter = letter;
            this.height = height;
        }

        public char getLetter() { return letter; }
        public int getHeight() { return height; }
    }
}