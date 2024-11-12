package com.sl.ms.carriage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sl.ms.carriage.domain.dto.WaybillDTO;
import com.sl.ms.carriage.entity.CarriageEntity;
import com.sl.ms.carriage.domain.dto.CarriageDTO;

import java.util.List;

/**
 * 运费管理表 服务类
 */
public interface CarriageService extends IService<CarriageEntity> {

    /**
     * 获取全部运费模板
     *
     * @return 运费模板对象列表
     */
    List<CarriageDTO> findAll();

    CarriageDTO saveOrUpdate(CarriageDTO carriageDTO);

    CarriageDTO compute(WaybillDTO waybillDTO);

    CarriageEntity findCarriage(WaybillDTO waybillDTO);
}
