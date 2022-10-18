package com.tradesk.appCode

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.appCode.home.leadsDetail.LeadDetailPresenter
import com.tradesk.appCode.home.leadsDetail.leadsNotesModule.ILeadDetailView
import com.tradesk.appCode.profileModule.gallaryModule.PermitsDetailAdapterGallary
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.filemanager.openAppPermits
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.extension.toast
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_gallary.*
import kotlinx.android.synthetic.main.activity_permits_image.*
import kotlinx.android.synthetic.main.activity_permits_image.mIvBack
import javax.inject.Inject

class PermitsImageActivity :  BaseActivity(), SingleListCLickListener, ILeadDetailView {

    var selected_position=0
    var selected_doc=0


    val mPermitsModel = ArrayList<AdditionalImageImageClient>()
    val mPermitsAdapterGallary by lazy { PermitsDetailAdapterGallary(this, this, mPermitsModel) }

    @Inject
    lateinit var presenter: LeadDetailPresenter<ILeadDetailView>
    var imagelist = ArrayList<AdditionalImageImageClient>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permits_image)
        presenter.onAttach(this)
        mTvTitle.setText(intent.getStringExtra("title"))
        GridLayoutManager(this, 2, RecyclerView.VERTICAL, false).apply {
            rvPermits.layoutManager = this
        }
        rvPermits.adapter = mPermitsAdapterGallary


//        if (isInternetConnected()){
//            presenter.getadditionalimagesjobs("1","30","permit")
//        }

        imagelist = intent.getStringArrayListExtra("imagelist") as ArrayList<AdditionalImageImageClient>
        mPermitsModel.clear()
        mPermitsModel.addAll(imagelist)
        mPermitsAdapterGallary.notifyDataSetChanged()


        mIvBack.setOnClickListener { onBackPressed() }
    }

    override fun onLeadDetail(it: LeadDetailModel) {
        
    }

    override fun onDeleteImage(it: SuccessModel) {
         
    }

    override fun onStatus(it: SuccessModel) {
         
    }

    override fun onLeadDeleteStatus(it: SuccessModel) {
        toast("Deleted Successfully")
        mPermitsModel.removeAt(selected_position)
        mPermitsAdapterGallary.notifyDataSetChanged()
    }

    override fun onDocDeleteStatus(it: SuccessModel) {
         
    }

    override fun onAddImage(it: SuccessModel) {
         
    }

    override fun onAddReminder(it: SuccessModel) {
         
    }

    override fun onProfileData(it: ProfileModel) {
         
    }

    override fun onAdditionalImagesData(it: AdditionalImagesWithClientModel) {

    }


    override fun onerror(data: String) {
         
    }

    override fun onGeneratedToken(lastAction: String) {
        
    }


    override fun onSingleListClick(item: Any, position: Int) {
        if (item.equals("PermitAdapter")){
            openAppPermits(this@PermitsImageActivity,mPermitsModel[position].image,mPermitsModel.get(position)._id)
        }else{
            showMenu(item as View, position)
        }

    }


    fun showMenu(anchor: View, position: Int ): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.getMenuInflater().inflate(R.menu.gallary_menu, popup.getMenu())
        popup.setOnMenuItemClickListener{

                if (isInternetConnected()) {
                    selected_position = position
                    with(JsonObject()) {
                        addProperty("job_id", intent.getStringExtra("id").toString())
                        addProperty("image_id",imagelist[position]._id)

                        presenter.deleteimages(this)
                    }

            }
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

    override fun onBackPressed() {
        setResult(if (mPermitsModel.size<imagelist.size) RESULT_OK else RESULT_CANCELED)
        super.onBackPressed()

    }
}

