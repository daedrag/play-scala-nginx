package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import repository._

import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, repository: OrderRepository)
  (implicit ec: ExecutionContext) extends AbstractController(cc) {

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
      Ok(s"Received order [${order.orderId}] from table [${order.tableId}] for item [${order.item}]")
    }
  }
}
