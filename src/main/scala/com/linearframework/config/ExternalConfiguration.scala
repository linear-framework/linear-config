package com.linearframework.config

import java.io.File
import java.net.URL

private[config] trait ExternalConfiguration extends Configuration {

  override final protected def getPropertiesFile: URL = {
    val file = new File(propertiesFile)
    if (!file.exists()) {
      throw new ConfigurationException(s"Configuration file [$propertiesFile] does not exist")
    }
    if (!file.isFile) {
      throw new ConfigurationException(s"Configuration file [$propertiesFile] is not a valid file")
    }
    if (!file.canRead) {
      throw new ConfigurationException(s"Configuration file [$propertiesFile] could not be read")
    }
    file.toURI.toURL
  }

}
