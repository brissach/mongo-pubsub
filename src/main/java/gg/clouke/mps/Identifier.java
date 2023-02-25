package gg.clouke.mps;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Clouke
 * @since 25.02.2023 10:29
 * © mongo-pubsub - All Rights Reserved
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Identifier {
    String value();
}
