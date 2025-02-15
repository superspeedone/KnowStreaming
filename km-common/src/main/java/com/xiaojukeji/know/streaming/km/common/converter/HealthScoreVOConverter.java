package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthScoreResult;
import com.xiaojukeji.know.streaming.km.common.bean.vo.health.HealthCheckConfigVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.health.HealthScoreBaseResultVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.health.HealthScoreResultDetailVO;
import com.xiaojukeji.know.streaming.km.common.enums.config.ConfigGroupEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;

import java.util.ArrayList;
import java.util.List;

public class HealthScoreVOConverter {
    private HealthScoreVOConverter() {
    }

    public static List<HealthScoreResultDetailVO> convert2HealthScoreResultDetailVOList(List<HealthScoreResult> healthScoreResultList, boolean useGlobalWeight) {
        Float globalWeightSum = 1f;
        if (!healthScoreResultList.isEmpty()) {
            globalWeightSum = healthScoreResultList.get(0).getAllDimensionTotalWeight();
        }

        List<HealthScoreResultDetailVO> voList = new ArrayList<>();
        for (HealthScoreResult healthScoreResult: healthScoreResultList) {
            HealthScoreResultDetailVO vo = new HealthScoreResultDetailVO();
            vo.setDimension(healthScoreResult.getCheckNameEnum().getDimensionEnum().getDimension());
            vo.setConfigName(healthScoreResult.getCheckNameEnum().getConfigName());
            vo.setConfigItem(healthScoreResult.getCheckNameEnum().getConfigItem());
            vo.setConfigDesc(healthScoreResult.getCheckNameEnum().getConfigDesc());
            if (useGlobalWeight) {
                vo.setWeightPercent(healthScoreResult.getBaseConfig().getWeight().intValue() * 100 / globalWeightSum.intValue());
            } else {
                vo.setWeightPercent(healthScoreResult.getBaseConfig().getWeight().intValue() * 100 / healthScoreResult.getPresentDimensionTotalWeight().intValue());
            }

            vo.setScore(healthScoreResult.calRawHealthScore());
            if (healthScoreResult.getTotalCount() <= 0) {
                // 未知
                vo.setPassed(null);
            } else {
                vo.setPassed(healthScoreResult.getPassedCount().equals(healthScoreResult.getTotalCount()));
            }

            vo.setCheckConfig(convert2HealthCheckConfigVO(ConfigGroupEnum.HEALTH.name(), healthScoreResult.getBaseConfig()));

            vo.setNotPassedResNameList(healthScoreResult.getNotPassedResNameList());
            vo.setCreateTime(healthScoreResult.getCreateTime());
            vo.setUpdateTime(healthScoreResult.getUpdateTime());
            voList.add(vo);
        }
        return voList;
    }

    public static List<HealthScoreBaseResultVO> convert2HealthScoreBaseResultVOList(List<HealthScoreResult> healthScoreResultList) {
        List<HealthScoreBaseResultVO> voList = new ArrayList<>();
        for (HealthScoreResult healthScoreResult: healthScoreResultList) {
            HealthScoreBaseResultVO vo = new HealthScoreBaseResultVO();
            vo.setDimension(healthScoreResult.getCheckNameEnum().getDimensionEnum().getDimension());
            vo.setConfigName(healthScoreResult.getCheckNameEnum().getConfigName());
            vo.setConfigDesc(healthScoreResult.getCheckNameEnum().getConfigDesc());
            vo.setWeightPercent(healthScoreResult.getBaseConfig().getWeight().intValue() * 100 / healthScoreResult.getPresentDimensionTotalWeight().intValue());
            vo.setScore(healthScoreResult.calRawHealthScore());
            vo.setPassed(healthScoreResult.getPassedCount().equals(healthScoreResult.getTotalCount()));
            vo.setCheckConfig(convert2HealthCheckConfigVO(ConfigGroupEnum.HEALTH.name(), healthScoreResult.getBaseConfig()));
            vo.setCreateTime(healthScoreResult.getCreateTime());
            vo.setUpdateTime(healthScoreResult.getUpdateTime());
            voList.add(vo);
        }
        return voList;
    }

    public static List<HealthCheckConfigVO> convert2HealthCheckConfigVOList(String groupName, List<BaseClusterHealthConfig> configList) {
        List<HealthCheckConfigVO> voList = new ArrayList<>();
        for (BaseClusterHealthConfig config: configList) {
            voList.add(convert2HealthCheckConfigVO(groupName, config));
        }
        return voList;
    }

    public static HealthCheckConfigVO convert2HealthCheckConfigVO(String groupName, BaseClusterHealthConfig config) {
        HealthCheckConfigVO vo = new HealthCheckConfigVO();
        vo.setDimensionCode(config.getCheckNameEnum().getDimensionEnum().getDimension());
        vo.setDimensionName(config.getCheckNameEnum().getDimensionEnum().name());
        vo.setConfigGroup(groupName);
        vo.setConfigName(config.getCheckNameEnum().getConfigName());
        vo.setConfigItem(config.getCheckNameEnum().getConfigItem());
        vo.setConfigDesc(config.getCheckNameEnum().getConfigDesc());
        vo.setWeight(config.getWeight());
        vo.setValue(ConvertUtil.obj2Json(config));
        return vo;
    }
}
