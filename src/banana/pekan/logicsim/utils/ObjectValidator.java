package banana.pekan.logicsim.utils;

public class ObjectValidator {

    Object a;
    Object b;

    boolean cycled = false;

    Object lastA;
    Object lastB;

    public ObjectValidator(Object a, Object b) {
        this.a = a;
        this.b = b;
    }

    public boolean hasCycled() {
        return cycled;
    }

    public boolean doMatch() {
        return a.equals(b);
    }

    public boolean arePositive() {
        return doMatch() && (!(a instanceof Boolean) || (boolean) a);
    }

    void onChange() {

        if (arePositive()) cycled = !cycled;

    }

    public void setA(Object a) {
        this.a = a;
        if (lastA != null && lastA != a) onChange();
        lastA = a;
    }

    public void setB(Object b) {
        this.b = b;
        if (lastB != null && lastB != b) onChange();
        lastB = b;
    }

    public Object getA() {
        return a;
    }

    public Object getB() {
        return b;
    }
}
