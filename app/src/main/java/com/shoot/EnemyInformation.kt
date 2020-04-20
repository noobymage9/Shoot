package com.shoot

data class EnemyInformation(val name: String, var endPointID : String, val method: String) {

    fun isTitle() = name.equals(NEARBY) ||
            name.equals(INTERNET)

    override fun equals(other: Any?): Boolean {
        return name == (other as EnemyInformation).name && endPointID == other.endPointID
    }

    companion object {
        val NEARBY = "nearby connection"
        val INTERNET = "internet"
    }

}