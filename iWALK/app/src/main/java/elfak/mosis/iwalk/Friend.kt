package elfak.mosis.iwalk

class Friend {
    private var friendId: String? = null
    private var friendUsername: String? = null
    private var friendImage: String? = null

    constructor(
        friendId: String?,
        friendUsername: String?,
        friendImage: String?
    ) {
        this.friendId = friendId
        this.friendUsername = friendUsername
        this.friendImage = friendImage
    }

    fun getFriendId(): String? {
        return friendId
    }

    fun setFriendId(friendId: String?) {
        this.friendId = friendId
    }

    fun getFriendUsername(): String? {
        return friendUsername
    }

    fun setFriendUsername(friendUsername: String?) {
        this.friendUsername = friendUsername
    }

    fun getFriendImage(): String? {
        return friendImage
    }

    fun setFriendImage(friendImage: String?) {
        this.friendImage = friendImage
    }
}