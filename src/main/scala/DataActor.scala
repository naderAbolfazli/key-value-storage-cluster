import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.Cluster
import akka.cluster.ddata.Replicator._
import akka.cluster.ddata._
import akka.util.Timeout
import scala.concurrent.duration._

object DataActor {
  case class Get(key: String)
  case class Add(key: String, value: String)
  case class Del(key: String)
}

class DataActor extends Actor with ActorLogging {
  import DataActor._
  implicit val node = Cluster(context.system)
  implicit val timeout = Timeout(10 seconds)

  val replicator: ActorRef = DistributedData(context.system).replicator
  val DataStruct = ORMultiMapKey[String, String]("pairs")
  replicator ! Subscribe(DataStruct, self)

  override def receive: Receive = {
    case Get(key) =>
      replicator ! Replicator.Get(DataStruct, ReadAll(5 seconds), Some((sender(), key)))

    case g@Replicator.GetSuccess(DataStruct, Some((replyTo: ActorRef, key: String))) =>
      replyTo ! g.get(DataStruct).entries.get(key)

    case GetFailure(DataStruct, Some(replyTo: ActorRef)) =>
      replyTo ! -1L
    case NotFound(DataStruct, Some(replyTo: ActorRef)) =>
      replyTo ! 0L

    case Add(key, value) =>
      replicator ! Update(DataStruct, ORMultiMap.empty[String, String], WriteAll(5 seconds))(_.addBinding(key, value))

    case Del(key) =>
      replicator ! Update(DataStruct, ORMultiMap.empty[String, String], WriteAll(5 seconds))(_.remove(node, key))

    case u: UpdateResponse[_] => println(u)

    case c@Changed(DataStruct) =>
      val data = c.get(DataStruct)
      log.info("changed to {}", data.entries)
  }
}
