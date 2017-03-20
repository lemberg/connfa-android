package com.ls.ui.drawer;


public class DrawerMenuItem {
    private final int name;
    private final int iconRes;
    private final int selIconRes;
    private final boolean group;
    private final EventMode eventMode;

    public DrawerMenuItem(int name, int iconRes, int selIconRes, EventMode eventMode, boolean group) {
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


    public int getName() {
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
