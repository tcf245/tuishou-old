package com.bfd.crawler;

public class MyClassLoader2 extends ClassLoader{

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.startsWith("com.bfd")){
            System.out.println("use classload 2 : load " + name);
            return loadClass(name);
        }

        return super.findClass(name);
    }
}
