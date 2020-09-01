import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static java.lang.Math.*;

public class Main {
    public static void main(String[] args) {

        try {
            final String text = Files.readString(Path.of(args[0]));
            final int characters = text.replaceAll("\\s", "").length();
            final int words = text.split(" ").length;
            final int sentences = text.split("[!?.]+").length;
            final int syllables = calculateSyllables(text);
            final int polySyllables = calculatePolySyllables(text);

            // Output above variables
            System.out.printf("The text is:%n%s%n"
                            + "Words: %d%nSentences: %d%nCharacters: %d%nSyllables: %d%nPolysyllables: %d%n",
                    text, words, sentences, characters, syllables, polySyllables);

            String input;
            try (Scanner scanner = new Scanner(System.in))
            {
                // Check that correct format is entered
                while (true)
                {
                    System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
                    input = scanner.next();
                    System.out.print("\n");
                    if (input.matches("(SMOG|all|ARI|CL|FK)"))
                    {
                        break;
                    }
                    System.out.println("Please enter anagrams specified!");
                }
            }

            if (input.equals("SMOG"))
            {
                processSMOGIndex(polySyllables, sentences);
            }
            else if (input.equals("ARI"))
            {
                processAutomatedIndex(characters, words, sentences);
            }
            else if (input.equals("CL"))
            {
                processColemanIndex(words, characters, sentences);
            }
            else if (input.equals("FK"))
            {
                processKincaid(words, syllables, sentences);
            }
            else
            {
                processAutomatedIndex(characters, words, sentences);
                processKincaid(words, syllables, sentences);
                processSMOGIndex(polySyllables, sentences);
                processColemanIndex(words, characters, sentences);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static void displayScores(String name, double score, String age)
    {
        System.out.printf("%s: %5.2f (about %s year olds).\n",
                name, score, age);
    }
    static void processColemanIndex(int words, int characters, int sentences)
    {
        double avgCharacter_L = avgCharactersPer100(characters, words);
        double avgSentence_S = avgSentencesPer100(sentences, words);

        String name = "Coleman–Liau index";
        double score = 0.0588 * avgCharacter_L - 0.296 * avgSentence_S - 15.8;
        String age = calculateAge(score);
        displayScores(name, score, age);
    }
    static void processSMOGIndex(int polysyllables, int sentences)
    {
        String name = "Simple Measure of Gobbledygook";
        double score = 1.043 * Math.sqrt(polysyllables * 30 / sentences) + 3.1291;
        String age = calculateAge(score);
        displayScores(name, score, age);
    }
    static void processKincaid(int words, int syllables, int sentences)
    {
        String name = "Flesch–Kincaid readability tests";
        double score = 0.39 * words / sentences + 11.8 * syllables / words - 15.59;
        String age = calculateAge(score);
        displayScores(name, score, age);
    }

    static void processAutomatedIndex(int characters, int words, int sentences)
    {
        String name = "Automated Readability Index";
        double score = 4.71 * characters / words + 0.5 * words / sentences - 21.43;
        String age = calculateAge(score);
        displayScores(name, score, age);
    }
    static String calculateAge(double score) {
        String[] ageGroups = new String[]{"6", "7", "9", "10", "11", "12",
                "13", "14", "15", "16", "17", "18", "24", "24+"};
        int level = (int) (Math.floor(score) - 1);
        return ageGroups[level];
    }


    static int calculateSyllables(String text)
    {
        int syllables = 0;
        // Make text clean
        text = cleanText(text);
        String[] wordArray = text.split(" ");
        // WORDS
        for (String s : wordArray)
        {
            int vowels = getVowels(s);
            // If word has no vowels in counts as one syllable
            if (vowels == 0) syllables++;
            else
            {
                syllables += vowels;
            }
        }
        return syllables;
    }

    static int calculatePolySyllables(String text)
    {
        int polySyllables = 0;
        // Make text clean
        text = cleanText(text);
        String[] wordArray = text.split(" ");
        // WORDS
        for (String s : wordArray)
        {
            int vowels = getVowels(s);
            // If word has no vowels in counts as one syllable
            if (vowels > 2) polySyllables++;
        }
        return polySyllables;
    }

    static double avgCharactersPer100(double characters, double words)
    {
        double avgWordsPercent = 100 / words;
        return characters * avgWordsPercent;
    }

    static double avgSentencesPer100(double sentences, double words)
    {
        double avgWordPercent = 100 / words;
        return sentences * avgWordPercent;
    }

    // get vowels following the syllables rules
    // Count the number of vowels in the word.
    // Do not count double-vowels (for example, "rain" has 2 vowels but only 1 syllable).
    // If the last letter in the word is 'e' do not count it as a vowel (for example, "side" has 1 syllable).
    // If at the end it turns out that the word contains 0 vowels, then consider this word as a 1-syllable one.
    static int getVowels(String s)
    {
        int vowels = 0;

        // CHARACTERS
        for (int i = 0; i < s.length(); i++)
        {
            // if character is vowel
            if (Character.toString(s.charAt(i)).matches("[aeiouy]"))
            {
                // Double vowels count as one
                if (i == 0 || !(Character.toString(s.charAt(i - 1)).matches("[aeiouy]")))
                {
                    // If the last letter in the word is e do not count
                    // if not last letter and not e
                    if (!(i == s.length() - 1 && Character.toString(s.charAt(i)).matches("[e]")))
                    {
                        vowels++;
                    }
                }
            }
        }
        return vowels;
    }
    // Make text cleaner so there's less worry about bugs
    static String cleanText(String text)
    {
        return text.replaceAll("[!?.,\\'\\\"]+", "");
    }
}
