package com.yzh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yzh.reggie.common.R;
import com.yzh.reggie.entity.Category;

import com.yzh.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public R<String> save(@RequestBody Category category){

        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page>page(int page , int pageSize){
        log.info("page={},pageSize={}",page,pageSize);
        //构建分页构造器
        Page pageInfo=new Page(page,pageSize);
        //构造器条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper();
        //添加排序条件
        queryWrapper.orderByDesc(Category::getSort);

        //执行查询
       categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);

    }
    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String>update(HttpServletRequest request, @RequestBody Category category){
        Long cateId=(Long)request.getSession().getAttribute("category");
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(cateId);
        categoryService.updateById(category);

        return  R.success("菜单分类信息修改成功");
    }

    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String>delete(Long ids){
        long id=ids;
        log.info("删除分类，id为{}",id);

        categoryService.remove(id);

        return R.success("分类删除成功");
    }

    /**
     *根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public  R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return  R.success(list);
    }


}
