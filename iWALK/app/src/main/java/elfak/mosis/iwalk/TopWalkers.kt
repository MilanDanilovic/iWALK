package elfak.mosis.iwalk

class TopWalkers {
    private var walkerId: String? = null
    private var walkerUsername: String? = null
    private var walkerImage: String? = null
    private var walkerScore: Number? = null

    constructor(
        walkerId: String?,
        walkerUsername: String?,
        walkerImage: String?,
        walkerScore: Number
    ) {
        this.walkerId = walkerId
        this.walkerUsername = walkerUsername
        this.walkerImage = walkerImage
        this.walkerScore = walkerScore
    }

    fun getWalkerId(): String? {
        return walkerId
    }

    fun setWalkerId(walkerId: String?) {
        this.walkerId = walkerId
    }

    fun getWalkerUsername(): String? {
        return walkerUsername
    }

    fun setWalkerUsername(walkerUsername: String?) {
        this.walkerUsername = walkerUsername
    }

    fun getWalkerImage(): String? {
        return walkerImage
    }

    fun setWalkerImaged(walkerImage: String?) {
        this.walkerImage = walkerImage
    }

    fun getWalkerScore(): Number? {
        return walkerScore
    }

    fun setWalkerScore(walkerScore: Number?) {
        this.walkerScore = walkerScore
    }
}