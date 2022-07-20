package com.yzh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzh.reggie.dto.DishDto;
import com.yzh.reggie.entity.Dish;
import com.yzh.reggie.entity.DishFlavor;
import com.yzh.reggie.mapper.DishMapper;
import com.yzh.reggie.service.DishFlavorService;
import com.yzh.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional//因为涉及多张表因此需要事务
    public void saveWithFlavor(DishDto dishDto) {
    //保存菜品信息到dish表
        this.save(dishDto);

        Long dishId= dishDto.getId();//菜品id
        //菜品口味
        List<DishFlavor> flavors=dishDto.getFlavors();

        flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

     //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 更新菜单
     * @param dishDto
     */


    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据--dish_flavor表的delete的操作
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id查询菜品和口味信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息从dish表查询
        Dish dish=this.getById(id);

        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询菜品对应口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors= dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 批量停售与起售
     * @param status
     * @param ids
     */
    @Override
    public void changeStatus(Integer status, List<Long>ids) {
        for (int i = 0; i < ids.size(); i++) {
            long id=ids.get(i);
            if (status == 0) {
                UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", id);
                updateWrapper.set("status", 0);
                this.update(null, updateWrapper);

            }

            if (status == 1) {
                UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", id);
                updateWrapper.set("status", 1);
                this.update(null, updateWrapper);
            }
        }
    }

    /**
     * 根据多条id删除菜品
     * @param ids
     */
    @Override
    public void removeDish(List<Long> ids) {
     for(int i=0;i<ids.size();i++){
         long id=ids.get(i);
         this.removeById(id);
     }
    }

}
