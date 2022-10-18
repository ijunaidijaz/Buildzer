package com.tradesk.data.entity

data class SingleUserProposalModel(
    val `data`: DataSingleUser,
    val message: String,
    val status: Int
)

data class DataSingleUser(
    val completedProposal: Int,
    val inprocessProposal: Int,
    val limit: Int,
    val page: Int,
    val pendingProposal: Int,
    val proposalData: List<ProposalDataSingleUser>,
    val totalPages: Int,
    val totalProposal: Int
)

data class ProposalDataSingleUser(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val client_id: String,
    val client_signature: String,
    val contract_id: String,
    val createdAt: String,
    val created_by: String,
    val date: String,
    val deleted: Boolean,
    val estimate: String,
    val extra_info: String,
    val images: List<Any>,
    val invoice_url: String,
    val items: List<ItemSingleUser>,
    val jobs: List<JobSingleUser>,
    val mail_sent: Boolean,
    val status: String,
    val subtotal: String,
    val tax: String,
    val total: String,
    val type: String,
    val updatedAt: String
)

data class ItemSingleUser(
    val _id: String,
    val active: Boolean,
    val amount: String,
    val createdAt: String,
    val created_by: Any,
    val deleted: Boolean,
    val description: String,
    val quantity: String,
    val title: String,
    val type: String,
    val updatedAt: String
)

data class JobSingleUser(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val additional_images: List<AdditionalImageSingleUser>,
    val address: AddressSingleUser,
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
    val users_assigned: List<UsersAssignedSingleUser>
)

data class AdditionalImageSingleUser(
    val _id: String,
    val image: String
)

data class AddressSingleUser(
    val city: String,
    val location: LocationSingleUser,
    val state: String,
    val street: String,
    val zipcode: String
)

data class UsersAssignedSingleUser(
    val _id: String,
    val user_id: String
)

data class LocationSingleUser(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)