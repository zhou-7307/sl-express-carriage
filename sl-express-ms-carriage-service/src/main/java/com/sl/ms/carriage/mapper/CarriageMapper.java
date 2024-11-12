package com.sl.ms.carriage.mapper;

import com.sl.ms.carriage.entity.CarriageEntity;
import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 运费管理表 mapper接口
 */
@Mapper
@Repository
public interface CarriageMapper extends BaseMapper<CarriageEntity> {

    @Select("select * from sl_carriage where template_type = #{templateType}")
    CarriageEntity selectCarriageByTemplateId(@Param("templateType") Integer templateType);

    @Select("select * from sl_carriage where template_type = #{templateType} and associated_city = #{associatedCity}")
    CarriageEntity selectEconomicRegionCarriage(@Param("templateType") Integer templateType,
                                                @Param("associatedCity") String associatedCity);
}
