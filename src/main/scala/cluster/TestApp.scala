package cluster

import akka.actor.typed._

import scala.concurrent.duration.DurationDouble

object TestApp extends App {
  val system = ActorSystem(SpawnProtocol(), "cluster-spike-system")
  implicit val ec = system.executionContext

//  private val singletonActor: SingletonActor[Counter.Command] = SingletonActor(
//    Behaviors
//      .supervise(Counter())
//      .onFailure[Exception](SupervisorStrategy.restart),
//    "GlobalCounter"
//  )
//
//  Thread.sleep(2000)

  private val proxy: ActorRef[Counter.Command] = ActorRefResolver(system).resolveActorRef("akka://cluster-spike-system@127.0.0.1:2552/system/singletonProxyGlobalCounter-no-dc#-752270305")

  val a: Runnable = () => {
    println("sending ----------->")
    proxy ! Counter.Increment
  }
  system.scheduler.scheduleAtFixedRate(1.seconds, 1.seconds)(a)
}
