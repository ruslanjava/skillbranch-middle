package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom

class User private constructor(
    private val firstName: String,
    private val lastName: String?,
    email: String? = null,
    rawPhone: String? = null,
    meta: Map<String, Any>? = null
) {

    val userInfo: String
    private val fullName: String
        get() = listOfNotNull(firstName, lastName)
            .joinToString(" ")
            .capitalize()

    private val initials: String
        get() = listOfNotNull(firstName, lastName)
            .map { it.first().toUpperCase() }
            .joinToString(" ")

    private var phone: String? = null
        set(value) {
            field = value?.replace("[^+\\d]".toRegex(), "")
        }

    private var _login: String? = null

    var login: String
        set(value) {
            _login = value?.toLowerCase()
        }
        get() = _login!!

    private lateinit var passwordHash: String

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    var accessCode: String? = null

    // for email
    constructor(
        firstName: String,
        lastName: String?,
        email: String,
        password: String
    ): this(firstName, lastName, email = email, meta = mapOf("auth" to "password")) {
        println("Secondary mail constructor")
        passwordHash = encrypt(password)
    }

    // for phone
    constructor(
        firstName: String,
        lastName: String?,
        rawPhone: String
    ): this(firstName, lastName, rawPhone = rawPhone, meta = mapOf("auth" to "sms")) {
        println("Secondary phone constructor")
        reassignCode()
    }

    // for csv
    constructor(
        firstName: String,
        lastName: String?,
        email: String,
        salt: String,
        passwordHash: String
    ): this(firstName, lastName, email = email, meta = mapOf("src" to "csv")) {
        println("Third csv constructor")
        this.salt = salt
        this.passwordHash = passwordHash
    }

    init {
        println("First init block, primary constructor was called ")

        check(!firstName.isBlank()) { "FirstName must be not blank" }
        check(email.isNullOrBlank() || rawPhone.isNullOrBlank()) {
            "Email or phone must be not blank"
        }

        phone = rawPhone
        login = email ?: phone!!

        userInfo = """
            firstName: $firstName
            lastName: $lastName
            login: $login
            fullName: $fullName
            initials: $initials
            email: $email
            phone: $phone
            meta: $meta
        """.trimIndent()
    }

    fun checkPassword(pass: String) = encrypt(pass) == passwordHash

    fun changePassword(oldPass: String, newPass: String) {
        if (checkPassword(oldPass)) passwordHash = encrypt(newPass)
        else IllegalArgumentException("The entered pasword does not match the current password")
    }

    private var _salt: String? = null

    private var salt: String
        get() {
            if (_salt == null) {
                _salt = ByteArray(16).also { SecureRandom().nextBytes(it) }.toString()
            }
            return _salt!!
        }
        set(value) {
            _salt = value
        }

    private fun encrypt(password: String) : String = salt.plus(password).md5()

    private fun generateAccessCode(): String {
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return StringBuilder().apply {
            repeat(6) {
                (possible.indices).random().also {index ->
                    append(possible[index])
                }
            }
        }.toString()
    }

    private fun sendUserAccessCodeToUser(phone: String, code: String) {
        println("..... sending access code: $code on $phone")
    }

    private fun String.md5() : String {
        val md5 = MessageDigest.getInstance("MD5")
        val digest = md5.digest(toByteArray()) // 16 byte
        val hexString = BigInteger(1, digest).toString(16)
        return hexString.padStart(32, '0')
    }

    companion object Factory {

        fun makeUser(
            fullName: String,
            email: String? = null,
            password: String? = null,
            phone:String? = null
        ): User {
            val(firstName, lastName) = fullName.fullNameToPair()
            return when {
                !phone.isNullOrBlank() -> User(firstName, lastName, phone)
                !email.isNullOrBlank() && !password.isNullOrBlank() -> User(firstName, lastName, email, password)
                else -> throw IllegalArgumentException("Email or phone must be not null or blank")
            }
        }

        private fun String.fullNameToPair() : Pair<String, String?> {
            return this.split(" ")
                .filter { it.isNotBlank() }
                .run {
                    when(size) {
                        1 -> first() to null
                        2 -> first() to last()
                        else -> throw IllegalArgumentException("Fullname must contain only first and last name, current split result ${this@fullNameToPair}")
                    }
                }
        }

    }

    internal fun reassignCode() : User {
        val code = generateAccessCode()
        passwordHash = encrypt(code)
        accessCode = code
        sendUserAccessCodeToUser(phone!!, code)
        return this
    }

}