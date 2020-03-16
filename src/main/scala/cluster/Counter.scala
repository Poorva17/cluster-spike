package cluster

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object Counter {
  trait Command
  case object Increment extends Command
  final case class GetValue(replyTo: ActorRef[Int]) extends Command
  case object GoodByeCounter extends Command

  def apply(): Behavior[Command] = {
    def updated(value: Int): Behavior[Command] = Behaviors.setup { ctx =>
      Behaviors.receiveMessage[Command] {
        case Increment =>
          println(s" ************ actor ${ctx.system.path}")
          updated(value + 1)
        case GetValue(replyTo) =>
          replyTo ! value
          Behaviors.same
        case GoodByeCounter =>
          // Possible async action then stop
          Behaviors.stopped
      }
    }

    updated(0)
  }
}
