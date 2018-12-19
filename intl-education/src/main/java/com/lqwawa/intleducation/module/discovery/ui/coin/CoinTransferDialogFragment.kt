package com.lqwawa.intleducation.module.discovery.ui.coin

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.lqwawa.intleducation.R
import com.lqwawa.intleducation.base.PresenterDialogFragment
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay.MemberChoiceAdapter
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay.PayCourseDialogFragment
import com.lqwawa.intleducation.module.discovery.vo.CourseVo

/**
 * 货币转让选择赠送的人Dialog
 */
class CoinTransferDialogFragment : PresenterDialogFragment<CoinTransferContract.Presenter>(),CoinTransferContract.View{

    private var mNavigator:CoinTransferNavigator? = null

    // 伴生对象
    companion object {
        /**
         * 私有的show方法
         * @param manager Fragment管理，
         * @param navigator 选择确定的回调处理
         */
        fun show(manager:FragmentManager,navigator:CoinTransferNavigator){
            // 调用BottomSheetDialogFragment以及准备好的显示方法
            val fragment = CoinTransferDialogFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.setNavigator(navigator)
            fragment.show(manager, CoinTransferDialogFragment::class.simpleName)
        }
    }

    private var mRootView: View? = null
    private var mDialogDesc: TextView? = null
    private var mRecycler: RecyclerView? = null
    private var mMemberAdapter: MemberChoiceAdapter? = null

    private var mChildLayout: LinearLayout? = null
    private var mChildName: TextView? = null
    private var mChildAccount:TextView? = null
    private var mMoreLayout: LinearLayout? = null
    private var mOtherLayout: LinearLayout? = null
    private var mChildContainer: LinearLayout? = null
    private var mInputName: EditText? = null
    private var mBtnWatchName: Button? = null
    private var mQueryName: TextView? = null

    // 确定 取消
    private var mBtnConfirm: Button? = null
    private var mBtnCancel:Button? = null

    private var mCourseVo: CourseVo? = null
    private var mTeacherIds: String? = null


    override fun initPresenter(): CoinTransferContract.Presenter {
        return CoinTransferPresenter(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context, R.style.AppTheme_Dialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 我对inflater充分的放心，inflater肯定不为Null
        val view = inflater!!.inflate(R.layout.dialog_coin_transfer,null)
        mDialogDesc = view.findViewById(R.id.dialog_desc) as TextView
        mRecycler = view.findViewById(R.id.recycler) as RecyclerView
        mChildLayout = view.findViewById(R.id.child_layout) as LinearLayout
        mChildName = view.findViewById(R.id.tv_child_name) as TextView
        mChildAccount = view.findViewById(R.id.tv_child_account) as TextView
        mMoreLayout = view.findViewById(R.id.more_layout) as LinearLayout
        mOtherLayout = view.findViewById(R.id.other_layout) as LinearLayout
        mChildContainer = view.findViewById(R.id.child_container) as LinearLayout
        mInputName = view.findViewById(R.id.et_nick_name) as EditText
        mBtnWatchName = view.findViewById(R.id.btn_watch_name) as Button
        mQueryName = view.findViewById(R.id.tv_query_name) as TextView
        mBtnConfirm = view.findViewById(R.id.btn_confirm) as Button
        mBtnCancel = view.findViewById(R.id.btn_cancel) as Button

        mRootView = view
        return view
    }

    fun setNavigator(navigator: CoinTransferNavigator){
        this.mNavigator = navigator
    }
}