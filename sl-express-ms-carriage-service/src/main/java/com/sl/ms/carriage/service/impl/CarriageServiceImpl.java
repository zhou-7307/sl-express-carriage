package com.sl.ms.carriage.service.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sl.ms.base.api.common.AreaFeign;
import com.sl.ms.base.domain.base.AreaDto;
import com.sl.ms.carriage.domain.constant.CarriageConstant;
import com.sl.ms.carriage.domain.dto.WaybillDTO;
import com.sl.ms.carriage.domain.enums.EconomicRegionEnum;
import com.sl.ms.carriage.entity.CarriageEntity;
import com.sl.ms.carriage.mapper.CarriageMapper;
import com.sl.ms.carriage.domain.dto.CarriageDTO;
import com.sl.ms.carriage.service.CarriageService;
import com.sl.transport.common.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Array;
import java.util.*;

@Slf4j
@Service
public class CarriageServiceImpl extends ServiceImpl<CarriageMapper,CarriageEntity> implements CarriageService {

    @Resource
    private AreaFeign areaFeign;
    @Resource
    private CarriageMapper carriageMapper;
    @Override
    public List<CarriageDTO> findAll() {
        // 构造查询条件
        LambdaQueryWrapper<CarriageEntity> queryWrapper = Wrappers.<CarriageEntity>lambdaQuery()
                //按创建时间倒序
                .orderByDesc(CarriageEntity::getCreated);
        // 查询数据库
        List<CarriageEntity> list = super.list(queryWrapper);

		//转化对象，返回集合数据
        return CollStreamUtil.toList(list, carriageEntity -> {
            CarriageDTO carriageDTO = BeanUtil.toBean(carriageEntity, CarriageDTO.class);
            //关联城市数据按照逗号分割成集合
            carriageDTO.setAssociatedCityList(StrUtil.split(carriageEntity.getAssociatedCity(), ','));
            return carriageDTO;
        });
    }

    @Override
    public CarriageDTO saveOrUpdate(CarriageDTO carriageDTO) {
        //判断carriageDTO中id是否为空，为空则为新增操作

        //判断是否是经济区互寄
         //是，判断关联城市是否重复
          //重复，抛出异常
          //不重复，进行新增操作
        //不是经济区互寄，判断模版是否存在
         //存在，抛出异常
         //不存在，进行新增操作

        //更新操作
        //1判断模版是否存在
        //1.1存在，抛出异常
        //1.2不存在，进行更新


        return null;
    }

    /**
     * 计算运费
     * @param waybillDTO
     * @return
     */
    @Override
    public CarriageDTO compute(WaybillDTO waybillDTO) {
        //判断运送区域类型并返回模版数据
        CarriageEntity  carriageEntity = findCarriage(waybillDTO);

        Double firstWeight = carriageEntity.getFirstWeight();
        Double continuousWeight = carriageEntity.getContinuousWeight();
        Integer lightThrowingCoefficient = carriageEntity.getLightThrowingCoefficient();
        //根据模版数据进行计算
        double transformWeight = waybillDTO.getMeasureLong()
                *waybillDTO.getMeasureWidth()
                *waybillDTO.getMeasureHigh()
                /lightThrowingCoefficient;

        double actualWeight = waybillDTO.getWeight()>transformWeight ? waybillDTO.getWeight() : transformWeight;
        //计算计费重量
        double computeWeight = computChargedWeight(actualWeight);
        //计算实际金额
        double expense = 1*firstWeight + (computeWeight-1)*continuousWeight;

        CarriageDTO carriageDTO = new CarriageDTO();
        BeanUtils.copyProperties(carriageEntity,carriageDTO);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(carriageEntity.getAssociatedCity());
        carriageDTO.setAssociatedCityList(arrayList);
        carriageDTO.setExpense(expense);
        carriageDTO.setComputeWeight(computeWeight);

        return carriageDTO;
    }

    /**
     * 计算计费重量
     * @param actualWeight 实际重量
     * @return
     */
    private double computChargedWeight(double actualWeight){
        if(actualWeight<=1){
            return 1;
        }
        if(actualWeight<10){
            return Math.round(actualWeight*10)/10;
        }
        if(actualWeight<100){
            return Math.ceil(actualWeight*2)/2;
        }
        if(actualWeight>=100){
            return Math.round(actualWeight);
        }
        return 0;
    }
    @Override
    public CarriageEntity findCarriage(WaybillDTO waybillDTO) {
        Long senderCityId = waybillDTO.getSenderCityId();
        Long receiverCityId = waybillDTO.getReceiverCityId();
        //判断是否同城
        if(senderCityId==receiverCityId){
            return carriageMapper.selectCarriageByTemplateId(CarriageConstant.SAME_CITY);
        }

        AreaDto senderCityDTO = areaFeign.get(senderCityId);
        AreaDto receiverCityDTO = areaFeign.get(receiverCityId);

        Long senderProvinceId = senderCityDTO.getParentId();
        Long receiverProvinceId = receiverCityDTO.getParentId();
        //判断是否省内
        if(senderProvinceId==receiverProvinceId){
            return carriageMapper.selectCarriageByTemplateId(CarriageConstant.SAME_PROVINCE);
        }

        Long[] provinces = {senderProvinceId,receiverProvinceId};
        //判断是否经济区互寄
        ArrayList<Long> arrayList;
        EconomicRegionEnum[] economicRegionEnums = EconomicRegionEnum.values();
        for(EconomicRegionEnum economicRegionEnum : economicRegionEnums){
            arrayList = new ArrayList<Long>();
            for(Long province : economicRegionEnum.getValue()){
                arrayList.add(province);
            }
            if(arrayList.contains(senderProvinceId)&&arrayList.contains(receiverProvinceId)){
                String associatedCity = economicRegionEnum.getCode();
                return carriageMapper.selectEconomicRegionCarriage(CarriageConstant.ECONOMIC_ZONE,associatedCity);
            }
        }

        //跨省
        return carriageMapper.selectCarriageByTemplateId(CarriageConstant.TRANS_PROVINCE);
    }

}
