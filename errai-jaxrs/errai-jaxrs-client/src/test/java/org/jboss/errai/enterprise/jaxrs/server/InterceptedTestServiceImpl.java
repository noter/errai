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

package org.jboss.errai.enterprise.jaxrs.server;

import org.jboss.errai.enterprise.jaxrs.client.shared.InterceptedTestService;

/**
 * Implementation of {@link InterceptedTestService} returning test data.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class InterceptedTestServiceImpl implements InterceptedTestService {

  @Override
  public String interceptedGetWithEndpointBypassing() {
    // should never be called
    return "not intercepted";
  }

  @Override
  public String interceptedGetWithResultManipulation(String result) {
    return result;
  }

  @Override
  public String interceptedGetWithParameterManipulation(String result) {
    return result;
  }

  @Override
  public String interceptedGetWithChainedInterceptors(String result) {
    return result;
  }
}