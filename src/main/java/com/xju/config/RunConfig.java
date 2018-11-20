package com.xju.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;


@Configuration
@ComponentScan(basePackages={"com"})
@MapperScan("com.dao")
public class RunConfig {
}