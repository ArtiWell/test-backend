package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorEntity
import mobi.sevenwinds.app.author.AuthorTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object BudgetService {
    suspend fun addRecord(body: BudgetRequest): BudgetResponse = withContext(Dispatchers.IO) {
        transaction {
            val author = AuthorEntity.wrapRows(AuthorTable
                .select {AuthorTable.id eq body.authorId})
                .firstOrNull()

            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
                this.authorId = author?.id

            }

            return@transaction entity.toResponse(author)
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {

            val paged = BudgetEntity.wrapRows(BudgetTable
                .select { BudgetTable.year eq param.year }
                .limit(param.limit, param.offset)
                .orderBy(BudgetTable.month)
                .orderBy(BudgetTable.amount,SortOrder.DESC)
)

            val data = AuthorEntity.wrapRows(AuthorTable
                .select { AuthorTable.id.inList(paged.filter { it.authorId != null }.map { it.authorId!!.value }) }
            ).associateBy { it.id }


            val allByParam = BudgetTable
                .select { BudgetTable.year eq param.year }

            val sumByType = BudgetEntity.wrapRows(allByParam)
                .map { it.toResponse(null) }
                .groupBy { it.type.name }
                .mapValues { it.value.sumOf { v -> v.amount } }

            return@transaction BudgetYearStatsResponse(
                total = allByParam.count(),
                totalByType = sumByType,
                items = paged.map { it.toResponse(data[it.authorId]) }

            )
        }
    }
}