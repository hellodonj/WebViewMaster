package com.lqwawa.intleducation.factory.data.entity.online;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;

/**
 * @author mrmedici
 * @desc 课程关联的讲授课程返回
 */
public class ParamResponseVo<T> extends ResponseVo<T> {

    private Param param;

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

    public static class Param extends BaseVo{

        private String name;
        private int firstId;
        private int fourthId;
        private int secondId;
        private int thirdId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getFirstId() {
            return firstId;
        }

        public void setFirstId(int firstId) {
            this.firstId = firstId;
        }

        public int getFourthId() {
            return fourthId;
        }

        public void setFourthId(int fourthId) {
            this.fourthId = fourthId;
        }

        public int getSecondId() {
            return secondId;
        }

        public void setSecondId(int secondId) {
            this.secondId = secondId;
        }

        public int getThirdId() {
            return thirdId;
        }

        public void setThirdId(int thirdId) {
            this.thirdId = thirdId;
        }
    }

}
