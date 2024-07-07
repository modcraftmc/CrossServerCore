package fr.modcraftmc.crossservercoreproxyextension;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ReflectionUtil {
    public static <T extends Annotation> List<Class<?>> getClassesWithAnnotation(Class<T> annotation){
        List<Class<?>> classes = new ArrayList<>();

        Class<?>[] projectClasses = getClasses("fr.modcraftmc.crossservercoreproxyextension");
        for (Class<?> clazz : projectClasses) {
            if (clazz.isAnnotationPresent(annotation)) {
                classes.add(clazz);
            }
        }

        return classes;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists())
            return classes;

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file,
                        (!packageName.equals("") ? packageName + "." : packageName) + file.getName()));
            } else if (file.getName().endsWith(".class"))
                try {
                    classes.add(Class
                            .forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                } catch (ClassNotFoundException e) {
                    System.err.println(e.getMessage());
                }
        }
        return classes;
    }

    public static Class<?>[] getClasses(String packageName) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(path);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directory : dirs)
            classes.addAll(findClasses(directory, packageName));

        return classes.toArray(new Class[classes.size()]);

    }
}
