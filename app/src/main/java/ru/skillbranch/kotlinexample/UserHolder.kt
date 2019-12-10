package ru.skillbranch.kotlinexample

object UserHolder {

    private val map = mutableMapOf<String, User>()

    private val phoneRegex: Regex by lazy {
        "\\+\\[1-9]\\[0-9]{10}".toRegex()
    }

    fun clear() {
        map.clear()
    }

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User {
        return User.makeUser(fullName, email=email, password = password)
            .also { user ->
                require(map[user.login] == null) { "A user with this email already exists" }
            }
            .also {
                user -> map[user.login] = user
            }
    }

    fun registerUserByPhone(fullName: String, rawPhone: String) : User {
        val phones = phoneRegex.split(rawPhone)
        require(phones.size == 1) { "Enter a valid phone number starting with a + and containing 11 digits" }
        val phone = phones.first()
        require((map[phone] == null)) { "A user with this phone already exists" }
        return User.makeUser(fullName, phone = phone)
            .also {user -> map[phone] = user }
    }

    fun requestAccessCode(phone: String) {
        val user = map[phone] as User
        map[phone] = user.reassignCode()
    }

    fun loginUser(login: String, password: String) : String? {
        return map[login.trim()]?.run {
            if (checkPassword(password)) this.userInfo
            else null
        }
    }

}