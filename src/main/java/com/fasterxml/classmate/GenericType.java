package com.fasterxml.classmate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * This class is used to pass full generics type information, and
 * avoid problems with type erasure (that basically removes most
 * usable type references from runtime Class objects).
 * It is based on ideas from
 * <a href="http://gafter.blogspot.com/2006/12/super-type-tokens.html"
 * >http://gafter.blogspot.com/2006/12/super-type-tokens.html</a>,
 *<p>
 * Usage is by sub-classing: here is one way to instantiate reference
 * to generic type <code>List&lt;Integer></code>:
 *<pre>
 *  GenericType type = new GenericType&lt;List&lt;Integer>>() { };
 *</pre>
 * which can be passed to methods that accept <code>GenericReference</code>.
 */
public abstract class GenericType <T>
{
    protected final Type type;

    protected GenericType()
    {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) { // verify that we do have parameterization
            throw new IllegalArgumentException("TypeReference constructed without actual type information");
        }
        if (!(superClass instanceof ParameterizedType)) { // sanity check; should never occur...
            throw new IllegalArgumentException("Internal error: TypeReference's super type not ParameterizedType, but "
                    +superClass.getClass().getName());
        }
        type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() { return type; }
}