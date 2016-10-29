package com.wang.floatingactionmenu.interfaces;

import com.wang.floatingactionmenu.FloatingActionMenu;

/**
 * Created on 2016/10/28.
 * Author: wang
 */

public interface MenuStateChangeListener {
    void onMenuOpened(FloatingActionMenu menu);

    void onMenuClosed(FloatingActionMenu menu);
}
