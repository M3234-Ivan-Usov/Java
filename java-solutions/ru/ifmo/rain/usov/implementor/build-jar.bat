@ECHO OFF
SET local=%CD%
cd ..\..\..\..\..\..\
SET root=%CD%
SET point=info.kgeorgiy.java.advanced.implementor
SET slash=info\kgeorgiy\java\advanced\implementor
SET korneev=%root%\java-advanced-2020\modules\%point%\%slash%
javac -d %local%\bin %local%\Implementor.java %local%\JarImplementor.java %local%\UpgradeMethod.java %korneev%\Impler.java %korneev%\JarImpler.java %korneev%\ImplerException.java
SET package=ru.ifmo.rain.usov.implementor
cd %local%\bin
jar -cvfe %local%\JarImpl.jar %package%.JarImplementor info\* ru\*