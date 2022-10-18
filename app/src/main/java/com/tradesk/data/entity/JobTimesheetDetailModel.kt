package com.tradesk.data.entity

data class JobTimesheetDetailModel(
    val `data`: DataJobTimesheet,
    val message: String,
    val status: Int
)

data class DataJobTimesheet(
    val job_details: List<JobDetail>
)

data class JobDetail(
    val JobSheettotalTime: String,
    val _id: String,
    val additional_images: List<Any>,
    val address: AddressJobTimesheet,
    val created_by: String,
    val description: String,
    val image: String,
    val job_log_time: List<JobLogTime>,
    val project_name: String,
    val source: String,
    val status: String,
    val type: String
)

data class AddressJobTimesheet(
    val city: String,
    val location: LocationJobTimesheet,
    val state: String,
    val street: String,
    val zipcode: String
)

data class JobLogTime(
    val _id: String,
    val clockedIn_address: ClockedInAddressJobTimesheet,
    val clockedOut_address: ClockedOutAddressJobTimesheet,
    val end_date: Any,
    val job_id: String,
    val start_date: String,
    val status: String,
    val total_time: String
)

data class LocationJobTimesheet(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)

data class ClockedInAddressJobTimesheet(
    val city: String,
    val location: LocationXJobTimesheet,
    val state: String,
    val street: String,
    val zipcode: String
)

data class ClockedOutAddressJobTimesheet(
    val city: String,
    val location: LocationXXJobTimesheet,
    val state: String,
    val street: String,
    val zipcode: String
)

data class LocationXJobTimesheet(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)

data class LocationXXJobTimesheet(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)