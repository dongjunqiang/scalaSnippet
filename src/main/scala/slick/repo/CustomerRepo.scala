package slick.repo

import com.typesafe.scalalogging.StrictLogging
import slick.Database
import slick.domain._

import scala.concurrent.Future

trait CustomerRepo extends StrictLogging {
  val database: Database

  import database.customers
  import database.databaseApi._
  import database.userTypeColumnType

  def register(name: String,userType: UserType): Future[Customer] = {
    database.run(registerAction(name, userType))
  }

  def registerAction(name: String,userType: UserType): DBIOAction[Customer, NoStream, Effect.Write] = {
    val q = (customers returning customers.map(_.id)
      into ((customer, id) => customer.copy(id = id))
      ) += Customer(None, name, None,userType,AuditInfo())
    q
  }

  def nextId(seqName: String): Future[Int] = {
    val rs = database.run[Int](Sequence[Int](seqName).next.result)
    rs
  }

  def listAll: Future[Seq[Customer]] = database.run(customers.result)

  def findByNameLike(name: String): Future[Seq[Customer]] = {
    val q = customers.filter(_.name like "%notyy%")
    database.run(q.result)
  }

  def findByUserType(userType: UserType): Future[Seq[Customer]] = {
    val q = customers.filter(_.userType === userType)
    database.run(q.result)
  }
}