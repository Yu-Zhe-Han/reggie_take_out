package com.yzh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzh.reggie.common.CustomException;
import com.yzh.reggie.dto.SetmealDto;
import com.yzh.reggie.entity.Dish;
import com.yzh.reggie.entity.Setmeal;
import com.yzh.reggie.entity.SetmealDish;
import com.yzh.reggie.mapper.SetmealMapper;
import com.yzh.reggie.service.SetmealDishService;
import com.yzh.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    public void isNotDishStatus(List<Long> ids) {

    }

    /**
     * 新增套餐，同时保存菜单的菜品
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //
        this.save(setmealDto);

        //套餐的菜品
        List<SetmealDish> setmealDishes=setmealDto.getSetmealDishes();

        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
                }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

    }


    /**
     * 删除套餐，同时删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count =this.count(queryWrapper);
        if(count>0){
            throw  new CustomException("套餐正在售卖中，无法删除");
        }
        //如果可以删除，先删除套餐表中的数据----setmeal
        this.removeByIds(ids);
        //删除关系表中的数据---setmeal_dish
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);

    }


    @Override
    public void changeStatus(Integer status, Long ids) {

        if(status==0){
            UpdateWrapper<Setmeal> updateWrapper=new UpdateWrapper<>();
            updateWrapper.eq("id",ids);
            updateWrapper.set("status", 0);
            this.update(null,updateWrapper);
        }

        if(status==1){
            UpdateWrapper<Setmeal> updateWrapper=new UpdateWrapper<>();
            updateWrapper.eq("id",ids);
            updateWrapper.set("status", 1);
            this.update(null,updateWrapper);
        }
    }
}
