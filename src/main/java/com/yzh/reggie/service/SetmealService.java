package com.yzh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yzh.reggie.dto.SetmealDto;
import com.yzh.reggie.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {

    /**
     * 判断套餐中是否有菜品停售了
     * @param ids
     */
    public void isNotDishStatus(List<Long>ids);

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除套餐和菜品的关联数据
     * @param ids
     */
    public void  removeWithDish(List<Long> ids);

    /**
     *  根据id修改套餐状态
     * @param status
     * @param ids
     */
    public void changeStatus(Integer status,Long ids);
}
