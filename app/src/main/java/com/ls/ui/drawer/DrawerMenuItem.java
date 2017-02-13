package com.ls.ui.drawer;


public class DrawerMenuItem {
    private String name;
    private int iconRes;
    private int selIconRes;
    private boolean group;
    private EventMode eventMode;

    public DrawerMenuItem(String name, int iconRes, int selIconRes, EventMode eventMode, boolean group) {
        this.name = name;
        this.iconRes = iconRes;
        this.selIconRes = selIconRes;
        this.eventMode = eventMode;
        this.group = group;
    }


    public int getIconRes() {
        return iconRes;
    }


    public int getSelIconRes() {
        return selIconRes;
    }


    public String getName() {
        return name;
    }


    public boolean isGroup() {
        return group;
    }

    public EventMode getEventMode() {
        return eventMode;
    }

    @Override
    public String toString() {
        return "DrawerMenuItem{" +
                "name='" + name + '\'' +
                ", iconRes=" + iconRes +
                ", selIconRes=" + selIconRes +
                ", eventMode=" + eventMode +
                ", group=" + group +
                '}';
    }
}
