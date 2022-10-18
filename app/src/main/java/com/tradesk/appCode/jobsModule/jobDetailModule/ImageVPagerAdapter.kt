package com.tradesk.appCode.jobsModule.jobDetailModule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.tradesk.R
import com.tradesk.data.entity.AdditionalImageLeadDetail
import com.makeramen.roundedimageview.RoundedImageView
import com.socialgalaxyApp.util.extension.loadWallRound

class ImageVPagerAdapter(
    private val images: MutableList<AdditionalImageLeadDetail>,
    val onClick: () -> Unit
) : PagerAdapter() {


    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return o === view
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View =
            LayoutInflater.from(container.context).inflate(R.layout.slidingimages_layout, null)
        val imageView = view.findViewById<RoundedImageView>(R.id.image)

        imageView.loadWallRound(images[position].image)
        imageView.setOnClickListener {
            onClick.invoke()
        }

//        startActivity(Intent(this, ImageActivity::class.java).putExtra("image",images[position].image)
//        Glide.with(container.getContext()).load(images[position])
//            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(imageView)

        /* Picasso.with(container.getContext())
                .load(images.get(position))
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);*/container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}