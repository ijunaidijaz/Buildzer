package com.tradesk.di.builder


import com.app.picnic360.appCode.loginModule.*
import com.tradesk.appCode.MainActivity
import com.tradesk.appCode.PermitsImageActivity
import com.tradesk.appCode.addExpensesModule.AddExpenseActivity
import com.tradesk.appCode.addExpensesModule.ExpensesListActivity
import com.tradesk.appCode.addExpensesModule.ExpensesModule
import com.tradesk.appCode.analyticsModule.MainAnalyticsActivity
import com.tradesk.appCode.analyticsModule.analyitcsJobsModule.AnalyticsJobsDetailsProvider
import com.tradesk.appCode.analyticsModule.analyticsHome.AnalyticsHomeProvider
import com.tradesk.appCode.analyticsModule.analyticsIncomeExpense.AnalyticsIncomeExpenseProvider
import com.tradesk.appCode.analyticsModule.analyticsJobs.AnalyticsJobsProvider
import com.tradesk.appCode.analyticsModule.analyticsTimesheet.TimesheetProvider
import com.tradesk.appCode.analyticsModule.analyticsUsers.AnalyticsUsersProvider
import com.tradesk.appCode.analyticsModule.analyticsUsers.analyticsUserDetails.AnalyticsUsersDetailProvider
import com.tradesk.appCode.calendarModule.CalendarProvider
import com.tradesk.appCode.calendarModule.monthDetailModule.CalendarActivity
import com.tradesk.appCode.calendarModule.monthDetailModule.CalendarModule
import com.tradesk.appCode.forgotPassword.ForgotActivity
import com.tradesk.appCode.home.HomeProvider
import com.tradesk.appCode.home.addJobsModule.AddJobActivity
import com.tradesk.appCode.home.addJobsModule.AddJobsModule
import com.tradesk.appCode.home.addLeadsModule.AddLeadActivity
import com.tradesk.appCode.home.addLeadsModule.AddLeadsModule
import com.tradesk.appCode.home.customersModule.CustomersActivity
import com.tradesk.appCode.home.leadsDetail.LeadDetailModule
import com.tradesk.appCode.home.leadsDetail.LeadsActivity
import com.tradesk.appCode.home.leadsDetail.leadsNotesModule.AddLeadNoteslModule
import com.tradesk.appCode.home.leadsDetail.leadsNotesModule.LeadNotesActivity
import com.tradesk.appCode.home.leadsDetail.leadsNotesModule.addNotesModule.AddNotesActivity
import com.tradesk.appCode.home.salePersonModule.AddSalesModule
import com.tradesk.appCode.home.salePersonModule.AddSalesPersonActivity
import com.tradesk.appCode.home.salePersonModule.SalesPersonActivity
import com.tradesk.appCode.jobsModule.JobsProvider
import com.tradesk.appCode.jobsModule.jobDetailModule.JobDetailActivity
import com.tradesk.appCode.jobsModule.jobDetailModule.OnGoingJobsActivity
import com.tradesk.appCode.jobsModule.jobDetailModule.jobChatModule.ChatActivity
import com.tradesk.appCode.jobsModule.jobDetailModule.jobChatModule.ChatModule
import com.tradesk.appCode.jobsModule.jobDetailModule.jobChatModule.ChatUsersActivity
import com.tradesk.appCode.loginModule.LoginActivity
import com.tradesk.appCode.notificationsModule.NotificationProvider
import com.tradesk.appCode.profileModule.ProfileProvider
import com.tradesk.appCode.profileModule.gallaryModule.GallaryActivity
import com.tradesk.appCode.profileModule.gallaryModule.subGallaryModule.SubGallaryActivity
import com.tradesk.appCode.profileModule.myProfileModule.MyProfileActivity
import com.tradesk.appCode.profileModule.proposalsModule.ProposalsActivity
import com.tradesk.appCode.profileModule.proposalsModule.ProposalsModule
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.AddProposalActivity
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.InvoicesActivity
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.PDFViewNewActivity
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.ProposaltemsActivity
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.addClientModule.AddClientActivity
import com.tradesk.appCode.profileModule.settingsModule.SettingsActivity
import com.tradesk.appCode.profileModule.settingsModule.changePassword.ChangePasswordActivity
import com.tradesk.appCode.profileModule.timesheetModule.TimesheetActivity
import com.tradesk.appCode.profileModule.timesheetModule.TimesheetDetailModule
import com.tradesk.appCode.profileModule.timesheetModule.jobTimesheetModule.JobTimeSheetActivity
import com.tradesk.appCode.profileModule.timesheetModule.jobTimesheetModule.JobTimesheetModule
import com.tradesk.appCode.profileModule.usersContModule.UsersContrActivity
import com.tradesk.appCode.signupModule.SignupActivity
import com.tradesk.appCode.splashModule.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

//    @ContributesAndroidInjector()
//    abstract fun bindHowItWorksActivity(): HowItWorksActivity

    @ContributesAndroidInjector(modules = [(LoginModule::class)])
    abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [(ProfileModule::class)])
    abstract fun bindProfileActivity(): MyProfileActivity

    @ContributesAndroidInjector()
    abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector()
    abstract fun bindSettingActivity(): SettingsActivity

    @ContributesAndroidInjector(modules = [(SignupModule::class)])
    abstract fun bindSignupActivity(): SignupActivity


    @ContributesAndroidInjector(modules = [(SignupModule::class)])
    abstract fun bindForgotActivity(): ForgotActivity


    @ContributesAndroidInjector(modules = [(AddClientModule::class)])
    abstract fun bindAddClientActivity(): AddClientActivity


    @ContributesAndroidInjector(modules = [(AddClientModule::class)])
    abstract fun bindCustomerActivity(): CustomersActivity


    @ContributesAndroidInjector(modules = [(AddSalesModule::class)])
    abstract fun bindAddSalesActivity(): AddSalesPersonActivity


    @ContributesAndroidInjector(modules = [(AddSalesModule::class)])
    abstract fun bindSalesActivity(): SalesPersonActivity


    @ContributesAndroidInjector(modules = [(AddSalesModule::class)])
    abstract fun bindContrActivity(): UsersContrActivity


    @ContributesAndroidInjector(modules = [(AddLeadsModule::class)])
    abstract fun bindAddLeadActivity(): AddLeadActivity


    @ContributesAndroidInjector(modules = [(AddLeadNoteslModule::class)])
    abstract fun bindLeadNotesActivity(): LeadNotesActivity


    @ContributesAndroidInjector(modules = [(AddLeadNoteslModule::class)])
    abstract fun bindAddNotesActivity(): AddNotesActivity


    @ContributesAndroidInjector(modules = [(AddJobsModule::class)])
    abstract fun bindAddJobActivity(): AddJobActivity


    @ContributesAndroidInjector(modules = [(LeadDetailModule::class)])
    abstract fun bindLeadActivity(): LeadsActivity


    @ContributesAndroidInjector(modules = [(ChatModule::class)])
    abstract fun bindChatActivity(): ChatActivity

    @ContributesAndroidInjector(modules = [(ChatModule::class)])
    abstract fun bindChatUsersActivity(): ChatUsersActivity

    @ContributesAndroidInjector(modules = [(LeadDetailModule::class)])
    abstract fun bindJobDetailActivity(): JobDetailActivity


    @ContributesAndroidInjector(modules = [(JobTimesheetModule::class)])
    abstract fun bindJobTimesheetActivity(): JobTimeSheetActivity


    @ContributesAndroidInjector(modules = [(LeadDetailModule::class)])
    abstract fun bindOngoingJobDetailActivity(): OnGoingJobsActivity


    @ContributesAndroidInjector(modules = [(LeadDetailModule::class)])
    abstract fun bindGallaryActivity(): GallaryActivity


    @ContributesAndroidInjector(modules = [(LeadDetailModule::class)])
    abstract fun bindSubGallaryActivity(): SubGallaryActivity


    @ContributesAndroidInjector(modules = [(LeadDetailModule::class)])
    abstract fun bindPermitsActivity(): PermitsImageActivity


    @ContributesAndroidInjector(modules = [(ChangePasswordModule::class)])
    abstract fun bindChangeActivity(): ChangePasswordActivity


    @ContributesAndroidInjector(modules = [(ProposalsModule::class)])
    abstract fun bindProposalsActivity(): ProposalsActivity


    @ContributesAndroidInjector(modules = [(ProposalsModule::class)])
    abstract fun bindAddProposalsActivity(): AddProposalActivity


    @ContributesAndroidInjector(modules = [(ProposalsModule::class)])
    abstract fun bindPDFViewActivity(): PDFViewNewActivity


    @ContributesAndroidInjector(modules = [(ProposalsModule::class)])
    abstract fun bindInvoiceActivity(): InvoicesActivity


    @ContributesAndroidInjector(modules = [(ProposalsModule::class)])
    abstract fun bindAddProposalsItemsActivity(): ProposaltemsActivity


    @ContributesAndroidInjector(modules = [(ExpensesModule::class)])
    abstract fun bindExpensesListActivity(): ExpensesListActivity


    @ContributesAndroidInjector(modules = [(CalendarModule::class)])
    abstract fun bindCalendarActivity(): CalendarActivity


    @ContributesAndroidInjector(modules = [(ExpensesModule::class)])
    abstract fun bindAddExpensesActivity(): AddExpenseActivity


    @ContributesAndroidInjector(modules = [(TimesheetDetailModule::class)])
    abstract fun bindTimesheetActivity(): TimesheetActivity


    @ContributesAndroidInjector(modules = [(HomeProvider::class), (JobsProvider::class), (CalendarProvider::class), (ProfileProvider::class), (NotificationProvider::class)])
    abstract fun bindMainActivity(): MainActivity


    @ContributesAndroidInjector(modules = [(AnalyticsHomeProvider::class), (TimesheetProvider::class), (AnalyticsJobsDetailsProvider::class), (AnalyticsJobsProvider::class), (CalendarProvider::class), (AnalyticsUsersProvider::class), (AnalyticsUsersDetailProvider::class), (AnalyticsIncomeExpenseProvider::class)])
    abstract fun bindMainAnalyicsActivity(): MainAnalyticsActivity

}