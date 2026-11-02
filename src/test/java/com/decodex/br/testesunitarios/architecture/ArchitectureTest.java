package com.decodex.br.testesunitarios.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

    private static final ArchRule domainModelsMustNotDependOnOuterLayers = noClasses()
            .that().resideInAPackage("..domain.model..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "..adapters..",
                    "..application..",
                    "..config..",
                    "org.springframework..",
                    "jakarta..");

    private static final ArchRule featureApplicationMustNotDependOnAdaptersOrConfiguration = noClasses()
            .that().resideInAPackage("..features..application..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "..adapters..",
                    "..config..",
                    "org.springframework..",
                    "jakarta..");

    @Test
    void domainModelsAndFeatureApplicationMustRemainIndependentFromOuterLayers() {
        JavaClasses classes = new ClassFileImporter().importPackages("com.decodex.br");

        domainModelsMustNotDependOnOuterLayers.check(classes);
        featureApplicationMustNotDependOnAdaptersOrConfiguration.check(classes);
    }
}
