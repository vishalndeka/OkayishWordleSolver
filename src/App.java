import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) throws Exception {
        // set game params here
        int turns = 6;
        int wordLength = 5;

        // storing all 5-letter words in word-list
        List<String> wordList;
        List<String> allWords;
        try (Stream<String> lines = Files.lines(Paths.get("src\\Collins Scrabble Words (2019).txt"))) {
            allWords = lines.filter(s->s.length()==wordLength).collect(Collectors.toList());
	    }

        try (Stream<String> lines = Files.lines(Paths.get("src\\Collins Scrabble Words (2019).txt"))) {
		    wordList = lines.filter(s->s.length()==wordLength).collect(Collectors.toList());
	    }
        
        Scanner sc = new Scanner(System.in);
        int[] flags = new int[wordLength];
        Arrays.fill(flags, -1);
        boolean completeCheck = false;
        String w = "";
        HashSet<Character> foundLetters = new HashSet<>();
        while(turns-->0) {
            String word = sc.next().toUpperCase();
            w = word;
            if(word.length()!=wordLength || !allWords.contains(word)) {
                System.out.println("Invalid input (or) Word doesn't exist!");
                turns++;
                continue;
            }

            completeCheck = true;
            System.out.println("Enter info from wordle: (-1 for grey, 0 for yellow, 1 for green)");
            for(int i=0; i<5; i++) {
                flags[i] = sc.nextInt();
                if(flags[i]!=1)
                    completeCheck = false;
            }

            if(completeCheck)
                break;

            for(int i=0; i<word.length(); i++) {
                final int j = i;
                if(flags[i]==-1) {
                    wordList = wordList.stream().filter(s->!s.contains(Character.toString(word.charAt(j)))
                    || foundLetters.contains(word.charAt(j))).collect(Collectors.toList());
                } else if(flags[i]==0) {
                    wordList = wordList.stream().filter(s->s.contains(Character.toString(word.charAt(j))) 
                    && s.charAt(j)!=word.charAt(j)).collect(Collectors.toList());
                    foundLetters.add(word.charAt(j));
                } else {
                    wordList = wordList.stream().filter(s->s.charAt(j)==word.charAt(j)).collect(Collectors.toList());
                    foundLetters.add(word.charAt(j));
                }
            }

            Collections.sort(wordList, new WordComparator(wordList));

            FileWriter fileWriter = new FileWriter("suggestions.txt");
            for (String str : wordList) {
                fileWriter.write(str + System.lineSeparator());
            }
            fileWriter.close();
            System.out.println("------------------------------");
        }

        sc.close();
        if(!completeCheck && turns==0) {
            System.out.println("Bruh Momento");
            return;
        }
            
        System.out.println("The word is: " + w + " and you took " + (6-turns) + " turns");

        
    }
}

class WordComparator implements Comparator<String> {
    private List<String> wordList;
    public WordComparator(List<String> wordList) {
        this.wordList = wordList;
    }
    @Override
    public int compare(String o1, String o2) {
        return info(o1)-info(o2);
    }
    
    public int info(String word) {
        List<String> w = new ArrayList<>(this.wordList);
        for(int i=0; i<word.length(); i++) {
            final int j = i;
            w = w.stream().filter(s->!s.contains(Character.toString(word.charAt(j)))).collect(Collectors.toList());
        }
        return w.size();
    }
}
