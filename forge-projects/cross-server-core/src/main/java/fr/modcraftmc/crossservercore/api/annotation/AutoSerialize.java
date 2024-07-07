package fr.modcraftmc.crossservercore.api.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Type;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoSerialize {
}
