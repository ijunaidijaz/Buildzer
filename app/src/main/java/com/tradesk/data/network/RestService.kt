package com.tradesk.data.network

import com.google.gson.JsonObject
import com.tradesk.data.entity.*
import com.tradesk.utils.NetworkConstants
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface RestService {
//    @POST(NetworkConstants.auth_detail)
//    fun auth_detail(@Body backend: JsonObject): Observable<AuthDetailEntity>
//
//    @POST(NetworkConstants.oauth2_token)
//    fun generate_token(
//        @Query("grant_type") grant_type: String,
//        @Query("client_id") client_id: String,
//        @Query("client_secret") client_secret: String,
//        @Query("username") username: String,
//        @Query("password") password: String
//    ): Observable<GenerateTokenEntity>
//
//    @POST(NetworkConstants.employee_signup)
//    fun employee_signup(@Body backend: JsonObject): Observable<RegisterEntity>
//

    @GET(NetworkConstants.drug)
    fun searchDrug(@Query("drug_name") drug_name: String): Observable<SearchDrugEntity>


    @GET(NetworkConstants.leaddetail + "{id}")
    fun getLeadDetail(@Path("id") id: String): Observable<LeadDetailModel>

    @GET(NetworkConstants.reminderdetail + "{id}")
    fun reminderdetail(@Path("id") id: String): Observable<ReminderDetailNewModel>

    @GET(NetworkConstants.jobsdetailtimesheet + "{id}")
    fun jobsdetailtimesheet(@Path("id") id: String): Observable<NewTimeSheetModelClass>

    @GET(NetworkConstants.subusersdetail + "{id}")
    fun subusersdetail(@Path("id") id: String): Observable<SubUsersDetailModel>

    @GET(NetworkConstants.leaddelete + "{id}")
    fun getLeadDelete(@Path("id") id: String): Observable<SuccessModel>


    @GET(NetworkConstants.leadsnotes + "{id}")
    fun getLeadNotes(@Path("id") id: String): Observable<NotesListModel>


    @GET(NetworkConstants.terms)
    fun getTrems(): Observable<TermsModel>

    @GET(NetworkConstants.getConversation)
    fun getConversation(@QueryMap json: HashMap<String, String>): Observable<GetConversationModel>

    @Multipart
    @POST(NetworkConstants.addConversation)
    fun addConversation(@PartMap map: HashMap<String, RequestBody>): Observable<AddConversationModel>

    @FormUrlEncoded
    @POST(NetworkConstants.addConversation)
    fun addConversationText(@FieldMap map: HashMap<String, String>): Observable<AddConversationModel>

    @GET(NetworkConstants.getleads)
    fun getChatleads(
        @Query("chat_started") chat_started: String = "true",
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Observable<LeadsModel>

    @FormUrlEncoded
    @POST(NetworkConstants.login)
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_token") device_token: String,
        @Field("device_type") device_type: String
    ): Observable<LoginModel>

    @FormUrlEncoded
    @POST(NetworkConstants.socialLogin)
    fun socialLogin(
        @Field("address") address: String,
        @Field("accessToken") password: String,
        @Field("device_token") device_token: String,
        @Field("device_type") device_type: String
    ): Observable<LoginModel>

    @FormUrlEncoded
    @POST(NetworkConstants.facebookLogin)
    fun fbLogin(
        @Field("address") address: String,
        @Field("accessToken") password: String,
        @Field("device_token") device_token: String,
        @Field("device_type") device_type: String,
        @Field("userType") userType: String?,
        @Field("city") city: String?,
        @Field("state") state: String?,
        @Field("postal_code") postal_code: String?,
        @Field("trade") trade: String?,
        @Field("latLong") latLong: String?
    ): Observable<LoginModel>

    @FormUrlEncoded
    @POST(NetworkConstants.socialLogin)
    fun socialLogin(
        @Field("address") address: String,
        @Field("accessToken") password: String,
        @Field("device_token") device_token: String,
        @Field("device_type") device_type: String,
        @Field("userType") userType: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("postal_code") postal_code: String,
        @Field("trade") trade: String,
        @Field("latLong") latLong: String
    ): Observable<LoginModel>
//    @FormUrlEncoded
//    @DELETE(NetworkConstants.deleteimages)
//    fun deleteimages(
//        @Field("job_id") job_id: String,
//        @Field("image_id") password: String
//    ): Observable<SuccessModel>


    @HTTP(method = "DELETE", path = NetworkConstants.deleteimages, hasBody = true)
//    @DELETE(NetworkConstants.deleteimages)
    fun deleteimages(@Body backend: JsonObject): Observable<SuccessModel>


    @FormUrlEncoded
    @POST(NetworkConstants.changepassword)
    fun changepassword(
        @Field("old_password") old_password: String,
        @Field("new_password") new_password: String
    ): Observable<SuccessModel>

    @FormUrlEncoded
    @POST(NetworkConstants.leadsaddnotes)
    fun leadaddnotes(
        @Field("leads_id") id: String,
        @Field("title") title: String,
        @Field("description") description: String
    ): Observable<SuccessModel>


    @FormUrlEncoded
    @POST(NetworkConstants.inouttime)
    fun gettime(
        @Field("job_id") job_id: String,
        @Field("status") status: String,
        @Field("start_date") start_date: String,
        @Field("end_date") end_date: String,
        @Field("timezone") endtimezone_date: String,
        @Field("address") address: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("zipcode") zipcode: String,
        @Field("latLong") latLong: String
    ): Observable<ClockInOutModel>

    @FormUrlEncoded
    @POST(NetworkConstants.inouttime)
    fun intime(
        @Field("job_id") job_id: String,
        @Field("status") status: String,
        @Field("start_date") end_date: String,
        @Field("timezone") timezone: String,
        @Field("address") address: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("zipcode") zipcode: String,
        @Field("latLong") latLong: String
    ): Observable<ClockInOutModel>

    @FormUrlEncoded
    @POST(NetworkConstants.inouttime)
    fun outtime(
        @Field("job_id") job_id: String,
        @Field("status") status: String,
        @Field("start_date") start_date: String,
        @Field("end_date") end_date: String,
        @Field("timezone") timezone: String,
        @Field("address") address: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("zipcode") zipcode: String,
        @Field("latLong") latLong: String
    ): Observable<ClockInOutModel>

    @FormUrlEncoded
    @POST(NetworkConstants.convertleads)
    fun convertleads(
        @Field("_id") id: String,
        @Field("type") type: String,
        @Field("status") status: String,
        @Field("converted_to_job") converted_to_job: String
    ): Observable<SuccessModel>

    @FormUrlEncoded
    @POST(NetworkConstants.addreminder)
    fun addreminder(
        @Field("id") id: String,
        @Field("client_id") client_id: String,
        @Field("remainder_type") remainder_type: String,
        @Field("type") type: String,
        @Field("dateTime") dateTime: String,
        @Field("timezone") timezone: String
    ): Observable<SuccessModel>

    @FormUrlEncoded
    @POST(NetworkConstants.addreminder)
    fun addreminderCal(
        @Field("remainder_type") remainder_type: String,
        @Field("type") type: String,
        @Field("dateTime") dateTime: String,
        @Field("description") datedescriptionTime: String,
        @Field("timezone") timezone: String
    ): Observable<SuccessModel>

    @FormUrlEncoded
    @POST(NetworkConstants.signup)
    fun signup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("address") address: String,
        @Field("password") password: String,
        @Field("userType") userType: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("postal_code") postal_code: String,
        @Field("trade") trade: String,
        @Field("latLong") latLong: String
    ): Observable<SignupModel>


    @FormUrlEncoded
    @POST(NetworkConstants.forgot)
    fun forgot(
        @Field("email") email: String
    ): Observable<SuccessModel>


    @Multipart
    @POST(NetworkConstants.addleads)
    fun addleads(@PartMap map: HashMap<String, RequestBody>): Observable<SuccessModel>

    @Multipart
    @POST(NetworkConstants.addleads)
    fun addleads(
        @PartMap map: HashMap<String, RequestBody>,
        @Part("users_assigned") users: ArrayList<RequestBody?>?
    ): Observable<SuccessModel>

    @Multipart
    @PUT(NetworkConstants.updateLeads)
    fun updateLeads(
        @PartMap map: HashMap<String, RequestBody>,
        @Part("users_assigned") users: ArrayList<RequestBody?>?
    ): Observable<SuccessModel>

    @Multipart
    @POST(NetworkConstants.editprofile)
    fun editProfile(@PartMap map: HashMap<String, RequestBody>): Observable<SuccessModel>


    @Multipart
    @POST(NetworkConstants.editprofiledocs)
    fun editprofiledocs(@PartMap map: HashMap<String, RequestBody>): Observable<SuccessModel>


    @Multipart
    @POST(NetworkConstants.add_addtional_images)
    fun add_addtional_images(@PartMap map: HashMap<String, RequestBody>): Observable<SuccessModel>


    @GET(NetworkConstants.getleads)
    fun getleads(
        @Query("type") type: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("status") status: String
    ): Observable<LeadsModel>


    @GET(NetworkConstants.notifications)
    fun notifications(
        @Query("page") page: String,
        @Query("limit") limit: String
//        @Query("isRead") isRead: String
    ): Observable<NotifiactionsModel>


    @GET(NetworkConstants.tradedetails)
    fun tradedetails(
        @Query("type") type: String,
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Observable<TradesModel>


    @GET(NetworkConstants.getProposalRevenue)
    fun getProposalRevenue(
        @Query("type") type: String,
        @Query("status") status: String
    ): Observable<RevenueModel>


    @GET(NetworkConstants.getleadssubusers)
    fun getleadssubusers(
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Observable<SubUsersModel>


    @GET(NetworkConstants.getreminders)
    fun getremindersid(
        @Query("type") type: String,
        @Query("id") id: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("status") status: String
    ): Observable<RemindersModel>

    @GET(NetworkConstants.timesheetlist)
    fun timesheetlist(
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("user_id") user_id: String
    ): Observable<TimeModelNewUPdate>

//    @GET(NetworkConstants.getadditionalimagesjobs)
//    fun getadditionalimagesjobs(
//        @Query("page") page: String,
//        @Query("limit") limit: String,
//        @Query("status") status: String
//    ): Observable<AdditonalImagesClassModel>

    @GET(NetworkConstants.getadditionalimagesjobs)
    fun getadditionalimagesjobs(
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("status") status: String
    ): Observable<AdditionalImagesWithClientModel>

    @GET(NetworkConstants.getreminders)
    fun getremindersdate(
        @Query("type") type: String,
        @Query("date") date: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("timezone") timezone: String,
    ): Observable<RemindersModel>

    @GET(NetworkConstants.clientslist)
    fun clientslist(
        @Query("type") type: String,
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Observable<ClientsListModel>

    @GET(NetworkConstants.clientslist)
    fun saleslist(
        @Query("type") type: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("trade") trade: String
    ): Observable<ClientsListModel>

    @GET(NetworkConstants.appVersion)
    fun appVersion(
        @Query("membership_type") membership_type: String
    ): Observable<AppVersionEntity>


    @GET(NetworkConstants.getProfile)
    fun getProfile(): Observable<ProfileModel>


    @GET(NetworkConstants.appVersion)
    fun appVersion(): Observable<AppVersionEntity>

    @Multipart
    @POST(NetworkConstants.addclient)
    fun addclient(@PartMap map: HashMap<String, RequestBody>): Observable<SuccessModel>


    @Multipart
    @POST(NetworkConstants.addsubusers)
    fun addsubusers(@PartMap map: HashMap<String, RequestBody>): Observable<SuccessModel>


    @Multipart
    @PUT(NetworkConstants.updatesaleclient)
    fun updatesaleclient(@PartMap map: HashMap<String, RequestBody>): Observable<SuccessModel>


    @FormUrlEncoded
    @POST(NetworkConstants.addjobsubusers)
    fun addjobsubusers(
        @Field("client_id") client_id: String,
        @Field("job_id") job_id: String
    ): Observable<SuccessModel>


    @Multipart
    @POST(NetworkConstants.proposaladd)
    fun addproposal(@PartMap map: HashMap<String, RequestBody>): Observable<AddProposalsModel>

    @Multipart
    @POST(NetworkConstants.updateProposal)
    fun updateProposal(@PartMap map: HashMap<String, RequestBody>): Observable<AddProposalsModel>

    @Multipart
    @POST(NetworkConstants.proposaladd)
    fun addproposal(@PartMap map: HashMap<String, RequestBody>,
                    @Part("doc_url[]") docs: ArrayList<RequestBody>?,
                    @Part imagesList: ArrayList<MultipartBody.Part>
    ): Observable<AddProposalsModel>

    @Multipart
    @POST(NetworkConstants.updateProposal)
    fun updateProposal(@PartMap map: HashMap<String,
            RequestBody>,@Part("doc_url[]") docs: ArrayList<RequestBody>?,
                       @Part("existingDocs[]") existingDocs: ArrayList<RequestBody>?,
                       @Part imagesList: ArrayList<MultipartBody.Part>
    ): Observable<AddProposalsModel>

    @Multipart
    @POST(NetworkConstants.addexpense)
    fun addexpense(@PartMap map: HashMap<String, RequestBody>): Observable<AddExpenseModel>


    @FormUrlEncoded
    @POST(NetworkConstants.sendproposal)
    fun sendproposal(
        @Field("id") id: String,
        @Field("email") email: String
    ): Observable<SuccessModel>
    @FormUrlEncoded
    @POST(NetworkConstants.changeProposalStatus)
    fun changeProposalStatus(
        @Field("id") id: String,
        @Field("status") status: String,
        @Field("type") type: String
    ): Observable<ChangeProposalStatus>
//    @Multipart
//    @POST(NetworkConstants.proposaladd)
//    fun addproposaltwo(@PartMap map: HashMap<String, RequestBody>,@Part("items[]") items: ArrayList<String>): Observable<AddProposalsModel>

//    @Multipart
//    @POST("/")
//    fun yourMethod(
//        @PartMap partMap: Map<String?, RequestBody?>?,
//        @PartMap map: Map<String?, RequestBody?>?,
//        @Part file: MultipartBody.Part?,
//        @Part("items[]") items: List<String?>?
//    ): Call<Result?>?


    @GET(NetworkConstants.proposaldetail + "{id}")
    fun getProposalDetail(@Path("id") id: String): Observable<ProposalDetailModel>

    @GET(NetworkConstants.clientdetails + "{id}")
    fun clientdetails(
        @Path("id") id: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("status") status: String,
        @Query("type") type: String
    ): Observable<ClientSalesModelNew>

    @DELETE(NetworkConstants.deleteproposal + "{id}")
    fun deleteproposal(@Path("id") id: String): Observable<SuccessModel>


    @DELETE(NetworkConstants.deleteprofiledocs + "{docUrl}")
    fun deleteprofiledocs(@Path("docUrl") docUrl: String): Observable<SuccessModel>


    @DELETE(NetworkConstants.deleteexpense + "{id}")
    fun deleteexpense(@Path("id") id: String): Observable<SuccessModel>

    @GET(NetworkConstants.proposallist)
    fun getproposals(
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("status") status: String,
        @Query("type") type: String,
        @Query("contract_id") contract_id: String
    ): Observable<PorposalsListModel>


    @GET(NetworkConstants.expenseslist)
    fun expenseslist(
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("job_id") job_id: String
    ): Observable<ExpensesListModel>


    @GET(NetworkConstants.calendardetail)
    fun calendardetail(
        @Query("timezone") timezone: String,
        @Query("date") date: String,
        @Query("dateType") dateType: String
    ): Observable<CalendarDetailModel>


    @GET(NetworkConstants.jobexpenseslist)
    fun jobexpenseslist(
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Observable<ExpensesJobsListModel>


    @GET(NetworkConstants.userproposallist)
    fun userproposallist(
        @Query("user_id") user_id: String,
        @Query("status") status: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("type") type: String
    ): Observable<ParticularUserModel>


    @GET(NetworkConstants.metricjobs)
    fun metricjobsmain(
        @Query("type") type: String,
        @Query("proposal_status") proposal_status: String
    ): Observable<MetircJobDataModel>


    @GET(NetworkConstants.metricjobs)
    fun metricjobs(
        @Query("type") type: String,
        @Query("proposal_status") proposal_status: String,
//        @Query("converted_to_job") converted_to_job: String,
        @Query("user_id") user_id: String
    ): Observable<ReveneModel>


    @GET(NetworkConstants.metricjobs)
    fun metricjobsleads(
        @Query("type") type: String,
        @Query("proposal_status") proposal_status: String,
        @Query("converted_to_job") converted_to_job: String,
        @Query("user_id") user_id: String
    ): Observable<ReveneModel>


    @GET(NetworkConstants.metricjobslist)
    fun metricjobslist(
        @Query("user_id") user_id: String,
        @Query("status") status: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("type") type: String
    ): Observable<SignleUserJobsModel>


    @GET(NetworkConstants.metricjobslist)
    fun metricjobslistLead(
        @Query("user_id") user_id: String,
        @Query("status") status: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("converted_to_job") converted_to_job: String,
        @Query("type") type: String
    ): Observable<SignleUserJobsModel>


    @GET(NetworkConstants.proposalItemslist)
    fun proposalItemslist(): Observable<DefaultItemsModel>


    @POST(NetworkConstants.addclient)
    fun addclients(@PartMap map: HashMap<String, RequestBody>): Observable<SuccessModel>

//    @GET(NetworkConstants.clientslist)
//    fun clientlist(
//        @Query("membership_type") membership_type: String
//    ): Observable<ClientsListModel>

//
//    @POST(NetworkConstants.employee_verification)
//    fun employee_verification(@Body backend: JsonObject): Observable<UserProfileEntity>
//
//    @POST(NetworkConstants.client_benefits)
//    fun all_benefits(@Body backend: JsonObject): Observable<AllBenefitsEntity>
//
//
//    @POST(NetworkConstants.employee_details)
//    fun employee_details(@Body backend: JsonObject): Observable<EmployeeDetailEntity>
//
//
//    @POST(NetworkConstants.employee_edit)
//    fun employee_edit(@Body backend: JsonObject): Observable<EditProfileEntity>
//
//    @POST(NetworkConstants.employee_choices)
//    fun employee_choices(@Body backend: JsonObject): Observable<ChoiceEntity>
//
//    //logout
//    @POST(NetworkConstants.revoke)
//    fun revoke(@Query("token") token: String): Observable<String>
//
//    @POST(NetworkConstants.buying_option)
//    fun buying_option(@Body backend: JsonObject): Observable<BuyingOptionEntity>
//
//    @POST(NetworkConstants.monthly_allowance)
//    fun monthly_allowance(@Body backend: JsonObject): Observable<MonthlyAllowanceEntity>
//
//    @POST(NetworkConstants.eye_care)
//    fun eye_care(@Body backend: JsonObject): Observable<EyeCareEntity>
//
//    @POST(NetworkConstants.life_assurance_flex)
//    fun life_assurance_flex(@Body backend: JsonObject): Observable<LifeAssuranceFlexEnity>
//
//    @POST(NetworkConstants.holiday_trading)
//    fun holiday_trading(@Body backend: JsonObject): Observable<HolidayTradingEntity>
//
//    @POST(NetworkConstants.private_medical)
//    fun private_medical(@Body backend: JsonObject): Observable<PrivateMedicalEntity>
//
//    @POST(NetworkConstants.private_medical_selectoption)
//    fun private_medical_selectoption(@Body backend: JsonObject): Observable<PrivateMedicalOptionsEntity>
//
//    @POST(NetworkConstants.private_medical_updateplans)
//    fun private_medical_updateplans(@Body backend: JsonObject): Observable<PrivateMedicalOptionsEntity>
//
//    @POST(NetworkConstants.pension_detail)
//    fun pension_detail(@Body backend: JsonObject): Observable<GroupPensionEntity>
//
//    @POST(NetworkConstants.pension_detail_optIn)
//    fun pension_detail_optIn(@Body backend: JsonObject): Observable<PensionOptinEntity>
//
//    @POST(NetworkConstants.workplace_isa)
//    fun workplace_isa(@Body backend: JsonObject): Observable<WorkplaceISAEntity>
//
//    @POST(NetworkConstants.income_protection)
//    fun income_protection(@Body backend: JsonObject): Observable<IncomeProtectionEntity>
//
//    @POST(NetworkConstants.life_assurance_beneficiaries)
//    fun life_assurance_beneficiaries(@Body backend: JsonObject): Observable<AddBeneficiaryEntity>
//
//    @POST(NetworkConstants.group_cash_plan)
//    fun group_cash_plan(@Body backend: JsonObject): Observable<CashPlanEntity>
//
//    @POST(NetworkConstants.group_cashPlan_selectoption)
//    fun group_cashPlan_selectoption(@Body backend: JsonObject): Observable<CashPlanOptionsEntity>
//
//    @POST(NetworkConstants.group_cashplan_update)
//    fun group_cashplan_update(@Body backend: JsonObject): Observable<CashPlanOptionsEntity>
//
//    @POST(NetworkConstants.life_insurance)
//    fun life_insurance(@Body backend: JsonObject): Observable<LifeInsuranceEntity>
//
//    @POST(NetworkConstants.work_loan)
//    fun work_loan(@Body backend: JsonObject): Observable<WorkLoanEntity>
//
//    @POST(NetworkConstants.group_dental)
//    fun group_dental(@Body backend: JsonObject): Observable<GroupDentalEntity>
//
//
//    @POST(NetworkConstants.eye_care_issuevoucher)
//    fun eye_care_issuevoucher(@Body backend: JsonObject): Observable<EyeCareEntity>
//
//    @POST(NetworkConstants.adviser_meeting)
//    fun adviser_meeting(@Body backend: JsonObject): Observable<AdviserMeetingsEntity>

//    @GET(NetworkConstants.drug)
//    fun searchDrug(@Query("drug_name") drug_name: String): Observable<SearchDrugEntity>

    ////coupon?pricebook_entry_id=01uW000000QwZuvIAF&pharmacy_address_id=a07W0000004v7WPIAY&membership_type=Silver

}
