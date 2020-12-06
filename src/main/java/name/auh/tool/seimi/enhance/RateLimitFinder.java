package name.auh.tool.seimi.enhance;

import java.lang.annotation.*;

/**
 * 标识被墙检测的位置，统一执行被墙逻辑
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimitFinder {

    boolean backToQueue() default false;

    boolean isHealthCheckUrl() default false;

}
