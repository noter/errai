/*
 * Copyright 2010 JBoss, a divison Red Hat, Inc
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

package org.jboss.errai.ioc.rebind;

import com.google.gwt.core.ext.typeinfo.JClassType;
import org.jboss.errai.bus.rebind.ProcessingContext;
import org.jboss.errai.ioc.rebind.ioc.InjectorFactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class ProcessorFactory {
    private Map<Class<? extends Annotation>, AnnotationHandler> annotationHandlers;
    private InjectorFactory injectorFactory;

    public ProcessorFactory(InjectorFactory factory) {
        this.annotationHandlers = new HashMap<Class<? extends Annotation>, AnnotationHandler>();
        this.injectorFactory = factory;
    }

    public void registerHandler(Class<? extends Annotation> annotation, AnnotationHandler handler) {
        annotationHandlers.put(annotation, handler);
    }

    @SuppressWarnings({"unchecked"})
    public void process(JClassType type, ProcessingContext context) {
        for (Class<? extends Annotation> aClass : annotationHandlers.keySet()) {
            if (type.isAnnotationPresent(aClass)) {
                injectorFactory.addType(type);
                annotationHandlers.get(aClass).handle(type, type.getAnnotation(aClass), context);
            }
        }
    }
}