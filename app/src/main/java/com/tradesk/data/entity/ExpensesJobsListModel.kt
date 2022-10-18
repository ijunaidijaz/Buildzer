package com.tradesk.data.entity

data class ExpensesJobsListModel(
    val `data`: DataExpensesJobs,
    val message: String,
    val status: Int
)

data class DataExpensesJobs(
    val expensesList: List<ExpensesExpensesJobs>,
    val limit: Int,
    val page: Int,
    val totalPages: Int
)

data class ExpensesExpensesJobs(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val additional_images: List<Any>,
    val address: AddressExpensesJobs,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val description: String,
    val end_date: String,
    val expenses: List<Expense>,
    val image: String,
    val project_name: String,
    val source: String,
    val start_date: String,
    val status: String,
    val type: String,
    val updatedAt: String,
    val users_assigned: List<UsersAssignedExpensesJobs>
)

data class AddressExpensesJobs(
    val city: String,
    val location: LocationExpensesJobs,
    val state: String,
    val street: String,
    val zipcode: String
)

data class Expense(
    val _id: String,
    val amount: String,
    val image: String,
    val title: String
)

data class UsersAssignedExpensesJobs(
    val _id: String,
    val user_id: String
)

data class LocationExpensesJobs(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)