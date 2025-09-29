package org.objects.finder;

/**
 * Strategy for searching whether a given value (by name or textual representation)
 * is present within a target object graph.
 */
public interface ValueFinder {

    /**
     * Returns whether the given searchKey is found within the target object graph.
     * This overload is case-insensitive by default.
     *
     * @param target    the root object to search
     * @param searchKey the value/name to look for
     * @return true if found, false otherwise
     */
    <T> boolean findValue(T target, String searchKey);

    /**
     * Returns whether the given searchKey is found within the target object graph,
     * honoring the provided case sensitivity flag.
     *
     * @param target        the root object to search
     * @param searchKey     the value/name to look for
     * @param caseSensitive whether comparisons are case-sensitive
     * @return true if found, false otherwise
     */
    <T> boolean findValue(T target, String searchKey, boolean caseSensitive);
}
