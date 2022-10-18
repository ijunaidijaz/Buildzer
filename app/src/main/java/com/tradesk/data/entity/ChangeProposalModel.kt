package com.tradesk.data.entity
import kotlinx.serialization.SerialName

data class ChangeProposalStatus (

    @SerialName("status") var status : Int,
    @SerialName("message") var message : String,
    @SerialName("data") var data : MData

)

data class MProposal (

    @SerialName("_id") var Id : String,
    @SerialName("client_id") var clientId : String,
    @SerialName("date") var date : String,
    @SerialName("estimate") var estimate : String,
    @SerialName("items") var items : List<Items>,
    @SerialName("subtotal") var subtotal : String,
    @SerialName("tax") var tax : String,
    @SerialName("total") var total : String,
    @SerialName("images") var images : List<String>,
    @SerialName("client_signature") var clientSignature : String,
    @SerialName("extra_info") var extraInfo : String,
    @SerialName("contract_id") var contractId : String,
    @SerialName("status") var status : String,
    @SerialName("type") var type : String,
    @SerialName("invoice_url") var invoiceUrl : String,
    @SerialName("created_by") var createdBy : String,
    @SerialName("active") var active : Boolean,
    @SerialName("deleted") var deleted : Boolean,
    @SerialName("mail_sent") var mailSent : Boolean,
    @SerialName("createdAt") var createdAt : String,
    @SerialName("updatedAt") var updatedAt : String,
    @SerialName("__v") var _v : Int

)
data class MData (

    @SerialName("proposal") var proposal : MProposal

)
data class Items (

    @SerialName("title") var title : String,
    @SerialName("description") var description : String,
    @SerialName("quantity") var quantity : String,
    @SerialName("amount") var amount : String,
    @SerialName("type") var type : String,
    @SerialName("created_by") var createdBy : String,
    @SerialName("active") var active : Boolean,
    @SerialName("deleted") var deleted : Boolean,
    @SerialName("_id") var Id : String,
    @SerialName("createdAt") var createdAt : String,
    @SerialName("updatedAt") var updatedAt : String

)
