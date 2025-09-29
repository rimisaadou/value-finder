package org.objects.finder;

import java.lang.reflect.AccessibleObject;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the {@link ValueFinder} interface that performs deep searching
 * for values within complex object graphs.
 * <p>
 * This implementation supports searching through various object types including:
 * <ul>
 *   <li>Simple values (primitives, strings, enums, etc.)</li>
 *   <li>Java Records</li>
 *   <li>Maps (searches both keys and values)</li>
 *   <li>Arrays (including primitive arrays)</li>
 *   <li>Collections and Iterables</li>
 *   <li>POJOs (Plain Old Java Objects) with fields</li>
 * </ul>
 * <p>
 * The search can match:
 * <ul>
 *   <li>Field or property names</li>
 *   <li>Map keys</li>
 *   <li>Array or list indices (when searchKey is a valid integer)</li>
 *   <li>String representations of values</li>
 * </ul>
 * <p>
 * The implementation handles circular references using an identity-based visited tracking mechanism.
 */
public class ValueFinderImpl implements ValueFinder {

    private static final Object VISITED_MARKER = new Object();

    /**
     * Searches for a value in the target object using case-sensitive matching.
     * <p>
     * This is a convenience method that delegates to {@link #findValue(Object, String, boolean)}
     * with caseSensitive set to true.
     *
     * @param target    the object to search within (can be null)
     * @param searchKey the key or value to search for (can be null)
     * @param <T>       the type of the target object
     * @return true if the searchKey is found anywhere in the object graph, false otherwise
     */
    @Override
    public <T> boolean findValue(T target, String searchKey) {
        return findValue(target, searchKey, true);
    }

    /**
     * Searches for a value in the target object with configurable case sensitivity.
     * <p>
     * The search is performed recursively through the entire object graph, checking:
     * <ul>
     *   <li>Field/property names</li>
     *   <li>Map keys</li>
     *   <li>Collection/array indices</li>
     *   <li>String representations of values</li>
     * </ul>
     *
     * @param target        the object to search within (can be null)
     * @param searchKey     the key or value to search for (can be null)
     * @param caseSensitive if true, performs case-sensitive comparison; if false, case-insensitive
     * @param <T>           the type of the target object
     * @return true if the searchKey is found anywhere in the object graph, false otherwise
     */
    @Override
    public <T> boolean findValue(T target, String searchKey, boolean caseSensitive) {
        if (target == null || searchKey == null) return false;
        return containsDeep(target, searchKey, caseSensitive, new IdentityHashMap<>());
    }

    /**
     * Recursively searches through an object graph for a matching key or value.
     * <p>
     * This method performs a depth-first search through the object structure,
     * tracking visited objects to handle circular references.
     *
     * @param obj           the current object being examined
     * @param searchKey     the key or value to search for
     * @param caseSensitive whether to perform case-sensitive matching
     * @param visited       identity map to track visited objects and prevent infinite loops
     * @return true if a match is found, false otherwise
     */
    private boolean containsDeep(Object obj, String searchKey, boolean caseSensitive,
                                 IdentityHashMap<Object, Object> visited) {
        if (obj == null) return false;
        if (isSimpleValue(obj)) {
            return equalsWithCaseOption(String.valueOf(obj), searchKey, caseSensitive);
        }
        if (visited.put(obj, VISITED_MARKER) != null) {
            return false; // already visited
        }

        Class<?> cls = obj.getClass();

        // Records
        if (cls.isRecord()) {
            return handleRecord(obj, searchKey, caseSensitive, visited, cls);
        }

        // Maps
        if (obj instanceof Map<?, ?> map) {
            return handleMap(map, searchKey, caseSensitive, visited);
        }

        // Arrays
        if (cls.isArray()) {
            return handleArray(obj, searchKey, caseSensitive, visited);
        }

        // Iterables
        if (obj instanceof Iterable<?> it) {
            return handleIterable(obj, it, searchKey, caseSensitive, visited);
        }

        // POJO fields
        if (handlePojoFields(obj, searchKey, caseSensitive, visited, cls)) return true;

        return equalsWithCaseOption(String.valueOf(obj), searchKey, caseSensitive);
    }

    // --- Handlers ---

    /**
     * Handles searching within Java Record instances.
     * <p>
     * Searches both the record component names and their values by invoking
     * the accessor methods reflectively.
     *
     * @param obj           the record instance
     * @param searchKey     the key or value to search for
     * @param caseSensitive whether to perform case-sensitive matching
     * @param visited       identity map to track visited objects
     * @param cls           the record class
     * @return true if a match is found in component names or values, false otherwise
     */
    private boolean handleRecord(Object obj, String searchKey, boolean caseSensitive,
                                 IdentityHashMap<Object, Object> visited, Class<?> cls) {
        for (var rc : cls.getRecordComponents()) {
            try {
                var accessor = rc.getAccessor();
                tryMakeAccessible(accessor);
                Object v = accessor.invoke(obj);
                if (equalsWithCaseOption(rc.getName(), searchKey, caseSensitive)) return true;
                if (containsDeep(v, searchKey, caseSensitive, visited)) return true;
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return false;
    }

    /**
     * Handles searching within Map instances.
     * <p>
     * Searches both the string representation of map keys and recursively
     * searches through all map values.
     *
     * @param map           the map to search
     * @param searchKey     the key or value to search for
     * @param caseSensitive whether to perform case-sensitive matching
     * @param visited       identity map to track visited objects
     * @return true if a match is found in keys or values, false otherwise
     */
    private boolean handleMap(Map<?, ?> map, String searchKey, boolean caseSensitive,
                              IdentityHashMap<Object, Object> visited) {
        // key match by string representation and case rule
        for (var entry : map.entrySet()) {
            Object k = entry.getKey();
            if (k != null && equalsWithCaseOption(String.valueOf(k), searchKey, caseSensitive)) {
                return true;
            }
        }
        for (Object v : map.values()) {
            if (containsDeep(v, searchKey, caseSensitive, visited)) return true;
        }
        return false;
    }

    /**
     * Handles searching within array instances (including primitive arrays).
     * <p>
     * If the searchKey is a valid integer, checks if it's a valid index for the array.
     * Otherwise, searches through all array elements for matching values.
     *
     * @param array         the array to search
     * @param searchKey     the key or value to search for
     * @param caseSensitive whether to perform case-sensitive matching
     * @param visited       identity map to track visited objects
     * @return true if searchKey is a valid index or a match is found in elements, false otherwise
     */
    private boolean handleArray(Object array, String searchKey, boolean caseSensitive,
                                IdentityHashMap<Object, Object> visited) {
        int len = java.lang.reflect.Array.getLength(array);
        Integer idx = tryParseIndex(searchKey);
        if (idx != null && idx >= 0 && idx < len) return true;

        for (int i = 0; i < len; i++) {
            Object el = java.lang.reflect.Array.get(array, i);
            if (el != null) {
                if (equalsWithCaseOption(String.valueOf(el), searchKey, caseSensitive)) return true;
                if (containsDeep(el, searchKey, caseSensitive, visited)) return true;
            }
        }
        return false;
    }

    /**
     * Handles searching within Iterable instances (including Collections).
     * <p>
     * For List instances, checks if searchKey is a valid index.
     * For all iterables, searches through elements for matching values.
     *
     * @param obj           the original object (used for List type checking)
     * @param it            the iterable to search
     * @param searchKey     the key or value to search for
     * @param caseSensitive whether to perform case-sensitive matching
     * @param visited       identity map to track visited objects
     * @return true if searchKey is a valid index or a match is found in elements, false otherwise
     */
    private boolean handleIterable(Object obj, Iterable<?> it, String searchKey, boolean caseSensitive,
                                   IdentityHashMap<Object, Object> visited) {
        Integer idx = tryParseIndex(searchKey);
        if (idx != null && obj instanceof java.util.List<?> list) {
            return idx >= 0 && idx < list.size();
        }
        for (Object el : it) {
            if (el != null) {
                if (equalsWithCaseOption(String.valueOf(el), searchKey, caseSensitive)) return true;
                if (containsDeep(el, searchKey, caseSensitive, visited)) return true;
            }
        }
        return false;
    }

    /**
     * Handles searching within POJO (Plain Old Java Object) fields.
     * <p>
     * Reflectively accesses all declared fields in the class hierarchy
     * (up to but not including Object.class), checking both field names and values.
     *
     * @param obj           the object to search
     * @param searchKey     the key or value to search for
     * @param caseSensitive whether to perform case-sensitive matching
     * @param visited       identity map to track visited objects
     * @param cls           the class of the object
     * @return true if a match is found in field names or values, false otherwise
     */
    private boolean handlePojoFields(Object obj, String searchKey, boolean caseSensitive,
                                     IdentityHashMap<Object, Object> visited, Class<?> cls) {
        for (Class<?> c = cls; c != null && c != Object.class; c = c.getSuperclass()) {
            for (java.lang.reflect.Field f : c.getDeclaredFields()) {
                try {
                    tryMakeAccessible(f);
                    Object v = f.get(obj);
                    if (equalsWithCaseOption(f.getName(), searchKey, caseSensitive)) return true;
                    if (containsDeep(v, searchKey, caseSensitive, visited)) return true;
                } catch (ReflectiveOperationException ignored) {
                }
            }
        }
        return false;
    }

    // --- Utilities ---

    /**
     * Compares two strings with configurable case sensitivity.
     *
     * @param a             the first string
     * @param b             the second string
     * @param caseSensitive if true, performs case-sensitive comparison; if false, case-insensitive
     * @return true if the strings are equal according to the case sensitivity rule, false otherwise
     */
    private static boolean equalsWithCaseOption(String a, String b, boolean caseSensitive) {
        if (a == null || b == null) return false;
        return caseSensitive ? a.equals(b) : a.equalsIgnoreCase(b);
    }

    /**
     * Determines if an object is a simple value type that should not be traversed further.
     * <p>
     * Simple values include: CharSequence, Number, Boolean, Character, UUID, and Enums.
     *
     * @param o the object to check
     * @return true if the object is a simple value type, false otherwise
     */
    private static boolean isSimpleValue(Object o) {
        return o instanceof CharSequence
                || o instanceof Number
                || o instanceof Boolean
                || o instanceof Character
                || o instanceof UUID
                || o.getClass().isEnum();
    }

    /**
     * Attempts to make an AccessibleObject (field, method, constructor) accessible.
     * <p>
     * Silently ignores SecurityException if access cannot be granted.
     *
     * @param ao the AccessibleObject to make accessible
     */
    private static void tryMakeAccessible(AccessibleObject ao) {
        try {
            ao.setAccessible(true);
        } catch (SecurityException ignored) {
        }
    }

    /**
     * Attempts to parse a string as an integer index.
     *
     * @param s the string to parse
     * @return the parsed integer if successful, null if parsing fails or input is null
     */
    private static Integer tryParseIndex(String s) {
        if (s == null) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}