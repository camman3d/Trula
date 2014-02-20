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

Each node has a `kind`, a `name`, and a set of `properties`. Properties are key-value pairs. Names are defined with a
preceding colon ':'. The syntax for kind and name is:

    Kind {
        :name
        Kind2:name2
    }

Properties are defined within square brackets. The syntax is:

    Foo[name = "bar"] {
        Child["My Name" = "baz", age = "4"]
    }

In the context of Scala or Java, the kind is equivalent to the type and name to the property name. Basic or primitive
types are used as properties.

**Example 1:** Given the following Scala code:

```scala
class Bar() {}

class Foo(val name: String, val next: Bar) {
    val age = name.length
}

val example = new Foo("trula!", new Bar())
```

The equivalent Trula structure definition for `example` would be:

    Foo[name = "trula!", age = "6"] {
        String:name
        Int:age
    }

In the context of XML, the kind is equivalent to the tag label and tag properties are kept. There are no names.

**Example 2:** Given the following XML:

```xml
<div id="container">
    <p>Hello world!</p>
</div>
```

The equivalent Trula structure definition would be:

    div[id = "container"] {
        p {
            String
        }
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

You can still enforce parentage and properties with wildcards:

    Foo > %[name = "bar"] {
        Baz
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

You can add/replace properties:

    Foo[name = "bar"] -> Foo[name = "baz"]

## Scala Engine

To use Trula to perform tree reduction you will use the `TreeReducer` class.

```scala
import com.joshmonson.trula.TreeReducer

val xml =
  <html>
    <body>
      <p>Hello World!</p>
    </body>
  </html>

val rules = "p -> div"
val treeReducer = new TreeReducer(rules)
val updated = treeReducer.reduce(xml)
println(updated.obj.get)
```
