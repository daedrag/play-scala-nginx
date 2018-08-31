import javax.inject._

import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}
import repository.{OrderRepository, OrderRepositoryImpl}
import actors.{TableActor, TableSupervisor}
import play.api.libs.concurrent.AkkaGuiceSupport

/**
  * Sets up custom components for Play.
  *
  * https://www.playframework.com/documentation/latest/ScalaDependencyInjection
  */
class Module(environment: Environment, configuration: Configuration)
    extends AbstractModule with AkkaGuiceSupport {

  override def configure() = {
    bind(classOf[OrderRepository])
      .to(classOf[OrderRepositoryImpl])
      .asEagerSingleton()
    
    bindActor[TableSupervisor]("tableSupervisor")
    bindActorFactory[TableActor, TableActor.Factory]
  }
}
