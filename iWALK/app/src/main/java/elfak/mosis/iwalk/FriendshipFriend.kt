package elfak.mosis.iwalk

class FriendshipFriend {
    private var friendId: String? = null

    constructor(
        friendId: String?
    ) {
        this.friendId = friendId
    }

    fun getFriendshipFriendId(): String? {
        return friendId
    }

    fun setFriendshipFriendId(friendId: String?) {
        this.friendId = friendId
    }
}
