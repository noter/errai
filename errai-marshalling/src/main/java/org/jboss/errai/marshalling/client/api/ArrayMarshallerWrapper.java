package org.jboss.errai.marshalling.client.api;

import java.util.Arrays;
import java.util.List;

import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.marshallers.AbstractNullableMarshaller;
import org.jboss.errai.marshalling.client.marshallers.ListMarshaller;

/**
 * A marshaller that wraps another marshaller, producing and consuming arrays of
 * objects handled by that marshaller.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
public class ArrayMarshallerWrapper<T> extends AbstractNullableMarshaller<T> {

  private final Marshaller<?> wrappedMarshaller;
  
  public ArrayMarshallerWrapper(Marshaller<?> wrappedMarshaller) {
    this.wrappedMarshaller = wrappedMarshaller;
  }

  /**
   * Throws UnsupportedOperationException.
   */
  @Override
  public Class<T> getTypeHandled() {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public T doNotNullDemarshall(EJValue o, MarshallingSession ctx) {
    List<?> values = ListMarshaller.INSTANCE.demarshall(o, ctx);
    return (T) values.toArray(wrappedMarshaller.getEmptyArray(ctx));
  }

  @Override
  public String doNotNullMarshall(Object o, MarshallingSession ctx) {
    Object[] a = (Object[]) o;
    return ListMarshaller.INSTANCE.marshall(Arrays.asList(a), o.getClass().getName(), ctx);
  }

  @Override
  public T[] getEmptyArray(MarshallingSession ctx) {
    return (T[]) ctx.addArrayDimension(wrappedMarshaller.getEmptyArray(ctx));
  }
}