package fr.modcraftmc.crossservercore;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ReflectionUtil {
    public static <T extends Annotation> List<Class<?>> getClassesWithAnnotation(Class<T> annotation){
        List<Class<?>> classes = new ArrayList<>();

        Type annotationType = Type.getType(annotation);
        List<IModFileInfo> modFiles = ModList.get().getModFiles();
        for (IModFileInfo modFile : modFiles) {
            ModFileScanData scanData = modFile.getFile().getScanResult();
            Stream<ModFileScanData.AnnotationData> annotationDatas = scanData.getAnnotations().stream().filter(annotationData -> annotationType.equals(annotationData.annotationType()));
            annotationDatas.forEach(annotationData -> {
                try {
                    classes.add(Class.forName(annotationData.memberName()));
                } catch (ClassNotFoundException e) {
                    CrossServerCore.LOGGER.error("Unable to find class " + annotationData.memberName() + " in mod " + modFile.getMods().get(0).getModId() + " for annotation " + annotationType.getClassName());
                }
            });
        }

        return classes;
    }
}
