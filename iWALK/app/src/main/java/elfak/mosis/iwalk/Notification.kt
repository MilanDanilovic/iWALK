package elfak.mosis.iwalk

class Notification {
    private var notificationId: String? = null
    private var description: String? = null
    private var date: String? = null
    private var time: String? = null
    private var status: String? = null
    private var senderId: String? = null
    private var receiverId: String? = null
    private var dogImage1: String? = null
    private var dogImage2: String? = null

    constructor(
        notificationId: String?,
        description: String?,
        date: String?,
        time: String?,
        status: String?,
        senderId: String?,
        receiverId: String?,
        dogImage1: String?,
        dogImage2: String?
    ) {
        this.notificationId = notificationId
        this.description = description
        this.date = date
        this.time = time
        this.status = status
        this.senderId = senderId
        this.receiverId = receiverId
        this.dogImage1 = dogImage1
        this.dogImage2 = dogImage2
    }

    fun getNotificationId(): String? {
        return notificationId
    }

    fun setNotificationId(notificationId: String?) {
        this.notificationId = notificationId
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    fun getDate(): String? {
        return date
    }

    fun setDate(date: String?) {
        this.date = date
    }

    fun getTime(): String? {
        return time
    }

    fun setTime(time: String?) {
        this.time = time
    }

    fun getSenderId(): String? {
        return senderId
    }

    fun setSenderId(senderId: String?) {
        this.senderId = senderId
    }

    fun getDogImage1(): String? {
        return dogImage1
    }

    fun setDogImage1(dogImage1: String?) {
        this.dogImage1 = dogImage1
    }

    fun getDogImage2(): String? {
        return dogImage2
    }

    fun setDogImage2(dogImage2: String?) {
        this.dogImage2 = dogImage2
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getReceiverId(): String? {
        return receiverId
    }

    fun setReceiverId(receiverId: String?) {
        this.receiverId = receiverId
    }
}