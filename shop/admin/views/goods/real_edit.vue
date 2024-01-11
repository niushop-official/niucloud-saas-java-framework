<template>
    <div class="main-container">
        <div class="detail-head">
            <div class="left" @click="router.push(`/shop/goods/list`)">
                <span class="iconfont iconxiangzuojiantou !text-xs"></span>
                <span class="ml-[1px]">{{ t('returnToPreviousPage') }}</span>
            </div>
            <span class="adorn">|</span>
            <span class="right">{{ formData.goods_id ? t('updateGoods') : t('addGoods') }}</span>
        </div>
        <el-card class="box-card !border-none" shadow="never">

            <el-tabs v-model="activeName" class="" @tab-click="tabHandleClick">
                <el-tab-pane :label="t('basicInfoTab')" name="basic">

                    <el-form :model="formData" label-width="120px" ref="basicFormRef" :rules="formRules" class="page-form">
                        <el-form-item :label="t('goodsType')" v-if="formData.goods_id">
                            <template v-for="(item) in goodsType" :key="item.type">
                                <div class="goods-type-wrap"
                                    :class="[formData.goods_type == item.type ? 'selected' : 'disabled']">
                                    <div class="goods-type-name">{{ item.name }}</div>
                                    <div class="goods-type-desc">({{ item.desc }})</div>
                                    <template v-if="formData.goods_type == item.type">
                                        <div class="triangle"></div>
                                        <div class="selected">✓</div>
                                    </template>
                                </div>
                            </template>
                        </el-form-item>
                        <el-form-item :label="t('goodsType')" v-else>
                            <div class="goods-type-wrap" :class="{ 'selected': formData.goods_type == item.type }"
                                v-for="(item) in goodsType" :key="item.type" @click="changeGoodsType(item)">
                                <div class="goods-type-name">{{ item.name }}</div>
                                <div class="goods-type-desc">({{ item.desc }})</div>
                                <template v-if="formData.goods_type == item.type">
                                    <div class="triangle"></div>
                                    <div class="selected">✓</div>
                                </template>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('goodsName')" prop="goods_name">
                            <el-input v-model="formData.goods_name" clearable :placeholder="t('goodsNamePlaceholder')"
                                class="input-width" maxlength="60" show-word-limit />
                        </el-form-item>
                        <el-form-item :label="t('subTitle')" prop="sub_title">
                            <el-input v-model="formData.sub_title" clearable :placeholder="t('subTitlePlaceholder')"
                                class="input-width" maxlength="80" show-word-limit />
                        </el-form-item>
                        <el-form-item :label="t('goodsImage')" prop="goods_image">
                            <upload-image v-model="formData.goods_image" :limit="10" />
                        </el-form-item>

                        <el-form-item :label="t('goodsCategory')" prop="goods_category">
                            <el-cascader v-model="formData.goods_category" :options="goodsCategoryOptions"
                                :props="goodsCategoryProps" clearable filterable @change="categoryHandleChange" />
                            <div class="ml-[10px]">
                                <span class="cursor-pointer text-primary mr-[10px]" @click="refreshGoodsCategory">{{
                                    t('refresh') }}</span>
                                <span class="cursor-pointer text-primary" @click="toGoodsCategoryEvent">{{
                                    t('addGoodsCategory') }}</span>
                            </div>
                        </el-form-item>

                        <el-form-item :label="t('brand')">
                            <el-select v-model="formData.brand_id" :placeholder="t('brandPlaceholder')" clearable>
                                <el-option v-for="item in brandOptions" :key="item.brand_id" :label="item.brand_name"
                                    :value="item.brand_id" />
                            </el-select>
                            <div class="ml-[10px]">
                                <span class="cursor-pointer text-primary mr-[10px]" @click="refreshGoodsBrand">{{
                                    t('refresh') }}</span>
                                <span class="cursor-pointer text-primary" @click="toGoodsBrandEvent">{{ t('addGoodsBrand')
                                }}</span>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('label')">
                            <el-checkbox-group v-model="formData.label_ids">
                                <el-checkbox :label="item.label_id" v-for="(item,index) in labelOptions" :key="index">{{ item.label_name }}
                                </el-checkbox>
                            </el-checkbox-group>
                            <div class="ml-[10px]">
                                <span class="cursor-pointer text-primary mr-[10px]" @click="refreshGoodsLabel">{{
                                    t('refresh') }}</span>
                                <span class="cursor-pointer text-primary" @click="toGoodsLabelEvent">{{ t('addGoodsLabel')
                                }}</span>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('goodsService')">
                            <el-checkbox-group v-model="formData.service_ids">
                                <el-checkbox :label="item.service_id" v-for="(item,index) in serviceOptions" :key="index">{{ item.service_name
                                }}
                                </el-checkbox>
                            </el-checkbox-group>
                            <div class="ml-[10px]">
                                <span class="cursor-pointer text-primary mr-[10px]" @click="refreshGoodsService">{{
                                    t('refresh') }}</span>
                                <span class="cursor-pointer text-primary" @click="toGoodsServiceEvent">{{
                                    t('addGoodsService') }}</span>
                            </div>
                        </el-form-item>

                        <el-form-item :label="t('supplier')"
                            v-if="formData.addon_shop_supplier && formData.addon_shop_supplier.status == 1">
                            <el-select v-model="formData.supplier_id" :placeholder="t('supplierPlaceholder')" clearable>
                                <el-option v-for="item in supplierOptions" :key="item.supplier_id"
                                    :label="item.supplier_name" :value="item.supplier_id" />
                            </el-select>
                            <div class="ml-[10px]">
                                <span class="cursor-pointer text-primary mr-[10px]" @click="refreshSupplier">{{ t('refresh')
                                }}</span>
                                <span class="cursor-pointer text-primary" @click="toSupplierEvent">{{ t('addSupplier')
                                }}</span>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('status')">
                            <el-radio-group v-model="formData.status">
                                <el-radio label="1">{{ t('statusOn') }}</el-radio>
                                <el-radio label="0">{{ t('statusOff') }}</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item :label="t('unit')" prop="unit">
                            <el-input v-model="formData.unit" clearable :placeholder="t('unitPlaceholder')"
                                class="input-width" show-word-limit maxlength="6" />
                        </el-form-item>
                        <el-form-item :label="t('virtualSaleNum')" prop="virtual_sale_num">
                            <el-input v-model="formData.virtual_sale_num" clearable
                                :placeholder="t('virtualSaleNumPlaceholder')" class="input-width" show-word-limit
                                maxlength="10">
                                <template #append>{{ formData.unit ? formData.unit : '件' }}</template>
                            </el-input>
                        </el-form-item>
                        <el-form-item :label="t('sort')" prop="sort">
                            <el-input v-model="formData.sort" clearable :placeholder="t('sortPlaceholder')"
                                class="input-width" show-word-limit maxlength="10" />
                        </el-form-item>

                    </el-form>

                </el-tab-pane>
                <el-tab-pane :label="t('priceStockTab')" name="price_stock">
                    <el-form :model="formData" label-width="120px" ref="priceStockFormRef" :rules="formRules"
                        class="page-form">
                        <el-form-item :label="t('specType')" prop="spec_type">
                            <el-radio-group v-model="formData.spec_type">
                                <el-radio label="single">{{ t('singleSpec') }}</el-radio>
                                <el-radio label="multi">{{ t('multiSpec') }}</el-radio>
                            </el-radio-group>
                        </el-form-item>

                        <template v-if="formData.spec_type == 'single'">
                            <el-form-item :label="t('price')" prop="price">
                                <el-input v-model="formData.price" clearable placeholder="0.00" class="input-width"
                                    type="number" maxlength="10">
                                    <template #append>{{ t('yuan') }}</template>
                                </el-input>
                            </el-form-item>

                            <el-form-item :label="t('marketPrice')" prop="market_price">
                                <el-input v-model="formData.market_price" clearable placeholder="0.00" class="input-width"
                                    type="number" maxlength="10">
                                    <template #append>{{ t('yuan') }}</template>
                                </el-input>
                            </el-form-item>

                            <el-form-item :label="t('costPrice')" prop="cost_price">
                                <el-input v-model="formData.cost_price" clearable placeholder="0.00" class="input-width"
                                    type="number" maxlength="10">
                                    <template #append>{{ t('yuan') }}</template>
                                </el-input>
                            </el-form-item>
                            <el-form-item :label="t('weight')" prop="weight">
                                <el-input v-model="formData.weight" clearable placeholder="0.000" class="input-width"
                                    type="number" maxlength="20">
                                    <template #append>kg</template>
                                </el-input>
                            </el-form-item>
                            <el-form-item :label="t('volume')" prop="volume">
                                <el-input v-model="formData.volume" clearable placeholder="0.000" class="input-width"
                                    type="number" maxlength="20">
                                    <template #append>m³</template>
                                </el-input>
                            </el-form-item>
                            <el-form-item :label="t('goodsStock')" prop="stock">
                                <el-input v-model="formData.stock" clearable :placeholder="t('goodsStockPlaceholder')"
                                    class="input-width" type="number" maxlength="10">
                                    <template #append>{{ formData.unit ? formData.unit : t('defaultUnit') }}</template>
                                </el-input>
                            </el-form-item>
                            <el-form-item :label="t('skuNo')">
                                <el-input v-model="formData.sku_no" clearable :placeholder="t('skuNoPlaceholder')"
                                    class="input-width" maxlength="50" />
                            </el-form-item>

                        </template>
                    </el-form>
                    <!-- 多规格 -->
                    <el-form :model="goodsSkuData" label-width="120px" ref="skuFormRef" class="page-form">
                        <div class="el-form-item asterisk-left" v-show="formData.spec_type == 'multi'">
                            <div class="el-form-item__label w-[120px]">{{ t('goodsSku') }}</div>
                            <div class="spec-wrap">
                                <!--规格项/规格值-->
                                <div class="spec-edit-list">

                                    <div class="spec-item" v-for="(item, index) in goodsSpecFormat" :key="item.id">
                                        <div class="spec-name-wrap">
                                            <el-input v-model="item.spec_name" clearable
                                                :placeholder="t('specNamePlaceholder')" class="input-width"
                                                maxlength="20" />
                                        </div>
                                        <div class="spec-value-wrap">
                                            <ul ref="specValueRef">
                                                <li :class="'draggable-element' + index"
                                                    v-for="(specValue, specIndex) in item.values" :key="specValue.id">

                                                    <el-input v-model="specValue.spec_value_name" clearable
                                                        :placeholder="t('specValueNamePlaceholder')" class="input-width"
                                                        :suffix-icon="Rank" maxlength="20"
                                                        @input="specValueNameInputListener">

                                                    </el-input>
                                                    <el-icon class="icon" :size="20" color="#7b7b7b"
                                                        @click="deleteSpecValue(index, specIndex)">
                                                        <CircleCloseFilled />
                                                    </el-icon>

                                                </li>
                                            </ul>
                                            <span class="text-color text-[14px] add-spec-value"
                                                @click="addSpecValue(index)">{{
                                                    t('addSpecValue') }}</span>
                                            <div class="box"></div>
                                        </div>

                                        <el-icon class="del-spec" :size="20" color="#7b7b7b" @click="deleteSpec(index)">
                                            <CircleCloseFilled />
                                        </el-icon>
                                    </div>

                                </div>

                                <div class="add-spec">
                                    <el-button type="primary" @click="addSpec">{{ t('addSpec') }}</el-button>
                                </div>

                                <!-- 批量设置 -->
                                <div class="batch-operation-sku" v-show="Object.keys(goodsSkuData).length">
                                    <label>{{ t('batchOperationSku') }}</label>

                                    <el-select v-model="batchOperation.spec" class="set-spec-select">
                                        <el-option :label="t('all')" value="" />
                                        <template v-for="(item, key) in goodsSkuData" :key="key">
                                            <el-option v-if="item.spec_name" :label="item.spec_name" :value="key" />
                                        </template>
                                    </el-select>

                                    <el-input v-model="batchOperation.price" clearable :placeholder="t('price')"
                                        class="set-input" type="number" maxlength="10" />
                                    <el-input v-model="batchOperation.market_price" clearable
                                        :placeholder="t('marketPrice')" class="set-input" type="number" maxlength="10" />
                                    <el-input v-model="batchOperation.cost_price" clearable :placeholder="t('costPrice')"
                                        class="set-input" type="number" maxlength="10" />
                                    <el-input v-model="batchOperation.stock" clearable :placeholder="t('stock')"
                                        class="set-input" type="number" maxlength="10" />
                                    <el-input v-model="batchOperation.weight" clearable :placeholder="t('skuWeight')"
                                        class="set-input" type="number" maxlength="20" />
                                    <el-input v-model="batchOperation.volume" clearable :placeholder="t('skuVolume')"
                                        class="set-input" type="number" maxlength="20" />
                                    <el-input v-model="batchOperation.sku_no" clearable maxlength="50"
                                        :placeholder="t('skuNo')" class="set-input" />
                                    <el-button type="primary" @click="saveBatch">{{ t('confirm') }}</el-button>
                                </div>

                                <!--sku列表-->
                                <div class="sku-table" v-show="Object.keys(goodsSkuData).length">

                                    <div class="el-table--fit el-table--default el-table" style="width: 100%;">
                                        <div class="el-table__inner-wrapper">
                                            <div class="el-table__header-wrapper">
                                                <table class="el-table__header" border="0" cellpadding="0" cellspacing="0"
                                                    style="width: 100%;">
                                                    <thead>
                                                        <tr>

                                                            <template v-for="(item, index) in goodsSpecFormat" :key="index">
                                                                <th class="el-table__cell" v-if="item.spec_name">
                                                                    <div class="cell">{{ item.spec_name }}</div>
                                                                </th>
                                                            </template>

                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('image') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('price') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('marketPrice') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('costPrice') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('stock') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('skuWeight') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('skuVolume') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('skuNo') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('defaultSku') }}</div>
                                                            </th>
                                                        </tr>
                                                    </thead>
                                                </table>
                                            </div>

                                            <div class="el-table__body-wrapper">
                                                <div class="el-scrollbar">
                                                    <div class="el-scrollbar__wrap el-scrollbar__wrap--hidden-default">
                                                        <div class="el-scrollbar__view"
                                                            style="display: inline-block; vertical-align: middle;">
                                                            <table class="el-table__body" cellspacing="0" cellpadding="0"
                                                                border="0" style="table-layout: fixed; width: 100%;">
                                                                <tbody tabindex="-1">
                                                                    <tr class="el-table__row"
                                                                        v-for="(item, key, index) in goodsSkuData"
                                                                        :key="key">
                                                                        <template v-for="(c, cIndex) in specData" :key="cIndex">
                                                                            <td class="el-table__cell" :rowspan="c.rowSpan"
                                                                                v-if="c.index == index">
                                                                                <div class="cell">{{
                                                                                    c.spec_value_name
                                                                                }}
                                                                                </div>
                                                                            </td>
                                                                        </template>

                                                                        <td class="el-table__cell">
                                                                            <div class="cell">
                                                                                <upload-image v-model="item.sku_image"
                                                                                    :limit="1" width="50px" height="50px" />
                                                                            </div>
                                                                        </td>

                                                                        <td class="el-table__cell">
                                                                            <div class="cell">
                                                                                <el-form-item :prop="key + '.price'" :rules="[{
                                                                                    trigger: 'blur',
                                                                                    validator: (rule: any, value: any, callback: any) => {
                                                                                        if (formData.spec_type == 'multi') {
                                                                                            if (value.length == 0) {
                                                                                                callback(t('pricePlaceholder'))
                                                                                            } else if (isNaN(value) || !regExp.digit.test(value)) {
                                                                                                callback(t('priceTips'))
                                                                                            } else if (value < 0) {
                                                                                                callback(t('priceNotZeroTips'))
                                                                                            } else {
                                                                                                callback();
                                                                                            }
                                                                                        } else {
                                                                                            callback();
                                                                                        }

                                                                                    }
                                                                                }]" class="sku-form-item-wrap">
                                                                                    <el-input v-model="item.price" clearable
                                                                                        placeholder="0.00" maxlength="10"
                                                                                        type="number" />
                                                                                </el-form-item>
                                                                            </div>
                                                                        </td>
                                                                        <td class="el-table__cell">
                                                                            <div class="cell">
                                                                                <el-form-item :prop="key + '.market_price'"
                                                                                    :rules="[{
                                                                                        trigger: 'blur',
                                                                                        validator: (rule: any, value: any, callback: any) => {
                                                                                            if (formData.spec_type == 'multi') {
                                                                                                if (isNaN(value) || !regExp.digit.test(value)) {
                                                                                                    callback(t('marketPriceTips'))
                                                                                                } else if (value < 0) {
                                                                                                    callback(t('marketPriceNotZeroTips'))
                                                                                                } else {
                                                                                                    callback();
                                                                                                }
                                                                                            } else {
                                                                                                callback();
                                                                                            }
                                                                                        }
                                                                                    }]" class="sku-form-item-wrap">
                                                                                    <el-input v-model="item.market_price"
                                                                                        clearable placeholder="0.00"
                                                                                        maxlength="10" type="number" />
                                                                                </el-form-item>
                                                                            </div>
                                                                        </td>
                                                                        <td class="el-table__cell">
                                                                            <div class="cell">
                                                                                <el-form-item :prop="key + '.cost_price'"
                                                                                    :rules="[{
                                                                                        trigger: 'blur',
                                                                                        validator: (rule: any, value: any, callback: any) => {
                                                                                            if (formData.spec_type == 'multi') {
                                                                                                if (isNaN(value) || !regExp.digit.test(value)) {
                                                                                                    callback(t('costPriceTips'))
                                                                                                } else if (value < 0) {
                                                                                                    callback(t('costPriceNotZeroTips'))
                                                                                                } else {
                                                                                                    callback();
                                                                                                }
                                                                                            } else {
                                                                                                callback();
                                                                                            }
                                                                                        }
                                                                                    }]" class="sku-form-item-wrap">
                                                                                    <el-input v-model="item.cost_price"
                                                                                        clearable placeholder="0.00"
                                                                                        maxlength="10" type="number" />
                                                                                </el-form-item>
                                                                            </div>
                                                                        </td>
                                                                        <td class="el-table__cell">
                                                                            <div class="cell">
                                                                                <el-form-item :prop="key + '.stock'" :rules="[{
                                                                                    trigger: 'blur',
                                                                                    validator: (rule: any, value: any, callback: any) => {
                                                                                        if (formData.spec_type == 'multi') {
                                                                                            if (value.length == 0) {
                                                                                                callback(t('stockPlaceholder'))
                                                                                            } else if (isNaN(value) || !regExp.number.test(value)) {
                                                                                                callback(t('stockTips'))
                                                                                            } else if (value < 0) {
                                                                                                callback(t('stockNotZeroTips'))
                                                                                            } else {
                                                                                                callback();
                                                                                            }

                                                                                        } else {
                                                                                            callback();
                                                                                        }

                                                                                    }
                                                                                }]" class="sku-form-item-wrap">
                                                                                    <el-input v-model="item.stock" clearable
                                                                                        placeholder="0"
                                                                                        @input="specStockSum" maxlength="10"
                                                                                        type="number" />
                                                                                </el-form-item>
                                                                            </div>
                                                                        </td>
                                                                        <td class="el-table__cell">
                                                                            <div class="cell">
                                                                                <el-form-item :prop="key + '.weight'"
                                                                                    :rules="[{
                                                                                        trigger: 'blur',
                                                                                        validator: (rule: any, value: any, callback: any) => {
                                                                                            if (formData.spec_type == 'multi') {
                                                                                                if (value.length > 0 && (isNaN(value) || !regExp.special.test(value))) {
                                                                                                    callback(t('weightTips'))
                                                                                                } else if (value < 0) {
                                                                                                    callback(t('weightNotZeroTips'))
                                                                                                } else {
                                                                                                    callback();
                                                                                                }
                                                                                            } else {
                                                                                                callback();
                                                                                            }
                                                                                        }
                                                                                    }]" class="sku-form-item-wrap">
                                                                                    <el-input v-model="item.weight"
                                                                                        clearable placeholder="0.000"
                                                                                        maxlength="20" type="number" />
                                                                                </el-form-item>
                                                                            </div>
                                                                        </td>
                                                                        <td class="el-table__cell">
                                                                            <div class="cell">
                                                                                <el-form-item :prop="key + '.volume'" :rules="[{
                                                                                    trigger: 'blur',
                                                                                    validator: (rule: any, value: any, callback: any) => {
                                                                                        if (formData.spec_type == 'multi') {
                                                                                            if (value.length > 0 && (isNaN(value) || !regExp.special.test(value))) {
                                                                                                callback(t('volumeTips'))
                                                                                            } else if (value < 0) {
                                                                                                callback(t('volumeNotZeroTips'))
                                                                                            } else {
                                                                                                callback();
                                                                                            }
                                                                                        } else {
                                                                                            callback();
                                                                                        }
                                                                                    }
                                                                                }]" class="sku-form-item-wrap">
                                                                                    <el-input v-model="item.volume"
                                                                                        clearable placeholder="0.000"
                                                                                        maxlength="20" type="number" />
                                                                                </el-form-item>
                                                                            </div>
                                                                        </td>
                                                                        <td class="el-table__cell">
                                                                            <div class="cell">
                                                                                <el-form-item class="sku-form-item-wrap">
                                                                                    <el-input v-model="item.sku_no"
                                                                                        clearable maxlength="50" />
                                                                                </el-form-item>
                                                                            </div>
                                                                        </td>
                                                                        <td class="el-table__cell">
                                                                            <div class="cell">
                                                                                <el-switch v-model="item.is_default"
                                                                                    :active-value="1" :inactive-value="0"
                                                                                    @change="specValueIsDefaultChangeListener($event, key)" />
                                                                            </div>
                                                                        </td>
                                                                    </tr>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                </div>

                            </div>
                        </div>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane :label="t('deliveryTab')" name="delivery">

                    <el-form :model="formData" label-width="120px" ref="deliveryFormRef" :rules="formRules"
                        class="page-form">

                        <el-form-item :label="t('deliveryType')" prop="delivery_type">
                            <el-checkbox-group v-model="formData.delivery_type">
                                <el-checkbox v-for="(item,index) in deliveryTypeCheckBox" :key="index" :label="item.key">{{ item.name }}
                                </el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>

                        <el-form-item :label="t('isFreeShipping')" v-show="formData.delivery_type.indexOf('express') != -1">
                            <el-switch v-model="formData.is_free_shipping" :active-value="1" :inactive-value="0" />
                        </el-form-item>

                        <el-form-item :label="t('feeType')" prop="fee_type"
                            v-show="formData.delivery_type.indexOf('express') != -1 && formData.is_free_shipping == 0">
                            <el-radio-group v-model="formData.fee_type">
                                <el-radio label="template">{{ t('selectTemplate') }}</el-radio>
                                <el-radio label="fixed">{{ t('fixedShipping') }}</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item :label="t('deliveryMoney')" prop="delivery_money"
                            v-show="formData.delivery_type.indexOf('express') != -1 && formData.is_free_shipping == 0 && formData.fee_type == 'fixed'">
                            <el-input v-model="formData.delivery_money" clearable
                                :placeholder="t('deliveryMoneyPlaceholder')" maxlength="10" class="input-width" />
                        </el-form-item>
                        <el-form-item :label="t('deliveryTemplateId')" prop="delivery_template_id"
                            v-show="formData.delivery_type.indexOf('express') != -1 && formData.is_free_shipping == 0 && formData.fee_type == 'template'">
                            <el-select v-model="formData.delivery_template_id"
                                :placeholder="t('deliveryTemplateIdPlaceholder')" clearable>
                                <el-option v-for="item in deliveryTemplateOptions" :key="item.template_id"
                                    :label="item.template_name" :value="item.template_id" />
                            </el-select>
                            <div class="ml-[10px]">
                                <span class="cursor-pointer text-primary mr-[10px]" @click="refreshDeliveryTemplate">{{
                                    t('refresh') }}</span>
                                <span class="cursor-pointer text-primary" @click="toDeliveryTemplateEvent">{{
                                    t('addDeliveryTemplateId') }}</span>
                            </div>
                        </el-form-item>
                    </el-form>

                </el-tab-pane>
                <el-tab-pane :label="t('goodsDesc')" name="detail">
                    <el-form :model="formData" label-width="120px" ref="detailFormRef" :rules="formRules" class="page-form">
                        <el-form-item :label="t('goodsDesc')" prop="goods_desc">
                            <editor v-model="formData.goods_desc" height="600px" class="editor-width" />
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
            </el-tabs>

        </el-card>
        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" @click="save()">{{ t('save') }}</el-button>
                <el-button @click="back()">{{ t('back') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, computed, nextTick } from 'vue'
import { t } from '@/lang'
import { TabsPaneContext, ElMessage, FormInstance } from 'element-plus'
import { Rank } from '@element-plus/icons-vue'
import Sortable from 'sortablejs'
import { range, cloneDeep } from 'lodash-es'
import { debounce } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import Test from '@/utils/test'

import {
    getGoodsType,
    getBrandList,
    getLabelList,
    getServeList,
    getSupplierList,
    addGoods,
    editGoods,
    getGoodsInit,
    getCategoryTree
} from '@/addon/shop/api/goods'

import {
    getShopDeliveryList,
    getShippingTemplateList
} from '@/addon/shop/api/delivery'

const route = useRoute()
const router = useRouter()
// const pageName = route.meta.title
const repeat = ref(false)

// 表单数据
const initialFormData = {
    goods_type: 'real',
    goods_id: '',

    // 基础信息
    goods_name: '',
    sub_title: '',
    goods_image: '',
    goods_category: '',
    brand_id: '',
    label_ids: [],
    service_ids: [],
    supplier_id: '',
    status: '1',
    sort: '',

    addon_shop_supplier: [],

    // 价格库存
    spec_type: 'single',
    price: '',
    market_price: '',
    cost_price: '',
    weight: '',
    volume: '',
    stock: '',
    sku_no: '',
    unit: '',
    virtual_sale_num: '',

    // 配送设置
    delivery_type: [],

    is_free_shipping: 1,
    fee_type: 'template',
    delivery_money: '',
    delivery_template_id: '',

    // 商品详情
    goods_desc: ''
}

const formData: Record<string, any> = reactive({ ...initialFormData })

formData.goods_id = ref(route.query.goods_id)

const basicFormRef = ref<FormInstance>()
const priceStockFormRef = ref<FormInstance>()
const deliveryFormRef = ref<FormInstance>()
const detailFormRef = ref<FormInstance>()
const skuFormRef = ref<FormInstance>()

const activeName = ref('basic')
const tabHandleClick = (tab: TabsPaneContext, event: Event) => {
    // console.log(tab, event)
}

// 商品类型
const goodsType = reactive([])

// 切换商品类型
const changeGoodsType = (data: any) => {
    router.push(data.path)
}

// 商品分类
const goodsCategoryOptions = reactive([])
const goodsCategoryProps = {
    multiple: true
}

const categoryHandleChange = (value: any) => {
    // console.log(value, formData.goods_category, formData.goods_category.toString())
}

// 品牌列表下拉框
const brandOptions = reactive([])

// 标签组列表复选框
const labelOptions = reactive([])

// 商品服务列表复选框
const serviceOptions = reactive([])

// 供应商列表下拉框
const supplierOptions = reactive([])

// 配送方式复选框
const deliveryTypeCheckBox = reactive([])

// 运费模板下拉框
const deliveryTemplateOptions = reactive([])

const goodsSpecFormat: any = reactive([]) // 规格项/规格值
const goodsSkuData: any = reactive({})// 商品SKU规格数据
const specData: any = reactive([]) // 商品规格值

// 商品类型
getGoodsType().then((res) => {
    const data = res.data
    if (data) {
        for (const k in data) {
            goodsType.push(data[k])
        }
    }
})

// 跳转到商品分类，添加分类
const toGoodsCategoryEvent = () => {
    const url = router.resolve({
        path: '/shop/goods/category'
    })
    window.open(url.href)
}

// 刷新商品分类
const refreshGoodsCategory = () => {
    getCategoryTree().then((res) => {
        const data = res.data
        if (data) {
            const goodsCategoryTree: any = []
            data.forEach((item: any) => {
                const children: any = []
                if (item.child_list) {
                    item.child_list.forEach((childItem: any) => {
                        children.push({
                            value: childItem.category_id,
                            label: childItem.category_name
                        })
                    })
                }
                goodsCategoryTree.push({
                    value: item.category_id,
                    label: item.category_name,
                    children
                })
            })
            goodsCategoryOptions.splice(0, goodsCategoryOptions.length, ...goodsCategoryTree)
        }
    })
}

refreshGoodsCategory()

// 跳转到商品品牌，添加品牌
const toGoodsBrandEvent = () => {
    const url = router.resolve({
        path: '/shop/goods/brand'
    })
    window.open(url.href)
}

// 商品品牌
const refreshGoodsBrand = () => {
    getBrandList({}).then((res) => {
        const data = res.data
        if (data) {
            brandOptions.splice(0, brandOptions.length, ...data)
        }
    })
}

refreshGoodsBrand()

// 跳转到商品标签，添加标签
const toGoodsLabelEvent = () => {
    const url = router.resolve({
        path: '/shop/goods/label'
    })
    window.open(url.href)
}

// 商品标签
const refreshGoodsLabel = () => {
    getLabelList({}).then((res) => {
        const data = res.data
        if (data) {
            labelOptions.splice(0, labelOptions.length, ...data)
        }
    })
}

refreshGoodsLabel()

// 跳转到商品服务，添加服务
const toGoodsServiceEvent = () => {
    const url = router.resolve({
        path: '/shop/goods/service'
    })
    window.open(url.href)
}

// 商品服务
const refreshGoodsService = () => {
    getServeList({}).then((res) => {
        const data = res.data
        if (data) {
            serviceOptions.splice(0, serviceOptions.length, ...data)
        }
    })
}

refreshGoodsService()

// 跳转到供应商
const toSupplierEvent = () => {
    const url = router.resolve({
        path: '/shop_supplier/supplier'
    })
    window.open(url.href)
}

// 供应商
const refreshSupplier = () => {
    getSupplierList({}).then((res) => {
        const data = res.data
        if (data) {
            supplierOptions.splice(0, supplierOptions.length, ...data)
        }
    })
}

// 配送方式
getShopDeliveryList().then((res) => {
    const data = res.data
    if (data) {
        deliveryTypeCheckBox.splice(0, deliveryTypeCheckBox.length, ...data)
    }
})

// 跳转到运费模板
const toDeliveryTemplateEvent = () => {
    const url = router.resolve({
        path: '/shop/order/shipping/template'
    })
    window.open(url.href)
}

// 运费模板
const refreshDeliveryTemplate = () => {
    getShippingTemplateList({}).then((res) => {
        const data = res.data
        if (data) {
            deliveryTemplateOptions.splice(0, deliveryTemplateOptions.length, ...data)
        }
    })
}

refreshDeliveryTemplate()

// 加载初始化数据
getGoodsInit({
    goods_id: formData.goods_id
}).then((res) => {
    const data = res.data
    if (data) {
        formData.addon_shop_supplier = data.addon_shop_supplier
        if (formData.addon_shop_supplier && formData.addon_shop_supplier.status == 1) {
            refreshSupplier()
        }

        if (formData.goods_id && data.goods_info) {
            // 基础信息
            formData.goods_name = data.goods_info.goods_name
            formData.sub_title = data.goods_info.sub_title
            formData.goods_type = data.goods_info.goods_type
            formData.goods_image = data.goods_info.goods_image
            formData.goods_category = data.goods_info.goods_category
            formData.brand_id = data.goods_info.brand_id
            formData.label_ids = data.goods_info.label_ids
            formData.service_ids = data.goods_info.service_ids
            formData.supplier_id = data.goods_info.supplier_id
            formData.status = data.goods_info.status
            formData.sort = data.goods_info.sort

            // 价格库存
            formData.spec_type = data.goods_info.spec_type
            formData.stock = data.goods_info.stock

            if (formData.spec_type == 'single') {
                // 单规格
                const skuInfo = data.goods_info.sku_list[0]
                formData.price = skuInfo.price
                formData.market_price = skuInfo.market_price
                formData.cost_price = skuInfo.cost_price
                formData.weight = skuInfo.weight
                formData.volume = skuInfo.volume
                formData.sku_no = skuInfo.sku_no
            } else if (formData.spec_type == 'multi') {
                // 多规格
                const specList = data.goods_info.spec_list
                specList.forEach((item: any) => {
                    const values: any = []
                    item.spec_values = item.spec_values.split(',')
                    item.spec_values.forEach((v: any) => {
                        values.push({
                            id: generateRandom(),
                            spec_value_name: v
                        })
                    })
                    goodsSpecFormat.push({
                        id: generateRandom(),
                        spec_id: item.spec_id,
                        goods_id: item.goods_id,
                        spec_name: item.spec_name,
                        values
                    })
                })

                const skuList = data.goods_info.sku_list
                skuList.forEach((item: any) => {
                    const skuSpec: any = []
                    goodsSpecFormat.forEach((specItem: any, index: any) => {
                        specItem.values.forEach((v: any) => {
                            if (v.spec_value_name == item.sku_spec_format.split(',')[index]) {
                                skuSpec.push({
                                    id: v.id,
                                    spec_value_name: v.spec_value_name
                                })
                            }
                        })
                    })
                    goodsSkuData[generateRandom()] = {
                        sku_id: item.sku_id,
                        spec_name: item.sku_spec_format.replace(/,/g, ' '),
                        sku_spec: skuSpec,
                        sku_image: item.sku_image,
                        price: item.price,
                        market_price: item.market_price,
                        cost_price: item.cost_price,
                        weight: item.weight,
                        volume: item.volume,
                        stock: item.stock,
                        sku_no: item.sku_no,
                        is_default: item.is_default
                    }
                })

                nextTick(() => {
                    refreshSkuTable()
                    bindSpecValue()
                })
            }

            formData.unit = data.goods_info.unit
            formData.virtual_sale_num = data.goods_info.virtual_sale_num

            // 配送设置
            formData.delivery_type = data.goods_info.delivery_type
            formData.is_free_shipping = data.goods_info.is_free_shipping
            formData.fee_type = data.goods_info.fee_type
            formData.delivery_money = data.goods_info.delivery_money
            formData.delivery_template_id = data.goods_info.delivery_template_id

            // 商品详情
            formData.goods_desc = data.goods_info.goods_desc
        }
    }
})

// 拖拽规格值
const specValueRef = ref()

// 绑定拖拽规格值事件
const bindSpecValue = () => {
    if (!specValueRef.value) return

    for (let i = 0; i < specValueRef.value.length; i++) {
        const item = specValueRef.value[i]
        const sortable = Sortable.create(item, {
            group: 'draggable-element-' + i,
            animation: 200,
            // 结束拖拽
            onEnd: event => {
                const temp = goodsSpecFormat[i].values[event.oldIndex!]
                goodsSpecFormat[i].values.splice(event.oldIndex!, 1)
                goodsSpecFormat[i].values.splice(event.newIndex!, 0, temp)

                nextTick(() => {
                    sortable.sort(
                        range(goodsSpecFormat[i].values.length).map(value => {
                            return value.toString()
                        })
                    )

                    // 渲染商品规格数据、表格
                    refreshGoodsSkuData()
                    refreshSkuTable()
                })
            }
        })
    }
}

// 生成随机数
const generateRandom = (len: number = 5) => {
    return Number(Math.random().toString().substr(3, len) + Date.now()).toString(36)
}

// 添加规格项
const addSpec = () => {
    if (goodsSpecFormat.length > 4) {
        ElMessage({
            type: 'warning',
            message: `${t('maxAddSpecTips')}`
        })
        return
    }
    goodsSpecFormat.push({
        id: generateRandom(),
        spec_name: '',
        values: [
            {
                id: generateRandom(),
                spec_value_name: ''
            }
        ]
    })
}

// 删除规格项
const deleteSpec = (index: number) => {
    goodsSpecFormat.splice(index, 1)
    // 渲染商品规格数据、表格、统计库存变化
    refreshGoodsSkuData()
    refreshSkuTable()
    specStockSum()
}

// 添加规格值
const addSpecValue = (index: number) => {
    goodsSpecFormat[index].values.push({
        id: generateRandom(),
        spec_value_name: ''
    })
    bindSpecValue()
}

// 监听规格值变化
const specValueNameInputListener = debounce((value) => {
    // 渲染商品规格数据、表格
    refreshGoodsSkuData()
    refreshSkuTable()
})

// 删除规格值
const deleteSpecValue = (index: number, specIndex: number) => {
    goodsSpecFormat[index].values.splice(specIndex, 1)
    // 渲染商品规格数据、表格、统计库存变化
    refreshGoodsSkuData()
    refreshSkuTable()
    specStockSum()
}

// 设置默认规格
const specValueIsDefaultChangeListener = (value: any, key: any) => {
    for (const k in goodsSkuData) {
        if (k == key) {
            goodsSkuData[k].is_default = value
        } else {
            goodsSkuData[k].is_default = 0
        }
    }
}

// 监听规格库存变化，统计总库存
const specStockSum = debounce(() => {
    let stock = 0
    for (const k in goodsSkuData) {
        if (goodsSkuData[k].stock) stock += parseInt(goodsSkuData[k].stock)
    }
    formData.stock = stock
})

// 刷新商品规格数据
const refreshGoodsSkuData = () => {
    const arr = goodsSpecFormat
    const tempGoodsSkuData = cloneDeep(goodsSkuData)// 记录原始数据，后续用作对比
    let skuData: any = {}
    for (const spec of arr) {
        let item_prop_arr: any = {}
        if (Object.keys(skuData).length > 0) {
            for (const ele_2 in skuData) {
                for (let ele_3 of spec.values) {
                    let sku_spec = cloneDeep(skuData[ele_2].sku_spec)// 防止对象引用
                    sku_spec.push(ele_3)
                    item_prop_arr[generateRandom()] = {
                        spec_name: `${skuData[ele_2].spec_name} ${ele_3.spec_value_name}`,
                        sku_spec,
                        sku_image: '',
                        price: '',
                        market_price: '',
                        cost_price: '',
                        weight: '',
                        volume: '',
                        stock: '',
                        sku_no: '',
                        is_default: 0
                    }
                }
            }
        } else {
            for (let ele_1 of spec.values) {
                let spec_name = ele_1.spec_value_name
                item_prop_arr[generateRandom()] = {
                    spec_name: spec_name,
                    sku_spec: [ele_1],
                    sku_image: '',
                    price: '',
                    market_price: '',
                    cost_price: '',
                    weight: '',
                    volume: '',
                    stock: '',
                    sku_no: '',
                    is_default: 0
                }
            }
        }

        skuData = Object.keys(item_prop_arr).length > 0 ? item_prop_arr : skuData
    }

    // 比对已存在的规格项/值，并且赋值
    for (const tempKey in tempGoodsSkuData) {
        for (const key in skuData) {
            const count = matchSkuSpecCount(tempGoodsSkuData[tempKey].sku_spec, skuData[key].sku_spec)
            if (count === skuData[key].sku_spec.length) {
                // 匹配成功后，要同步最新的规格项名称、规格值集合
                const specName = skuData[key].spec_name
                const skuSpec = skuData[key].sku_spec
                Object.assign(skuData[key], tempGoodsSkuData[tempKey])
                skuData[key].spec_name = specName
                skuData[key].sku_spec = skuSpec
                break
            }
        }
    }

    for (const item in goodsSkuData) {
        delete goodsSkuData[item]
    }

    let firstSpec = ''

    for (const key in skuData) {
        if (firstSpec == '') {
            firstSpec = key
            skuData[key].is_default = 1
        }
        goodsSkuData[key] = skuData[key]
    }
}

// 匹配规格值
const matchSkuSpecCount = (oVal: any, nVal: any) => {
    let count = 0// 匹配次数，与规格值相等时为匹配成功
    for (let i = 0; i < oVal.length; i++) {
        for (let j = 0; j < nVal.length; j++) {
            if (oVal[i].id === nVal[j].id) {
                count++
                break
            }
        }
    }
    return count
}

// 刷新商品规格表格
const refreshSkuTable = () => {
    let length = 0
    // 统计有效规格数量
    for (let i = 0; i < goodsSpecFormat.length; i++) {
        if (goodsSpecFormat[i].spec_name != '' && goodsSpecFormat[i].values.length > 0) {
            length++
        }
    }

    let row = 1 // 跨行
    const tempSpecData = []

    // 规格值单元格合并
    for (let i = length - 1; i >= 0; i--) {
        for (let k = 0; k < Object.keys(goodsSkuData).length;) {
            if (goodsSpecFormat[i].values.length > 0) {
                for (const ele of goodsSpecFormat[i].values) {
                    tempSpecData.push({
                        index: k,
                        colSpan: i,
                        rowSpan: row,
                        spec_value_name: ele.spec_value_name
                    })
                    k = k + row
                }
            } else {
                k++
            }
        }
        row = row * goodsSpecFormat[i].values.length
    }

    tempSpecData.reverse()
    specData.splice(0, specData.length, ...tempSpecData)
}

const batchOperation: any = reactive({
    spec: '', // 所选规格id，空为全部
    price: '', // 销售价
    market_price: '', // 划线价
    cost_price: '', // 成本价
    stock: '', // 库存
    weight: '', // 重量(kg)
    volume: '', // 体积(m³)
    sku_no: '' // 商品编码
})

// 批量设置确认
const saveBatch = () => {
    // 验证输入内容
    if (batchOperation.price && (isNaN(batchOperation.price) || !regExp.digit.test(batchOperation.price))) {
        ElMessage({
            type: 'warning',
            message: `${t('priceTips')}`
        })
        return
    }
    if (batchOperation.market_price && (isNaN(batchOperation.market_price) || !regExp.digit.test(batchOperation.market_price))) {
        ElMessage({
            type: 'warning',
            message: `${t('marketPriceTips')}`
        })
        return
    }
    if (batchOperation.cost_price && (isNaN(batchOperation.cost_price) || !regExp.digit.test(batchOperation.cost_price))) {
        ElMessage({
            type: 'warning',
            message: `${t('costPriceTips')}`
        })
        return
    }
    if (batchOperation.stock && (isNaN(batchOperation.stock) || !regExp.number.test(batchOperation.stock))) {
        ElMessage({
            type: 'warning',
            message: `${t('stockTips')}`
        })
        return
    }
    if (batchOperation.weight && (isNaN(batchOperation.weight) || !regExp.special.test(batchOperation.stock))) {
        ElMessage({
            type: 'warning',
            message: `${t('weightTips')}`
        })
        return
    }
    if (batchOperation.volume && (isNaN(batchOperation.volume) || !regExp.special.test(batchOperation.volume))) {
        ElMessage({
            type: 'warning',
            message: `${t('volumeTips')}`
        })
        return
    }

    if (batchOperation.spec) {
        // 设置指定规格
        if (batchOperation.price) goodsSkuData[batchOperation.spec].price = batchOperation.price
        if (batchOperation.market_price) goodsSkuData[batchOperation.spec].market_price = batchOperation.market_price
        if (batchOperation.cost_price) goodsSkuData[batchOperation.spec].cost_price = batchOperation.cost_price
        if (batchOperation.stock) goodsSkuData[batchOperation.spec].stock = batchOperation.stock
        if (batchOperation.weight) goodsSkuData[batchOperation.spec].weight = batchOperation.weight
        if (batchOperation.volume) goodsSkuData[batchOperation.spec].volume = batchOperation.volume
        if (batchOperation.sku_no) goodsSkuData[batchOperation.spec].sku_no = batchOperation.sku_no
    } else {
        // 设置全部规格
        for (const key in goodsSkuData) {
            if (batchOperation.price) goodsSkuData[key].price = batchOperation.price
            if (batchOperation.market_price) goodsSkuData[key].market_price = batchOperation.market_price
            if (batchOperation.cost_price) goodsSkuData[key].cost_price = batchOperation.cost_price
            if (batchOperation.stock) goodsSkuData[key].stock = batchOperation.stock
            if (batchOperation.weight) goodsSkuData[key].weight = batchOperation.weight
            if (batchOperation.volume) goodsSkuData[key].volume = batchOperation.volume
            if (batchOperation.sku_no) goodsSkuData[key].sku_no = batchOperation.sku_no
        }
    }

    // 保存完清空
    batchOperation.price = ''
    batchOperation.market_price = ''
    batchOperation.cost_price = ''
    batchOperation.stock = ''
    batchOperation.weight = ''
    batchOperation.volume = ''
    batchOperation.sku_no = ''
}

// 正则表达式
const regExp = {
    required: /[\S]+/,
    number: /^\d{0,10}$/,
    digit: /^\d{0,10}(.?\d{0,2})$/,
    special: /^\d{0,10}(.?\d{0,3})$/
}

// 表单验证规则
const formRules = computed(() => {
    return {
        goods_name: [
            {
                required: true,
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (value === '') {
                        callback(new Error(t('goodsNamePlaceholder')))
                    }
                    if (value.length > 60) {
                        callback(new Error(t('goodsNameMaxLengthTips')))
                    } else {
                        callback()
                    }
                }
            }
        ],
        sub_title: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (value.length > 80) {
                        callback(new Error(t('subTitleMaxLengthTips')))
                    } else {
                        callback()
                    }
                }
            }
        ],
        goods_image: [
            { required: true, message: t('goodsImagePlaceholder'), trigger: 'blur' }
        ],
        goods_category: [
            { required: true, message: t('goodsCategoryPlaceholder'), trigger: 'blur' }
        ],
        sort: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (isNaN(value) || !regExp.number.test(value)) {
                        callback(new Error(t('sortTips')))
                    } else {
                        callback()
                    }
                }
            }
        ],
        // unit: [
        //     {required: true, message: t('unitPlaceholder'), trigger: 'blur'}
        // ],
        // 单规格
        price: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (formData.spec_type == 'single') {
                        if (value === '') {
                            callback(new Error(t('pricePlaceholder')))
                        } else if (isNaN(value) || !regExp.digit.test(value)) {
                            callback(new Error(t('priceTips')))
                        } else if (value < 0) {
                            callback(new Error(t('priceNotZeroTips')))
                        } else {
                            callback()
                        }
                    } else {
                        callback()
                    }
                }
            }
        ],
        market_price: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (formData.spec_type == 'single') {
                        if (isNaN(value) || !regExp.digit.test(value)) {
                            callback(new Error(t('marketPriceTips')))
                        } else if (value < 0) {
                            callback(new Error(t('marketPriceNotZeroTips')))
                        } else {
                            callback()
                        }
                    } else {
                        callback()
                    }
                }
            }
        ],
        cost_price: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (formData.spec_type == 'single') {
                        if (isNaN(value) || !regExp.digit.test(value)) {
                            callback(new Error(t('costPriceTips')))
                        } else if (value < 0) {
                            callback(new Error(t('costPriceNotZeroTips')))
                        } else {
                            callback()
                        }
                    } else {
                        callback()
                    }
                }
            }
        ],
        weight: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (formData.spec_type == 'single') {
                        if (isNaN(value) || !regExp.special.test(value)) {
                            callback(new Error(t('weightTips')))
                        } else if (value < 0) {
                            callback(new Error(t('weightNotZeroTips')))
                        } else {
                            callback()
                        }
                    } else {
                        callback()
                    }
                }
            }
        ],
        volume: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (formData.spec_type == 'single') {
                        if (isNaN(value) || !regExp.special.test(value)) {
                            callback(new Error(t('volumeTips')))
                        } else if (value < 0) {
                            callback(new Error(t('volumeNotZeroTips')))
                        } else {
                            callback()
                        }
                    } else {
                        callback()
                    }
                }
            }
        ],
        stock: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (formData.spec_type == 'single') {
                        if (value === '') {
                            callback(new Error(t('stockPlaceholder')))
                        } else if (isNaN(value) || !regExp.number.test(value)) {
                            callback(new Error(t('stockTips')))
                        } else if (value < 0) {
                            callback(new Error(t('stockNotZeroTips')))
                        } else {
                            callback()
                        }
                    } else {
                        callback()
                    }
                }
            }
        ],
        virtual_sale_num: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (formData.spec_type == 'single') {
                        if (isNaN(value) || !regExp.number.test(value)) {
                            callback(new Error(t('virtualSaleNumTips')))
                        } else if (value < 0) {
                            callback(new Error(t('virtualSaleNumNotZeroTips')))
                        } else {
                            callback()
                        }
                    } else {
                        callback()
                    }
                }
            }
        ],
        // 多规格
        spec_type: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (formData.spec_type == 'multi') {
                        if (Object.keys(goodsSkuData).length == 0) {
                            callback(new Error(t('pleaseEditSpecPlaceholder')))
                        }
                    }
                    callback()
                }
            }
        ],
        delivery_type: [
            { required: true, message: t('deliveryTypePlaceholder'), trigger: 'blur' }
        ],
        delivery_money: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (formData.delivery_type.indexOf('express') != -1 && formData.is_free_shipping == 0 && formData.fee_type == 'fixed') {
                        if (formData.delivery_template_id.length == 0 && value === '') {
                            callback(new Error(t('deliveryMoneyPlaceholder')))
                        } else if (isNaN(value) || !regExp.digit.test(value)) {
                            callback(new Error(t('deliveryMoneyTips')))
                        } else if (value < 0) {
                            callback(new Error(t('deliveryMoneyNotZeroTips')))
                        } else {
                            callback()
                        }
                    } else {
                        callback()
                    }
                }
            }
        ],
        delivery_template_id: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (formData.delivery_type.indexOf('express') != -1 && formData.is_free_shipping == 0 && formData.fee_type == 'template') {
                        if (formData.delivery_money.length == 0 && value === '') {
                            callback(new Error(t('deliveryTemplateIdPlaceholder')))
                        } else {
                            callback()
                        }
                    } else {
                        callback()
                    }
                }
            }
        ],
        goods_desc: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (value === '') {
                        callback(new Error(t('goodsDescPlaceholder')))
                    } else if (value.length < 5 || value.length > 50000) {
                        callback(new Error(t('goodsDescMaxTips')))
                        return false
                    } else {
                        callback()
                    }
                }
            }
        ]
    }
})

const verify = (callback: any) => {
    const formRef = [
        {
            key: 'basic',
            verify: false,
            ref: basicFormRef.value
        },
        {
            key: 'price_stock',
            verify: false,
            ref: priceStockFormRef.value
        },
        {
            key: 'delivery',
            verify: false,
            ref: deliveryFormRef.value
        },
        {
            key: 'detail',
            verify: false,
            ref: detailFormRef.value
        },
        {
            key: 'price_stock',
            verify: false,
            ref: skuFormRef.value
        }
    ]
    formRef.forEach((el: any, index) => {
        el.ref.validate((valid: any) => {
            el.verify = valid
        })
    })

    if (formData.spec_type == 'multi') {
        let specVerify = true
        for (let i = 0; i < goodsSpecFormat.length; i++) {
            const spec = goodsSpecFormat[i]
            if (Test.require(spec.spec_name)) {
                specVerify = false
                ElMessage({ type: 'warning', message: `${t('specNameRequire')}` })
                break
            }
            for (let v = 0; v < spec.values.length; v++) {
                const value = spec.values[v]
                if (Test.require(value.spec_value_name)) {
                    specVerify = false
                    ElMessage({ type: 'warning', message: `${t('specValueRequire')}` })
                    break
                }
            }
            if (!specVerify) break
        }
        if (!specVerify) {
            activeName.value = 'price_stock'
            return
        }
    }

    setTimeout(() => {
        let verify = true
        // 检测验证，并且定位tab页面
        for (let i = 0; i < formRef.length; i++) {
            if (formRef[i].verify == false) {
                activeName.value = formRef[i].key
                verify = false
                break
            }
        }
        if (verify && callback) callback()
    }, 10)
}

// 保存数据
const save = () => {
    verify(() => {
        if (repeat.value) return
        repeat.value = true

        const api = formData.goods_id ? editGoods : addGoods
        const data = cloneDeep(formData)
        if (formData.spec_type == 'multi') {
            data.stock = 0
            for (const k in goodsSkuData) {
                if (goodsSkuData[k].stock) data.stock += parseInt(goodsSkuData[k].stock)
            }
        }
        const goodsCategory: any = []
        data.goods_category.forEach((item: any) => {
            if (typeof item == 'object') {
                item.forEach((second: any) => {
                    if (goodsCategory.indexOf(second) == -1) {
                        goodsCategory.push(second)
                    }
                })
            } else {
                if (goodsCategory.indexOf(item) == -1) {
                    goodsCategory.push(item)
                }
            }
        })
        data.goods_category = goodsCategory
        data.goods_sku_data = goodsSkuData
        data.goods_spec_format = goodsSpecFormat

        api(data).then(res => {
            repeat.value = false
            router.push('/shop/goods/list')
        // eslint-disable-next-line n/handle-callback-err
        }).catch(err => {
            repeat.value = false
        })
    })
}

const back = () => {
    router.push('/shop/goods/list')
}

</script>

<style lang="scss" scoped>
.goods-type-wrap {
    width: 120px;
    height: 60px;
    background: #fff;
    border-radius: 3px;
    float: left;
    text-align: center;
    padding-top: 8px;
    position: relative;
    cursor: pointer;
    line-height: 23px;
    border: 1px solid #e7e7e7;

    .goods-type-name {
        font-size: 14px;
        font-weight: 600;
        color: rgba(0, 0, 0, .85);
    }

    .goods-type-desc {
        font-size: 12px;
        font-weight: 400;
        color: #999;
    }

    .triangle {
        position: absolute;
        bottom: 0;
        right: 0;
        width: 0;
        height: 0;
        border-bottom: 26px solid var(--el-color-primary);
        border-left: 26px solid transparent;
    }

    .selected {
        position: absolute;
        bottom: -2px;
        right: 2px;
        color: #fff;
    }

    &.selected {
        border: 1px solid var(--el-color-primary);
    }

    &.disabled {
        cursor: not-allowed;

        .goods-type-name {
            color: #999;
        }
    }

    &:nth-child(2n) {
        margin: 0 12px;
    }

}

.spec-wrap {
    flex: 1;

    .spec-edit-list {
        .spec-item {
            background: #f7f7f7;
            padding: 20px;
            margin-bottom: 20px;
            position: relative;
            border-radius: 6px;

            .spec-name-wrap {
                /*margin-bottom: 20px;*/
            }

            .spec-value-wrap {
                padding: 25px 30px 0 30px;
                position: relative;

                ul {

                    display: flex;
                    flex-wrap: wrap;
                    flex: 1;
                    align-items: baseline;

                    li {
                        margin: 0 10px 10px 0;
                        position: relative;

                        .input-width {
                            width: 200px;
                        }

                        .icon {
                            width: 32px;
                            padding: 0;
                            display: none;
                            position: absolute;
                            top: -12px;
                            right: -20px;
                            cursor: pointer;
                        }

                        &:hover {
                            .icon {
                                display: block;
                            }
                        }
                    }

                }

                .add-spec-value {
                    cursor: pointer;
                    user-select: none;
                }

                .box {
                    position: absolute;
                    top: 0;
                    left: 10px;
                    width: 20px;
                    height: 40px;
                    border: 1px solid #b8b9bd;
                    border-top: none;
                    border-right: none;
                }
            }

            .del-spec {
                border: none;
                position: absolute;
                top: 10px;
                right: 10px;
                display: none;
                cursor: pointer;

            }

            &:hover {
                .del-spec {
                    display: block;
                }
            }
        }
    }
}

.el-input__suffix {
    cursor: pointer;
}

.el-table__row:focus {
    outline: none !important;
}

.add-spec {
    margin-bottom: 16px;
}

.batch-operation-sku {
    display: flex;
    margin-bottom: 16px;
    background-color: #ffffff;
    -ms-flex-wrap: nowrap;
    flex-wrap: nowrap;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;

    label {
        font-size: 14px;
        margin-right: 10px;
    }

    .set-spec-select {
        margin-right: 10px;
        max-width: 130px;
    }

    .set-input {
        max-width: 130px;
        min-width: 60px;
        margin-right: 10px;
    }
}

.editor-width {
    width: 990px;
}

.sku-table {
    :focus {
        outline: none;
    }
}

.sku-form-item-wrap :deep(.el-form-item__content) {
    margin-left: 0 !important;
}

.sku-table :deep(.el-table__cell .cell) {
    overflow: initial !important;
}
</style>
