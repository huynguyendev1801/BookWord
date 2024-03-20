import java.util.Objects;

public class BookWord {
    private String text;
    private int count;

    public BookWord(String wordText) {
        this.text = wordText.toLowerCase();
        this.count = 1;
    }

    public String getWord() {
        return text;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        count++;
    }

    @Override
    public boolean equals(Object wordToCompare) {
        if (this == wordToCompare) return true;
        if (wordToCompare == null || getClass() != wordToCompare.getClass()) return false;
        BookWord bookWord = (BookWord) wordToCompare;
        return Objects.equals(text, bookWord.text);
    }

    @Override
    public int hashCode() {
        // Custom hashing algorithm (djb2)
        // https://copyprogramming.com/howto/djb2-hash-function
        int hash = 5381; // Initial hash value
        for (int i = 0; i < text.length(); i++) {
            hash = ((hash << 5) + hash) + text.charAt(i); // hash * 33 + char
        }
        return hash;
    }
    
    @Override
    public String toString() {
        return "BookWord{" +
                "text='" + text + '\'' +
                ", count=" + count +
                '}';
    }
}
