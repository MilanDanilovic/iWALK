package elfak.mosis.iwalk

class FavouriteWalker {
    private var walkerImage: String? = null
    private var walkerUsername: String? = null
    private var walkerId: String? = null

    constructor(
        walkerImage: String?,
        walkerUsername: String?,
        walkerId: String?
    ) {
        this.walkerImage = walkerImage
        this.walkerUsername = walkerUsername
        this.walkerId = walkerId
    }

    fun getWalkerImage(): String? {
        return walkerImage
    }

    fun setWalkerImage(walkerImage: String?) {
        this.walkerImage = walkerImage
    }

    fun getWalkerUsername(): String? {
        return walkerUsername
    }

    fun setWalkerUsername(walkerUsername: String?) {
        this.walkerUsername = walkerUsername
    }

    fun getWalkerId(): String? {
        return walkerId
    }

    fun setWalkerId(walkerId: String?) {
        this.walkerId = walkerId
    }
}