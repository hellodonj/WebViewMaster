package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * @author mrmedici
 * @desc V5.1.0新版本基础课程实体
 */
public class LQBasicsOuterEntity extends BaseVo{

    private int dataType;
    private String dataName;
    private List<LQBasicsInnerEntity> list;

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public List<LQBasicsInnerEntity> getList() {
        return list;
    }

    public void setList(List<LQBasicsInnerEntity> list) {
        this.list = list;
    }

    public static class LQBasicsInnerEntity extends BaseVo{

        private int id;
        private String configValue;
        private String level;
        private int paramThreeId;
        private int paramTwoId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getConfigValue() {
            return configValue;
        }

        public void setConfigValue(String configValue) {
            this.configValue = configValue;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public int getParamThreeId() {
            return paramThreeId;
        }

        public void setParamThreeId(int paramThreeId) {
            this.paramThreeId = paramThreeId;
        }

        public int getParamTwoId() {
            return paramTwoId;
        }

        public void setParamTwoId(int paramTwoId) {
            this.paramTwoId = paramTwoId;
        }
    }
}
