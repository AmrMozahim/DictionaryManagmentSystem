public class Word implements Comparable<Word> {
    private String word;
    private String englishMeaning;
    private String arabicMeaning;
    private String example;
    private String type;

    public Word(String word, String englishMeaning, String arabicMeaning,
                String example, String type) {
        this.word = word;
        this.englishMeaning = englishMeaning;
        this.arabicMeaning = arabicMeaning;
        this.example = example;
        this.type = type;
    }

    @Override
    public int compareTo(Word other) {
        return this.word.compareToIgnoreCase(other.word);
    }

    @Override
    public String toString() {
        return word + ";" + englishMeaning + ";" + arabicMeaning +
                ";" + example + ";" + type;
    }

    public static Word fromString(String line) {
        String[] parts = line.split(";");
        if (parts.length == 5) {
            return new Word(parts[0], parts[1], parts[2], parts[3], parts[4]);
        }
        return null;
    }

    public String getWord() { return word; }
    public String getEnglishMeaning() { return englishMeaning; }
    public String getArabicMeaning() { return arabicMeaning; }
    public String getExample() { return example; }
    public String getType() { return type; }

    public void setWord(String word) { this.word = word; }
    public void setEnglishMeaning(String englishMeaning) { this.englishMeaning = englishMeaning; }
    public void setArabicMeaning(String arabicMeaning) { this.arabicMeaning = arabicMeaning; }
    public void setExample(String example) { this.example = example; }
    public void setType(String type) { this.type = type; }
}