package ru.nsu.dashkovskii.hashtable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса HashTable.
 */
class HashTableTest {

    @Test
    void testPutGetUpdate() {
        HashTable<String, Number> ht = new HashTable<>();
        ht.put("one", 1);
        assertEquals(1, ht.get("one"));
        ht.update("one", 1.0);
        assertEquals(1.0, ht.get("one"));
    }

    @Test
    void testRemove() {
        HashTable<String, Integer> ht = new HashTable<>();
        ht.put("x", 10);
        assertEquals(10, ht.remove("x"));
        assertNull(ht.get("x"));
    }

    @Test
    void testEquals() {
        HashTable<String, Integer> a = new HashTable<>();
        HashTable<String, Integer> b = new HashTable<>();
        a.put("a", 1);
        b.put("a", 1);
        assertEquals(a, b);
        b.put("b", 2);
        assertNotEquals(a, b);
    }

    @Test
    void testIteratorConcurrentModification() {
        HashTable<String, Integer> ht = new HashTable<>();
        ht.put("x", 1);
        var it = ht.iterator();
        ht.put("y", 2);
        assertThrows(
                ru.nsu.dashkovskii.exceptions.ConcurrentModificationException.class,
                it::hasNext);
    }

    @Test
    void testContainsKey() {
        HashTable<String, Integer> ht = new HashTable<>();
        ht.put("key", 100);
        assertTrue(ht.containsKey("key"));
        assertFalse(ht.containsKey("nonexistent"));
    }

    @Test
    void testSize() {
        HashTable<String, Integer> ht = new HashTable<>();
        assertEquals(0, ht.size());
        ht.put("a", 1);
        assertEquals(1, ht.size());
        ht.put("b", 2);
        assertEquals(2, ht.size());
        ht.remove("a");
        assertEquals(1, ht.size());
    }

    @Test
    void testToString() {
        HashTable<String, Integer> ht = new HashTable<>();
        ht.put("x", 1);
        String str = ht.toString();
        assertTrue(str.contains("x=1"));
    }

    @Test
    void testHashCode() {
        HashTable<String, Integer> a = new HashTable<>();
        HashTable<String, Integer> b = new HashTable<>();
        a.put("key", 123);
        b.put("key", 123);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testUpdateNonexistentKey() {
        HashTable<String, Integer> ht = new HashTable<>();
        assertThrows(IllegalArgumentException.class, () -> ht.update("missing", 10));
    }

    @Test
    void testNullKey() {
        HashTable<String, Integer> ht = new HashTable<>();
        ht.put(null, 10);
        assertEquals(10, ht.get(null));
        assertTrue(ht.containsKey(null));
        assertEquals(10, ht.remove(null));
        assertFalse(ht.containsKey(null));
    }

    @Test
    void testIteration() {
        HashTable<String, Integer> ht = new HashTable<>();
        ht.put("a", 1);
        ht.put("b", 2);
        ht.put("c", 3);

        int count = 0;
        for (HashNode<String, Integer> node : ht) {
            assertNotNull(node.key);
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    void testResize() {
        HashTable<Integer, String> ht = new HashTable<>();
        for (int i = 0; i < 100; i++) {
            ht.put(i, "value" + i);
        }
        assertEquals(100, ht.size());
        for (int i = 0; i < 100; i++) {
            assertEquals("value" + i, ht.get(i));
        }
    }
}
