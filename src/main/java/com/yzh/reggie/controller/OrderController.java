package com.yzh.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yzh.reggie.common.R;
import com.yzh.reggie.entity.Category;
import com.yzh.reggie.entity.Dish;
import com.yzh.reggie.entity.Orders;
import com.yzh.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }
    /**
     * 订单查看
     */
    @GetMapping("/page")
    public R<Page>showorder(int page,int pageSize){
        //构建分页构造器
        Page pageInfo=new Page(page,pageSize);
        //构造器条件构造器
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper();
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);

        //执行查询
        orderService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 订单派送与完成
     * @param orders
     * @return
     */
    @PutMapping
    public R<String>changeStatus(@RequestBody Orders orders){
        UpdateWrapper<Orders> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("id",orders.getId());
        updateWrapper.set("status", orders.getStatus());
        orderService.update(null,updateWrapper);
        return R.success("正在派送");

    }
}

