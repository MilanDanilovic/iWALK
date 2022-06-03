package elfak.mosis.iwalk

class Pet {
    private var petId: String? = null
    private var petName: String? = null
    private var petBreed: String? = null
    private var petWeight: String? = null
    private var petDescription: String? = null
    private var petUserId: String? = null
    private var petImage: String? = null

    constructor(
        petId: String?,
        petName: String?,
        petBreed: String?,
        petWeight: String?,
        petDescription: String?,
        petUserId: String?
        //petImage: String?
    ) {
        this.petId = petId
        this.petName = petName
        this.petBreed = petBreed
        this.petWeight = petWeight
        this.petDescription = petDescription
        this.petUserId = petUserId
        //this.petImage = petImage
    }


    fun getPetId(): String? {
        return petId
    }

    fun setPetId(petId: String?) {
        this.petId = petId
    }

    fun getPetName(): String? {
        return petName
    }

    fun setPetName(petName: String?) {
        this.petName = petName
    }

    fun getPetBreed(): String? {
        return petBreed
    }

    fun setPetBreed(petBreed: String?) {
        this.petBreed = petBreed
    }

    fun getPetWeight(): String? {
        return petWeight
    }

    fun setPetWeight(petWeight: String?) {
        this.petWeight = petWeight
    }

    fun getPetDescription(): String? {
        return petDescription
    }

    fun setPetDescription(petDescription: String?) {
        this.petDescription = petDescription
    }

    fun getPetUserId(): String? {
        return petUserId
    }

    fun setPetUserId(petUserId: String?) {
        this.petUserId = petUserId
    }

    fun getPetImage(): String? {
        return petImage
    }

    fun setPetImage(petImage: String?) {
        this.petImage = petImage
    }
}