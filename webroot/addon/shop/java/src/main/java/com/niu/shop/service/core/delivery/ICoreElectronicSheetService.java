package com.niu.shop.service.core.delivery;

import com.niu.shop.service.core.delivery.param.ShopDeliveryPrintElectronicSheetParam;
import com.niu.shop.service.core.delivery.vo.ElectronicSheetPrintVo;

import java.util.List;

public interface ICoreElectronicSheetService {
    List<ElectronicSheetPrintVo> printElectronicSheet(ShopDeliveryPrintElectronicSheetParam param);
}
