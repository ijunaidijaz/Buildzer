package com.tradesk.data.entity

data class TimeSheetModel(
    val `data`: DataTimeSheet,
    val message: String,
    val status: Int
)

data class DataTimeSheet(
    val clockedInJob: List<ClockedInJobTimeSheet>,
    val jobsData: List<JobsDataTimeSheet>,
    val limit: Int,
    val page: Int,
    val totalPages: Int,
    val total_time: TotalTimeTimeSheet
)

data class ClockedInJobTimeSheet(
    val _id: String,
    val address: AddressTimeSheet,
    val end_date: Any,
    val job_id: String,
    val start_date: String,
    val status: String,
    val total_time: String
)

data class JobsDataTimeSheet(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val additional_images: List<AdditionalImageTimeSheet>,
    val address: AddressXTimeSheet,
    val client: List<ClientTimeSheet>,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val description: String,
    val end_date: String,
    val image: String,
    val job_log_time: JobLogTimeTimeSheet,
    val project_name: String,
    val source: String,
    val start_date: String,
    val status: String,
    val type: String,
    val updatedAt: String,
    val users_assigned: List<UsersAssignedTimeSheet>
)

data class TotalTimeTimeSheet(
    val _id: Any,
    val totalLogTime: String,
    val totalclockedIn: String,
    val totalclockedOut: String
)

data class AddressTimeSheet(
    val city: String,
    val location: LocationTimeSheet,
    val state: String,
    val street: String,
    val zipcode: String
)

data class LocationTimeSheet(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)

data class AdditionalImageTimeSheet(
    val _id: String,
    val image: String
)

data class AddressXTimeSheet(
    val city: String,
    val location: LocationX,
    val state: String,
    val street: String,
    val zipcode: String
)

data class ClientTimeSheet(
    val _id: String,
    val name: String,
    val phone_no: String,
    val type: String
)

data class JobLogTimeTimeSheet(
    val _id: String,
    val address: AddressXXTimeSheet,
    val end_date: Any,
    val job_id: String,
    val start_date: String,
    val status: String,
    val total_time: String
)

data class UsersAssignedTimeSheet(
    val _id: String,
    val user_id: String
)

data class LocationXTimeSheet(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)

data class AddressXXTimeSheet(
    val city: String,
    val location: LocationXXTimeSheet,
    val state: String,
    val street: String,
    val zipcode: String
)

data class LocationXXTimeSheet(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)