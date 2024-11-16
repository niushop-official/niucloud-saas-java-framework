package com.niu.shop.event.marketing;

import com.niu.core.common.component.context.event.Event;
import com.niu.shop.entity.active.ShopActive;
import lombok.Data;

@Data
public class ActiveEndAfterEvent extends Event {
    private ShopActive active;
}
