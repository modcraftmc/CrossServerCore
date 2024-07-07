package fr.modcraftmc.crossservercoreproxyextension.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoSerialize {
}
