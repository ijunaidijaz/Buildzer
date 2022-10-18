package com.tradesk.data.entity

data class ParticularUserModel(
    val `data`: DataParticular,
    val message: String,
    val status: Int
)

data class DataParticular(
    val completedProposal: Int,
    val inprocessProposal: Int,
    val limit: Int,
    val page: Int,
    val pendingProposal: Int,
    val proposalData: List<ProposalData>,
    val totalPages: Int,
    val totalProposal: Int
)

data class ProposalData(
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
    val images: List<String>,
    val invoice_url: String,
    val items: List<ItemParticular>,
    val jobs: List<JobParticular>,
    val mail_sent: Boolean,
    val status: String,
    val subtotal: String,
    val tax: String,
    val total: String,
    val type: String,
    val updatedAt: String
)

data class ItemParticular(
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

data class JobParticular(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val additional_images: List<Any>,
    val address: AddressParticular,
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
    val users_assigned: List<UsersAssignedParticular>
)

data class AddressParticular(
    val city: String,
    val location: LocationParticular,
    val state: String,
    val street: String,
    val zipcode: String
)

data class UsersAssignedParticular(
    val _id: String,
    val user_id: String
)

data class LocationParticular(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)