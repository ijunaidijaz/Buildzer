package com.tradesk.utils.extension

import androidx.fragment.app.FragmentManager
import com.tradesk.R


/**
 * Created by rajit on 18/01/20.
 */
internal fun androidx.fragment.app.FragmentManager.removeFragment(
    tag: String,
    slideIn: Int = R.anim.slide_left,
    slideOut: Int = R.anim.slide_right
) {
    this.beginTransaction()
        .disallowAddToBackStack()
        .setCustomAnimations(slideIn, slideOut)
        .remove(this.findFragmentByTag(tag)!!)
        .commitNow()
}

internal fun androidx.fragment.app.FragmentManager.replaceFragment(
    containerViewId: Int,
    fragment: androidx.fragment.app.Fragment
) {
//    popBackStack(fragment.tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//    this.beginTransaction()
//        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//        .add(containerViewId, fragment,fragment.tag)
//        .addToBackStack(fragment.tag)
//        .commit()

    popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    this.beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(containerViewId, fragment, fragment.tag)
        .commit()
}

internal fun androidx.fragment.app.FragmentManager.addFragment(
    containerViewId: Int,
    fragment: androidx.fragment.app.Fragment
) {
    this.beginTransaction().addToBackStack(fragment.tag)
        .add(R.id.mainContainer, fragment, fragment.tag)
        .commit()
}

internal fun androidx.fragment.app.FragmentManager.replaceFragmentAnim(
    containerViewId: Int,
    fragment: androidx.fragment.app.Fragment,
    tag: String,
    slideIn: Int = R.anim.slide_left,
    slideOut: Int = R.anim.slide_right
) {
    this.beginTransaction().disallowAddToBackStack()
        .setCustomAnimations(slideIn, slideOut)
        .replace(containerViewId, fragment)
        .commit()
}





