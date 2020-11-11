@ECHO OFF
SET local=%CD%
cd ..\..\..\..\..\..\
SET root=%CD%
SET point=info.kgeorgiy.java.advanced.implementor
SET slash=info\kgeorgiy\java\advanced\implementor
SET korneev=%root%\java-advanced-2020\modules\%point%\%slash%
javadoc -private -d %local%\javadoc %local%\Implementor.java %local%\UpgradeMethod.java %local%\JarImplementor.java %korneev%\Impler.java %korneev%\JarImpler.java %korneev%\ImplerException.java
