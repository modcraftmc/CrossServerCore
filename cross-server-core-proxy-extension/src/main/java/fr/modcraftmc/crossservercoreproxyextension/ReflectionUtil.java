package fr.modcraftmc.crossservercoreproxyextension;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {
    public static <T extends Annotation> List<Class<?>> getClassesWithAnnotation(Class<T> annotation) {
        List<Class<?>> classes = new ArrayList<>();

        ClassLoader classLoader = CrossServerCoreProxy.class.getClassLoader();
        Class<?>[] projectClasses = getClasses("fr.modcraftmc.crossservercoreproxyextension", classLoader);

        for (Class<?> clazz : projectClasses) {
            if (clazz.isAnnotationPresent(annotation)) {
                classes.add(clazz);
            }
        }

        return classes;
    }

    private static Class[] getClasses(String packageName, ClassLoader classLoader) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        try {
            for (final ClassPath.ClassInfo info : ClassPath.from(classLoader).getTopLevelClasses()) {
                if (info.getName().startsWith(packageName + ".")) {
                    classes.add(info.load());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return classes.toArray(new Class[classes.size()]);
    }
}
