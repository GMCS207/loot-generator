package edu.grinnell.csc207.lootgenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the LootGenerator class, testing helper methods and main.
 */
public class Tests {

    /**
     * Tests pickMonster to ensure it selects a monster from the list.
     */
    @Test
    public void testPickMonster() throws Exception {
        List<Monster> monsters = new ArrayList<>();
        monsters.add(new Monster("Goblin", "Normal", 1, "TC1"));
        monsters.add(new Monster("Orc", "Normal", 2, "TC2"));
        
        Random rand = new Random(42);
        Monster selected = LootGenerator_pickMonster(monsters, rand);
        assertEquals(true, monsters.contains(selected), 
            "Selected monster should be in the input list");
    }

    /**
     * Tests fetchTreasureClass to ensure it returns the monster's treasure class.
     */
    @Test
    public void testFetchTreasureClass() throws Exception {
        Monster monster = new Monster("Skeleton", "Undead", 3, "TC_Skeleton");
        String tc = LootGenerator_fetchTreasureClass(monster);
        assertEquals("TC_Skeleton", tc, 
            "fetchTreasureClass should return the monster's treasure class");
    }

    /**
     * Tests generateBaseItem with a simple treasure class hierarchy.
     */
    @Test
    public void testGenerateBaseItem() throws Exception {
        Map<String, List<String>> treasureClasses = new HashMap<>();
        treasureClasses.put("TC1", Arrays.asList("TC2", "Plate Mail"));
        treasureClasses.put("TC2", Arrays.asList("Chain Mail", "Leather Armor"));
        
        Random rand = new Random(42);
        String item = LootGenerator_generateBaseItem("TC1", treasureClasses, rand);
        assertEquals(true, 
            item.equals("Plate Mail") || item.equals("Chain Mail") || item.equals("Leather Armor"),
            "Generated item should be a valid base item");
    }

    /**
     * Tests generateBaseStats to ensure it generates a defense value within range.
     */
    @Test
    public void testGenerateBaseStats() throws Exception {
        Armor armor = new Armor("Chain Mail", 10, 20);
        Random rand = new Random(42);
        int defense = LootGenerator_generateBaseStats(armor, rand);
        assertEquals(true, 
            defense >= 10 && defense <= 20, 
            "Defense should be between minDefense and maxDefense");
    }

    /**
     * Tests generateAffix to ensure it selects an affix from the list.
     */
    @Test
    public void testGenerateAffix() throws Exception {
        List<MagicAffix> affixes = new ArrayList<>();
        affixes.add(new MagicAffix("Fire", "+Fire Damage", 5, 10));
        affixes.add(new MagicAffix("Ice", "+Cold Damage", 3, 8));
        
        Random rand = new Random(42);
        MagicAffix selected = LootGenerator_generateAffix(affixes, rand);
        assertEquals(true, 
            affixes.contains(selected), 
            "Selected affix should be in the input list");
    }

    /**
     * Tests parseMonsters with a fake dataset.
     */
    @Test
    public void testParseMonsters() throws Exception {
        String fakeData = """
                          Zombie\tUndead\t5\tTC_Zombie
                          Skeleton\tUndead\t3\tTC_Skeleton""";
        java.nio.file.Files.write(
            java.nio.file.Paths.get("test_monsters.txt"), 
            fakeData.getBytes()
        );
        
        List<Monster> monsters = LootGenerator_parseMonsters("test_monsters.txt");
        assertEquals(2, monsters.size(), "Should parse two monsters");
        assertEquals("Zombie", monsters.get(0).name, "First monster name should be Zombie");
        assertEquals("TC_Skeleton", monsters.get(1).treasureClass, 
            "Second monster treasure class should be TC_Skeleton");
        
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get("test_monsters.txt"));
    }

    /**
     * Tests parseArmors with a mock dataset.
     */
    @Test
    public void testParseArmors() throws Exception {
        String mockData = """
                          Plate Mail\t50\t100
                          Leather Armor\t10\t20""";
        java.nio.file.Files.write(
            java.nio.file.Paths.get("test_armors.txt"), 
            mockData.getBytes()
        );
        
        Map<String, Armor> armors = LootGenerator_parseArmors("test_armors.txt");
        assertEquals(2, armors.size(), "Should parse two armors");
        assertEquals(50, armors.get("Plate Mail").minDefense, 
            "Plate Mail minDefense should be 50");
        assertEquals(20, armors.get("Leather Armor").maxDefense, 
            "Leather Armor maxDefense should be 20");
        
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get("test_armors.txt"));
    }

    // Helper methods to access private methods via reflection
    private static Monster LootGenerator_pickMonster(List<Monster> monsters, Random rand) 
        throws Exception {
        java.lang.reflect.Method method = LootGenerator.class
            .getDeclaredMethod("pickMonster", List.class, Random.class);
        method.setAccessible(true);
        return (Monster) method.invoke(null, monsters, rand);
    }

    private static String LootGenerator_fetchTreasureClass(Monster monster) 
        throws Exception {
        java.lang.reflect.Method method = LootGenerator.class
            .getDeclaredMethod("fetchTreasureClass", Monster.class);
        method.setAccessible(true);
        return (String) method.invoke(null, monster);
    }

    private static String LootGenerator_generateBaseItem(
        String tc, Map<String, List<String>> treasureClasses, Random rand) 
        throws Exception {
        java.lang.reflect.Method method = LootGenerator.class
            .getDeclaredMethod("generateBaseItem", String.class, Map.class, Random.class);
        method.setAccessible(true);
        return (String) method.invoke(null, tc, treasureClasses, rand);
    }

    private static int LootGenerator_generateBaseStats(Armor armor, Random rand) 
        throws Exception {
        java.lang.reflect.Method method = LootGenerator.class
            .getDeclaredMethod("generateBaseStats", Armor.class, Random.class);
        method.setAccessible(true);
        return (Integer) method.invoke(null, armor, rand);
    }

    private static MagicAffix LootGenerator_generateAffix(List<MagicAffix> affixes, Random rand) 
        throws Exception {
        java.lang.reflect.Method method = LootGenerator.class
            .getDeclaredMethod("generateAffix", List.class, Random.class);
        method.setAccessible(true);
        return (MagicAffix) method.invoke(null, affixes, rand);
    }

    @SuppressWarnings("unchecked")
    private static List<Monster> LootGenerator_parseMonsters(String filePath) 
        throws Exception {
        java.lang.reflect.Method method = LootGenerator.class
            .getDeclaredMethod("parseMonsters", String.class);
        method.setAccessible(true);
        return (List<Monster>) method.invoke(null, filePath);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Armor> LootGenerator_parseArmors(String filePath) 
        throws Exception {
        java.lang.reflect.Method method = LootGenerator.class
            .getDeclaredMethod("parseArmors", String.class);
        method.setAccessible(true);
        return (Map<String, Armor>) method.invoke(null, filePath);
    }
}