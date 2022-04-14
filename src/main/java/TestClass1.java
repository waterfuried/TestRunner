public class TestClass1 {
    @MarkedAnnotation.BeforeSuite
    public static void doSomethingFirst() {
        System.out.println("\tfirst of all do something");
    }

    @MarkedAnnotation.Test(value=4)
    public static void doSomething() {
        System.out.println("\tthen do something thereafter");
    }

    @MarkedAnnotation.Test(value=7)
    public static void doSomethingElse() {
        System.out.println("\tthen do something else");
    }

    @MarkedAnnotation.AfterSuite
    public static void doSomethingLater() {
        System.out.println("\tand finally do something great");
    }
}