package com.tradesk.utils

interface OnDialogButtonClickListener {
    fun onPositiveButtonClicked()
    fun onNegativeButtonClicked()
    fun onNeutralButtonClicked()
}

interface NetworkConstants {

    companion object {
        const val SUCCESS = 200
        const val AUTHFAIL = 401
        const val ERROR404 = 404


        const val login = "users/login"
        const val signup = "users/signup"
        const val socialLogin = "users/google-login"
        const val facebookLogin = "users/facebook-login"
        const val forgot = "users/forget_password"
        const val addleads = "leads/add_leads"
        const val updateLeads = "leads/update_lead"
        const val getleads = "leads/get_leads"
        const val tradedetails = "/client/trade_details"
        const val notifications = "notification/get_notifications"
        const val getProposalRevenue = "proposal/getProposalRevenue"
        const val getleadssubusers = "/leads/get_sub_users"
        const val timesheetlist = "/jobTimeSheet/job_details?"
        const val inouttime = "/jobTimeSheet/addJobTime"
        const val getreminders = "remainder/getRemainder?"
        const val getadditionalimagesjobs = "leads/getAdditionalImagesJobs?"
        const val leaddetail = "leads/get_leads_by_id/"
        const val reminderdetail = "remainder/reminder_by_id/"
        const val jobsdetailtimesheet = "jobTimeSheet/jobSheet_details/"
        const val subusersdetail = "leads/subUsers_details/"
        const val leaddelete = "leads/deleteLeads/"
        const val leadsnotes = "leads/get_notes/"
        const val addclient = "client/addClient"
        const val getConversation = "conversation/getConversation/"
        const val addConversation = "conversation/addConversation/"
        const val addsubusers = "leads/add_sub_users"
        const val updatesaleclient = "client/client_update"
        const val addjobsubusers = "leads/add_job_users"
        const val clientslist = "client/client_list"
        const val changepassword = "users/changePassword"
        const val terms = "termsCondition/terms_condition"
        const val leadsaddnotes = "leads/add_notes"
        const val convertleads = "leads/convert_leads"
        const val addreminder = "remainder/add_remainder"
        const val getProfile = "users/getProfile"
        const val editprofile = "users/editProfile"
        const val editprofiledocs = "users/addDocs"
        const val deleteprofiledocs = "users/deleteUrl/"
        const val add_addtional_images = "leads/add_addtional_images"
        const val proposaladd = "proposal/addProposal"
        const val addexpense = "statement/expenses/add"
        const val proposallist = "proposal/proposal_list"
        const val updateProposal = "proposal/updateProposal"
        const val changeProposalStatus = "proposal/changeProposalStatus"
        const val expenseslist = "statement/expenses/list"
        const val calendardetail = "remainder/reminder_details_by_month"
        const val jobexpenseslist = "statement/expenses/job_expenses_list"
        const val proposalItemslist = "proposal/getProposalItems"
        const val proposaldetail = "proposal/proposal_details/"
        const val clientdetails = "client/client_details/"
        const val sendproposal = "/proposal/sendProposalMail"
        const val deleteproposal = "proposal/deleteProposal/"
        const val deleteexpense = "statement/expenses/delete/"

        const val userproposallist = "graph/metric/proposalList"
        const val metricjobs = "graph/metric/jobs"
        const val metricjobslist = "graph/metric/jobsList"
        const val deleteimages = "leads/remove_additional_images"

        const val drug = "drug"


        const val coupon = "coupon"
        const val couponViaEmail = "couponViaEmail"
        const val textCard = "textCard"
        const val couponViaPhone = "couponViaPhone"
        const val dashboard = "dashboard"
        const val resend = "resend"
        const val invitebyReferral = "invitebyReferral"
        const val goldCardPayment = "goldCardPayment"
        const val paymentCard = "paymentCard"
        const val referral = "referral"//referral_status=All&user_type
        const val notices = "notices"//notices_status=All&user_type
        const val feedback = "feedback"//notices_status=All&user_type
        const val task = "task"//task_status=Active, user_type
        const val myCharity = "myCharity"
        const val profileDetail = "profileDetail"
        const val editProfileImage = "editProfileImage"
        const val editProfile = "editProfile"
        const val profileDetailVerifyOTP = "profileDetailVerifyOTP"
        const val pharmacyNearMe = "pharmacyNearMe"
        const val appVersion = "appVersion"
        const val faq = "faq"
        const val emailCard = "emailCard"
        const val address = "address"
        const val payment = "payment"
        const val charityCardList = "charityCardList"
        const val defaultAddress = "defaultAddress"
        const val pharmacyList = "pharmacyList"
        const val preSignUpVerification = "preSignUpVerification"
        const val postSignUpVerification = "postSignUpVerification"
        const val signUp = "signUp"
        const val signIn = "signIn"
        const val forgotPassword = "forgotPassword"
        const val verifyOTP = "verifyOTP"
        const val sendAgain = "sendAgain"
        const val charity = "charity"
        const val charityDetail = "charityDetail"
        const val registeredCharityDetail = "registeredCharityDetail"
        const val selectCharity = "selectCharity"
        const val profilePassword = "profilePassword"
        const val profileEnableAuth = "profileEnableAuth"
        const val profileSecurity = "profileSecurity"
        const val notificationPreference = "notificationPreference"
        const val updateCard = "updateCard"

        //        const val store= "store"
        const val store = "storeHomePage"
        const val myCart = "myCart"
        const val disableRecurringPayment = "disableRecurringPayment"
        const val getCart = "getCart"
        const val storeProductDetail = "storeProductDetail"
        const val favoriteMedication = "favoriteMedication"
        const val recentPurchases = "recentPurchases"
        const val goldSummary = "membershipOrderSummary"
        const val storeSummary = "orderSummary"
        const val paymentCards = "paymentCard"
        const val selectPaymentCard = "selectPaymentCard"
        const val storePayment = "storePayment"
        const val storePaymentResponse = "storePaymentResponse"
        const val storeOrderHistory = "orderHistory"
    }
}

interface PreferenceConstants {
    companion object {
        val USER_TOKEN = "USER_TOKEN"
        val SharedPrefenceName = "GBRxApp"
        val USER_LOGGED_IN = "user_logged"
        val HOWITWORK = "HOWITWORK"
        val USER_LOGO = "USER_LOGO"
        val USER_ID = "USER_ID"
        val USER_TYPE = "USER_TYPE"
        val USER_NAME = "USER_NAME"
        val MEMBER_TYPE = "MEMBER_TYPE"
        val REFERRAL = "REFERRAL"
        val DEVICE_TOKEN = "device_token"
        val LANGUAGE_TYPE = "language_type"
        val ACCESSTOKEN = "accesstoken"
        val BASE_URL = "BASE_URL"
        val GotToken = "GotToken"
        val TOKENTYPE = "TOKENTYPE"
        val USER_DATA = "userData"
        val FREQ_SEARCH = "FREQ_SEARCH"
        val CHARITY = "CHARITY"
        val LAT = "lat"
        val LNG = "lng"
        val LANGUAGE = "language"


    }
}