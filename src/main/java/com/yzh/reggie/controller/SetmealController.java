package com.yzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yzh.reggie.common.R;
import com.yzh.reggie.dto.DishDto;
import com.yzh.reggie.dto.SetmealDto;
import com.yzh.reggie.entity.Category;
import com.yzh.reggie.entity.Dish;
import com.yzh.reggie.entity.Setmeal;
import com.yzh.reggie.service.CategoryService;
import com.yzh.reggie.service.SetmealDishService;
import com.yzh.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> page(int page , int pageSize, String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构建分页构造器
        Page<Setmeal>pageInfo=new Page(page,pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>();
        //构造器条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper();
        //过滤条件
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");

        List<Setmeal> records=pageInfo.getRecords();

        List<SetmealDto> list=records.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId=item.getCategoryId();//分类id

            Category category = categoryService.getById(categoryId);//根据id查分类对象
            if(category!=null){
                String categoryName=category.getName();

                setmealDto.setCategoryName(categoryName);}

            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);

    }
    @PostMapping
    public R<String>add(@RequestBody SetmealDto setmealDto ){
        log.info("套餐信息：{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return  R.success("新增套餐成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public  R<String> delete(@RequestParam List<Long> ids){
         setmealService.removeWithDish(ids);

        return  R.success("套餐数据删除成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list =setmealService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 起售与停售套餐
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String>changeStatus(@PathVariable Integer status,Long ids){
        log.info("id:",ids);
        log.info("status",status);
        setmealService.changeStatus(status,ids);
        return R.success("修改成功");
    }
}
