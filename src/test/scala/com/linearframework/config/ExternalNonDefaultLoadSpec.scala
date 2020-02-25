package com.linearframework.config

import com.linearframework.BaseSpec
import java.io.File

class ExternalNonDefaultLoadSpec extends BaseSpec {

  "No config.file property" should "be present. These tests will fail if -Dconfig.file is set" in {
    System.getProperty("config.file") should be (null)
  }

  "The test properties" should "load" in {
    val path = new File(this.getClass.getClassLoader.getResource("test.properties").toString.replaceAll("^file:", "")).getAbsolutePath
    val config = Configuration.load(path)
    config.getString("hello") should be ("test hello")
    config.getString("goodbye") should be ("test goodbye")
    config.getString("see.you.soon") should be ("test see you soon")
  }

}
