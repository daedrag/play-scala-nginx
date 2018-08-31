package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import repository._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import play.api.libs.streams.ActorFlow
import akka.util.Timeout
import akka.actor.ActorRef
import akka.stream.scaladsl.Flow
import akka.NotUsed
import play.api.libs.json.JsValue

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(
    @Named("tableSupervisor") tableSupervisor: ActorRef,
    repository: OrderRepository,
    cc: ControllerComponents)
    (implicit ec: ExecutionContext) extends AbstractController(cc) {

  import akka.pattern.ask
  import actors.TableSupervisor.NewTable

  implicit val timeout: Timeout = 5 seconds
  private val logger = Logger(getClass)

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
  
  def addOrder(tableId: String, item: String) = Action.async {
    repository.addOrder(NewOrder(tableId, item)).map { order => 
      tableSupervisor ! order
      Ok(s"Received order [${order.orderId}] from table [${order.tableId}] for item [${order.item}]")
    }
  }
  
  def streamOrders(tableId: String) = WebSocket.acceptOrResult[Any, String] { request =>
    wsFutureFlow(tableId).map { flow => 
      Right(flow)
    }
  }

  /**
   * Creates a Future containing a Flow of in and out.
   */
  def wsFutureFlow(tableId: String): Future[Flow[Any, String, NotUsed]] = {
    implicit val timeout: Timeout = 2 seconds
    val future: Future[Any] = tableSupervisor ? NewTable(tableId)
    val futureFlow = future.mapTo[Flow[Any, String, NotUsed]]
    futureFlow
  }
}
