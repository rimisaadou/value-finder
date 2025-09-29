# Value Finder

A lightweight Java library for deep searching within complex object graphs. Value Finder enables you to search for field names, values, or keys throughout nested data structures with ease.

## 🎯 Overview

Value Finder provides a simple yet powerful API to determine whether a specific value or field name exists anywhere within an object graph, regardless of nesting depth. It intelligently traverses various data structures while handling edge cases like circular references.

## ✨ Features

- **🔍 Deep Object Graph Traversal** - Recursively searches through arbitrarily nested objects
- **🎨 Multiple Data Structure Support**:
  - Plain Old Java Objects (POJOs) with private fields
  - Java Records
  - Collections (List, Set, etc.)
  - Maps (searches both keys and values)
  - Arrays (including primitive arrays)
  - Any Iterable implementation
- **🎯 Flexible Matching Capabilities**:
  - Field and property names
  - Map keys
  - Collection/array indices (when search key is numeric)
  - String representations of values
- **⚙️ Configurable Case Sensitivity** - Choose between case-sensitive or case-insensitive search
- **🔄 Circular Reference Safety** - Handles objects with circular references without infinite loops
- **⚡ Efficient Simple Value Detection** - Optimized handling of primitives, strings, enums, UUIDs, etc.

## 📋 Requirements

- Java 23 or higher
- Maven 3.x (for building from source)

## 🚀 Quick Start

### Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.example</groupId>
    <artifactId>searcher</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

``` markdown
## 📝 Simple Summary - How to Use

Using Value Finder is straightforward and requires just **3 simple steps**:

### Step 1: Create a ValueFinder Instance
```

java ValueFinder finder = new ValueFinderImpl();```

### Step 2: Prepare Your Object
Have any Java object ready - it can be a POJO, Record, Collection, Map, Array, or any combination of nested structures.

```java
Person person = new Person("John", 30);
// or
List<String> items = List.of("apple", "banana", "cherry");
// or
Map<String, Object> config = Map.of("host", "localhost", "port", 8080);
```
```

Step 3: Search for Values or Field Names
Call findValue() with your object and the search term:``` java
// Search (case-insensitive by default)
boolean found = finder.findValue(person, "john");  // returns true

// Search with case sensitivity control
boolean found = finder.findValue(person, "JOHN", true);  // returns false (case-sensitive)
```

That's It! 🎉
The library will automatically:
✅ Search through all nested objects
✅ Check field names and values
✅ Handle Collections, Maps, and Arrays
✅ Deal with circular references safely
✅ Return true if found, false otherwise
Quick Example``` java
import org.objects.finder.ValueFinder;
import org.objects.finder.ValueFinderImpl;

public class SimpleDemo {
public static void main(String[] args) {
// Step 1: Create finder
ValueFinder finder = new ValueFinderImpl();

        // Step 2: Have your object
        var data = Map.of(
            "user", "Alice",
            "age", 28,
            "active", true
        );
        
        // Step 3: Search!
        System.out.println(finder.findValue(data, "Alice"));  // true
        System.out.println(finder.findValue(data, "age"));    // true
        System.out.println(finder.findValue(data, "28"));     // true
        System.out.println(finder.findValue(data, "xyz"));    // false
    }
}
```

No configuration needed. No complex setup. Just create, search, and get results!```

