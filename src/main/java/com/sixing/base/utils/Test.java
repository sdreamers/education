package com.sixing.base.utils;

import org.springframework.cglib.beans.BeanCopier;

public class Test {

    static class A implements Cloneable {

        private B b;

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }

        @Override
        protected A clone() throws CloneNotSupportedException {
            return (A)super.clone();
        }
    }

    static class B implements Cloneable {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        protected B clone() throws CloneNotSupportedException {
            return (B)super.clone();
        }
    }

    public static void main(String[] args) throws Exception{
        B b = new B();
        b.setName("22");

        A a = new A();
        a.setB(b);

        A colne = new A();
        BeanCopier.create(A.class, A.class, false).copy(a, colne, null);
        System.out.println(a);
        System.out.println(colne);
        System.out.println(b);
        System.out.println(colne.getB());
    }
}
