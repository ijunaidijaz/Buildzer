package com.tradesk.di.module

data class MapModel(
    val plus_code: PlusCode,
    val results: List<Result>,
    val status: String
)

data class PlusCode(
    val compound_code: String,
    val global_code: String
)

data class Result(
    val address_components: List<AddressComponent>,
    val formatted_address: String,
    val geometry: Geometry,
    val place_id: String,
    val plus_code: PlusCode,
    val postcode_localities: List<String>,
    val types: List<String>
)

data class Geometry(
    val bounds: Bounds,
    val location: Location,
    val location_type: String,
    val viewport: Viewport
)

data class Bounds(
    val northeast: Northeast,
    val southwest: Southwest
)

data class Southwest(
    val lat: Double,
    val lng: Double
)

data class Northeast(
    val lat: Double,
    val lng: Double
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Viewport(
    val northeast: Northeast,
    val southwest: Southwest
)

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)