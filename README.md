This repository contains tests to verify whether it's possible to provide an alernative custom bean for built-in beans `Instance` and `Event`.

See also https://issues.jboss.org/browse/CDI-712.

Weld 2 and Weld 3
=================
Not supported at all.

`AlternativeInstanceTest` fails with `Unsatisfied dependencies for type Integer with qualifiers @TestQualifier at org.jboss.cdi.builtinbeans.AlternativeInstanceTest.testAlternativeInstance(AlternativeInstanceTest.java:60)`. 
**Custom `Instance` bean is ignored and there is no bean for type `Integer` and qualifier `@TestQualifier`**.

`AlternativeEventTest` fails `AlternativeEventTest.testAlternativeEvent(AlternativeEventTest.java:63)`.
**Custom `Event` bean is ignored and event is fired.**

OWB 1.7 and OWB 2.0
===================
Supported but does not work correctly.

`AlternativeInstanceTest` fails with `testAlternativeInstance(org.jboss.cdi.builtinbeans.AlternativeInstanceTest): expected:<1> but was:<42>`.
**Original `Instance` is not used for an injection point with the same type but different qualifiers.**

`AlternativeEventTest` fails with `testAlternativeEvent(org.jboss.cdi.builtinbeans.AlternativeEventTest): expected:<1> but was:<0>`.
**Original `Event` is not used for an injection point with the same type but different qualifiers.**
