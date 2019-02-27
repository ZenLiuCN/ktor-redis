package cn.zenliu.ktor.redis

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.config.MapApplicationConfig
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import io.lettuce.core.codec.Utf8StringCodec
import kotlinx.coroutines.plus
import org.junit.jupiter.api.Test

internal class RedisFacotryTest{

   @Test
   fun test(){
       withTestApplication{
          application.install(RedisFactory){
              url="redis://12345678901@192.168.99.100:6379/0?timeout=10s"
          }
           handleRequest {
               val client=RedisFactory.newClient(Utf8StringCodec())
               assert(client.set("TEST:USER:n6KjAhyNl1Mg2v8q","n4n5bbZupN4Ibffq")=="OK")
               assert(client.get("TEST:USER:n6KjAhyNl1Mg2v8q")=="n4n5bbZupN4Ibffq")
               val keys=client.keys("TEST:USER:*")
               assert(keys.size==1 && keys.contains("TEST:USER:n6KjAhyNl1Mg2v8q"))
           }
       }
   }

}
