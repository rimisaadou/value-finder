package org.objects.finder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objects.finder.api.Car;
import org.objects.finder.api.ComplexObject;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ValueFinderImplTest {
    static ValueFinder finder;
    
    @BeforeAll
    static void setUp() {
         finder = new ValueFinderImpl();
    }

    @Test
    void testFindValueInSimpleStringMatchCaseSensitive() {
        String target = "hello";
        assertTrue(finder.findValue(target, "hello"));
    }

    @Test
    void testFindValueInSimpleStringNoCaseSensitive() {
        String target = "hello";
        assertTrue(finder.findValue(target, "HELLO", false));
    }

    @Test
    void testFindValueInStringNonMatching() {
        String target = "hello";
        assertFalse(finder.findValue(target, "world"));
    }

    @Test
    void testFindValueInNullTarget() {
        assertFalse(finder.findValue(null, "search"));
    }

    @Test
    void testFindValueInNullSearchKey() {
        String target = "hello";
        assertFalse(finder.findValue(target, null));
    }

    @Test
    void testFindValueInMapKeyMatch() {
        Map<String, String> target = new HashMap<>();
        target.put("key1", "value1");
        target.put("key2", "value2");
        assertTrue(finder.findValue(target, "key1"));
    }

    @Test
    void testFindValueInMapValueMatch() {
        Map<String, String> target = new HashMap<>();
        target.put("key1", "value1");
        target.put("key2", "value2");
        assertTrue(finder.findValue(target, "value1"));
    }

    @Test
    void testFindValueInArrayElementMatch() {
        String[] target = {"apple", "banana", "cherry"};
        assertTrue(finder.findValue(target, "banana"));
    }

    @Test
    void testFindValueInArrayIndexMatch() {
        String[] target = {"apple", "banana", "cherry"};
        assertTrue(finder.findValue(target, "1"));
    }

    @Test
    void testFindValueInArrayNonMatching() {
        String[] target = {"apple", "banana", "cherry"};
        assertFalse(finder.findValue(target, "grape"));
    }

    @Test
    void testFindValueInIterableElementMatch() {
        List<String> target = Arrays.asList("apple", "banana", "cherry");
        assertTrue(finder.findValue(target, "cherry"));
    }

    @Test
    void testFindValueInIterableIndexMatch() {
        List<String> target = Arrays.asList("apple", "banana", "cherry");
        assertTrue(finder.findValue(target, "1"));
    }

    @Test
    void testFindValueInIterableNonMatching() {
        List<String> target = Arrays.asList("apple", "banana", "cherry");
        assertFalse(finder.findValue(target, "grape"));
    }

    @Test
    void testFindValueInRecordMatchingField() {
        record SampleRecord(String field1, int field2) { }
        SampleRecord target = new SampleRecord("value1", 42);
        assertTrue(finder.findValue(target, "field1"));
    }

    @Test
    void testFindValueInRecordMatchingFieldValue() {
        record SampleRecord(String field1, int field2) { }
        SampleRecord target = new SampleRecord("value1", 42);
        assertTrue(finder.findValue(target, "value1"));
    }

    @Test
    void testFindValueInPojoMatchingField() {
        class SamplePojo {
            private final String field1 = "value1";
            private final int field2 = 42;
        }
        SamplePojo target = new SamplePojo();
        assertTrue(finder.findValue(target, "field1"));
    }

    @Test
    void testFindValueInPojoMatchingFieldValue() {
        class SamplePojo {
            private final String field1 = "value1";
            private final int field2 = 42;
        }
        SamplePojo target = new SamplePojo();
        assertTrue(finder.findValue(target, "value1"));
    }

    @Test
    void testFindValueCircularReference() {
        Map<String, Object> map1 = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        map1.put("key", map2);
        map2.put("key", map1);
        assertFalse(finder.findValue(map1, "nonExistent"));
    }
    
    @Test
    void testComplexObject() {
        var complexObject = new ComplexObject(
                "Test Object",
                25,
                new Car("Toyota", "Camry", 2024),
                new String[]{"item1", "item2", "item3"},
                new int[][]{{1, 2}, {3, 4}},
                new ArrayList<>(List.of("list1", "list2"))
        );
        assertFalse(finder.findValue(null, "nonExistent"));
    }
}