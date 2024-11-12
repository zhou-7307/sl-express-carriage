package com.sl.ms.carriage.controller;

import com.sl.ms.carriage.domain.dto.CarriageDTO;

import com.sl.ms.carriage.domain.dto.WaybillDTO;
import com.sl.ms.carriage.service.CarriageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Validated
@RestController
@Api(tags = "运费管理")
@RequestMapping("/carriages")
public class CarriageController {

    @Resource
    private CarriageService carriageService;

    @GetMapping
    @ApiOperation(value = "运费模板列表")
    public List<CarriageDTO> findAll() {
        return this.carriageService.findAll();
    }

    @PostMapping
    @ApiOperation(value = "新增或修改模版")
    public CarriageDTO saveOrUpdate(@RequestBody CarriageDTO carriageDTO){
        return carriageService.saveOrUpdate(carriageDTO);
    }

    @PostMapping("compute")
    @ApiOperation(value = "运费计算")
    public CarriageDTO compute(@RequestBody WaybillDTO waybillDTO){
        return carriageService.compute(waybillDTO);
    }
}
