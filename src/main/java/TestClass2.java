public class TestClass2 {
    @MarkedAnnotation.Test(value=2)
    public static void doSomething() {
        System.out.println("\tdo something already");
    }

    @MarkedAnnotation.Test(value=5)
    public static void doSomethingDifferent() {
        System.out.println("\tdo something different");
    }

    @MarkedAnnotation.Test(value=5)
    public static void doSomethingMore() {
        System.out.println("\tdo something more");
    }

    @MarkedAnnotation.AfterSuite
    public static void doSomethingLater() {
        System.out.println("\tdo something eventually");
    }
    /*@MarkedAnnotation.AfterSuite
    public void doSomethingAfterAll() {
        System.out.println("\tdo something eventually");
    }*/
}