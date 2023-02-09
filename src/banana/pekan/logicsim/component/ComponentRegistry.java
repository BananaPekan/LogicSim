package banana.pekan.logicsim.component;

import banana.pekan.logicsim.component.components.CustomComponent;

import java.util.ArrayList;

public class ComponentRegistry {

    public static ArrayList<CustomComponent> customComponents;

    public static void init() {
        customComponents = new ArrayList<>();
    }

    public static boolean isRegistered(CustomComponent component) {
        return customComponents.stream().anyMatch(comp -> comp.getName().equals(component.getName()));
    }

    public static void registerCustom(CustomComponent component) {
        if (!isRegistered(component)) {
            customComponents.add(component);
        }
    }

    public ComponentRegistry() {
        customComponents = new ArrayList<>();
    }

}
