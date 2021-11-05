package repositories

import models.{Bank, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import services.BillService
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BankRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] with BillService {
  import profile.api._

  def addBank(bank: Bank): Future[String] = {
    db.run(slickBank += bank).map(res => "Bank successfully created")
  }

  def getBank(user: User): Future[Option[Bank]] = {
    val query = slickBank.filter(_.userId === user.id )
    db.run(query.result.headOption)
  }

  def updateBank(id: Long, name: String, bankCode: String, guichetCode: String, account: String, ribKey: BigDecimal, iban: String, userId: Long): Future[Int] = {
    db.run(slickBank
    .filter(_.id === id)
    .map(x=>(x.name, x.bankCode, x.guichetCode, x.account, x.ribKey, x.iban, x.userId))
    .update(name, bankCode, guichetCode, account, ribKey, iban, userId))
  }

  def deleteBank(id: Long): Future[Int] = db.run{
    slickBank.filter(_.id === id).delete
  }
}
