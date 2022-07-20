package com.yzh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yzh.reggie.dto.DishDto;
import com.yzh.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor

    public void saveWithFlavor(DishDto dishDto);

    public void updateWithFlavor(DishDto dishDto);

    //根据id查询菜品和口味信息
    public DishDto getByIdWithFlavor(Long id);

    //根据id修改菜品状态
    public void changeStatus(Integer status, List<Long> ids);
    //批量删除菜品
    public void removeDish(List<Long> ids);
}
