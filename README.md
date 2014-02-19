# Trula
This is a language and engine which allows you to define methods of transforming tree structures according to
predefined rules.

## Language

### Matching

Matching nodes is somewhat similar to how CSS does matching (but not exactly the same). You can match
either up or down within the tree. In CSS you can match parents and ancestors. In Trula you can
match parents, ancestors, children, and descendants.

    Ancestor >> Parent > Node {
        Child
        [ Descendant ]
    }

There are two properties against which a node is checked for a match: `kind` and `name`. You can specify that
you are looking for a name by putting a colon `:` in front of it. For example:

    Kind1 {
        :name1
        Kind2:name2
    }


In the context of Scala or Java, the kind is equivalent to the type and name to the property name. For example,
an object created from the following Scala class

```scala
class Foo(val name: String) {
    val age = name.length
}
```

would match against the following structure definition:

    Foo {
        String:name
        Int:age
    }

You can specify only the nodes/properties you are interested in:

    Foo {
        :age
    }

Trula supports ordinality. The following example matches a 5th child that is a Bar kind:

    Foo {
        Bar(4)
    }

You can specify that you want to match properties that are absent with `!`:

    Foo {
        !Bar
    }

You can specify wildcards `%` which match any node:

    Foo {
        % {
            Bar
        }
    }

You can still enforce parentage with wildcards:

    Foo > % {
        Bar
    }

### Transforming

The rule syntax is:

    MatchingStructure -> Transformation

You can transform the structure:

    Foo {
        Bar
    } -> Bar {
        Foo
    }

You can rename nodes:

    Foo -> Bar = Foo

You can delete nodes:

    Foo -> ~

You can invoke a method and use the returned result:

    Foo {
        :p1
        :p2
    } -> :result = process(:p1, :p2)

When referencing nodes in the right-hand side of the rule, it will use the first one found. So if you have
multiple nodes with the same kind and/or name, then you'll need to use labels. You can attach labels within
the left-hand structure for use within the right-hand side:

    Foo {
        @bar1 Bar
        @bar2 Bar
    } -> Foo {
        @bar1 {
            @bar2
        }
    }
