package com.linearframework.config

import com.linearframework.BaseSpec

class DefaultLoadSpec extends BaseSpec {

  "No config.file property" should "be present. These tests will fail if -Dconfig.file is set" in {
    System.getProperty("config.file") should be (null)
  }

  "The default properties" should "load" in {
    val config = Configuration.load()
    config.getString("hello") should be ("hello")
    config.getString("goodbye") should be ("goodbye")

    a [PropertyNotFoundException] should be thrownBy config.getString("see.you.soon")
    config.getString("see.you.soon", "not found") should be ("not found")

    config.getInt("forty") should be (40)
    config.getInt("forty", 300) should be (40)

    config.getDouble("forty") should be (40d)
    config.getDouble("forty", 300.0) should be (40d)

    config.getLong("forty") should be (40L)
    config.getLong("forty", 300L) should be (40L)

    config.getBoolean("flag") should be (true)
    config.getBoolean("flag", default = false) should be (true)
  }

}
