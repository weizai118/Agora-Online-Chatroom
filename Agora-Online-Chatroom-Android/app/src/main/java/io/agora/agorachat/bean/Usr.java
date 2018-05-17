package io.agora.agorachat.bean;

import java.util.Arrays;

public class Usr {
    private String name;

    public Usr() {
    }

    public Usr(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usr usr = (Usr) o;
        return (name == usr.name) || (name != null && name.equals(usr.name));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{name});
    }
}
