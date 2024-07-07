package fr.modcraftmc.crossservercoreproxyextension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// AutoRegister only work with classes which only use AutoSerialize and with default constructor
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoRegister {
    String value();
}
