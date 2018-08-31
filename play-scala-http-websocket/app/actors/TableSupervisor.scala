package actors

import javax.inject.Inject

import akka.actor._
import play.api.libs.concurrent.InjectedActorSupport
import akka.stream.scaladsl.Flow
import play.api.libs.json.JsValue
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import repository.Order
import scala.collection.mutable
import akka.NotUsed

class TableSupervisor @Inject()(childFactory: TableActor.Factory)
  (implicit ec: ExecutionContext)
  extends Actor with InjectedActorSupport with ActorLogging {
  
  import TableSupervisor.NewTable
  import TableActor.WatchTable
  import akka.pattern.{ask, pipe}
  
  val tableMap: mutable.Map[String, ActorRef] = mutable.HashMap()
  
  override def receive = {
    case NewTable(tableId) =>
      implicit val timeout: Timeout = 2 seconds
      val childActor: ActorRef = actorOf(tableId)
      val future = (childActor ? WatchTable()).mapTo[Flow[Any, String, NotUsed]]
      pipe(future) to sender()
      
    case order: Order =>
      val childActor: ActorRef = actorOf(order.tableId)
      childActor ! order
  }
  
  def actorOf(tableId: String): ActorRef = {
    if (tableMap.contains(tableId)) {
        tableMap.get(tableId).get
      } else {
        val childName = s"tableActor-$tableId"
        var child: ActorRef = injectedChild(childFactory(tableId), childName)
        tableMap += (tableId -> child)
        child
      }
  }
}

object TableSupervisor {
  case class NewTable(tableId: String)
}