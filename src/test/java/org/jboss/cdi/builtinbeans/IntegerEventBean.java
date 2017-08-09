/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.cdi.builtinbeans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Event;
import javax.enterprise.event.NotificationOptions;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.TypeLiteral;

public class IntegerEventBean implements Bean<Event<Integer>> {

    @Override
    public Event<Integer> create(CreationalContext<Event<Integer>> creationalContext) {
        return new Event<Integer>() {

            @Override
            public void fire(Integer event) {
                // Noop
            }

            @Override
            public Event<Integer> select(Annotation... qualifiers) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <U extends Integer> Event<U> select(Class<U> subtype, Annotation... qualifiers) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <U extends Integer> Event<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <U extends Integer> CompletionStage<U> fireAsync(U event) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <U extends Integer> CompletionStage<U> fireAsync(U event, NotificationOptions options) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public void destroy(Event<Integer> instance, CreationalContext<Event<Integer>> creationalContext) {
    }

    @SuppressWarnings("serial")
    @Override
    public Set<Type> getTypes() {
        return Collections.singleton(new TypeLiteral<Event<Integer>>() {
        }.getType());
    }

    @SuppressWarnings("serial")
    @Override
    public Set<Annotation> getQualifiers() {
        return Collections.singleton(TestQualifier.Literal.INSTANCE);
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return Dependent.class;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public boolean isAlternative() {
        return true;
    }

    @Override
    public Class<?> getBeanClass() {
        return IntegerEventBean.class;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public boolean isNullable() {
        return false;
    }

}