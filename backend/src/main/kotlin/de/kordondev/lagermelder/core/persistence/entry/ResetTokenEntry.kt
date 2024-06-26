package de.kordondev.lagermelder.core.persistence.entry

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.Hibernate

@Entity
@Table(name = "reset_token")
data class ResetTokenEntry(
  @Id
  @Column(name = "token", unique = true)
  val token: String,

  @Column(name = "user_id")
  val userId: Long,
) {

    companion object {
        fun of(token: String, userId: Long): ResetTokenEntry {
            return ResetTokenEntry(
                token = token,
                userId = userId
            )
        }
    }

    override fun toString(): String {
        return "$userId $token"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ResetTokenEntry

        return token != null && token == other.token
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
