package cn.zenliu.ktor.redis

import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import io.lettuce.core.RedisClient
import io.lettuce.core.codec.RedisCodec
import java.nio.ByteBuffer
import java.nio.charset.Charset

class RedisFactory(val conf: RedisConfiguration) {
    /**
     * configuration of redis ref[lettuce](https://github.com/lettuce-io/lettuce-core)
     * @property url String
     * @constructor
     */
    data class RedisConfiguration(
        var url: String
    )

    fun newClient() = RedisClient.create(conf.url)

    companion
    object Feature : ApplicationFeature<ApplicationCallPipeline, RedisConfiguration, RedisFactory> {
        override val key: AttributeKey<RedisFactory> = AttributeKey("RedisFactory")
        private lateinit var _factory: RedisFactory
        val factory get() = _factory
        val client by lazy { factory.newClient() }
        fun <K : Any, V : Any> newConnection(
            keyEncoder: (K) -> ByteBuffer,
            keyDecoder: (ByteBuffer) -> K,
            valueEncoder: (V) -> ByteBuffer,
            valueDecoder: (ByteBuffer) -> V
        ) = client.connect(
            createCodec<K, V>(
                keyEncoder,
                keyDecoder,
                valueEncoder,
                valueDecoder
            )
        )

        fun <K : Any, V : Any> newConnection(
            codec: RedisCodec<K, V>
        ) = client.connect(codec)

        fun <K : Any, V : Any> newAsyncClient(codec: RedisCodec<K, V>) = newConnection(codec).async()
        fun <K : Any, V : Any> newSyncClient(codec: RedisCodec<K, V>) = newConnection(codec).sync()
        fun <K : Any, V : Any> newClient(codec: RedisCodec<K, V>) = newConnection(codec).sync()
        fun <K : Any, V : Any> newReactiveClient(codec: RedisCodec<K, V>) = newConnection(codec).reactive()

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: RedisConfiguration.() -> Unit
        ): RedisFactory {
            return RedisFactory(RedisConfiguration("").apply(configure)).apply {
                this@Feature._factory = this
            }
        }


        /**
         * create RedisCodec by lambda
         * @param k2b (K) -> ByteBuffer
         * @param b2k (ByteBuffer) -> K
         * @param v2b (V) -> ByteBuffer
         * @param b2v (ByteBuffer) -> V
         * @return RedisCodec<K, V>
         */
        fun <K : Any, V : Any> createCodec(
            k2b: (K) -> ByteBuffer,
            b2k: (ByteBuffer) -> K,
            v2b: (V) -> ByteBuffer,
            b2v: (ByteBuffer) -> V
        ) = object : RedisCodec<K, V> {
            override fun decodeKey(bytes: ByteBuffer): K = b2k.invoke(bytes)
            override fun encodeValue(value: V): ByteBuffer = v2b.invoke(value)
            override fun encodeKey(key: K): ByteBuffer = k2b.invoke(key)
            override fun decodeValue(bytes: ByteBuffer): V = b2v.invoke(bytes)
        }

        fun ByteBuffer.decodeString(charset: Charset = Charsets.UTF_8): String {
            return charset.decode(this).toString()
        }

    }
}
