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
     *      1, если в классе есть метод с аннотацией BeforeSuite
     *      2, если в классе есть метод с аннотацией BeforeSuite
     *
     *      -1, если в классе более одного метода с аннотацией BeforeSuite
     *      -2, если в классе более одного метода с аннотацией AfterSuite
     *      -4, если в классе нет методов с аннотацией Test
     */
    static int checkValidity(Method[] methods) {
        boolean beforeSuite, afterSuite, test;
        beforeSuite = afterSuite = test = false;
        int res = 0;

        for (Method m : methods) {
            if (m.getAnnotation(MarkedAnnotation.BeforeSuite.class) != null)
                if (beforeSuite) res = -1; else beforeSuite = true;
            if (m.getAnnotation(MarkedAnnotation.AfterSuite.class) != null)
                if (afterSuite) res = -2; else afterSuite = true;
            if (m.getAnnotation(MarkedAnnotation.Test.class) != null) test = true;
        }
        if (!test)
            res = -4;
        else {
            if (beforeSuite) res += 1;
            if (afterSuite) res += 2;
        }
        return res;
    }

    static void startActions(Method[] methods) throws RuntimeException {
        int v = checkValidity(methods);
        if (v < 0)
            if (v == -4)
                throw new RuntimeException("Класс не содержит методов с аннотацией @Test");
            else
                throw new RuntimeException("Класс содержит более одного метода с аннотацией @" +
                    (v == -1 ? "BeforeSuite" : "AfterSuite"));
        else {
            // выполнить метод с аннотацией BeforeSuite
            if ((v & 1) == 1)
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
            if ((v & 2) == 2)
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