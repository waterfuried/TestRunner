import java.lang.annotation.*;

public class MarkedAnnotation {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface BeforeSuite {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Test { int value() default 1; }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface AfterSuite {}

    public static final int ANNOTATION_BEFORE = 1;
    public static final int ANNOTATION_AFTER = 2;
    public static final int ANNOTATION_TEST = 4;
}