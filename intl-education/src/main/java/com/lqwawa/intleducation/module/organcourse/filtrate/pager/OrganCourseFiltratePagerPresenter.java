package com.lqwawa.intleducation.module.organcourse.filtrate.pager;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.OrganCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.organcourse.filtrate.OrganCourseFiltrateParams;

import java.util.List;

/**
 * @author wangchao
 * @desc 学程馆二级筛选子页面的Presenter
 */
public class OrganCourseFiltratePagerPresenter extends BasePresenter<OrganCourseFiltratePagerContract.View>
        implements OrganCourseFiltratePagerContract.Presenter {

    public OrganCourseFiltratePagerPresenter(OrganCourseFiltratePagerContract.View view) {
        super(view);
    }

    @Override
    public void requestCourseData(final boolean more, int pageIndex,
                                  int pageSize, OrganCourseFiltrateParams params) {
        // organId = "5e069b1a-9d90-49ed-956c-946e9f934b68";
        OrganCourseHelper.requestOrganCourseData(params.getOrganId(), pageIndex, pageSize,
                params.getKeyString(),
                params.getLevel(),
                params.getParamOneId(), params.getParamTwoId(), params.getParamThreeId(),
                params.getLibraryType(), params.getSort(),
                new DataSource.Callback<List<CourseVo>>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        final OrganCourseFiltratePagerContract.View view = (OrganCourseFiltratePagerContract.View) getView();
                        if (EmptyUtil.isNotEmpty(view)) {
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(List<CourseVo> courseVos) {
                        final OrganCourseFiltratePagerContract.View view = (OrganCourseFiltratePagerContract.View) getView();
                        if (EmptyUtil.isNotEmpty(view)) {
                            if (more) {
                                view.onMoreCourseLoaded(courseVos);
                            } else {
                                view.onCourseLoaded(courseVos);
                            }
                        }
                    }
                });
    }

    @Override
    public void requestCourseResourceData(boolean more, int pageIndex, int pageSize, OrganCourseFiltrateParams params) {
        OrganCourseHelper.requestOrganCourseResourceData(params.getOrganId(), pageIndex, pageSize,
                params.getKeyString(),
                params.getLevel(),
                new DataSource.Callback<List<CourseVo>>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        final OrganCourseFiltratePagerContract.View view = (OrganCourseFiltratePagerContract.View) getView();
                        if (EmptyUtil.isNotEmpty(view)) {
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(List<CourseVo> courseVos) {
                        final OrganCourseFiltratePagerContract.View view = (OrganCourseFiltratePagerContract.View) getView();
                        if (EmptyUtil.isNotEmpty(view)) {
                            if (more) {
                                view.onMoreCourseResourceLoaded(courseVos);
                            } else {
                                view.onCourseResourceLoaded(courseVos);
                            }
                        }
                    }
                });
    }

}
