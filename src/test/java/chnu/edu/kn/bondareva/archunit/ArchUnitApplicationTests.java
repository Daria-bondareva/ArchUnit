package chnu.edu.kn.bondareva.archunit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

@SpringBootTest
class ArchUnitApplicationTests {
    private JavaClasses applicationClasses;

    @BeforeEach
    public void initialize() {
        applicationClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("chnu.edu.kn.bondareva.archunit");
    }

    // Layered Architecture
    @Test
    void shouldFollowLayerArchitecture() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")

                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")

                .check(applicationClasses);
    }

    // Naming Conventions
    @Test
    void servicesShouldHaveNameEndingService() {
        classes().that().resideInAPackage("..service..")
                .should().haveSimpleNameEndingWith("Service")
                .check(applicationClasses);
    }

    @Test
    void controllersShouldHaveNameEndingController() {
        classes().that().resideInAPackage("..controller..")
                .should().haveSimpleNameEndingWith("Controller")
                .check(applicationClasses);
    }

    @Test
    void repositoriesShouldHaveNameEndingRepository() {
        classes().that().resideInAPackage("..repository..")
                .should().haveSimpleNameEndingWith("Repository")
                .check(applicationClasses);
    }

    @Test
    void modelsShouldNotHaveServiceInName() {
        noClasses().that().resideInAPackage("..model..")
                .should().haveSimpleNameEndingWith("Service")
                .check(applicationClasses);
    }

    // Package Locations
    @Test
    void controllersShouldBeInControllerPackage() {
        classes().that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..controller..")
                .check(applicationClasses);
    }

    @Test
    void servicesShouldBeInServicePackage() {
        classes().that().haveSimpleNameEndingWith("Service")
                .should().resideInAPackage("..service..")
                .check(applicationClasses);
    }

    @Test
    void repositoriesShouldBeInRepositoryPackage() {
        classes().that().haveSimpleNameEndingWith("Repository")
                .should().resideInAPackage("..repository..")
                .check(applicationClasses);
    }

    // Annotations
    @Test
    void controllersShouldBeAnnotatedWithRestController() {
        classes().that().resideInAPackage("..controller..")
                .should().beAnnotatedWith(RestController.class)
                .orShould().beAnnotatedWith(Controller.class)
                .check(applicationClasses);
    }

    @Test
    void servicesShouldBeAnnotatedWithService() {
        classes().that().resideInAPackage("..service..")
                .should().beAnnotatedWith(Service.class)
                .check(applicationClasses);
    }

    @Test
    void repositoriesShouldBeAnnotatedWithRepository() {
        classes().that().resideInAPackage("..repository..")
                .should().beAnnotatedWith(Repository.class)
                .check(applicationClasses);
    }

    @Test
    void modelsShouldBeAnnotatedWithDocument() {
        classes().that().resideInAPackage("..model..")
                .should().beAnnotatedWith(Document.class)
                .check(applicationClasses);
    }

    // Layer Dependencies & Isolation
    @Test
    void controllersShouldNotDependOnOtherControllers() {
        noClasses().that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(applicationClasses);
    }

    @Test
    void repositoriesShouldNotDependOnServices() {
        noClasses().that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..service..")
                .check(applicationClasses);
    }

    @Test
    void modelsShouldNotDependOnControllers() {
        noClasses().that().resideInAPackage("..model..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(applicationClasses);
    }

    // General Coding Rules
    @Test
    void noStandardStreams() {
        NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.check(applicationClasses);
    }

    @Test
    void noGenericExceptions() {
        NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS.check(applicationClasses);
    }

    // Member & Method Rules
    @Test
    void modelFieldsShouldBePrivate() {
        fields().that().areDeclaredInClassesThat().resideInAPackage("..model..")
                .should().notBePublic()
                .check(applicationClasses);
    }

    @Test
    void configsShouldBeAnnotatedWithConfiguration() {
        classes().that().resideInAPackage("..config..")
                .should().beAnnotatedWith(Configuration.class)
                .check(applicationClasses);
    }

    @Test
    void interfacesShouldNotHaveInterfaceInName() {
        noClasses().that().areInterfaces()
                .should().haveSimpleNameContaining("Interface")
                .check(applicationClasses);
    }
}
