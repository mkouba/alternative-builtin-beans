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
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class AlternativeEventTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class).addAsManifestResource(BeansXml.getBeansXmlAsset(), "beans.xml").addAsServiceProvider(Extension.class, EventExtension.class)
                .addClasses(Bravo.class, Alpha.class, EventExtension.class, IntegerEventBean.class);
    }

    @Test
    public void testAlternativeEvent(Alpha alpha, Bravo bravo) {

        // This should be noop if alternative built-in beans are supported
        alpha.integerCustomEvent.fire(Integer.valueOf(42));
        assertTrue(bravo.getPayloads().isEmpty());

        // Also verify the original Event is used for other cases
        alpha.integerOriginalEvent.fire(Integer.valueOf(1));
        assertEquals(1, bravo.getPayloads().size());
        assertEquals(Integer.valueOf(1), bravo.getPayloads().get(0));

        alpha.bravoEvent.fire(new Bravo());
        assertEquals(2, bravo.getPayloads().size());
        assertTrue(bravo.getPayloads().get(1) instanceof Bravo);
    }

    @Dependent
    public static class Alpha {

        // For this injection point the custom bean should be used
        @Inject
        @TestQualifier
        Event<Integer> integerCustomEvent;

        @Inject
        @TestQualifier
        Event<Bravo> bravoEvent;

        @Inject
        @Default
        Event<Integer> integerOriginalEvent;

    }

    @ApplicationScoped
    public static class Bravo {

        private List<Object> payloads = new CopyOnWriteArrayList<>();

        void observeAllIntegers(@Observes Integer event) {
            payloads.add(event);
        }

        void observeAllBravos(@Observes Bravo event) {
            payloads.add(event);
        }

        public List<Object> getPayloads() {
            return payloads;
        }

    }

    public static class EventExtension implements Extension {

        void afterBeanDiscovery(@Observes AfterBeanDiscovery event) {
            event.addBean(new IntegerEventBean());
        }

    }

}
