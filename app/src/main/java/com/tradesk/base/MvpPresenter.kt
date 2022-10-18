package com.tradesk.base


interface MvpPresenter<V : MvpView> {

    fun onAttach(mvpView: V)

    fun onDetach()

    fun handleApiError(errorType: Int, message: String)

    fun setUserAsLoggedOut()

    fun generateToken(it: String, lastAction: String)

    fun authDetail(lastAction: String)
    fun logoutUser(lastAction: String)
}
