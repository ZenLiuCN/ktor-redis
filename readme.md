[![](https://jitpack.io/v/ZenLiuCN/ktor_redis.svg)](https://jitpack.io/#ZenLiuCN/ktor_redis)
# ktor-redis
ktor redis client feature base on [lettuce]()(https://github.com/lettuce-io/lettuce-core)
# example
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
dependencies {
	        implementation 'com.github.ZenLiuCN:ktor_redis:v0.0.1'
	}
```
```kotlin
//initialize
fun Application.main(){
install(RedisFactory){
    // configruation for redis to connect
    url="redis://12345678901@192.168.99.100:6379/0?timeout=10s"
  }
}
//use anywhere
fun someRedisOpts(){
    val client=RedisFactory.newClient(Utf8StringCodec())
    println(client.set("somekey","somevalue"))
    println(client.get("somekey")
}

```
# limit
now only add url as configruation parameter
