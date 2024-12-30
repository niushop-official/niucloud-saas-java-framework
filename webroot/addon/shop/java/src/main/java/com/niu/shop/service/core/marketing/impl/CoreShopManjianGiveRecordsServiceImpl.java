package com.niu.shop.service.core.marketing.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.shop.entity.manjian.ShopManjian;
import com.niu.shop.entity.manjian.ShopManjianGiveRecords;
import com.niu.shop.mapper.manjian.ShopManjianGiveRecordsMapper;
import com.niu.shop.mapper.manjian.ShopManjianMapper;
import com.niu.shop.service.core.marketing.ICoreShopManjianGiveRecordsService;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CoreShopManjianGiveRecordsServiceImpl extends ServiceImpl<ShopManjianGiveRecordsMapper, ShopManjianGiveRecords> implements ICoreShopManjianGiveRecordsService {
}
