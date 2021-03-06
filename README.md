## Overview

ClassMate is a zero-dependency Java library for accurately introspecting type information, including reliable resolution of generic type declarations for both classes ("types") and members (fields, methods and constructors).

Project is licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)

## Documentation

[Project wiki](/cowtowncoder/java-classmate/wiki) has Javadocs.

External links that may help include:

* [Resolving Generic Types with Classmate](http://www.cowtowncoder.com/blog/archives/2012/04/entry_471.html) (some simple usage examples)
* [Problem with java.lang.reflect.Type](http://www.cowtowncoder.com/blog/archives/2010/12/entry_436.html) (explanation of issues ClassMate was written to solve)

-----

## Usage

### Maven dep

To use ClassMate via Maven, include following dependency:

    <dependency>
      <groupId>com.fasterxml</groupId>
      <artifactId>classmate</artifactId>
      <version>0.5.5</version>
    </dependency>

### Non-Maven

Downloads available from [Project wiki](/cowtowncoder/java-classmate/wiki).

### Resolving Class type information

Main class used for fully resolving type information for classes is `com.fasterxml.classmate.TypeResolver`.
TypeResolver does simple caching for resolved supertypes (since many subtypes resolve to smaller set of supertypes, typically). Since all access to shared data is synchronized, a single `TypeResolver` instance is typically shared for a single system (as a plain old static singleton): there are no benefits to instantiating more instances.

Its main resolution methods are:

* `resolve(Class cls)`: given a plain old class, will use generic type information that super type declarations (extends, implements) may have.
* `resolve(GenericType<T>)`: given a subtype of `GenericType` (which uses ["Super-type Token" pattern](http://gafter.blogspot.com/2006/12/super-type-tokens.html)), fully resolve type information
* `resolve(Class<?> baseType, Class<?> typeParameter1, ... , Class<?> typeParameter2)`: given base type (like `List.class`) and zero or more type parameters (either as `Class` es to resolve, or as `ResolvedType` s), resolves type information

Result in all these cases is an instance of `ResolvedType`, which you can think of as generic type information containing replacement for `java.lang.Class`. It is also the starting point for resolving member (constructor, field, method) information.

### Resolving Member information

Member information resolution is done by `com.fasterxml.classmate.MemberResolver`, which takes a `ResolvedType` and produces `ResolvedTypeWithMembers`. As with `TypeResolver`, a single instance is typically shared by all code; but since no reuse of information is done, creating new instances is cheap and need not be avoided.

There are a few configuration options that can be used to determine things like:

* Whether to include information from `java.lang.Object` (default: ignore and do not include)
* Which members to filter out before aggregation (default: no filtering, include all members)
* Which annotations to include in resolved members (default: include nothing)
* For method annotations included, whether annotations from overridden methods are be inherited by overriding methods.
* Which annotation overrides (aka "mix-ins") to use for which classes (default: no overrides)

`ResolvedTypeWithMembers` has simple accessors for:

* Constructors: only constructors of the resolved type itself included (no constructors of superclasses)
* Fields: all fields from resolved type and its superclasses are included; expect in cases where fields are masked, in which case masked fields (super-class field with same name as a field on its sub-class) are not included.
* Static methods: only methods declared by resolved type itself
* Member methods: all methods from resolved type and its supertypes are included; except for overriding in which case only overriding method is included (which also means that methods from interfaces are typically not included, when implementing class has overriding method intance)

Annotations of all member types can be overridden by annotation overrides; annotation value defaulting only works for members that use inheritance, meaning just member methods.

Member information is lazily constructed. Access to member information is synchronized such that it is safe to share `ResolvedTypeWithMembers` instances.
