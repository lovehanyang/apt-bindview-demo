package com.chuchujie.apt_processor;

import com.chuchujie.apt_annotation.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {

    private Messager mMessager;
    private Elements mElementsUtils;
    private Map<String, ClassCreatorProxy> mProxyMap = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
        mElementsUtils = processingEnvironment.getElementUtils();
        mMessager.printMessage(Diagnostic.Kind.WARNING, "打印日志测试...");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(BindView.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "processing...");
        mProxyMap.clear();
        //得到所有注解
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : elements) {
            //获取到被注解的变量textview，imageview等
            VariableElement variableElement = (VariableElement) element;
            //获取到类
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            //获取到类名
            String fullClassName = classElement.getQualifiedName().toString();
            //为每个java类建立映射表
            ClassCreatorProxy proxy = mProxyMap.get(fullClassName);
            //获取包名
            PackageElement packageElement = mElementsUtils.getPackageOf(classElement);
            String packageName = packageElement.getQualifiedName().toString();

            if (proxy == null) {
                proxy = new ClassCreatorProxy(packageName, classElement);
                mProxyMap.put(fullClassName, proxy);
            }

            BindView bindAnnotation = variableElement.getAnnotation(BindView.class);
            int id = bindAnnotation.value();
            proxy.setVariableElementMap(id, variableElement);
        }

        //开始通过JavaPoet生成代码
        for (String key : mProxyMap.keySet()) {
            ClassCreatorProxy classCreatorProxy = mProxyMap.get(key);
            JavaFile javaFile = JavaFile.builder(classCreatorProxy.getPackageName(), classCreatorProxy.generateJavaCode()).build();
            //生成文件
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, "generate success ...");
        return true;
    }
}
