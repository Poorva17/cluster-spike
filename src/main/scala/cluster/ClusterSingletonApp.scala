package cluster

import akka.Done
import akka.actor.CoordinatedShutdown
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorRefResolver, ActorSystem, SpawnProtocol, SupervisorStrategy}
import akka.cluster.typed.{ClusterSingleton, SingletonActor}

import scala.concurrent.{ExecutionContext, Future}

object ClusterSingletonApp extends App {
  val system = ActorSystem(SpawnProtocol(), "cluster-spike-system")
  implicit val ec: ExecutionContext = system.executionContext

  Thread.sleep(2000)
  println("joined cluster ===================")

  private val singleton: ClusterSingleton = ClusterSingleton(system)
  private val singletonActor: SingletonActor[Counter.Command] = SingletonActor(
    Behaviors
      .supervise(Counter())
      .onFailure[Exception](SupervisorStrategy.resume),
    "GlobalCounter"
  )
  private val proxy: ActorRef[Counter.Command] =
    singleton.init(singletonActor.withStopMessage(Counter.GoodByeCounter))

  proxy ! Counter.Increment

  println(s"proxy ---------> ${ActorRefResolver(system).toSerializationFormat(proxy)}")
  println("Counter actor UP")

  CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseBeforeServiceUnbind, "print") { () =>
  println("stopping")
    Future {println("shutting down"); Done}
  }
}
