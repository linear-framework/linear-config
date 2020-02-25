package com.linearframework.config

import java.net.URL

private[config] trait ClasspathConfiguration extends Configuration {

  override final protected def getPropertiesFile: URL = {
    var url = Thread.currentThread.getContextClassLoader.getResource(propertiesFile)
    if (url == null)
      url = this.getClass.getClassLoader.getResource(propertiesFile)
    if (url == null)
      url = this.getClass.getResource(propertiesFile)
    if (url == null)
      throw new ConfigurationException(s"Configuration file [$propertiesFile] does not exist")
    url
  }

}
