package com.tradesk.data.entity

data class SignleUserJobsModel(
    val `data`: DataSignleUser,
    val message: String,
    val status: Int
)

data class DataSignleUser(
    val completedJobs: Int,
    val followUpJobs: Int,
    val jobsData: List<JobsDataSignleUser>,
    val limit: Int,
    val ongoingJobs: Int,
    val page: Int,
    val pendingJobs: Int,
    val totalJobs: Int,
    val totalPages: Int
)

data class JobsDataSignleUser(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val additional_images: List<AdditionalImageSignleUser>,
    val address: AddressSignleUser,
    val converted_to_job: Boolean,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val description: String,
    val image: String,
    val project_name: String,
    val source: String,
    val status: String,
    val type: String,
    val updatedAt: String,
    val users_assigned: List<UsersAssignedSignleUser>
)

data class AdditionalImageSignleUser(
    val _id: String,
    val image: String
)

data class AddressSignleUser(
    val city: String,
    val location: LocationSignleUser,
    val state: String,
    val street: String,
    val zipcode: String
)

data class UsersAssignedSignleUser(
    val _id: String,
    val user_id: String
)

data class LocationSignleUser(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)