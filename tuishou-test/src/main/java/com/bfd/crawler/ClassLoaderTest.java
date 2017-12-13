package com.bfd.crawler;

public class ClassLoaderTest {
    private static MyClassLoader classLoader = new MyClassLoader();
    private static MyClassLoader2 classLoader2 = new MyClassLoader2();

    public static void main(String[] args) {
        try {
//            Hello hello = (Hello) classLoader.findClass("com.bfd.crawler.EngHello").newInstance();
            Hello hello = null;
            Class c = classLoader.findClass("com.bfd.crawler.EngHello");
            hello = (Hello) c.newInstance();
            hello.hello();


            Hello hello2 = null;
            Class c2 = classLoader2.findClass("com.bfd.crawler.EngHello");
            hello2 = (Hello) c2.newInstance();
            hello2.hello();

            System.out.println(hello.getClass().getName());
            System.out.println(hello2.getClass().getName());
            System.out.println(hello.getClass().equals(hello2.getClass()));
            System.out.println(hello instanceof com.bfd.crawler.Hello);


        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


}
