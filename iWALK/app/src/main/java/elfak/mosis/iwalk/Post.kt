package elfak.mosis.iwalk

class Post {
    private var postId: String? = null
    private var postDescription: String? = null
    private var postDate: String? = null
    private var postTime: String? = null
    private var postUserId: String? = null
    private var postDogImage1: String? = null
    private var postDogImage2: String? = null
    private var postStatus: String? = null
    private var postWalkerId: String? = null

    constructor(
        postId: String?,
        postDescription: String?,
        postDate: String?,
        postTime: String?,
        postUserId: String?,
        //postDogImage1: String?,
        //postDogImage2: String?
    ) {
        this.postId = postId
        this.postDescription = postDescription
        this.postDate = postDate
        this.postTime = postTime
        this.postUserId = postUserId
        //this.postDogImage1 = postDogImage1
        //this.postDogImage2 = postDogImage2
    }

    constructor(
        postId: String?,
        postDescription: String?,
        postDate: String?,
        postTime: String?,
        postUserId: String?,
        postDogImage1: String?,
        postDogImage2: String?,
        postStatus: String?,
        postWalkerId: String?
    ) {
        this.postId = postId
        this.postDescription = postDescription
        this.postDate = postDate
        this.postTime = postTime
        this.postUserId = postUserId
        this.postDogImage1 = postDogImage1
        this.postDogImage2 = postDogImage2
        this.postStatus = postStatus
        this.postWalkerId = postWalkerId
    }

    fun getPostId(): String? {
        return postId
    }

    fun setPostId(postId: String?) {
        this.postId = postId
    }

    fun getPostDescription(): String? {
        return postDescription
    }

    fun setPostDescription(postDescription: String?) {
        this.postDescription = postDescription
    }

    fun getPostDate(): String? {
        return postDate
    }

    fun setPostDate(postDate: String?) {
        this.postDate = postDate
    }

    fun getPostTime(): String? {
        return postTime
    }

    fun setPostTime(postTime: String?) {
        this.postTime = postTime
    }

    fun getPostUserId(): String? {
        return postUserId
    }

    fun setPostUserId(postUserId: String?) {
        this.postUserId = postUserId
    }

    fun getPostDogImage1(): String? {
        return postDogImage1
    }

    fun setPostDogImage1(postDogImage1: String?) {
        this.postDogImage1 = postDogImage1
    }

    fun getPostDogImage2(): String? {
        return postDogImage2
    }

    fun setPostDogImage2(postDogImage2: String?) {
        this.postDogImage2 = postDogImage2
    }

    fun getPostStatus(): String? {
        return postStatus
    }

    fun setPostStatus(postStatus: String?) {
        this.postStatus = postStatus
    }

    fun getPostWalkerId(): String? {
        return postWalkerId
    }

    fun setPostWalkerId(postWalkerId: String?) {
        this.postWalkerId = postWalkerId
    }
}