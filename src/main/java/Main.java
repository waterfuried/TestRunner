/*
    1. Создать класс, который может выполнять «тесты», в качестве тестов выступают классы
       с наборами методов с аннотациями @Test.
       Для этого у него должен быть статический метод start(), которому в качестве
       параметра передается или объект типа Class, или имя класса.
       Из «класса-теста» вначале должен быть запущен метод с аннотацией @BeforeSuite,
       если такой имеется, далее запущены методы с аннотациями @Test, а по завершению
       всех тестов – метод с аннотацией @AfterSuite.
       К каждому тесту необходимо также добавить приоритеты (int числа от 1 до 10),
       в соответствии с которыми будет выбираться порядок их выполнения,
       если приоритет одинаковый, то порядок не имеет значения.
       Методы с аннотациями @BeforeSuite и @AfterSuite должны присутствовать
       в единственном экземпляре, иначе необходимо бросить RuntimeException
       при запуске «тестирования».

       Это домашнее задание никак не связано с темой тестирования через JUnit
       и использованием этой библиотеки, то есть проект пишется с нуля.
*/
import java.lang.reflect.Method;
import java.util.*;

public class Main {
    /**
     * проверить наличие в классе методов с аннотациями BeforeSuite, AfterSuite и Test
     * @param methods - массив методов класса
     * @return
     *      0, если в классе есть методы с аннотацией Test
     *      ANNOTATION_BEFORE, если в классе есть метод с аннотацией BeforeSuite
     *      ANNOTATION_AFTER, если в классе есть метод с аннотацией BeforeSuite
     *
     *      -ANNOTATION_BEFORE, если в классе более одного метода с аннотацией BeforeSuite
     *      -ANNOTATION_AFTER, если в классе более одного метода с аннотацией AfterSuite
     *      -ANNOTATION_TEST, если в классе нет методов с аннотацией Test
     */
    static int checkValidity(Method[] methods) {
        boolean beforeSuite, afterSuite, test;
        beforeSuite = afterSuite = test = false;
        int res = 0;

        for (Method m : methods) {
            if (m.getAnnotation(MarkedAnnotation.BeforeSuite.class) != null)
                if (beforeSuite) res = -MarkedAnnotation.ANNOTATION_BEFORE; else beforeSuite = true;
            if (m.getAnnotation(MarkedAnnotation.AfterSuite.class) != null)
                if (afterSuite) res = -MarkedAnnotation.ANNOTATION_AFTER; else afterSuite = true;
            if (m.getAnnotation(MarkedAnnotation.Test.class) != null) test = true;
        }
        if (!test)
            res -= MarkedAnnotation.ANNOTATION_TEST;
        else if (res == 0) {
            if (beforeSuite) res += MarkedAnnotation.ANNOTATION_BEFORE;
            if (afterSuite) res += MarkedAnnotation.ANNOTATION_AFTER;
        }
        return res;
    }

    static void startActions(Method[] methods) throws RuntimeException {
        int v = checkValidity(methods);
        if (v < 0) {
            v = Math.abs(v);
            if ((v & MarkedAnnotation.ANNOTATION_TEST) == MarkedAnnotation.ANNOTATION_TEST)
                throw new RuntimeException("Класс не содержит методы с аннотацией @Test");
            else
                throw new RuntimeException("Класс содержит более одного метода с аннотацией @" +
                        (v == MarkedAnnotation.ANNOTATION_BEFORE ? "BeforeSuite" : "AfterSuite"));
        } else {
            // выполнить метод с аннотацией BeforeSuite
            if ((v & MarkedAnnotation.ANNOTATION_BEFORE) == MarkedAnnotation.ANNOTATION_BEFORE)
                for (Method m : methods)
                    if (m.getAnnotation(MarkedAnnotation.BeforeSuite.class) != null)
                        try { m.invoke(null); }
                        catch (Exception ex) { ex.printStackTrace(); }

            // выполнить методы с аннотацией Test
            ArrayList<Method> meth = new ArrayList<>(Arrays.asList(methods));
            int minIdx;
            do {
                minIdx = -1;
                for (int i = 0; i < meth.size(); i++) {
                    if (meth.get(i).getAnnotation(MarkedAnnotation.Test.class) != null)
                        if (minIdx < 0)
                            minIdx = i;
                        else
                            if (meth.get(i).getAnnotation(MarkedAnnotation.Test.class).value() <
                                meth.get(minIdx).getAnnotation(MarkedAnnotation.Test.class).value())
                                minIdx = i;
                }
                if (minIdx >= 0) {
                    try { meth.get(minIdx).invoke(null); }
                    catch (Exception ex) { ex.printStackTrace(); }
                    meth.remove(minIdx);
                }
            } while (minIdx >= 0);

            // выполнить метод с аннотацией AfterSuite
            if ((v & MarkedAnnotation.ANNOTATION_AFTER) == MarkedAnnotation.ANNOTATION_AFTER)
                for (Method m : methods)
                    if (m.getAnnotation(MarkedAnnotation.AfterSuite.class) != null)
                        try { m.invoke(null); }
                        catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    static void start(Class<?> classObj) {
        System.out.println("running methods of " + classObj.getSimpleName());
        startActions(classObj.getDeclaredMethods());
    }

    static void start(String className) {
        System.out.println("running methods of " + className);
        try { startActions(Class.forName(className).getDeclaredMethods()); }
        catch (ClassNotFoundException ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) {
        start(TestClass1.class);
        start("TestClass2");
        start("TestClassEmpty");
    }
}