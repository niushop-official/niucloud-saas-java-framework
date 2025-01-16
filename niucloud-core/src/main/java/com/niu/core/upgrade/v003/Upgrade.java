package com.niu.core.upgrade.v003;

import com.niu.core.common.component.upgrade.IUpgradeProvider;
import com.niu.core.common.component.upgrade.annotation.UpgradeProvider;

@UpgradeProvider(addon = "core", version = "0.0.3")
public class Upgrade extends IUpgradeProvider {

    @Override
    public void handle() {
        System.out.println("core 0.0.3");
    }
}
