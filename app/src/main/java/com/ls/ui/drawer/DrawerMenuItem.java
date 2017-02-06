package com.ls.ui.drawer;

import com.ls.utils.L;

public class DrawerMenuItem {
    private String name;
    private int iconRes;
    private int selIconRes;
    private DrawerMenu.DrawerItem drawerItem;

    public DrawerMenuItem(String name, int iconRes, int selIconRes, DrawerMenu.DrawerItem drawerItem, boolean group) {
        this.name = name;
        this.iconRes = iconRes;
        this.selIconRes = selIconRes;
        this.drawerItem = drawerItem;
        this.group = group;
    }


    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public void setSelIconRes(int selIconRes) {
        this.selIconRes = selIconRes;
    }

    public int getSelIconRes() {
        return selIconRes;
    }

    private boolean group;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public DrawerMenu.DrawerItem getDrawerItem() {
        return drawerItem;
    }

    @Override
    public String toString() {
        return "DrawerMenuItem{" +
                "name='" + name + '\'' +
                ", iconRes=" + iconRes +
                ", selIconRes=" + selIconRes +
                ", drawerItem=" + drawerItem +
                ", group=" + group +
                '}';
    }
}
