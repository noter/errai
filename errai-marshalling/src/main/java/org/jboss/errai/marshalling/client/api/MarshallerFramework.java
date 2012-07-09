/*
 * Copyright 2011 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.marshalling.client.api;

import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.extension.InitVotes;
import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.MarshallingSessionProviderFactory;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.api.json.impl.gwt.GWTJSON;
import org.jboss.errai.marshalling.client.protocols.MarshallingSessionProvider;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONParser;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class MarshallerFramework implements EntryPoint {
  private static MarshallerFactory marshallerFactory;

  static {
    InitVotes.waitFor(MarshallerFramework.class);
    marshallerFactory = GWT.create(MarshallerFactory.class);

    ParserFactory.registerParser(new Parser() {
      @Override
      public EJValue parse(String input) {
        return GWTJSON.wrap(JSONParser.parseStrict(input));
      }
    });

    InitVotes.voteFor(MarshallerFramework.class);
  }


  @Override
  public void onModuleLoad() {
  }

  public static void initializeDefaultSessionProvider() {
    if (!MarshallingSessionProviderFactory.isMarshallingSessionProviderRegistered()) {
      MarshallingSessionProviderFactory.setMarshallingSessionProvider(new MarshallingSessionProvider() {
        @Override
        public MarshallingSession getEncoding() {
          return new MarshallerFramework.JSONMarshallingSession();
        }

        @Override
        public MarshallingSession getDecoding() {
          return new MarshallerFramework.JSONMarshallingSession();
        }

        @Override
        public boolean hasMarshaller(String fqcn) {
          return MarshallerFramework.getMarshallerFactory().getMarshaller(null, fqcn) != null;
        }

        @Override
        public Marshaller getMarshaller(String fqcn) {
          return MarshallerFramework.getMarshallerFactory().getMarshaller(null, fqcn);
        }
      });
    }
  }


  public static class JSONMarshallingSession extends AbstractMarshallingSession {

    public JSONMarshallingSession() {
      super(new MappingContext() {
        @Override
        public Marshaller<Object> getMarshaller(String clazz) {
          return marshallerFactory.getMarshaller("json", clazz);
        }

        @Override
        public boolean hasMarshaller(String clazzName) {
          return marshallerFactory.getMarshaller(clazzName, "json") != null;
        }

        @Override
        public boolean canMarshal(String cls) {
          return marshallerFactory.getMarshaller("json", cls) != null;
        }
      });
    }

    @Override
    public String determineTypeFor(String formatType, Object o) {
      EJValue jsonValue = (EJValue) o;

      if (jsonValue.isObject() != null) {
        EJObject jsonObject = jsonValue.isObject();
        if (jsonObject.containsKey(SerializationParts.ENCODED_TYPE)) {
          return jsonObject.get(SerializationParts.ENCODED_TYPE).isString().stringValue();
        }
        else {
          return Map.class.getName();
        }
      }
      else if (jsonValue.isString() != null) {
        return String.class.getName();
      }
      else if (jsonValue.isNumber() != null) {
        return Double.class.getName();
      }
      else if (jsonValue.isBoolean() != null) {
        return Boolean.class.getName();
      }
      else if (jsonValue.isArray() != null) {
        return List.class.getName();
      }
      else if (jsonValue.isNull()) {
        return null;
      }
      throw new RuntimeException("unknown type: cannot reverse map value to concrete Java type: " + o);
    }
    
    @Override
    public Object addArrayDimension(Object emptyArray) {
      Class<?> arrayClass = emptyArray.getClass();
      int dims = 0;
      while (arrayClass.isArray()) {
        arrayClass = arrayClass.getComponentType();
        dims++;
      }
      
      // plus one more dimension
      dims++;

      Class<?>[] arrayClasses = new Class[dims];
      JavaScriptObject[] castableTypeMaps = new JavaScriptObject[dims];
      int[] queryIdExprs = new int[dims]; 
      int[] dimExprs = new int[dims];
      for (int i = 0; i < dims; i++) {
        arrayClasses[i] = arrayClass;
        castableTypeMaps[i] = getCastableTypeMap(emptyArray);
        queryIdExprs[i] = getQueryId(emptyArray);
        dimExprs[i] = 0;
      }
      
      int seedType = 0; // FIXME this will only work for non-primitive values
      return initDims(arrayClasses, castableTypeMaps, queryIdExprs, dimExprs, dims, seedType);
    }

    private native int getQueryId(Object emptyArray) /*-{
      var queryId = emptyArray.@com.google.gwt.lang.Array::queryId;
      @java.lang.System::out.@java.io.PrintStream::println(Ljava/lang/String;)("The queryId is " + queryId); 
      return queryId;
    }-*/;

    private native JavaScriptObject getCastableTypeMap(Object emptyArray) /*-{
      return null;//return @com.google.gwt.lang.Util::getCastableTypeMap(Ljava/lang/Object;)(emptyArray);
    }-*/;

    public native Object initDims(Class<?> arrayClasses[], 
        JavaScriptObject[] castableTypeMapExprs, int[] queryIdExprs, 
        int[] dimExprs, int count, int seedType) /*-{
      return @com.google.gwt.lang.Array::initDims([Ljava/lang/Class;[Lcom/google/gwt/core/client/JavaScriptObject;[I[III)();;
    }-*/;
  }

  public static MarshallerFactory getMarshallerFactory() {
    return marshallerFactory;
  }
}
