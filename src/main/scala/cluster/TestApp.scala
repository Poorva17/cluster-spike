package cluster

import akka.actor.typed._
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.typed.{Cluster, ClusterSingleton, SingletonActor}

import scala.concurrent.duration.DurationDouble

object TestApp extends App {
  val system = ActorSystem(SpawnProtocol(), "cluster-spike-system")
  implicit val ec = system.executionContext

  private val singletonActor: SingletonActor[Counter.Command] = SingletonActor(
    Behaviors
      .supervise(Counter())
      .onFailure[Exception](SupervisorStrategy.restart),
    "GlobalCounter"
  )

  Thread.sleep(2000)

  val singleton = ClusterSingleton(system)
  private val proxy: ActorRef[Counter.Command] = singleton.init(singletonActor)

  val a: Runnable = () => {
    println("sending msg ===============>")
    proxy ! Counter.Increment
  }
  system.scheduler.scheduleAtFixedRate(1.seconds, 1.seconds)(a)

}
