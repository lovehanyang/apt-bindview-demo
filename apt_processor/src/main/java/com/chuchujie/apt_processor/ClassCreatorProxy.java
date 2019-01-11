package com.chuchujie.apt_processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

//创建java文件代理类（生成代码的工具类）
public class ClassCreatorProxy {

    private TypeElement mTypeElement;
    private String mPackageName;
    private String mBindingClassName;
    private Map<Integer, VariableElement> mVariableElementMap = new HashMap<>();


    public ClassCreatorProxy(String packageName, TypeElement classElement) {
        mPackageName = packageName;
        mBindingClassName = classElement.getSimpleName().toString() + "_ViewBinding";
        mTypeElement = classElement;

    }

    public void setVariableElementMap(int id, VariableElement element) {
        mVariableElementMap.put(id, element);
    }

    public String getPackageName() {
        return mPackageName;
    }

    public TypeSpec generateJavaCode() {
        TypeSpec typeSpec = TypeSpec.classBuilder(mBindingClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateMethods())
                .build();
        return typeSpec;
    }

    private MethodSpec generateMethods() {
        ClassName className = ClassName.bestGuess(mTypeElement.getQualifiedName().toString());
        MethodSpec.Builder builder =
                MethodSpec
                        .methodBuilder("bind")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(className, "className");

        for (int id : mVariableElementMap.keySet()) {
            VariableElement variableElement = mVariableElementMap.get(id);
            String name = variableElement.getSimpleName().toString();
            String type = variableElement.asType().toString();
            builder.addCode("className." + name + " = " + "(" + type + ")(((android.app.Activity)className).findViewById( " + id + "));");
        }
        return builder.build();


    }
}
