package banana.pekan.logicsim.utils;

public class ObjectValidator {

    Object a;
    Object b;

    public ObjectValidator(Object a, Object b) {
        this.a = a;
        this.b = b;
    }

    public boolean doMatch() {
        return a.equals(b);
    }

    public void setA(Object a) {
        this.a = a;
    }

    public void setB(Object b) {
        this.b = b;
    }

    public Object getA() {
        return a;
    }

    public Object getB() {
        return b;
    }
}
