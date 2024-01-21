import org.hibernate.Hibernate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "reset_token")
data class ResetTokenEntry(
  @Id
  @Column(name = "token", unique = true)
  val token: String,

  @Column(name="department_id")
  val departmentId: Long,
) {

    companion object {
        fun of(token: String, departmentId: Long): ResetTokenEntry {
            return ResetTokenEntry(
                token = token,
                departmentLeaderMail = departmentLeaderMail,
                departmentId = departmentId,
            )
        }
    }

    override fun toString(): String {
        return "$departmentId $token"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ResetTokenEntry

        return token != null && token == other.token
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
