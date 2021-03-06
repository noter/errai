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

package org.jboss.errai.enterprise.client.jaxrs;

import org.jboss.errai.common.client.api.extension.InitVotes;
import org.jboss.errai.ioc.client.api.IOCBootstrapTask;
import org.jboss.errai.ioc.client.api.TaskOrder;
import org.jboss.errai.marshalling.client.api.MarshallerFramework;

import com.google.gwt.core.client.GWT;

/**
 * Bootstrapper for the JAX-RS module.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@IOCBootstrapTask(TaskOrder.Before)
public class JaxrsModuleBootstrapper implements Runnable {
  static {
    // ensure that the marshalling framework has been initialized
    MarshallerFramework.initializeDefaultSessionProvider();
  }
  
  @Override
  public void run() {
    InitVotes.waitFor(JaxrsModuleBootstrapper.class);
    JaxrsProxyLoader proxyLoader = GWT.create(JaxrsProxyLoader.class);
    proxyLoader.loadProxies();
    InitVotes.voteFor(JaxrsModuleBootstrapper.class);
  }
}
