data class Node (var field: Matrix, var indexOfField: Int = 0, var distance: Int = 0, var step: Int = 1) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (!field.contentDeepEquals(other.field)) return false

        return true
    }

    override fun hashCode(): Int {
        return field.contentDeepHashCode()
    }
}