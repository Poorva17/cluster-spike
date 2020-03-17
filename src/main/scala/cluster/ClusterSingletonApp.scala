package cluster

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed._
import akka.cluster.typed.{ClusterSingleton, SingletonActor}

import scala.concurrent.ExecutionContext

object ClusterSingletonApp extends App {
  val system = ActorSystem(SpawnProtocol(), "cluster-spike-system")
  implicit val ec: ExecutionContext = system.executionContext
  private val singleton: ClusterSingleton = ClusterSingleton(system)

  private val singletonActor: SingletonActor[Counter.Command] = SingletonActor(Behaviors.supervise(Counter()).onFailure[Exception](SupervisorStrategy.restart), "GlobalCounter").withStopMessage(Counter.GoodByeCounter)
  private val proxy: ActorRef[Counter.Command] = singleton.init(singletonActor)

  println(s"proxy ----------> ${ActorRefResolver(system).toSerializationFormat(proxy)}")
  proxy ! Counter.Increment

  println("Counter actor UP")

}
