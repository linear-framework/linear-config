package com.linearframework.config

import com.linearframework.BaseSpec
import org.scalatest.Ignore

@Ignore // This class requires both JVM args and environment variables to be set.  See tests for details.
class PropertyOverloadSpec extends BaseSpec {

  "No config.file property" should "be present. These tests will fail if -Dconfig.file is set" in {
    System.getProperty("config.file") should be (null)
  }

  "Environment variables" must "be set. These tests will fail if not set" in {
    System.getenv("test.property.location") should be ("environment")
    System.getenv("see.you.soon") should be("see you soon")
    System.getenv("flag") should be ("false")
  }

  "JVM arguments" must "be present. These tests will fail if is not set" in {
    System.getProperty("test.property.location") should be ("jvm")
    System.getProperty("see.you.later") should be ("see you later")
  }

  "The default properties" should "load" in {
    val config = Configuration.load()
    config.getString("hello") should be ("hello")
    config.getString("goodbye") should be ("goodbye")
  }

  "JVM arguments" should "take precedence over environment variables and property files" in {
    val config = Configuration.load()
    config.getString("test.property.location") should be ("jvm")
    config.getString("see.you.later") should be ("see you later")
  }

  "Environment variables" should "take precendence over property files" in {
    val config = Configuration.load()
    config.getString("see.you.soon") should be ("see you soon")
    config.getBoolean("flag") should be (false)
  }

}
