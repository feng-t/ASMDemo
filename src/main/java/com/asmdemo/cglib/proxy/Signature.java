package com.asmdemo.cglib.proxy;

public class Signature {
    private String name;
    private String desc;

    public Signature(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
    public String toString() {
        return name + desc;
    }
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Signature))
            return false;
        Signature other = (Signature)o;
        return name.equals(other.name) && desc.equals(other.desc);
    }

    public int hashCode() {
        return name.hashCode() ^ desc.hashCode();
    }
}
