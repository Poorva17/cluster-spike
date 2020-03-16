package cluster

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, SpawnProtocol, SupervisorStrategy}
import akka.cluster.typed.{ClusterSingleton, SingletonActor}

import scala.concurrent.duration.DurationDouble

object TestApp extends App {
  val system = ActorSystem(SpawnProtocol(), "test-system")
  private val singleton: ClusterSingleton = ClusterSingleton(system)
  implicit val ec = system.executionContext

  private val singletonActor: SingletonActor[Counter.Command] = SingletonActor(Behaviors.supervise(Counter()).onFailure[Exception](SupervisorStrategy.restart), "GlobalCounter")
  private val proxy: ActorRef[Counter.Command] = singleton.init(singletonActor.withStopMessage(Counter.GoodByeCounter))

  val a: Runnable = new Runnable {
    override def run(): Unit = {
      println("sending -------------> ")
      proxy ! Counter.Increment
    }
  }
  system.scheduler.scheduleAtFixedRate(1.seconds, 1.seconds)(a)

}
