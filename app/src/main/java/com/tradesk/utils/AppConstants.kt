package com.tradesk.utils

import java.util.regex.Pattern


object AppConstants {
    val PATTERN =
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!*@#\$%^&+=])(?=\\S+\$).{6,}\$")
    const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 2010
    const val REQUEST_CODE_ASK_CAMERA_MULTIPLE_PERMISSIONS = 1005
    const val CAMERA_PERMISSION = android.Manifest.permission.CAMERA
    const val LOCATION_SERVICE = "location"
    const val ACCESS_FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION
    const val ACCESS_COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION
    const val REQUEST_ASK_LOCATION_MULTIPLE_PERMISSIONS = 1003
    const val READ_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
    const val RECORD_AUDIO = android.Manifest.permission.RECORD_AUDIO
    const val WRITE_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE

    const val REQUEST_CAMERA = 1001
    const val REQUEST_GALLERY = 1002
    const val DIALOG_DENY = 0
    const val DIALOG_NEVER_ASK = 1

    internal val sdfServer = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    internal val sdfMMMddyyyy = "MMM/dd/yyyy"
    internal val sdfddMMyyy = "dd/MM/yyyy"
    internal val sdfddMMyyyHHss = "dd/MM/yyyy  HH:ss"
    internal val sdfhhmma = "hh:mm a"
    internal val LNG = "lng"
    internal var TAB = 0
    internal val PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN"
    internal val PREF_KEY_CURRENT_USER_NAME = "PREF_KEY_CURRENT_USER_NAME"
    internal val PREF_KEY_CURRENT_USER_EMAIL = "PREF_KEY_CURRENT_USER_EMAIL"

    internal val SCHEME_GROUP_PENSION = "Group Pension"
    internal val SCHEME_LIFE_ASSURANCE_FLEX = "Life Assurance Flex"
    internal val SCHEME_LIFE_ASSURANCE = "Life Assurance"
    internal val SCHEME_GROUP_PRIVATE_MEDICAL = "Group Private Medical"
    internal val SCHEME_GROUP_CASH_PLAN = "Group Cash Plan"
    internal val SCHEME_CRITICAL_ILLNESS = "Critical Illness"
    internal val SCHEME_GROUP_DENTAL = "Group Dental"
    internal val SCHEME_WORKPLACE_ISA = "Workplace ISA"
    internal val SCHEME_WORKPLACE_LOANS = "Workplace Loans"
    internal val SCHEME_EYECARE_VOUCHER = "Eyecare Voucher"
    internal val SCHEME_HOLIDAY_TRADING = "Holiday Trading"
    internal val SCHEME_MONTHLY_ALLOWANCE = "Monthly Allowance"
    internal val SCHEME_BUYING_OPTIONS = "Buying Options"
    internal val SCHEME_EMPLOYEE_ASSISTANCE_PROGRAM = "Employee Assistance Program"
    internal val SCHEME_FLAT_PAGE = "Flat Page Scheme"
    internal val SCHEME_INCOME_PROTECTION = "Income Protection"
    internal val SCHEME_PORTAL_FEES = "Portal Fees"
    internal val SCHEME_PRIVATE_CLIENTS = "Private Clients"
    internal val SCHEME_ADVISER_MEETING = "Adviser Meetings"


}