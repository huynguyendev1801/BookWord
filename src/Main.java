import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;
/*
The ArrayList.contains method has a linear time complexity, making it less efficient for large datasets.
Collections.binarySearch method, though more efficient due to binary search, still requires sorting the dictionary, which adds to the time complexity.
The SimpleHashSet.contains method, leveraging the constant-time lookup of hash sets, proved to be the most efficient, especially for larger datasets.

ArrayList.contains: O(n)
Collections.binarySearch: O(n log n)
SimpleHashSet.contains: O(1)

Therefore, for efficient misspelled word detection, using a hash set like SimpleHashSet is recommended.

 */
public class Main {
    public static void main(String[] args) {
        // Read the novel War and Peace
        ArrayList<String> novelWords = readNovel("src/WarAndPeace.txt");

        // Read the dictionary
        Set<String> dictionary = readDictionary("src/US.txt");

        // Create an ArrayList of BookWord objects for the novel words
        ArrayList<BookWord> novelBookWords = createBookWords(novelWords);

        // Create a SimpleHashSet of BookWord objects for the dictionary
        SimpleHashSet<BookWord> dictionarySet = createDictionarySet(dictionary);





        // Count the total number of words in the novel
        int totalWords = novelWords.size();

        // Count the total number of unique words in the novel
        int uniqueWords = countUniqueWords(novelWords);

        // Print the total number of words in the novel
        System.out.println("Total number of words in the novel: " + totalWords);

        // Print the total number of unique words in the novel
        System.out.println("Total number of unique words in the novel: " + uniqueWords);

        // Calculate and print the 15 most frequent words and their counts
        printMostFrequentWords(novelBookWords, 15);

        // Print words that occur exactly 41 times in the novel
        printWordsWithOccurrence(novelBookWords, 41);

        // Find and print the number of misspelled words in the novel using different methods
        int misspelledWords1 = countMisspelledWordsUsingArrayList(novelBookWords, dictionary);
        int misspelledWords2 = countMisspelledWordsUsingBinarySearch(novelBookWords, dictionary);
        int misspelledWords3 = countMisspelledWordsUsingHashSet(novelBookWords, dictionarySet);

        System.out.println("Number of misspelled words (Method 1): " + misspelledWords1);
        System.out.println("Number of misspelled words (Method 2): " + misspelledWords2);
        System.out.println("Number of misspelled words (Method 3): " + misspelledWords3);

        List<Pair> pairs = findWarPeacePairs(novelWords);

        // Calculate and print the distances and statistics
        int totalDistance = 0;
        for (Pair pair : pairs) {
            totalDistance += pair.getDistance();
            System.out.println(pair.toString());
        }
        double averageDistance = pairs.isEmpty() ? 0 : (double) totalDistance / pairs.size();
        System.out.println("The total sum of distances between the matched pairs of war and peace = " + totalDistance);
        System.out.println("The average distance between the matched pairs of war and peace = " + averageDistance);

    }

    // Method to read the novel from a file and return a list of words
    private static ArrayList<String> readNovel(String fileName) {
        ArrayList<String> words = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(fileName));
            String regEx = "\\.|\\?|\\!|\\s|\"|\\(|\\)|\\,|\\_|\\-|\\:|\\;|\\n";

            scanner.useDelimiter(regEx);
            while (scanner.hasNext()) {
                String word = scanner.next().toLowerCase();
                // Check if the word is not empty before adding it to the list
                if (!word.isEmpty()) {
                    words.add(word);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return words;
    }

    // Method to read the dictionary from a file and return a set of words
    private static Set<String> readDictionary(String fileName) {
        Set<String> dictionary = new HashSet<>();
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().toLowerCase();
                dictionary.add(word);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dictionary;
    }

    // Method to create BookWord objects for the novel words
    private static ArrayList<BookWord> createBookWords(ArrayList<String> words) {
        ArrayList<BookWord> bookWords = new ArrayList<>();
        for (String word : words) {
            // Check if the word is not empty before creating a BookWord object
            if (!word.isEmpty()) {
                BookWord bookWord = new BookWord(word);
                if (bookWords.contains(bookWord)) {
                    int index = bookWords.indexOf(bookWord);
                    bookWords.get(index).incrementCount();
                } else {
                    bookWords.add(bookWord);
                }
            }
        }
        return bookWords;
    }
    // Method to create a SimpleHashSet of BookWord objects for the dictionary
    private static SimpleHashSet<BookWord> createDictionarySet(Set<String> dictionary) {
        SimpleHashSet<BookWord> dictionarySet = new SimpleHashSet<>();
        for (String word : dictionary) {
            dictionarySet.insert(new BookWord(word));
        }
        return dictionarySet;
    }

    // Method to count the total number of unique words
    private static int countUniqueWords(ArrayList<String> words) {
        return new HashSet<>(words).size();
    }

    // Method to print the 15 most frequent words
    private static void printMostFrequentWords(ArrayList<BookWord> bookWords, int num) {
        Collections.sort(bookWords, (a, b) -> {
            if (a.getCount() != b.getCount()) {
                return Integer.compare(b.getCount(), a.getCount());
            } else {
                return a.getWord().compareTo(b.getWord());
            }
        });

        System.out.println("Top " + num + " most frequent words:");
        for (int i = 0; i < Math.min(num, bookWords.size()); i++) {
            System.out.println(bookWords.get(i));
        }
    }

    // Method to print words that occur exactly 'count' times
    private static void printWordsWithOccurrence(ArrayList<BookWord> bookWords, int count) {
        ArrayList<String> words = new ArrayList<>();
        for (BookWord bookWord : bookWords) {
            if (bookWord.getCount() == count) {
                words.add(bookWord.getWord());
            }
        }
        Collections.sort(words);
        System.out.println("Words occurring exactly " + count + " times:");
        for (String word : words) {
            System.out.println(word);
        }
    }

    // Method to count misspelled words using ArrayList.contains()
    private static int countMisspelledWordsUsingArrayList(ArrayList<BookWord> novelBookWords, Set<String> dictionary) {
        int misspelledCount = 0;
        for (BookWord bookWord : novelBookWords) {
            if (!dictionary.contains(bookWord.getWord())) {
                misspelledCount++;
            }
        }
        return misspelledCount;
    }

    // Method to count misspelled words using Collections.binarySearch()
    private static int countMisspelledWordsUsingBinarySearch(ArrayList<BookWord> novelBookWords, Set<String> dictionary) {
        int misspelledCount = 0;
        ArrayList<String> dictionaryList = new ArrayList<>(dictionary);
        Collections.sort(dictionaryList);
        for (BookWord bookWord : novelBookWords) {
            if (Collections.binarySearch(dictionaryList, bookWord.getWord()) < 0) {
                misspelledCount++;
            }
        }
        return misspelledCount;
    }

    // Method to count misspelled words using SimpleHashSet.contains()
    private static int countMisspelledWordsUsingHashSet(ArrayList<BookWord> novelBookWords, SimpleHashSet<BookWord> dictionarySet) {
        int misspelledCount = 0;
        for (BookWord bookWord : novelBookWords) {
            if (!dictionarySet.contains(new BookWord(bookWord.getWord()))) {
                misspelledCount++;
            }
        }
        return misspelledCount;
    }
    // Method to find and match occurrences of "war" and "peace"
    private static ArrayList<Pair> findWarPeacePairs(ArrayList<String> words) {
        ArrayList<Pair> pairs = new ArrayList<>();
        HashSet<Integer> usedWarIndices = new HashSet<>();

        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).equals("peace")) {
                int closestWarIndex = findClosestWarIndex(words, i, usedWarIndices);
                if (closestWarIndex != -1) {
                    Pair pair = new Pair(closestWarIndex, i);
                    pairs.add(pair);
                    usedWarIndices.add(closestWarIndex);
                }
            }
        }
        return pairs;
    }

    // Method to find the index of the closest occurrence of "war" to a given index
    private static int findClosestWarIndex(ArrayList<String> words, int index, HashSet<Integer> usedWarIndices) {
        int closestWarIndex = -1;
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).equals("war") && !usedWarIndices.contains(i)) {
                int distance = Math.abs(i - index);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestWarIndex = i;
                }
            }
        }
        return closestWarIndex;
    }


    // Pair class to represent a pair of war and peace occurrences
    static class Pair {
        private final int warIndex;
        private final int peaceIndex;

        public Pair(int warIndex, int peaceIndex) {
            this.warIndex = warIndex;
            this.peaceIndex = peaceIndex;
        }

        public int getDistance() {
            return Math.abs(warIndex - peaceIndex);
        }

        @Override
        public String toString() {
            return "shortest distance between war at pos(" + (warIndex + 1) + ") and peace(" + (peaceIndex + 1) + ") = " + getDistance();
        }
    }

}
