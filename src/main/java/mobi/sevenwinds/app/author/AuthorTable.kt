package mobi.sevenwinds.app.author

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable


object AuthorTable : IntIdTable("author") {
    val fullName = text("full_name")
    val dateCreated = datetime("date_created")
}

class AuthorEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AuthorEntity>(AuthorTable)

    var fullName by AuthorTable.fullName
    var dateCreated by AuthorTable.dateCreated


    fun toResponse(): AuthorRecord {
        return AuthorRecord(fullName, dateCreated)
    }
}