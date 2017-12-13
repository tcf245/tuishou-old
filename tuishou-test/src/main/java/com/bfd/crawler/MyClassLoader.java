package com.bfd.crawler;

public class MyClassLoader extends ClassLoader{

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.startsWith("com.bfd")){
            System.out.println("load " + name);
            return loadClass(name);
        }
        return super.findClass(name);
    }
}
