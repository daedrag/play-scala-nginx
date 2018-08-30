package repository

import java.util.UUID.randomUUID
import javax.inject.{Inject, Singleton}
import play.api.{Logger, MarkerContext}

import scala.concurrent.{ExecutionContext, Future}

case class NewOrder(tableId: String, item: String)
case class Order(tableId: String, item: String, orderId: String)

trait OrderRepository {
  def addOrder(order: NewOrder)(implicit mc: MarkerContext): Future[Order]
}

@Singleton
class OrderRepositoryImpl @Inject()()(implicit ec: ExecutionContext) extends OrderRepository {
  private val logger = Logger(this.getClass)
  
  override def addOrder(order: NewOrder)(implicit mc: MarkerContext): Future[Order] = {
    Future {
      logger.info(s"Received new order: table ${order.tableId}, item: ${order.item}")
      val orderId = randomUUID().toString()
      Order(order.tableId, order.item, orderId) 
    }
  }
}