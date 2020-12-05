package com.renhui.remote.ui;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

public class JRadioButtonMenu extends JMenu {
    private ButtonGroup mButtonGroup;

    public JRadioButtonMenu(String title) {
        super(title);
        this.mButtonGroup = new ButtonGroup();
    }

    public void removeAll() {
        super.removeAll();
        this.mButtonGroup = new ButtonGroup();
    }

    public void add(JRadioButtonMenuItem menuItem) {
        add(menuItem);
        this.mButtonGroup.add(menuItem);
    }

    public void setSelectedIndex(int index) {
        this.mButtonGroup.clearSelection();
        ((JRadioButtonMenuItem)getMenuComponent(index)).setSelected(true);
    }
}
