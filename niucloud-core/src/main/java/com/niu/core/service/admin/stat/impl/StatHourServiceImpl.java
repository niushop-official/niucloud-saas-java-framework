package com.niu.core.service.admin.stat.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.stat.StatHour;
import com.niu.core.mapper.stat.StatHourMapper;
import com.niu.core.service.admin.stat.param.StatHourParam;
import com.niu.core.service.admin.stat.param.StatHourSearchParam;
import com.niu.core.service.admin.stat.IStatHourService;
import com.niu.core.service.admin.stat.vo.StatHourInfoVo;
import com.niu.core.service.admin.stat.vo.StatHourListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 小时统计实现
 */
@Service
public class StatHourServiceImpl implements IStatHourService {
        
    @Resource
    StatHourMapper statHourMapper;

    /**
     * 小时统计列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<StatHourListVo>
     */
    @Override
    public PageResult<StatHourListVo> list(PageParam pageParam, StatHourSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<StatHour> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<StatHour> iPage = statHourMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<StatHourListVo> list = new LinkedList<>();
        for (StatHour item : iPage.getRecords()) {
            StatHourListVo vo = new StatHourListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit).setData(list);
    }

    /**
     * 小时统计详情
     * @param id 主键
     * @return StatHourInfoVo
     */
    @Override
    public StatHourInfoVo info(Integer id) {
        StatHour model = statHourMapper.selectOne(
                new QueryWrapper<StatHour>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        StatHourInfoVo vo = new StatHourInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 小时统计添加
     * @param addParam 参数
     */
    @Override
    public void add(StatHourParam addParam) {
        StatHour model = new StatHour();
        model.setSiteId(addParam.getSiteId());
        model.setAddon(addParam.getAddon());
        model.setField(addParam.getField());
        model.setFieldTotal(addParam.getFieldTotal());
        model.setYear(addParam.getYear());
        model.setMonth(addParam.getMonth());
        model.setDay(addParam.getDay());
        model.setStartTime(System.currentTimeMillis() / 1000);
        model.setLastTime(addParam.getLastTime());
        model.setHour0(addParam.getHour0());
        model.setHour1(addParam.getHour1());
        model.setHour2(addParam.getHour2());
        model.setHour3(addParam.getHour3());
        model.setHour4(addParam.getHour4());
        model.setHour5(addParam.getHour5());
        model.setHour6(addParam.getHour6());
        model.setHour7(addParam.getHour7());
        model.setHour8(addParam.getHour8());
        model.setHour9(addParam.getHour9());
        model.setHour10(addParam.getHour10());
        model.setHour11(addParam.getHour11());
        model.setHour12(addParam.getHour12());
        model.setHour13(addParam.getHour13());
        model.setHour14(addParam.getHour14());
        model.setHour15(addParam.getHour15());
        model.setHour16(addParam.getHour16());
        model.setHour17(addParam.getHour17());
        model.setHour18(addParam.getHour18());
        model.setHour19(addParam.getHour19());
        model.setHour20(addParam.getHour20());
        model.setHour21(addParam.getHour21());
        model.setHour22(addParam.getHour22());
        model.setHour23(addParam.getHour23());
        statHourMapper.insert(model);
    }

    /**
     * 小时统计编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, StatHourParam editParam) {
        StatHour model = statHourMapper.selectOne(
                new QueryWrapper<StatHour>()
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        model.setSiteId(editParam.getSiteId());
        model.setAddon(editParam.getAddon());
        model.setField(editParam.getField());
        model.setFieldTotal(editParam.getFieldTotal());
        model.setYear(editParam.getYear());
        model.setMonth(editParam.getMonth());
        model.setDay(editParam.getDay());
        model.setLastTime(editParam.getLastTime());
        model.setHour0(editParam.getHour0());
        model.setHour1(editParam.getHour1());
        model.setHour2(editParam.getHour2());
        model.setHour3(editParam.getHour3());
        model.setHour4(editParam.getHour4());
        model.setHour5(editParam.getHour5());
        model.setHour6(editParam.getHour6());
        model.setHour7(editParam.getHour7());
        model.setHour8(editParam.getHour8());
        model.setHour9(editParam.getHour9());
        model.setHour10(editParam.getHour10());
        model.setHour11(editParam.getHour11());
        model.setHour12(editParam.getHour12());
        model.setHour13(editParam.getHour13());
        model.setHour14(editParam.getHour14());
        model.setHour15(editParam.getHour15());
        model.setHour16(editParam.getHour16());
        model.setHour17(editParam.getHour17());
        model.setHour18(editParam.getHour18());
        model.setHour19(editParam.getHour19());
        model.setHour20(editParam.getHour20());
        model.setHour21(editParam.getHour21());
        model.setHour22(editParam.getHour22());
        model.setHour23(editParam.getHour23());
        statHourMapper.updateById(model);
    }

    /**
     * 小时统计删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        StatHour model = statHourMapper.selectOne(
                new QueryWrapper<StatHour>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        statHourMapper.delete(new QueryWrapper<StatHour>().eq("id", id));
    }

}
