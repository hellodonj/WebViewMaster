package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class CatalogResourceListResult
        extends ModelResult<CatalogResourceListResult.CatalogResourceListModel> {

    public static class CatalogResourceListModel extends Model {
        private List<MaterialInfo> MaterialList;

        public List<MaterialInfo> getMaterialList() {
            return MaterialList;
        }

        public void setMaterialList(List<MaterialInfo> materialList) {
            MaterialList = materialList;
        }
    }
}
