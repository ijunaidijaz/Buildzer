package com.tradesk.appCode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_image.*
import android.view.View
import com.tradesk.utils.extension.loadWallImage
import com.github.chrisbanes.photoview.PhotoView
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams;
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.tradesk.R
import com.tradesk.data.entity.*

class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
//        val photoView = findViewById<View>(com.buildzer.R.id.photo_view) as PhotoView
//        photoView.loadWallImage(intent.getStringExtra("image").toString())

        if (intent.getStringExtra("expense").equals("Expenses")){
            view_pager.visibility=View.GONE
            photo_view.visibility=View.VISIBLE
            photo_view.loadWallImage(intent.getStringExtra("image").toString())
        }else{
            view_pager.visibility=View.VISIBLE
            photo_view.visibility=View.GONE
            if (intent.hasExtra("title")){
                if (intent.getStringExtra("title").equals("Job Images")){
                    val imagelist = intent.getStringArrayListExtra("imagelist")   as ArrayList<AdditionalImageImageClient>
                    viewPager.adapter = JobImagesPagerAdapter(imagelist)
                }else if (intent.getStringExtra("title").equals("Permits")){
                    val imagelist = intent.getStringArrayListExtra("imagelist")   as ArrayList<AdditionalImageLeadDetail>
                    viewPager.adapter = PermitsPagerAdapter(imagelist)
                }else{
                    val imagelistDocs = intent.getStringArrayListExtra("imagelist")   as ArrayList<String>
                    viewPager.adapter = DocsPagerAdapter(imagelistDocs)
                }

            }else{
                val imagelist = intent.getStringArrayListExtra("imagelist")   as ArrayList<AdditionalImageLeadDetail>
                viewPager.adapter = SamplePagerAdapter(imagelist)
            }
        }




        mIvBack.setOnClickListener { finish() }
    }

    internal class JobImagesPagerAdapter(private val images: MutableList<AdditionalImageImageClient>) : PagerAdapter() {
        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)
            photoView.loadWallImage(images[position].image)
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }


    }

    internal class SamplePagerAdapter(private val images: MutableList<AdditionalImageLeadDetail>) : PagerAdapter() {
        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)
            photoView.loadWallImage(images[position].image)
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }


    }

    internal class DocsPagerAdapter(private val images: MutableList<String>) : PagerAdapter() {
        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)
            photoView.loadWallImage(images[position])
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }


    }

    internal class PermitsPagerAdapter(private val images: MutableList<AdditionalImageLeadDetail>) : PagerAdapter() {
        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)
            photoView.loadWallImage(images[position].image)
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }


    }
}