package com.shoot

data class EnemyInformation(val name: String, var endPointID : String) {

    fun isTitle() = name.equals(BLUETOOTH) ||
                name.equals(LOCAL_AREA_NETWORK) ||
            name.equals(INTERNET)

    override fun equals(other: Any?): Boolean {
        return name == (other as EnemyInformation).name && endPointID == other.endPointID
    }
    companion object {
        val BLUETOOTH = "bluetooth"
        val LOCAL_AREA_NETWORK = "local_area_network"
        val INTERNET = "internet"
    }

}