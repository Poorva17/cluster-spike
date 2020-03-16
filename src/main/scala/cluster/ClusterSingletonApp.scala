package cluster

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, SpawnProtocol, SupervisorStrategy}
import akka.cluster.typed.{ClusterSingleton, SingletonActor}

object ClusterSingletonApp extends App {
  val system = ActorSystem(SpawnProtocol(), "cluster-spike-system")
  private val singleton: ClusterSingleton = ClusterSingleton(system)

  private val singletonActor: SingletonActor[Counter.Command] = SingletonActor(Behaviors.supervise(Counter()).onFailure[Exception](SupervisorStrategy.restart), "GlobalCounter")
  private val proxy: ActorRef[Counter.Command] = singleton.init(singletonActor.withStopMessage(Counter.GoodByeCounter))
  proxy ! Counter.Increment
}
