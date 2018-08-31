package actors

import akka.actor.Actor
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.Flow
import play.api.libs.json.JsValue
import akka.stream.scaladsl.Keep
import akka.stream.scaladsl.MergeHub
import akka.stream.scaladsl.BroadcastHub
import akka.stream.Materializer
import scala.concurrent.ExecutionContext
import javax.inject.Inject
import akka.actor.ActorLogging
import repository.Order
import akka.NotUsed
import akka.util.Timeout
import scala.concurrent.duration._

class TableActor @Inject()()(implicit mat: Materializer, ec: ExecutionContext) extends Actor with ActorLogging {
  import TableActor.WatchTable
  
  implicit val timeout: Timeout = 100 millis
  
  // Log events to the console
  val in = Sink.foreach[Any](println)

  val (hubSink, hubSource) = MergeHub.source[String](perProducerBufferSize = 16)
    .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both)
    .run()
    
  lazy val websocketFlow = {
    Flow.fromSinkAndSource(in, hubSource)
  }

  override def receive = {
    case WatchTable() => 
      sender() ! websocketFlow

    case Order(tableId, item, orderId) =>
      val ack = s"Received order [$orderId] from table [$tableId] for item [$item]"
      log.info(ack)
      Source.single(ack).runWith(hubSink)
  }

}

object TableActor {
  trait Factory {
    def apply(id: String): Actor
  }
  
  case class WatchTable()
}