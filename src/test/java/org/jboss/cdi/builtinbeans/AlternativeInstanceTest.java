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

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class AlternativeInstanceTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class).addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsServiceProvider(Extension.class, InstanceExtension.class)
                .addClasses(Bravo.class, Alpha.class, InstanceExtension.class, IntegerInstanceBean.class);
    }

    @Test
    public void testAlternativeInstance(Alpha alpha) {
        // This should result in UnsatisfiedResolutionException if alternative built-in beans are not supported
        assertEquals(Integer.valueOf(42), alpha.integerCustomInstance.get());
        // Also verify the original Instance is used for other cases
        assertEquals(Bravo.class.getName(), alpha.bravoInstance.get().ping());
        assertEquals(Integer.valueOf(1), alpha.integerOriginalInstance.get());
    }

    @Dependent
    public static class Alpha {

        // For this injection point the custom bean should be used
        @Inject
        @TestQualifier
        Instance<Integer> integerCustomInstance;

        @Inject
        @TestQualifier
        Instance<Bravo> bravoInstance;

        // See Bravo#produceInteger()
        @Inject
        @Default
        Instance<Integer> integerOriginalInstance;

    }

    @Dependent
    @TestQualifier
    public static class Bravo {

        @Dependent
        @Produces
        Integer produceInteger() {
            return Integer.valueOf(1);
        }

        String ping() {
            return Bravo.class.getName();
        }

    }

    public static class InstanceExtension implements Extension {

        void afterBeanDiscovery(@Observes AfterBeanDiscovery event) {
            event.addBean(new IntegerInstanceBean());
        }

    }

    public static class IntegerInstanceBean implements Bean<Instance<Integer>> {

        @Override
        public Instance<Integer> create(CreationalContext<Instance<Integer>> creationalContext) {
            return new Instance<Integer>() {

                @Override
                public Iterator<Integer> iterator() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Integer get() {
                    return Integer.valueOf(42);
                }

                @Override
                public Instance<Integer> select(Annotation... qualifiers) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public <U extends Integer> Instance<U> select(Class<U> subtype, Annotation... qualifiers) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public <U extends Integer> Instance<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean isUnsatisfied() {
                    return false;
                }

                @Override
                public boolean isAmbiguous() {
                    return false;
                }

                @Override
                public void destroy(Integer instance) {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public void destroy(Instance<Integer> instance, CreationalContext<Instance<Integer>> creationalContext) {
        }

        @SuppressWarnings("serial")
        @Override
        public Set<Type> getTypes() {
            return Collections.singleton(new TypeLiteral<Instance<Integer>>() {
            }.getType());
        }

        @SuppressWarnings("serial")
        @Override
        public Set<Annotation> getQualifiers() {
            Set<Annotation> qualifiers = new HashSet<>();
            qualifiers.add(TestQualifier.Literal.INSTANCE);
            qualifiers.add(new AnnotationLiteral<Any>() {
            });
            return qualifiers;
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
            return IntegerInstanceBean.class;
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

}
