package com.linearframework.config

import com.linearframework.BaseSpec
import org.scalatest.Ignore

@Ignore // This class requires JVM arguments to be set.  See tests for details.
class JvmArgumentLoadSpec extends BaseSpec {

  "The config.file property" should "be set. These tests will fail if -Dconfig.file is not set to test.properties" in {
    System.getProperty("config.file") should be ("test.properties")
    System.getProperty("config.file.2") should be ("application.properties")
  }

  "The test properties" should "load" in {
    val config = Configuration.load()
    config.getString("hello") should be ("test hello")
    config.getString("goodbye") should be ("test goodbye")
    config.getString("see.you.soon") should be ("test see you soon")
  }

  "The JVM argument" can "be customized" in {
    val config = Configuration.load("config.file.2", "test.properties")
    config.getString("hello") should be ("hello")
    config.getString("goodbye") should be ("goodbye")
  }

}
