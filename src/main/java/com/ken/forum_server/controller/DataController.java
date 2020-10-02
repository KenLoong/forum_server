package com.ken.forum_server.controller;

import com.ken.forum_server.common.Result;
import com.ken.forum_server.dto.DataDto;
import com.ken.forum_server.service.DataService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DataController {

    @Autowired
    private DataService dataService;

    // 统计网站UV
    @RequestMapping(path = "/data/uv", method = RequestMethod.POST)
    @RequiresRoles({"admin"})
    public Result getUV(@RequestBody DataDto dataDto) {

        //转化日期格式
//        String strDateFormat = "yyyy-MM-dd";
//        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
//
//        Date start = new Date(sdf.format(dataDto.getStart()));
//        Date end = new Date(sdf.format(dataDto.getEnd()));

        Date start = dataDto.getStart();
        Date end = dataDto.getEnd();

        long uv = dataService.calculateUV(start, end);
        Map<String,Object> map = new HashMap<>();
        map.put("uvResult", uv);
        map.put("uvStartDate", start);
        map.put("uvEndDate", end);
        return new Result().success(map);
    }

    // 统计活跃用户
    @RequestMapping(path = "/data/dau", method = RequestMethod.POST)
    @RequiresRoles({"admin"})
    public Result getDAU(@RequestBody DataDto dataDto) {
        //转化日期格式
//        String strDateFormat = "yyyy-MM-dd";
//        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
//
//        Date start = new Date(sdf.format(dataDto.getStart()));
//        Date end = new Date(sdf.format(dataDto.getEnd()));

        Date start = dataDto.getStart();
        Date end = dataDto.getEnd();

        long dau = dataService.calculateDAU(start, end);
        Map<String,Object> map = new HashMap<>();
        map.put("dauResult", dau);
        map.put("dauStartDate", start);
        map.put("dauEndDate", end);
        return new Result().success(map);
    }


}
