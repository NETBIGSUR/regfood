package com.trus.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trus.reggie.common.R;
import com.trus.reggie.entity.Employee;
import com.trus.reggie.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {


    @Autowired
    private EmployeeService employeeService = null;

    /**
     * 员工登录
     * @param request 请求
     * @param employee 员工实体
     * @return 员工
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        /**
         * 1、将页面捉交的密码password进行md5加密处理
         * 2、 根据页面提交的用户名username查询数据库
         * 3、 如果没有查询到则返回登灭失败结果
         * 4、密码比对，如果不一致则返回登录失败结果
         * 5、 查看员工状态，如果为已禁用状态，则返问员工已禁用结果
         * 6、 登录成功，将员Tid存入Session并返回登录成功结果
         */

        /*
         * 1、将页面捉交的密码password进行md5加密处理
         */
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        /**
         * 2、 根据页面提交的用户名username查询数据库
         */
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee one = employeeService.getOne(queryWrapper);

        /**
         * 3.如果没有查询到则返回登灭失败结果
         */
        if(one == null){
            return R.error("用户不存在");
        }

        /**
         * 密码比对
         */
        if(!one.getPassword().equals(password)){
            return R.error("密码错误");
        }

        /**
         * 5、 查看员工状态，如果为已禁用状态，则返问员工已禁用结果
         */
        if (one.getStatus() == 0){
            return R.error("账号已禁用");
        }
        /**
         * 6、 登录成功，将员Tid存入Session并返回登录成功结果
         */
        request.getSession().setAttribute("employee",one.getId());

        return R.success(one);

    }

    @PostMapping("/logout")
    public R<String > logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出OK");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){

        String psw = DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8));
        employee.setPassword(psw);
//
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        Long user = (Long) request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(user);
//        employee.setUpdateUser(user);
//
        log.info("添加的信息： {}", employee.toString());
        employeeService.save(employee);


        return R.success("添加成功");
    }


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize, String name){
        log.info("page:{},pageSize:{},name:{}",page,pageSize,name);

        Page pageInfo = new Page(page,pageSize);

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){

//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
//        employee.setUpdateTime(LocalDateTime.now());
//
        employeeService.updateById(employee);
        return R.success("用户状态修改成功");

    }

    @GetMapping("/{id}")
    public R<Employee> getOne(@PathVariable String id){
//        Long id = employee.getId();
        log.info(id);
//        System.out.println(id + "---");
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Employee::getId,id);
        Employee one = employeeService.getOne(queryWrapper);
        if( one != null) {
            return R.success(one);
        }
        return R.error("未找到相关信息");
    }

//    @PutMapping
    public R<Employee> editEmployee(@RequestBody Employee employee){
        Long id = employee.getId();
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Employee::getId,id);
        Employee one = employeeService.getOne(queryWrapper);
        return R.success(one);
    }
}
