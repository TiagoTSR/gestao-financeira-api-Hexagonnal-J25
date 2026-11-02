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

    @Test
    void domainModelsMustRemainIndependentFromFrameworkAndInfrastructure() {
        JavaClasses classes = new ClassFileImporter().importPackages("com.decodex.br");

        domainModelsMustNotDependOnOuterLayers.check(classes);
    }
}
