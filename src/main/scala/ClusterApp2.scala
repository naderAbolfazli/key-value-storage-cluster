import akka.actor.{ActorSystem, Address, Props}
import akka.cluster.Cluster
import akka.pattern._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.util.Success

object ClusterApp2 extends App {
  val port = 2552
  // Override the configuration of the port
  val config = ConfigFactory.parseString(
    s"""
        akka.remote.netty.tcp.port=$port
        akka.remote.artery.canonical.port=$port
        """).withFallback(ConfigFactory.load())

  val system = ActorSystem("ClusterSystem", config)
  val cluster = Cluster(system)

  val joinAddress = Address("akka", "ClusterSystem", "127.0.0.1", 2551)
  cluster.join(joinAddress)


  val dataActor = system.actorOf(Props(new DataActor))


  import DataActor._
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout = Timeout(5 seconds)

  // testing block
  {
    dataActor ! Add("3", "nader")
    dataActor ! Add("2", "ali")

    val f = dataActor ? Get("2")
    f.onComplete({
      case Success(result) => println(result)
    })
  }

}
