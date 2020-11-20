package io.javaoperatorsdk.operator.processing;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.sun.tools.javac.code.Symbol;
import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@SupportedAnnotationTypes(
        "io.javaoperatorsdk.operator.api.Controller")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ControllerAnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements
                    = roundEnv.getElementsAnnotatedWith(annotation);
            annotatedElements.stream().filter(element -> element instanceof Symbol.ClassSymbol)
                    .map(e -> (Symbol.ClassSymbol) e)
                    .forEach(controllerClassSymbol -> generateDoneableClass(controllerClassSymbol));
        }
        return false;
    }

    private void generateDoneableClass(Symbol.ClassSymbol controllerClassSymbol) {
        try {
            // TODO: the resourceType retrieval logic is currently very fragile, done for testing purposes and need to be improved to cover all possible conditions
            final TypeMirror resourceType = ((DeclaredType) controllerClassSymbol.getInterfaces().head).getTypeArguments().get(0);
            Symbol.ClassSymbol customerResourceSymbol = (Symbol.ClassSymbol) processingEnv.getElementUtils().getTypeElement(resourceType.toString());
            JavaFileObject builderFile = processingEnv.getFiler()
                    .createSourceFile(customerResourceSymbol.className() + "Doneable");
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                final MethodSpec constructor = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeName.get(resourceType), "resource")
                        .addParameter(Function.class, "function")
                        .addStatement("super(resource,function);")
                        .build();
                final TypeSpec typeSpec = TypeSpec.classBuilder(customerResourceSymbol.name + "Doneable")
                        .addAnnotation(RegisterForReflection.class)
                        .superclass(ParameterizedTypeName.get(ClassName.get(CustomResourceDoneable.class), TypeName.get(resourceType)))
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(constructor)
                        .build();
                JavaFile file = JavaFile.builder(customerResourceSymbol.packge().fullname.toString(), typeSpec)
                        .build();
                file.writeTo(out);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
