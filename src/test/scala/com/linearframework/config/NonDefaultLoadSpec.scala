package com.linearframework.config

import com.linearframework.BaseSpec

class NonDefaultLoadSpec extends BaseSpec {

  "No config.file property" should "be present. These tests will fail if -Dconfig.file is set" in {
    System.getProperty("config.file") should be (null)
  }

  "The test properties" should "load" in {
    val config = Configuration.load("test.properties")
    config.getString("hello") should be ("test hello")
    config.getString("goodbye") should be ("test goodbye")
    config.getString("see.you.soon") should be ("test see you soon")
  }

}
