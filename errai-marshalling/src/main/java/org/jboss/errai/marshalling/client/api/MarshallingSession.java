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


/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public interface MarshallingSession {
  public MappingContext getMappingContext();
  
  public Marshaller<Object> getMarshallerInstance(String fqcn);
  
  public String determineTypeFor(String formatType, Object o);
  
  public boolean hasObjectHash(Object reference);
  
  public boolean hasObjectHash(String hashCode);
  
  public <T> T getObject(Class<T> type, String hashCode);
  
  public void recordObjectHash(String hashCode, Object instance);
  
  public String getObjectHash(Object o);

  public boolean isEncoded(Object ref);

  /**
   * Returns a 0-length array with one more dimension than the given array. For example, for a parameter value of
   * {@code int[0][0]}, the return value will be {@code int[0][0][0]}.
   * <p>
   * This method is used by the {@link ArrayMarshallerWrapper} in order to create arrays of the correct runtime type.
   * 
   * @param emptyArray
   *          An array of any component type with any number of dimensions. Must not be null.
   * @return An array of n+1 dimensions (where n is the number of dimensions on the given array), each dimension having
   *         size 0.
   */
  public Object addArrayDimension(Object emptyArray);
}
