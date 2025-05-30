package edu.grinnell.csc207.lootgenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class LootGenerator {
    /** The path to the dataset (either the small or large set). */
    private static final String DATA_SET = "data/large"; // change to small or large

    public static void main(String[] args) {
        System.out.println("This program kills monsters and generates loot!");
        try {
            // Parse data files
            List<Monster> monsters = parseMonsters(DATA_SET + "/monstats.txt");
            Map<String, List<String>> treasureClasses = parseTreasureClasses(DATA_SET + "/TreasureClassEx.txt");
            Map<String, Armor> armors = parseArmors(DATA_SET + "/armor.txt");
            List<MagicAffix> prefixes = parseAffixes(DATA_SET + "/MagicPrefix.txt");
            List<MagicAffix> suffixes = parseAffixes(DATA_SET + "/MagicSuffix.txt");

            Scanner scanner = new Scanner(System.in);
            Random rand = new Random();
            boolean fightAgain = true;

            while (fightAgain) { // while game loop active
                // First, pick a random monster
                Monster monster = pickMonster(monsters, rand);
                System.out.println("\nFighting " + monster.name + "...");
                System.out.println("You have slain " + monster.name + "!");
                System.out.println(monster.name + " dropped:\n");

                // Then fetch and resolve treasure class to base item
                String tc = fetchTreasureClass(monster);
                String itemName = generateBaseItem(tc, treasureClasses, rand);
                Armor baseArmor = armors.get(itemName);
                if (baseArmor == null) {
                    System.out.println("Item not found: " + itemName);
                    continue;
                }

                // Generate base stats
                int defense = generateBaseStats(baseArmor, rand);
                String baseStatString = "Defense: " + defense;

                // Generate prefixes
                String prefixName = "";
                String prefixStat = "";
                if (rand.nextBoolean()) {
                    MagicAffix prefix = generateAffix(prefixes, rand);
                    prefixName = prefix.name;
                    int value = prefix.minValue + rand.nextInt(prefix.maxValue - prefix.minValue + 1);
                    prefixStat = value + " " + prefix.property;
                }

                // Generate suffixes
                String suffixName = "";
                String suffixStat = "";
                if (rand.nextBoolean()) {
                    MagicAffix suffix = generateAffix(suffixes, rand);
                    suffixName = suffix.name;
                    int value = suffix.minValue + rand.nextInt(suffix.maxValue - suffix.minValue + 1);
                    suffixStat = value + " " + suffix.property;
                }

                // Build full item name
                String fullName = (prefixName.isEmpty() ? "" : prefixName + " ") +
                        baseArmor.name +
                        (suffixName.isEmpty() ? "" : " " + suffixName);

                // Print out the item
                System.out.println(fullName);
                System.out.println(baseStatString);
                if (!prefixStat.isEmpty())
                    System.out.println(prefixStat);
                if (!suffixStat.isEmpty())
                    System.out.println(suffixStat);

                // Send output for game loop
                System.out.print("\nFight again [y/n]? :");
                String input = scanner.nextLine().trim().toLowerCase();
                while (!input.equals("y") && !input.equals("n")) {
                    System.out.print("Fight again [y/n]? :");
                    input = scanner.nextLine().trim().toLowerCase();
                }
                fightAgain = input.equals("y");
                if (input.equals("n")) { // If player says no to new game...
                    System.out.println("Thanks for playing!"); // print exit message
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading data files: " + e.getMessage());
        }
    }

    // Helper Methods

    /**
     * Randomly selects a monster from the provided list.
     *
     * @param monsters the list of monsters to choose from
     * @param rand     random number generator used for selection
     * @return a randomly selected Monster object from the list
     */
    private static Monster pickMonster(List<Monster> monsters, Random rand) {
        return monsters.get(rand.nextInt(monsters.size()));
    }

    /**
     * Retrieves the treasure class associated with a given monster.
     *
     * @param monster the Monster object whose treasure class is to be fetched
     * @return the treasure class identifier as a String
     */
    private static String fetchTreasureClass(Monster monster) {
        return monster.treasureClass;
    }

    /**
     * Recursively resolves a treasure class to a base item by selecting a random
     * drop from the treasure class.
     * The process continues until a base item is found.
     *
     * @param tc              the treasure class identifier to resolve
     * @param treasureClasses map containing treasure class identifiers to lists of
     *                        possible drops
     * @param rand            random number generator used for selecting drops
     * @return the resolved base item identifier as a String
     */
    private static String generateBaseItem(String tc, Map<String, List<String>> treasureClasses, Random rand) {
        while (treasureClasses.containsKey(tc)) {
            List<String> drops = treasureClasses.get(tc);
            tc = drops.get(rand.nextInt(drops.size()));
        }
        return tc; // Base item when no longer a TC
    }

    /**
     * Generates a random defense value for the specified armor within its defined
     * range.
     *
     * @param armor Armor object for which to generate a defense value
     * @param rand  random number generator used to calculate the defense value
     * @return a random defense value between armor.minDefense and armor.maxDefense
     *         (inclusive)
     */
    private static int generateBaseStats(Armor armor, Random rand) {
        return armor.minDefense + rand.nextInt(armor.maxDefense - armor.minDefense + 1);
    }

    /**
     * Selects a random magical affix from the provided list.
     *
     * @param affixes the list of MagicAffix objects to choose from
     * @param rand    random number generator used for selection
     * @return a randomly selected MagicAffix object
     */
    private static MagicAffix generateAffix(List<MagicAffix> affixes, Random rand) {
        return affixes.get(rand.nextInt(affixes.size()));
    }

    // Parsing Methods

    /**
     * Parses the monstats.txt file into a list of Monster objects.
     *
     * @param filePath the path to the monstats.txt file
     * @return a list of Monster objects parsed from the file
     * @throws IOException if an error occurs while reading the file
     */
    private static List<Monster> parseMonsters(String filePath) throws IOException {
        List<Monster> monsters = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 4) {
                    monsters.add(new Monster(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]));
                }
            }
        }
        return monsters;
    }

    /**
     * Parses the TreasureClassEx.txt file into a map of treasure class identifiers
     * to their corresponding drop lists.
     *
     * @param filePath the path to the TreasureClassEx.txt file
     * @return a map where keys are treasure class identifiers and values are lists
     *         of possible drops
     * @throws IOException if an error occurs while reading the file
     */
    private static Map<String, List<String>> parseTreasureClasses(String filePath) throws IOException {
        Map<String, List<String>> treasureClasses = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 1) {
                    List<String> drops = new ArrayList<>();
                    for (int i = 1; i < parts.length; i++) {
                        drops.add(parts[i]);
                    }
                    treasureClasses.put(parts[0], drops);
                }
            }
        }
        return treasureClasses;
    }

    /**
     * Parses the armor.txt file into a map of armor names to Armor objects.
     *
     * @param filePath the path to the armor.txt file
     * @return a map where keys are armor names and values are Armor objects
     * @throws IOException if an error occurs while reading the file
     */
    private static Map<String, Armor> parseArmors(String filePath) throws IOException {
        Map<String, Armor> armors = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 3) {
                    armors.put(parts[0], new Armor(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                }
            }
        }
        return armors;
    }

    /**
     * Parses either MagicPrefix.txt or MagicSuffix.txt into a list of MagicAffix
     * objects.
     *
     * @param filePath the path to the MagicPrefix.txt or MagicSuffix.txt file
     * @return a list of MagicAffix objects parsed from the file
     * @throws IOException if an error occurs while reading the file
     */
    private static List<MagicAffix> parseAffixes(String filePath) throws IOException {
        List<MagicAffix> affixes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 4) {
                    affixes.add(
                            new MagicAffix(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
                }
            }
        }
        return affixes;
    }
}

// The Supporting Classes

class Monster {
    String name, treasureClass, type;
    int level;

    Monster(String name, String type, int level, String treasureClass) {
        this.name = name;
        this.type = type;
        this.level = level;
        this.treasureClass = treasureClass;
    }
}

class Armor {
    String name;
    int minDefense, maxDefense;

    Armor(String name, int minDefense, int maxDefense) {
        this.name = name;
        this.minDefense = minDefense;
        this.maxDefense = maxDefense;
    }
}

class MagicAffix {
    String name, property;
    int minValue, maxValue;

    MagicAffix(String name, String property, int minValue, int maxValue) {
        this.name = name;
        this.property = property;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}