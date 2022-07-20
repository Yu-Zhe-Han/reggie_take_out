package com.yzh.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yzh.reggie.common.R;
import com.yzh.reggie.entity.Category;
import com.yzh.reggie.entity.OrderDetail;
import com.yzh.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/{id}")
    public R<List<OrderDetail>>searchOrdel(OrderDetail orderDetail){
        LambdaQueryWrapper<OrderDetail> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(orderDetail.getOrderId()!=null,OrderDetail::getOrderId,orderDetail.getOrderId());
        List<OrderDetail> list=orderDetailService.list(queryWrapper);


        return R.success(list);
    }

}
