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
